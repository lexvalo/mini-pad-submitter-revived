/*
 * [Head.java]
 *
 * Summary: Simulates a browser posting a form to CGI via HEAD.
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
 */
package com.mindprod.http;

import com.mindprod.common18.EIO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Simulates a browser posting a form to CGI via HEAD.
 * <p/>
 * It does not read any data. It is used for link checking where the server accepts HEAD requests.
 * If the server might ignore HEAD requests, use Probe instead.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.3 2010-11-14 new method setInstanceFollowRedirects
 * @see com.mindprod.htmlmacros.macros.Head
 * @since 1998
 */
@SuppressWarnings( { "WeakerAccess" } )
public final class Head extends Http
    {
    /**
     * constructor
     */
    public Head()
        {
        }

    /**
     * Check if URL is working
     * Send a form full of data to the CGI host using HEAD.
     *
     * @param url complete URL including get parms pre-encoded.
     *            http: or https:
     *
     * @return responseCode
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase" } )
    public int send( URL url )
        {
        try
            {
            init();
            this.url = url;
            // O P E N
            // urlc will contain subclasses of URLConnection like:
            // http: HttpURLConnection
            // https: HttpsURLConnectionImpl
            // file: FileURLConnection
            final HttpURLConnection urlc = ( HttpURLConnection ) url.openConnection();
            urlc.setAllowUserInteraction( false );
            urlc.setDoInput( true );
            urlc.setDoOutput( false );// nothing beyond original request
            urlc.setUseCaches( false );
            urlc.setRequestMethod( "HEAD" );
            setStandardProperties( urlc );
            urlc.connect(); // ignored if already connected.
            // getResponseCode will block until the server responds.
            // save responseCode for later retrieval
            responseCode = urlc.getResponseCode();
            rawResponseMessage = urlc.getResponseMessage();
            urlc.disconnect();
            return responseCode;
            }
        catch ( ClassCastException e )
            {
            // was not an http: url
            interruptResponseMessage = "Protocol not http: or https:";
            return responseCode;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            return responseCode;
            }
        } // end probe

    /**
     * Check if URL is working
     * Send a form full of data to the CGI host using HEAD.
     * You must have done a setParms first.
     *
     * @param host   host name of the website, Should be form:"mindprod.com", no lead http://.
     * @param port   -1 if default, 8081 for local echoserver.
     * @param action action of form, page on website. Usually has a lead /.
     *
     * @return responseCode
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase" } )
    public int send( String host, int port, String action )
        {
        try
            {
            // defaults
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
            url = new URL( url.toString() + getEncodedParms( EIO.UTF8 ) );
            return send( url );
            }
        catch ( URISyntaxException e )
            {
            interruptResponseMessage = "Bad URI/URL";
            return responseCode;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            return responseCode;
            }
        } // end probe
    }
