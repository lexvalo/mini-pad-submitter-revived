/*
 * [Chase.java]
 *
 * Summary: Chase redirection to find where page permanently redirected if any.
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
 *  1.9 2008-08-22 support accept-charset, accept-encoding and accept-language. Fix bugs in gzip support.
 *  2.0 2009-02-20 major refactoring. separate setParms and setPostParms. new send method. Post can have both types
 *                 of parm.
 *  2.1 2010-02-07 new methods Post.setBody Http.setRequestProperties.
 *  2.2 2010-04-05 new method getURL
 *  2.3 2010-11-14 new method setInstanceFollowRedirects
 */
package com.mindprod.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.System.*;

/**
 * Chase redirection to find where page permanently redirected if any.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.3 2010-11-14 new method setInstanceFollowRedirects
 * @since 2010
 */
@SuppressWarnings( { "WeakerAccess" } )
public final class Chase extends Http
    {
    /**
     * constructor
     */
    public Chase()
        {
        }

    /**
     * Chase redirection to find where page permanently redirected if any.
     * Just follows one leg of redirect chain.
     *
     * @param url complete URL including any parms, pre-encoded (use Http.encodeParms).
     *            might be http: or https:.
     *
     * @return host's response where URL permanently redirected, null if not redirected.
     * Note returns a String, not a URL.  It must be interpreted to create a full URL string.
     * @see Get#send
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase" } )
    public String send( final URL url )
        {
        try
            {
            // urlc will contain subclasses of URLConnection like:
            // http: HttpURLConnection
            // https: HttpsURLConnectionImpl
            // file: FileURLConnection
            init();
            this.url = url;
            // O P E N
            // will return HttpURLConnection for http: or HttpsURLConnection for https:
            final HttpURLConnection urlc = ( HttpURLConnection ) url.openConnection();
            urlc.setAllowUserInteraction( false );
            urlc.setDoInput( true ); // needed to see Location
            urlc.setDoOutput( false );// nothing beyond original request
            urlc.setUseCaches( false );
            urlc.setRequestMethod( "GET" );
            setStandardProperties( urlc );
            urlc.setInstanceFollowRedirects( false ); // we chase only the first leg. Otherwise we won't know about
            // redirects.
            if ( DEBUGGING )
                {
                dumpHeaders( "---sent headers---", urlc );
                }
            // this is similar to HTTP.connectAndProcessResponse, except we don't read the page, and return the location.
            // send the message. Won't return from connect until other end responds with header.
            urlc.connect(); // ignored if already connected.
            // getInputStream not needed.
            // getResponseCode will block until the server responds.
            // save responseCode for later retrieval
            responseCode = urlc.getResponseCode();
            rawResponseMessage = urlc.getResponseMessage();
            if ( DEBUGGING )
                {
                dumpHeaders( "---response headers---", urlc );
                }
            // do longhand so can trace in debugger
            final String location;
            if ( 301 <= responseCode && responseCode <= 303 )
                {
                // 301 	HTTP_MOVED_PERM 	Moved Permanently	This means the page has moved permanently. Please
                // change
                // your links since the old URL will probably soon stop working.
                // 302 	HTTP_MOVED_TEMP 	Temporary Redirect	Just informative. The actual page they gave you is not
                // literally the one you requested. Just treat this like an OK
                // 303 	HTTP_SEE_OTHER 	See Other	This usually means the page has changed from http:// to https://
                // or vice versa. Please update your links.
                location = urlc.getHeaderField( "Location" );
                }
            else
                {
                location = null;
                }
            // don't bother to read page content or even open the InputStream.
            if ( DEBUGGING )
                {
                out.println( "--------------------------------" );
                out.println( "ResponseCode : " + responseCode );
                out.println( "ResponseMessage : " + getResponseMessage() );
                out.println( "Location : " + location );
                }
            // C L O S E
            urlc.disconnect();
            return location;
            }
        catch ( ClassCastException e )
            {
            // was not an http: url
            interruptResponseMessage = "Bug : not http/https : " + e.getMessage();
            return null;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            return null;
            }
        } // end get
    }
