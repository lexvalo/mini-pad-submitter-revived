/*
 * [Get.java]
 *
 * Summary: simulates a browser posting a form to CGI via GET.
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * simulates a browser posting a form to CGI via GET.
 * <p/>
 * See com.mindprod.submitter for sample code to use this class.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.3 2010-11-14 new method setInstanceFollowRedirects
 * @since 1998
 */
public final class Get extends Http
    {
    /**
     * the connection. null if no connection in progess
     */
    HttpURLConnection urlc;

    /**
     * constructor
     */
    public Get()
        {
        }

    /**
     * disconnect/abort the current connection
     */
    public void disconnect()
        {
        if ( urlc != null )
            {
            urlc.disconnect();
            urlc = null;
            }
        }

    /**
     * Send a form full of data to the CGI host using GET.
     * Note. This method ignores any setParms.
     *
     * @param url      complete URL including get parms, pre-encoded, http: or https:.
     * @param encoding encoding of the byte stream result, usually UTF-8 or or ISO-8859-1.
     *
     * @return CGI host's response with headers and embedded length fields stripped.
     * @see com.mindprod.http.Get
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase", "WeakerAccess" } )
    public String send( URL url, Charset encoding )
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
            // this does not connect with server, just gets us an object to control the connection
            urlc = ( HttpURLConnection ) url.openConnection();
            // if this is https: urlc will be a sun.net.www.protocol.https.DelegateHttpsURLConnection object
            urlc.setAllowUserInteraction( false );
            urlc.setDoInput( true );
            urlc.setDoOutput( false );// nothing beyond original request
            urlc.setUseCaches( false );
            urlc.setRequestMethod( "GET" );    // <-- vhere we decide GET, HEAD, POST ectc.
            setStandardProperties( urlc );
            final String result = connectAndProcessResponse( encoding, urlc );  // does the connect and read
            urlc = null;
            return result;
            }
        catch ( ClassCastException e )
            {
            // was not an http: url
            interruptResponseMessage = "Bug : not http/https : " + e.getMessage();
            urlc = null;
            return null;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            urlc = null;
            return null;
            }
        } // end get

    /**
     * Send a form full of data to the CGI host using GET.
     * Must have previously specified the parms with setParms.
     *
     * @param host     host name of the website, Should be form:"mindprod.com", no lead http://.
     * @param port     -1 if default, 8081 for local echoserver.
     * @param action   action of form, page on website. Usually has a lead / and no trailing /.
     * @param encoding encoding of the byte stream result, usually UTF-8 or or ISO-8859-1.
     *
     * @return CGI host's response with headers and embedded length fields stripped.
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase" } )
    public String send( String host, int port, String action, Charset encoding )
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
            url = new URL( url.toString() + getEncodedParms( encoding ) );
            return send( url, encoding );
            }
        catch ( URISyntaxException e )
            {
            interruptResponseMessage = "Bad URI/URL";
            return null;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            return null;
            }
        } // end get
    }
