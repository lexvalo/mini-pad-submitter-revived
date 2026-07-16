/*
 * [Http.java]
 *
 * Summary: Base class to Post, Get, Head, Probe and Chase to send/receive HTTP messages.
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
 *  1.0 1998-01-01 initial version
 *  1.1 2007-07-19 improved handling of responseCode
 *  1.2 2007-07-27 use UTF-8 instead of 8859_1.
 *  1.3 2007-08-24 readStringBlocking, readBytesBlocking, encoding on Get
 *  1.4 2007-09-26 add TIMEOUT
 *  1.5 2007-12-30 add alternate get and post methods that take a full URL.
 *  1.6 2008-01-14 add gzip option on read
 *  1.7 2008-07-25 add configurable User-Agent, add Base Http class.
 *  1.8 2008-07-27 handle case where URL given was not HTTP
 *  1.9 2008-08-22 support accept-charset, accept-encoding and accept-language. Fix bugs in gzip support.
 *  2.0 2009-02-20 major refactoring. separate setParms and setPostParms. new send method. Post can have both types
 *                 of parm.
 *  2.1 2010-02-07 new methods Post.setBody Http.setRequestProperties.
 *  2.2 2010-04-05 new method getURL
 *  2.3 2010-11-14 new method setInstanceFollowRedirects
 *  2.4 2011-02-03 change documentation to reflect that the HTTP package handled both http: and https: equally well.
 *  2.5 2011-04-01 allow gzip compression. Update User agent.
 *  2.6 2011-05-01 getRawResponseMessage and getResponseMessage(uses standard wordings).
 *  2.7 2011-05-19 change all encoding parms from String to Charset type for tighter parameter checking.
 *  2.8 2011-08-30 update User Agent
 *  2.9 2011-11-09 add configuring getter/setters for Accept-Property, Accept-Charset etc.
 *  3.0 2014-01-29 adjust default User Agent
 *  3.1 2014-04-17 give a message about any exceptions rather than just a -1 return code.
 *  3.2 2014-07-15 add isGood method to categorise the responseCode
 */
package com.mindprod.http;

import com.mindprod.common18.Build;
import com.mindprod.common18.ST;
import com.mindprod.fastcat.FastCat;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.System.*;

/**
 * Base class to Post, Get, Head, Probe and Chase to send/receive HTTP messages.
 * <p/>
 * Originally based on work by Jonathan Revusky
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.2 2014-07-15 add isGood method to categorise the responseCode
 * @since 1998-01-01
 */
abstract class Http
    {
    // declarations

    /**
     * encoding for IBM437
     */
    public static final Charset IBM437 = Charset.forName( "IBM437" );

    /**
     * encoding for IBM850
     */
    public static final Charset IBM850 = Charset.forName( "IBM850" );

    /**
     * encoding for iso-8859-1
     */
    public static final Charset ISO88591 = Charset.forName( "ISO-8859-1" );

    /**
     * encoding for UTF-16
     */
    public static final Charset UTF16 = Charset.forName( "UTF-16" );

    /**
     * encoding for UTF-8
     */
    public static final Charset UTF8 = Charset.forName( "UTF8" );

    /**
     * encoding for code page 1252
     */
    public static final Charset WINDOWS1252 = Charset.forName( "windows-1252" );

    /**
     * true if want extra debugging output.  If you change this to true, make sure you set it back to false before
     * distributing http or any package that uses it.
     */
    static final boolean DEBUGGING = false;

    /**
     * message length to presume when no length given
     */
    static final int DEFAULT_LENGTH = 32 * 1024;

    private static final int FIRST_COPYRIGHT_YEAR = 1998;

    private static final int HIGHEST_LEGIT_RESPONSE_CODE = 522;

    private static final int LOWEST_LEGIT_RESPONSE_CODE = 100;

    /**
     * undisplayed copyright notice
     */
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 1998-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * when package released.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2014-07-15";

    /**
     * embedded version string.
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String VERSION_STRING = "3.2";

    /**
     * used to convert responseCode to responseMessage.
     */
    private static final String[] responseCodeLookup;

    // /declarations
    static
        {
        System.setProperty( "java.net.preferIPv4Stack", "true" );
        // SNI left enabled (was force-disabled here in the original 2014 code for old
        // non-SNI shareware sites) - modern CDNs (Cloudflare etc.) require SNI to route
        // TLS to the right certificate on a shared IP; disabling it breaks every https: site.
        System.setProperty( "jdk.tls.ephemeralDHKeySize", "2048" );
        }

    static
        {
        /**
         * There is a  more elaborate csv list in Brokenlinks
         * get wording by indexing into array by responseCode offset.
         * We don't use a resource. That would complicate things for building
         * jars that use the http package.
         * Last updated 2014-04-18
         */
        responseCodeLookup = new String[ HIGHEST_LEGIT_RESPONSE_CODE - LOWEST_LEGIT_RESPONSE_CODE + 1 ];
        // not all codes in the range are accounted for.
        // this code generated by BrokenLinks.Prepare
        means( 100, "OK to continue with request." );
        means( 101, "Server has switched protocols in upgrade header." );
        means( 200, "ok" );
        means( 201, "Object created, reason = new URI." );
        means( 202, "accepted: Async completion (TBS)" );
        means( 203, "non-authoritative information" );
        means( 204, "no content" );
        means( 205, "reset content" );
        means( 206, "partial content" );
        means( 207, "multistatus" );
        means( 208, "already reported" );
        means( 226, "IM (Instance maniplation) used." );
        means( 300, "Server could not determine what to return." );
        means( 301, "permanent redirect" );
        means( 302, "temporary redirect" );
        means( 303, "temporary redirect to new access method" );
        means( 304, "not recently modified" );
        means( 305, "Redirection to proxy, location header specifies proxy to use." );
        means( 307, "temporary redirect" );
        means( 308, "permanent redirect" );
        means( 400, "Invalid syntax" );
        means( 401, "Access denied, authorisation required." );
        means( 402, "payment required" );
        means( 403, "request forbidden" );
        means( 404, "page not found" );
        means( 405, "method not allowed" );
        means( 406, "No response acceptable to client found." );
        means( 407, "proxy authentication required" );
        means( 408, "Server timed out waiting for request" );
        means( 409, "User should resubmit with more information" );
        means( 410, "Resource withdrawn without redirect" );
        means( 411, "length required" );
        means( 412, "precondition failed" );
        means( 413, "The server is refusing to process a request because the request entity is larger than the server is willing or able to process." );
        means( 414, "request-URI too long" );
        means( 415, "unsupported media type" );
        means( 416, "requested range not satisfiable" );
        means( 421, "Protocol Extension Unknown" );
        means( 422, "Protocol Extension Refused" );
        means( 429, "too many requests from a client in a short time" );
        means( 449, "Retry after doing the appropriate action." );
        means( 500, "Internal server error" );
        means( 501, "not implemented" );
        means( 502, "Error response received from gateway" );
        means( 503, "temporarily overloaded" );
        means( 504, "gateway timeout" );
        means( 505, "HTTP version not supported" );
        means( 506, "server configuration error" );
        means( 507, "not enough RAM" );
        means( 508, "loop detected" );
        means( 509, "bandwidth exceeded" );
        means( 510, "mandatory extension policy rejected" );
        means( 520, "Protocol Extension Error" );
        means( 521, "Protocol Extension Not Implemented" );
        means( 522, "Protocol Extension Parameters Not Acceptable" );
        }

    /**
     * string composed from interrupts
     */
    protected String interruptResponseMessage;

    /**
     * string back from website
     */
    protected String rawResponseMessage;

    /**
     * responseCode from most recent post
     */
    int responseCode;

    /**
     * URL, including encoded get Parameters.
     */
    URL url;

    /**
     * true=auto follow redirects, false=treat redirect as error, just read  first leg redirect message..
     */
    private boolean followRedirects = true;

    /**
     * Allow 50 seconds to connect, measured in millis as the default.
     */
    private int connectTimeout = Build.CONNECT_TIMEOUT;

    /**
     * Allow 40 seconds for a read to go without progress, measured in millis as the default.
     */
    private int readTimeout = Build.READ_TIMEOUT;

    /**
     * Accept-Charset for header, has getter/setter
     */
    private String acceptCharset = Build.ACCEPT_CHARSET;

    /**
     * Accept-Encoding for header, when debugging avoid gzip to make Wireshark sniffing easier. We don't handle deflate.
     * Deflate is one of the PKZip compressors.
     * Currently no SetAcceptEncoding method to override this default.
     * Firefox uses  gzip, deflate
     * in = new InflaterInputStream(conn.getInputStream()), new Inflater(true)); may let us handle deflate
     */
    private String acceptEncoding = DEBUGGING ? "identity" : "gzip,x-gzip,identity";

    /**
     * Accept property for header : application/xhtml+xml,application/xml
     * Can be be overrridden
     * <p>
     * default "application/octet-stream," +
     * "application/x-java-jnlp-file," +
     * "application/x-java-serialized-object," +
     * "application/xhtml+xml," +
     * "application/xml," +
     * "application/zip," +
     * "image/gif," +
     * "image/jpeg," +
     * "image/png," +
     * "text/css," +
     * "text/html," +
     * "text/plain," +
     * "text/x-java-source," +
     * "text/xml," +
     * "* ; q = . 2 , * / * ; q = . 2 ";
     * firefox:  text/html,application/xhtml+xml,application/xml;q=0.9, * / * ; q = 0  . 8
     * see https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html for an explanation of the q
     */
    private String acceptProperty = Build.ACCEPT_MIMES;

    /**
     * parameters we send with the command. c.f. PostParms sent in message body with a post
     */
    private String[] parms;

    /**
     * the page containing the URL we pretend to be.
     * By default null, for none.
     */
    private String referer = null;

    /**
     * additional request properties for the connection, pairs key, value
     */
    private String[] requestProperties = new String[ 0 ];

    /**
     * the browser we pretend to be, by default Firefox can be overridden with setter.
     * e.g. Mozilla/5.0 (Windows NT 10.0; WOW64; rv:47.0) Gecko/20100101 Firefox/47.00
     *
     * @see <a href="http://mindprod.com/jgloss/http.html">details on User-Agent</a>
     */
    private String userAgent = Build.USER_AGENT;

    /**
     * no public instantiation.  Just a base class.
     */
    Http()
        {
        }
    // methods

    /**
     * display the contents of the header fields key: value, value, value
     *
     * @param title Title to decorated the dump.
     * @param urlc  HTTP connection
     */
    protected static void dumpHeaders( final String title, final HttpURLConnection urlc )
        {
        err.println( title );
        Map<String, List<String>> pairs = urlc.getHeaderFields();
        for ( Map.Entry<String, List<String>> entry : pairs.entrySet() )
            {
            // this does not require an expensive get lookup to find the value.
            String key = entry.getKey();
            List<String> values = entry.getValue();
            out.print( key + ":" );
            for ( String value : values )
                {
                out.print( " [" + value + "]" );
                }
            err.println();
            }
        }// /method

    /**
     * encode a set of parms for the command, separated with ? = & =     * This method does not automatically include
     * the result in the message sent to the host.
     *
     * @param encoding for URLEncoder
     * @param parms    0..n strings to be send as parameter, alternating keyword/value
     *
     * @return all the parms in one string encoded with lead ?
     * @throws java.io.UnsupportedEncodingException if bad encoding
     */
    private static String encodeParms( Charset encoding, String... parms ) throws UnsupportedEncodingException
        {
        // for post, will usually have empty list of parms for command.
        if ( parms == null || parms.length == 0 )
            {
            return "";
            }
        assert ( parms.length & 1 ) == 0 : "must have an even number of parms, keyword=value";
        int estLength = 10; // allow a few slots for multibyte chars
        for ( String p : parms )
            {
            estLength += p.length() + 1;
            }
        final StringBuilder sb = new StringBuilder( estLength );
        for ( int i = 0; i < parms.length - 1; i += 2 )
            {
            sb.append( i == 0 ? "?" : "&" );
            sb.append( URLEncoder.encode( parms[ i ], encoding.name()
                    /* encoding */ ) );
            sb.append( '=' );
            sb.append( URLEncoder.encode( parms[ i + 1 ], encoding.name()
                    /* encoding */ ) );
            }
        return sb.toString();
        }// /method

    /**
     * Guess what charSet encoding the response will be in.
     *
     * @param contentType     contents of content type field
     * @param defaultEncoding charSet to use if empty content type field, e.g. "UTF-8"
     * @param url             url of server we just probed, who is responsible for the contentType
     *
     * @return charsetEncoding to use e.g. "UTF-8"
     */
    static Charset guessCharset( final String contentType, final Charset defaultEncoding, final URL url )
        {
        if ( contentType == null )
            {
            return defaultEncoding;
            }
        else
            {
            //  Content-Type: text/html; charset=utf-8;
            int place = contentType.lastIndexOf( "charset=" );
            if ( place >= 0 )
                {
                String charset = null;
                try
                    {
                    charset = contentType.substring( place + "charset=".length() ).trim().toUpperCase();
                    // trim possible enclosing "
                    charset = ST.trimLeading( ST.trimTrailing( charset, '\"' ), '\"' );
                    // trim possible trailing ;
                    charset = ST.trimTrailing( charset, ';' );
                    if ( charset.equalsIgnoreCase( "CP-1251" ) )
                        {
                        charset = "windows-1251";
                        }
                    return Charset.forName( charset );
                    }
                catch ( IllegalArgumentException e /*  UnsupportedCharsetException IllegalCharsetNameException */ )
                    {
                    err.println( "Warning: " + url.toString() + " unrecognised charset " + charset + " using " +
                                 defaultEncoding + " instead." );
                    return defaultEncoding;
                    }
                }
            else
                {
                return defaultEncoding;
                }
            }
        }// /method

    /**
     * used to build lookup responseCode to responseMessage table
     *
     * @param responseCode response code
     * @param meaning      corresponding meaning of the response code, the responseMessage
     */
    private static void means( int responseCode, String meaning )
        {
        responseCodeLookup[ responseCode - LOWEST_LEGIT_RESPONSE_CODE ] = meaning;
        }// /method

    /**
     * convert responseCode to a standard responseMessage
     *
     * @param responseCode       code e.g. 200 for OK
     * @param rawResponseMessage raw response message from server
     *
     * @return String describing the response message.
     */
    private static String responseCodeToResponseMessage( int responseCode, String rawResponseMessage )
        {
        if ( LOWEST_LEGIT_RESPONSE_CODE <= responseCode && responseCode <= HIGHEST_LEGIT_RESPONSE_CODE )
            {
            final String responseMessage = responseCodeLookup[ responseCode - LOWEST_LEGIT_RESPONSE_CODE ];
            if ( responseMessage != null )
                {
                return responseMessage;
                }
            }
        if ( responseCode == -1 )
            {
            return "no connect";
            }
        else
            {
            return "unknown";
            }
        }// /method

    /**
     * process the response from the request we sent the server
     *
     * @param defaultCharSet Encoding to use to interpret the result.
     * @param urlc           the HttpURLConnection, all ready to go but for the connect.
     *
     * @return content of the response, decompressed, decoded.
     * @throws java.io.IOException if trouble reading the stream.
     */
    String connectAndProcessResponse( Charset defaultCharSet, HttpURLConnection urlc )
            throws IOException
        {
        // send the message. Might get Exception. Waits until server responds.
        urlc.connect(); // ignored if already connected.
        // save responseCode for later retrieval
        responseCode = urlc.getResponseCode();
        rawResponseMessage = urlc.getResponseMessage();
        // HttpURLConnection auto-follows same-protocol redirects but refuses to cross
        // http<->https, so a plain 301 from http to https (very common in 2026) would
        // otherwise come back here as a tiny redirect-stub body instead of the real content.
        // Follow those manually, bounded to avoid redirect loops.
        int crossProtocolRedirects = 0;
        while ( ( responseCode == 301 || responseCode == 302 || responseCode == 303
                  || responseCode == 307 || responseCode == 308 )
                && crossProtocolRedirects < 5 )
            {
            final String location = urlc.getHeaderField( "Location" );
            if ( location == null )
                {
                break;
                }
            final URL redirectedUrl = new URL( urlc.getURL(), location );
            urlc.disconnect();
            urlc = ( HttpURLConnection ) redirectedUrl.openConnection();
            urlc.setAllowUserInteraction( false );
            urlc.setDoInput( true );
            urlc.setDoOutput( false );
            urlc.setUseCaches( false );
            urlc.setRequestMethod( "GET" );
            setStandardProperties( urlc );
            urlc.connect();
            responseCode = urlc.getResponseCode();
            rawResponseMessage = urlc.getResponseMessage();
            crossProtocolRedirects++;
            }
        // get size of message. -1 means comes in an indeterminate number of chunks.
        int estimatedLength = urlc.getContentLength();
        if ( estimatedLength <= 0 )
            {
            // quite common for no length field
            estimatedLength = DEFAULT_LENGTH;
            }
        // InputStream gives us the raw bytes. We must decompress and decode the 8-bit chars..
        // actually a sun.net.www.protocol.http.HttpURLConnection.HttpInputStream
        final InputStream is = urlc.getInputStream();
        final String contentType = urlc.getContentType();
        final Charset charset = guessCharset( contentType, defaultCharSet, url );
        // content encoding might be null. We don't handle deflate or Unix compress.
        final boolean gzipped = "gzip".equals( urlc.getContentEncoding() )
                                || "x-gzip".equals( urlc.getContentEncoding() );
        // R E A D
        String result = Read.readStringBlocking( is,
                estimatedLength,
                gzipped,
                charset );
        if ( DEBUGGING )
            {
            err.println( "--------------------------------" );
            err.println( "ResponseCode : " + responseCode );
            err.println( "ResponseMessage : " + getResponseMessage() );
            err.println( "ContentType : " + contentType );
            err.println( "Charset : " + charset );
            err.println( "ContentEncoding : " + urlc.getContentEncoding() );
            err.println( "Result : " + ( result == null ? "null" : result.substring( 0,
                    Math.min( result.length(), 300 ) ) ) );
            }
        // C L O S E
        is.close();
        urlc.disconnect();
        return result;
        }// /method

    /**
     * get the parms for the command encoded, separated with ? = & =     * @param encoding for URLEncoder
     *
     * @return all the parms in one string encoded with lead ?
     * @throws java.io.UnsupportedEncodingException if bad encoding
     */
    String getEncodedParms( Charset encoding ) throws UnsupportedEncodingException
        {
        return encodeParms( encoding, this.parms );
        }// /method

    /**
     * first thing before accessing web
     */
    protected void init()
        {
        responseCode = -1;
        rawResponseMessage = null;
        interruptResponseMessage = null;
        }// /method

    /**
     * set up the standard properties on the connection
     *
     * @param urlc Connection we are setting up.
     */
    protected void setStandardProperties( URLConnection urlc )
        {
        // firefox sets Connection: keep-alive in the header.  THere is also SO_KEEP_ALIVE
        urlc.setConnectTimeout( connectTimeout );
        urlc.setReadTimeout( readTimeout );
        if ( userAgent != null )
            {
            urlc.setRequestProperty( "User-Agent", userAgent );
            }
        if ( urlc instanceof HttpURLConnection )
            {
            // setFollowRedirects sets default for class.
            ( ( HttpURLConnection ) urlc ).setInstanceFollowRedirects( followRedirects );
            }
        if ( referer != null )
            {
            // note HTTP spells referrer incorrectly.
            urlc.setRequestProperty( "Referer", referer );
            }
        for ( int i = 0; i < requestProperties.length; i += 2 )
            {
            urlc.setRequestProperty( requestProperties[ i ], requestProperties[ i + 1 ] );
            }
        urlc.setRequestProperty( "Accept", acceptProperty );
        urlc.setRequestProperty( "Accept-Charset", acceptCharset );
        // no deflate, could be added later if we can find code to handle it.
        urlc.setRequestProperty( "Accept-Encoding", acceptEncoding );
        // relaxed, prefer English
        final Locale locale = Locale.getDefault();
        // e.g. en-CA;q=1,en;q=.8, canadian, but if not available use English.
        // firefox uses en-US,en;q=0.5
        urlc.setRequestProperty( "Accept-Language", locale.toString().replace( '_', '-' ) + ";q=1," +
                                                    locale.getLanguage() + ";q=.8" );
        }// /method

    /**
     * prepare to talk to sites that do not support Server Name Identification
     * This is a static method. If you use it is a multithread situation, you will get conflicts.
     * You must do all your false work in a batch, then all your true work in a batch.
     */
    public static void disableSNI()
        {
        System.setProperty( "jsse.enableSNIExtension", "false" );
        }

    /**
     * prepare to talk to sites that support Server Name Identification
     * This is a static method. If you use it is a multithread situation, you will get conflicts.
     * You must do all your false work in a batch, then all your true work in a batch.
     */
    public static void enableSNI()
        {
        System.setProperty( "jsse.enableSNIExtension", "true" );
        }

    /**
     * get current Accept-Charset
     *
     * @return e.g. "utf-8,iso-8859-1,utf-16;q=0.7,*;q=0.3";
     */
    public String getAcceptCharset()
        {
        return acceptCharset;
        }// /method

    /**
     * change default Accept-Charset
     *
     * @param acceptCharset e.hg.  "iso-8859-1,utf-8,utf-16;q=0.7,*;q=0.3
     */
    public void setAcceptCharset( final String acceptCharset )
        {
        this.acceptCharset = acceptCharset;
        }// /method

    /**
     * get current Accept-Encoding
     *
     * @return e.g. "gzip,x-gzip,identity"
     */
    public String getAcceptEncoding()
        {
        return acceptEncoding;
        }// /method

    /**
     * change the default encoding Accept-Encoding
     *
     * @param acceptEncoding e.g. ""gzip,x-gzip,identity""
     */
    public void setAcceptEncoding( final String acceptEncoding )
        {
        this.acceptEncoding = acceptEncoding;
        }// /method

    /**
     * get current AcceptProperty
     *
     * @return e.g. "gzip,x-gzip,identity"
     */
    public String getAcceptProperty()
        {
        return acceptProperty;
        }// /method

    /**
     * change the default Accept-Property MIME types
     *
     * @param acceptProperty e.g. "gzip,x-gzip,identity".
     */
    public void setAcceptProperty( final String acceptProperty )
        {
        this.acceptProperty = acceptProperty;
        }// /method

    /**
     * get current connect time out in ms
     *
     * @return connect timeout is ms.
     */
    public int getConnectTimeout()
        {
        return connectTimeout;
        }// /method

    /**
     * override the default connect timeout of 50 seconds. It only applies to this request.
     * Sometimes the connection will ignore the timeout. Oracle thinks it is a feature.
     *
     * @param connectTimeout timeout to connect in ms. Note int, not long.
     */
    public void setConnectTimeout( int connectTimeout )
        {
        this.connectTimeout = connectTimeout;
        }// /method

    /**
     * Response message from Java Exception
     *
     * @return responseMessage
     * @see #getRawResponseMessage
     */
    public String getInterruptResponseMessage()
        {
        return ST.canonical( interruptResponseMessage );
        }// /method

    /**
     * responseCode from most recent post/get exactly as received from the server
     *
     * @return responseCode
     * @see #getResponseMessage
     */
    public String getRawResponseMessage()
        {
        if ( rawResponseMessage == null )
            {
            return "";
            }
        else
            {
            return rawResponseMessage;
            }
        }// /method

    /**
     * get current read time out in ms.
     *
     * @return read timeout is ms.
     */
    public int getReadTimeout()
        {
        return readTimeout;
        }// /method

    /**
     * override the default read timeout of 40 seconds. It only applies to this request.
     * Sometimes the connection will ignore the timeout. Oracle thinks it is a feature.
     *
     * @param readTimeout timeout to connect in ms. Note int, not long.
     */
    public void setReadTimeout( int readTimeout )
        {
        this.readTimeout = readTimeout;
        }// /method

    /**
     * ges the Referrer ie. the name of a web page this request ostensibly came from.
     *
     * @return referrer e.g "http://mindprod.com/index.html", null for none.
     * @see <a href="http://mindprod.com/jgloss/http.html">details on Referrer</a>
     */
    public String getReferer()
        {
        return referer;
        }// /method

    /**
     * set the Referrer ie. the name of a web page this request ostensibly came from.
     * Note that the word Referrer is spelled incorrectly as Referer  the HTTP spec.
     *
     * @param referer e.g "http://mindprod.com/index.html", null for none.
     *
     * @see <a href="http://mindprod.com/jgloss/http.html">details on Referrer</a>
     */
    public void setReferer( String referer )
        {
        this.referer = referer;
        }// /method

    /**
     * responseCode from most recent post/get
     * Meaning of various codes are described at HttpURLConnection and at http://mindprod.com/jgloss/http.html
     *
     * @return responseCode
     * @see java.net.HttpURLConnection
     */
    public int getResponseCode()
        {
        return responseCode;
        }// /method

    /**
     * responseCode from most recent post/get tidied to standard form
     *
     * @return responseCode
     * @see #getRawResponseMessage
     */
    public String getResponseMessage()
        {
        // we have three sources of information to compose the response message.
        // 1. raw string returned from website
        // 2. responseCode translated into words
        // 3. wording about exceptions (now handled by getInterruptResponseMessage())
        final FastCat sb = new FastCat( 2 );
        if ( !ST.isEmpty( rawResponseMessage ) )
            {
            sb.append( rawResponseMessage );
            }
        final String translatedResponseMessage = responseCodeToResponseMessage( responseCode, rawResponseMessage );
        if ( !ST.isEmpty( translatedResponseMessage ) && !translatedResponseMessage.equalsIgnoreCase( rawResponseMessage ) )
            {
            sb.append( translatedResponseMessage );
            }
        return sb.toSeparatedList( " : " );
        }// /method

    /**
     * Get URL for this connection.
     *
     * @return URL, including encoded GET Parameters, but not POST parameters.
     */
    public URL getURL()
        {
        return url;
        }// /method

    /**
     * get current User-Agent
     *
     * @return e.g. "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0) Gecko/20100101 Firefox/8.0"
     */
    public String getUserAgent()
        {
        return userAgent;
        }// /method

    /**
     * override the default User-Agent
     *
     * @param userAgent User-Agent  a browser uses in an HTTP header to identify itself.
     *                  null for no User Agent.  By default you get Firefox.
     *
     * @see <a href="http://mindprod.com/jgloss/http.html">details on User-Agent</a>
     */
    public void setUserAgent( String userAgent )
        {
        this.userAgent = userAgent;
        }// /method

    /**
     * do we follow redirects on just show first leg
     *
     * @return true if will follow redirects to the end
     */
    public boolean isFollowRedirects()
        {
        return followRedirects;
        }// /method

    /**
     * does the recent responseCode represent a good status?
     *
     * @return true if good status
     */
    public boolean isGood()
        {
        switch ( responseCode )
            {
            case 200:
            case 201:
            case 202:
            case 301:
            case 302:
            case 303:
            case 304:
            case 307:
            case 308:
                return true;
            case 400:
            case 401:
            case 402:
            case 403:
            case 404:
            default:
                return false;
            }
        }// /method

    /**
     * control whether redirects are automatically followed or treated as errors.
     *
     * @param followRedirects true=auto follow, false=treat as error. default is true.
     *
     * @see java.net.HttpURLConnection#setInstanceFollowRedirects(boolean)
     */
    public void setInstanceFollowRedirects( boolean followRedirects )
        {
        this.followRedirects = followRedirects;
        }// /method

    /**
     * set the parms that will be send tacked onto the end of the URL, get-style
     *
     * @param parms 0..n strings to be send as parameter, alternating keyword/value
     *
     * @see Post#setPostParms(String...)
     */
    public void setParms( final String... parms )
        {
        assert ( parms.length & 1 ) == 0 : "must have an even number of parms, keyword=value";
        this.parms = parms;
        }// /method

    /**
     * set additional requestProperties. Replaced previous set.
     *
     * @param requestProperties pairs: key value.
     */
    public void setRequestProperties( String... requestProperties )
        {
        if ( ( requestProperties.length & 1 ) != 0 )
            {
            throw new IllegalArgumentException( "setRequestProperties needs an even number of parameters: key,value" );
            }
        this.requestProperties = requestProperties;
        }// /method
    // /methods
    }
