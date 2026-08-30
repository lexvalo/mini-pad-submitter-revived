/*
 * [Read.java]
 *
 * Summary: Read a stream from a server.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

/**
 * Read a stream from a server.
 * <p/>
 * See com.mindprod.submitter for sample code to use this class.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.3 2010-11-14 new method setInstanceFollowRedirects
 * @since 1998
 */
@SuppressWarnings( { "WeakerAccess" } )
public final class Read
    {
    /**
     * characters should be arriving within a millisecond in ordinary circumstances. In order to avoid hogging at the
     * CPU in a mad loop to read non-existence characters, we sleep and try again later. Time to sleep in ms.
     */
    private static final int SLEEP_TIME = 100;   // measured in millis,  tenth of a second.

    /**
     * Static only.  Prevent instantiation.
     */
    private Read()
        {
        }

    /**
     * Reads exactly len bytes from the input stream into the byte array. This method reads repeatedly from the
     * underlying stream until all the bytes are read.
     *
     * @param in  stream to read
     * @param b   the buffer into which the data is read.
     * @param off the start offset of the data in the array, not offset into the file!
     * @param len the number of bytes to read.
     *
     * @return number of bytes actually read.
     * @throws IOException if an I/O error occurs, usually a timeout.
     */
    @SuppressWarnings( { "NestedAssignment", "EmptyCatchBlock" } )
    public static int readBytesBlocking( final InputStream in,
                                         final byte b[],
                                         final int off,
                                         final int len ) throws IOException
        {
        int totalBytesRead = 0;
        while ( totalBytesRead < len )
            {
            final int bytesRead = in.read( b, off + totalBytesRead, len - totalBytesRead );
            if ( bytesRead < 0 )
                {
                return totalBytesRead;
                }
            // bytesRead will be at least 1
            totalBytesRead += bytesRead;
            }
        return totalBytesRead;
        } // end readBytesBlocking

    /**
     * Used to read until EOF on an InputStream that sometimes returns 0 bytes because data have not arrived yet. Does
     * not close the stream. Formerly called readEverything.
     *
     * @param is              InputStream to read from.
     * @param estimatedLength Estimated number of <strong>bytes</strong> that will be read. -1 or 0 mean you have no
     *                        idea. Best to
     *                        make some sort of guess a little on the high side.
     * @param gzipped         true if the bytes are compressed with gzip. Request decompression.
     * @param charSet         The encoding of the byte stream. readStringBlocking converts to a standard Unicode-16
     *                        String. usually UTF-8 or ISO-8859-1.
     *
     * @return String representing the contents of the entire stream.
     * @throws IOException if connection lost, timeout etc., possibly UnsupportedEncodingException If the named charset
     *                     is not supported
     */
    @SuppressWarnings( { "NestedAssignment", "EmptyCatchBlock" } )
    public static String readStringBlocking( InputStream is,
                                             int estimatedLength,
                                             boolean gzipped,
                                             Charset charSet ) throws IOException
        {
        if ( estimatedLength <= 0 )
            {
            estimatedLength = Http.DEFAULT_LENGTH;
            }
        final int chunkSizeInBytes = Math.min( estimatedLength, 4 * 1024 );
        final byte[] ba = new byte[ chunkSizeInBytes ];
        // O P E N
        final ByteArrayOutputStream baos = new ByteArrayOutputStream( estimatedLength + 1024 );
        final InputStream decoder;
        if ( gzipped )
            {
            decoder = new GZIPInputStream( is, 4 * 1024/* buffsize in bytes */ );
            }
        else
            {
            decoder = is;
            }
        // -1 means eof, 0 means none available for now.
        int bytesRead;
        // -1 means eof
        while ( ( bytesRead = decoder.read( ba, 0, chunkSizeInBytes ) ) >= 0 )
            {
            baos.write( ba, 0, bytesRead );
            }
        // C L O S E
        decoder.close();
        baos.close();
        // we do not close the inputstream
        return new String( baos.toByteArray(), charSet );
        } // end readStringBlocking
    }
