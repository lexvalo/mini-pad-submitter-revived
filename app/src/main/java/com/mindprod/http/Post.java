/*
 * [Post.java]
 *
 * Summary: simulates a browser posting a form to CGI via POST.
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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * simulates a browser posting a form to CGI via POST.
 * <p/>
 * See com.mindprod.submitter for sample code to use this class.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.3 2010-11-14 new method setInstanceFollowRedirects
 * @since 1998
 */
public final class Post extends Http
    {
    /**
     * parameters we send in the body of the message for a post.  c.f. parms[] send on tail of command,
     * after a blank line after the URL.
     */
    private String[] postParms;

    /**
     * the body of the Post message following the Post parms
     */
    private String body = "";

    /**
     * mime type of contents
     */
    private String contentType = "application/x-www-form-urlencoded";

    /**
     * Constructor
     */
    public Post()
        {
        }

    /**
     * get the parms for the command encoded, separated with   & =.  no lead ?
     *
     * @param encoding encoding for URLEncoder
     *
     * @return all the parms in one string encoded without lead ?
     * @throws java.io.UnsupportedEncodingException if bad encoding
     */
    String getEncodedPostParms( Charset encoding ) throws UnsupportedEncodingException
        {
        if ( postParms == null || postParms.length == 0 )
            {
            return "";
            }
        int estLength = 10; // allow a few slots for multibyte chars
        for ( String p : postParms )
            {
            estLength += p.length() + 1;
            }
        final StringBuilder sb = new StringBuilder( estLength );
        for ( int i = 0; i < postParms.length - 1; i += 2 )
            {
            if ( i != 0 )
                {
                sb.append( "&" );
                }
            sb.append( URLEncoder.encode( postParms[ i ], encoding.name() ) );
            sb.append( '=' );
            sb.append( URLEncoder.encode( postParms[ i + 1 ], encoding.name() ) );
            }
        return sb.toString();
        }

    /**
     * Send a form full of data to the CGI host using POST
     * setPostParms must have been called previously, and possibly setParms as well.
     *
     * @param host     domain of the website. no lead http:
     * @param port     -1 if default, 8081 for local echoserver.
     * @param action   action of form, page on website. Usually has a lead /.
     * @param encoding encoding of the byte stream result, usually UTF-8 or or ISO-8859-1.
     *
     * @return CGI host's response with headers and embedded length fields stripped
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase" } )
    public String send( String host, int port, String action, Charset encoding )
        {
        try
            {
            init();
            // URL will encode target and parms.
            URL url = new URI( "http",
                    null,
                    host,
                    port,
                    action,
                    null,
                    null ).toURL();
            // these are the GET parms not the POST parms.  Normally they are empty.
            final String encodedParms = getEncodedParms( encoding );
            if ( encodedParms.length() > 0 )
                {
                url = new URL( url.toString() + getEncodedParms( encoding ) );
                }
            return send( url, encoding );
            }
        catch ( URISyntaxException e )
            {
            interruptResponseMessage = "Invalid URI/URL";
            return null;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            return null;
            }
        } // end post

    /**
     * Send a form full of data to the CGI host using POST
     * Must have done a setParms(optional) and setPostParms beforehand.
     *
     * @param url      URL of the website, including host, path but not the parms.
     *                 Call setPostParms and setPostBody before calling send.
     *                 http: or https:
     * @param encoding encoding of the byte stream result, usually UTF-8 or or ISO-8859-1.
     *
     * @return CGI host's response with headers and embedded length fields stripped
     */
    @SuppressWarnings( { "UnusedAssignment", "MethodNamesDifferingOnlyByCase", "WeakerAccess" } )
    public String send( URL url, Charset encoding )
        {
        try
            {
            init();
            this.url = url;
            // urlc will contain subclasses of URLConnection like:
            // http: HttpURLConnection
            // https: HttpsURLConnectionImpl
            // file: FileURLConnection
            final HttpURLConnection urlc = ( HttpURLConnection ) url.openConnection();
            urlc.setAllowUserInteraction( false );
            urlc.setDoInput( true );
            urlc.setDoOutput( true );// parms at the tail of this
            urlc.setUseCaches( false );
            urlc.setRequestMethod( "POST" );
            setStandardProperties( urlc );
            final byte[] encodedPostParms = getEncodedPostParms( encoding ).getBytes( EIO.UTF8 );
            // could set Referrer: here
            final byte[] encodedBody = body.getBytes( encoding );
            urlc.setRequestProperty( "Content-Length",
                    Integer.toString( encodedPostParms.length + encodedBody.length )  /* in bytes */ );
            urlc.setRequestProperty( "Content-Type", contentType );
            // since post parms can be arbitrarily long. We have to connect first before sending any of it.
            urlc.connect(); // ignored if already connected.
            final OutputStream os = urlc.getOutputStream();
            // parms are the data content.
            os.write( encodedPostParms );
            os.write( encodedBody );
            os.close();
            return connectAndProcessResponse( encoding, urlc );
            }
        catch ( ClassCastException e )
            {
            // was not an http: url
            interruptResponseMessage = "Bug: not http/https: " + e.getMessage();
            return null;
            }
        catch ( IOException e )
            {
            interruptResponseMessage = e.getClass().getName() + " : " + e.getMessage();
            return null;
            }
        }

    /**
     * declare Mime Type of the message contents.
     * Leave as default for sending post-style parms or encoded forms.
     *
     * @param contentType mime type of message
     */
    public void setContentType( String contentType )
        {
        this.contentType = contentType;
        }

    /**
     * set the body of the post, after the post parms. usually empty..
     *
     * @param body text to form the body of the post.
     *
     * @see #setPostParms(String...)
     */
    public void setPostBody( String body )
        {
        if ( body == null )
            {
            body = "";
            }
        this.body = body;
        }

    /**
     * set the parms that will be send in the Post body.
     *
     * @param postParms 0..n strings to be send as parameter, alternating keyword/value, not encoded.
     *
     * @see Http#setParms(String...)
     */
    public void setPostParms( String... postParms )
        {
        if ( postParms == null )
            {
            postParms = new String[ 0 ];
            }
        assert ( postParms.length & 1 ) == 0 : "must have an even number of post parms, keyword=value";
        this.postParms = postParms;
        }
    }
