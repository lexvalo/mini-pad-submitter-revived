/*
 * [Probe.java]
 *
 * Summary: simulates a browser posting a form to CGI via HEAD/GET.
 *
 * Copyright: (c) 1998-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  2.0 2009-02-20 major refactoring. separate setParms and setPostParms. new send method. Post can have both types
 *                 of parm.
 *  2.1 2010-02-07 new methods Post.setBody Http.setRequestProperties.
 *  2.2 2010-04-05 new method getURL
 *  2.3 2010-11-14 new method setInstanceFollowRedirects
 *  2.4 2012-02-26 Probe send now has supportsHEAD parm and their is a getSupportsHEAD parm.
 */
package com.mindprod.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * simulates a browser posting a form to CGI via HEAD/GET.
 * <p/>
 * It does not read any data. It is used for link checking where the server might ignore HEAD requests.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.4 2012-02-26 Probe send now has supportsHEAD parm and their is a getSupportsHEAD parm.
 * @since 1998
 */
@SuppressWarnings( { "WeakerAccess" } )
public final class Probe extends Http
    {
    /**
     * 0=site did not support HEAD 1=site supports HEAD 2=don't know.
     */
    int supportsHEAD;

    /**
     * constructor
     */
    public Probe()
        {
        }

    /**
     * Did last  HEAD probe work?
     *
     * @return 0=site does not support HEAD 1=site supports HEAD 2=don't know because both HEAD and GET probes failed.
     */
    public int getSupportsHEAD()
        {
        return supportsHEAD;
        }

    /**
     * Check if a URL is working. Does a HEAD probe, if that fails does a GET probe in case server is ignoring HEAD
     * requests.
     * We don't pay any attention to the content.
     *
     * @param url          complete URL including get parm,  http: or https:
     * @param supportsHEAD 0=site does not support HEAD 1=site supports HEAD 2=don't know.
     *                     enables bypassing HEAD or GET request for speed.
     *                     In other wards: 0=use GET 1=use HEAD 2=use HEAD then GET.
     *
     * @return responseCode
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase" } )
    public int send( URL url, int supportsHEAD )
        {
        this.supportsHEAD = 2;
        try
            {
            init();
            this.url = url;
            HttpURLConnection urlc;
            if ( supportsHEAD == 1 || supportsHEAD == 2 )
                {
                // O P E N
                // urlc will contain subclasses of URLConnection like:
                // http: HttpURLConnection
                // https: HttpsURLConnectionImpl
                // file: FileURLConnection
                urlc = ( HttpURLConnection ) url.openConnection();
                urlc.setAllowUserInteraction( false );
                urlc.setDoInput( true );
                urlc.setDoOutput( false );// nothing beyond original request
                urlc.setUseCaches( false );
                // use a head first, it is the official way.
                urlc.setRequestMethod( "HEAD" );
                setStandardProperties( urlc );
                urlc.connect(); // ignored if already connected.
                // getResponseCode will block until the server responds.
                // save responseCode for later retrieval
                responseCode = urlc.getResponseCode();
                rawResponseMessage = urlc.getResponseMessage();
                urlc.disconnect();
                if ( responseCode == HttpURLConnection.HTTP_OK /* 200 */ )
                    {
                    this.supportsHEAD = 1;
                    return responseCode;
                    }
                }
            // HEAD failed. It may be because the server is being a jerk and ignoring HEADs. Try a GET.
            if ( supportsHEAD == 0 || supportsHEAD == 2 )
                {
                // get a fresh connection
                // urlc will contain subclasses of URLConnection like:
                // http: HttpURLConnection
                // https: HttpsURLConnectionImpl
                // file: FileURLConnection
                urlc = ( HttpURLConnection ) url.openConnection();
                urlc.setAllowUserInteraction( false );
                urlc.setDoInput( true );
                urlc.setDoOutput( false );// nothing beyond original request
                urlc.setUseCaches( false );
                // use a head first, it is the official way.
                urlc.setRequestMethod( "GET" );
                setStandardProperties( urlc );
                urlc.connect(); // ignored if already connected.
                // getResponseCode will block until the server responds.
                // save responseCode for later retrieval
                responseCode = urlc.getResponseCode();
                rawResponseMessage = urlc.getResponseMessage();
                // we need to be polite and dispose of the InputStream, even if we don't read the response.
                final InputStream is = urlc.getInputStream();
                // C L O S E
                is.close();
                urlc.disconnect();
                if ( responseCode == HttpURLConnection.HTTP_OK /* 200 */ )
                    {
                    this.supportsHEAD = 0;
                    }
                return responseCode;
                }
            }
        catch ( ClassCastException e )
            {
            // was not an http: url
            interruptResponseMessage = "Bug : not http/https : " + e.getMessage();
            return responseCode;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            return responseCode;
            }
        return responseCode;
        } // end probe

    /**
     * Send a form full of data to the CGI host using HEAD/GET.
     * Must do a setParms beforehand. Lets you know if page exists.
     *
     * @param host         host name of the website, Should be form:"mindprod.com", no lead http://.
     * @param port         -1 if default, 8081 for local echoserver.
     * @param action       action of form, page on website. Usually has a lead /.
     * @param supportsHEAD 0=site does not support HEAD 1=site supports HEAD 2=don't know.
     *                     enables bypassing HEAD or GET request for speed.
     *                     In other wards: 0=use GET 1=use HEAD 2=use HEAD then GET.
     *
     * @return responseCode
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase" } )
    public int send( String host, int port, String action, int supportsHEAD )
        {
        try
            {
            init();
            // O P E N
            // URL will encode target and parms.
            URL url = new URI( "http",
                    null,
                    host,
                    port,
                    action,
                    null,
                    null ).toURL();
            // encoding does not matter since we are not interested in text of result, just status code.
            url = new URL( url.toString() + getEncodedParms( Probe.UTF8 ) );
            return send( url, supportsHEAD );
            }
        catch ( URISyntaxException e )
            {
            return responseCode;
            }
        catch ( IOException e )
            {
            return responseCode;
            }
        } // end probe
    }
