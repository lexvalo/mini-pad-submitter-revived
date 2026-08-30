/*
 * [Fetch.java]
 *
 * Summary: Reads HTTP page with a generic http: https: file: etc, arbitrary URL.
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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * Reads HTTP page with a generic http: https: file: etc, arbitrary URL.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.3 2010-11-14 new method setInstanceFollowRedirects
 * @since 1998
 */
@SuppressWarnings( { "WeakerAccess" } )
public final class Fetch extends Http
    {
    /**
     * constructor
     */
    public Fetch()
        {
        }

    /**
     * Read a message given an URL.  Does not support getResponseCode or getResponseMessage.
     *
     * @param url            complete URL including any parms, pre-encoded (use Http.encodeParms).
     *                       might be http: https: file: and possibly others.
     * @param defaultCharset default encoding of the byte stream result, usually UTF-8 or or ISO-8859-1.
     *
     * @return host's response with headers and embedded length fields stripped.
     * @see com.mindprod.filetransfer.Download
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase" } )
    public String send( final URL url, final Charset defaultCharset )
        {
        try
            {
            // defaults
            init();
            // we can't get at the true responseCode, The requires an HTTPURLConnection.
            this.url = url;
            // O P E N
            final URLConnection urlc = url.openConnection();
            // Not actually connecting yet, just getting connection object,
            // urlc will contain subclasses of URLConnection like:
            // http: HttpURLConnection
            // https: HttpsURLConnectionImpl
            // file: FileURLConnection
            urlc.setAllowUserInteraction( false );
            urlc.setDoInput( true );
            urlc.setDoOutput( false );// nothing beyond original request
            urlc.setUseCaches( false );
            // we leave it up to URLConnection to figure out the request method.
            setStandardProperties( urlc );
            urlc.connect(); // ignored if already connected.
            // urlConnection does not support   getResponseCode or  getResponseMessage
            // get size of message. -1 means comes in an indeterminate number of chunks.
            int estimatedLength = urlc.getContentLength();
            if ( estimatedLength < 0 )
                {
                // quite common for no length field
                estimatedLength = Http.DEFAULT_LENGTH;
                }
            final InputStream is = urlc.getInputStream();
            // get Content-Type: text/html; charset=utf-8
            final String contentType = urlc.getContentType();
            final Charset charset = guessCharset( contentType, defaultCharset, url );
            // content encoding might be null
            final boolean gzipped = "gzip".equals( urlc.getContentEncoding() )
                                    || "x-gzip".equals( urlc.getContentEncoding() );
            // R E A D
            String result = Read.readStringBlocking( is,
                    estimatedLength,
                    gzipped,
                    charset );
            if ( DEBUGGING )
                {
                out.println( "--------------------------------" );
                out.println( "ContentType : " + contentType );
                out.println( "Charset : " + charset );
                out.println( "ContentEncoding : " + urlc.getContentEncoding() );
                out.println( "Result : " + ( result == null ? "null" : result.substring( 0,
                        Math.min( result.length(), 300 ) ) ) );
                }
            // C L O S E
            is.close();
            // There is no corresponding URLConnection.disconnect
            return result;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            return null;
            }
        } // end fetch
    }
