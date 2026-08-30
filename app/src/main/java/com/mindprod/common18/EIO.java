/*
 * [EIO.java]
 *
 * Summary: Encapsulated I/O methods.
 *
 * Copyright: (c) 2014-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2014-05-10 initial.
 *  1.1 2014-07-25 on read, automatically trim buffersize so it is no longer than needed for file size.
 */
package com.mindprod.common18;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Encapsulated I/O methods.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2014-07-25 on read, automatically trim buffersize so it is no longer than needed for file size.
 * @see com.mindprod.hunkio.HunkIO
 * @since 2014-05-10
 */
public final class EIO
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
    public static final Charset UTF8 = Charset.forName( "UTF-8" );

    /**
     * encoding for code page 1252
     */
    public static final Charset WINDOWS1252 = Charset.forName( "windows-1252" );

    /**
     * true if you want extra debugging output and test code
     */
    private static final boolean DEBUGGING = false;

    /**
     * optimal ratio for allocating  buffers
     */
    private static final double OPTIMAL_CHAR_BUFFER_RATIO = 0.5;
    // /declarations

    /**
     * Misc contains only static methods.
     */
    private EIO()
        {
        }
    // methods

    /**
     * change the extension of a file.
     *
     * @param ext new extension, without the . "" will remove the extension
     *
     * @return file with new extension.
     */
    public static File changeExtension( final File f, final String ext )
        {
        final String path = getCanOrAbsPath( f );
        int place = path.lastIndexOf( '.' );
        if ( place < 0 )
            {
            place = path.length();
            }
        return new File( path.substring( 0, place ) + "." + ext );
        }

    /**
     * Open a BufferedReader
     *
     * @param f                 file
     * @param bufferSizeInBytes total space to allocate for Stream and Reader buffers.
     * @param charset           e.g.   Charset.forName( "UTF-8" );
     *
     * @return BufferedReader
     */
    public static BufferedReader getBufferedReader( final File f,
                                                    final int bufferSizeInBytes,
                                                    final Charset charset ) throws FileNotFoundException
        {
        final int trimBufferSize = ( int ) Math.max( 256, Math.min( f.length() * 2, bufferSizeInBytes ) );
        final FileInputStream fis = new FileInputStream( f );
        return getBufferedReader( fis, trimBufferSize, charset );
        }// /method

    /**
     * Open a BufferedReader
     *
     * @param is                InputStream
     * @param bufferSizeInBytes total space to allocate for Stream and Reader buffers.
     * @param charset           e.g.   Charset.forName( "UTF-8" ); null=default
     *
     * @return BufferedReader
     */
    public static BufferedReader getBufferedReader( final InputStream is,
                                                    final int bufferSizeInBytes,
                                                    final Charset charset )
        {
        final int streamBufferSizeInBytes = ( int ) ( bufferSizeInBytes * ( 1 - OPTIMAL_CHAR_BUFFER_RATIO ) );
        final int readerBufferSizeInChars = ( int ) ( bufferSizeInBytes * OPTIMAL_CHAR_BUFFER_RATIO / 2 );
        final BufferedInputStream bis = new BufferedInputStream( is, streamBufferSizeInBytes );
        final InputStreamReader eisr = new InputStreamReader( bis, ( charset != null ? charset : Charset.defaultCharset() ) );
        return new BufferedReader( eisr, readerBufferSizeInChars );
        }// /method

    /**
     * Open a BufferedWriter, no appending
     *
     * @param f                 file
     * @param bufferSizeInBytes total space to allocate for Stream ind Writer buffers
     * @param charset           e.g.   Charset.forName( "UTF-8" );
     *
     * @return BufferedWriter
     */
    public static BufferedWriter getBufferedWriter( final File f,
                                                    final int bufferSizeInBytes,
                                                    final Charset charset ) throws FileNotFoundException
        {
        return getBufferedWriter( f, bufferSizeInBytes, false, charset );
        }// /method

    /**
     * Open a BufferedWriter
     *
     * @param f                 file
     * @param bufferSizeInBytes total space to allocate for Stream ind Writer buffers
     * @param charset           e.g.   Charset.forName( "UTF-8" );
     * @param appending         true if want to append.
     *
     * @return BufferedWriter
     */
    public static BufferedWriter getBufferedWriter( final File f,
                                                    final int bufferSizeInBytes,
                                                    boolean appending,
                                                    final Charset charset ) throws FileNotFoundException
        {
        final int streamBufferSizeInBytes = ( int ) ( bufferSizeInBytes * ( 1 - OPTIMAL_CHAR_BUFFER_RATIO ) );
        final int writerBufferSizeInChars = ( int ) ( bufferSizeInBytes * OPTIMAL_CHAR_BUFFER_RATIO / 2 );
        final FileOutputStream fos = new FileOutputStream( f, appending );
        final BufferedOutputStream bos = new BufferedOutputStream( fos, streamBufferSizeInBytes );
        final OutputStreamWriter eosw = new OutputStreamWriter( bos, ( charset != null ? charset : Charset.defaultCharset() ) );
        return new BufferedWriter( eosw, writerBufferSizeInChars );
        }// /method

    /**
     * like File.getCanonicalPath, but if fails returns getAbsolutePath
     * Name compatible with TrueZip get
     *
     * @param file file whose canonical name you want.
     *
     * @return canonical name of file.
     * @see java.io.File#getCanonicalPath()
     * @see java.io.File#getAbsolutePath()
     */
    public static String getCanOrAbsPath( final File file )
        {
        try
            {
            return file.getCanonicalPath();
            }
        catch ( IOException e )
            {
            return file.getAbsolutePath();
            }
        }// /method

    /**
     * Get the core of the filename e.g. E:/mindprod/jgloss/xx.html --> xx
     * It is  the name with the extension stripped.
     * <p/>
     * File may have no . or it may end in a . or it may have and embedded dot.
     * This is not intended for directories, but it will give the last leg
     * of the directory name with any .xxx trimmed off.
     * <p/>
     * Ideally this would be an instance method of File.
     *
     * @param file file whose core you want to know
     *
     * @return simple name of file with extension chopped off.
     * @see java.io.File#getName()
     */
    public static String getCoreName( final File file )
        {
        // for directory, get last leg of directory name.
        final String name = file.getName(); /* e.g. xx.html, or mydir */
        int place = name.lastIndexOf( '.' );
        if ( place < 0 )
            {
            return name;
            }
        else
            {
            return name.substring( 0, place );
            }
        }// /method

    /**
     * get a buffered DataInputStream
     *
     * @param f                 file
     * @param bufferSizeInBytes size of buffer
     *
     * @return DataInputStream
     */
    public static DataInputStream getDataInputStream( final File f, final int bufferSizeInBytes ) throws FileNotFoundException
        {
        final int trimBufferSize = ( int ) Math.max( 256, Math.min( f.length(), bufferSizeInBytes ) );
        final FileInputStream fis = new FileInputStream( f );
        return getDataInputStream( fis, trimBufferSize );
        }// /method

    /**
     * get a buffered DataInputStream
     *
     * @param is                InputStream
     * @param bufferSizeInBytes size of buffer
     *
     * @return DataInputStream
     */
    public static DataInputStream getDataInputStream( final InputStream is, final int bufferSizeInBytes )
        {
        final BufferedInputStream bis = new BufferedInputStream( is, bufferSizeInBytes );
        return new DataInputStream( bis );
        }// /method

    /**
     * get a buffered DataOutputStream
     *
     * @param os                OutputStream
     * @param bufferSizeInBytes size of buffer
     *
     * @return DataOutputStream
     */
    public static DataOutputStream getDataOutputStream( final OutputStream os, final int bufferSizeInBytes )
        {
        final BufferedOutputStream bos = new BufferedOutputStream( os, bufferSizeInBytes );
        return new DataOutputStream( bos );
        }// /method

    /**
     * get a buffered DataOutputStream
     *
     * @param f                 file
     * @param bufferSizeInBytes size of buffer
     *
     * @return DataOutputStream
     */
    public static DataOutputStream getDataOutputStream( final File f, final int bufferSizeInBytes ) throws FileNotFoundException
        {
        final FileOutputStream fos = new FileOutputStream( f, false /* no append */ );
        return getDataOutputStream( fos, bufferSizeInBytes );
        }// /method

    /**
     * Get the extension of the filename e.g. E:/mindprod/jgloss/xx.html --> html
     * <p/>
     * File may have no . or it may end in a . or it may have and embedded dot.
     * This is not intended for directories. It will return anything after the .
     * in the last leg of the directory name.
     * <p/>
     * Ideally this would be an instance method of File.
     *
     * @param file file whose extension you want to know
     *
     * @return extension without the dot, possibly ""
     */
    public static String getExtension( final File file )
        {
        final String name = file.getName();  /* e.g. xx.html, or mydir */
        int place = name.lastIndexOf( '.' );
        if ( place < 0 )
            {
            return "";
            }
        else
            {
            // Happily, substring returns "" when pointing just past end of string.
            return name.substring( place + 1 );
            }
        }// /method

    /**
     * get a buffered ObjectInputStream
     *
     * @param is                input stream
     * @param bufferSizeInBytes size of buffer
     * @param gzipped           true if objectstream is gzipped
     *
     * @return ObjectInputStream
     */
    public static ObjectInputStream getObjectInputStream( final InputStream is, final int bufferSizeInBytes, boolean gzipped ) throws IOException
        {
        final BufferedInputStream bis = new BufferedInputStream( is, bufferSizeInBytes / 2 );
        if ( gzipped )
            {
            final GZIPInputStream gzis = new GZIPInputStream( bis, bufferSizeInBytes / 2 );
            return new ObjectInputStream( gzis );
            }
        else
            {
            return new ObjectInputStream( bis );
            }
        }// /method

    /**
     * get a buffered ObjectInputStream
     *
     * @param f                 file
     * @param bufferSizeInBytes size of buffer
     * @param gzipped           true if objectstream is gzipped
     *
     * @return ObjectInputStream
     */
    public static ObjectInputStream getObjectInputStream( final File f, final int bufferSizeInBytes, final boolean gzipped ) throws IOException
        {
        final int trimBufferSize = ( int ) Math.max( 256, Math.min( f.length() * 2, bufferSizeInBytes ) );
        final FileInputStream fis = new FileInputStream( f );
        return getObjectInputStream( fis, trimBufferSize, gzipped );
        }// /method

    /**
     * get a buffered ObjectOutputStream
     *
     * @param os                output stream
     * @param bufferSizeInBytes size of buffer
     * @param gzipped           true if objectstream is gzipped
     *
     * @return ObjectOutputStream
     */
    public static ObjectOutputStream getObjectOutputStream( final OutputStream os, final int bufferSizeInBytes, boolean gzipped ) throws IOException
        {
        final BufferedOutputStream bos = new BufferedOutputStream( os, bufferSizeInBytes / 2 );
        if ( gzipped )
            {
            final GZIPOutputStream gzos = new GZIPOutputStream( bos, bufferSizeInBytes / 2 );
            return new ObjectOutputStream( gzos );
            }
        else
            {
            return new ObjectOutputStream( bos );
            }
        }// /method

    /**
     * get a buffered ObjectOutputStream
     *
     * @param f                 file
     * @param bufferSizeInBytes size of buffer
     * @param gzipped           true if objectstream is gzipped
     *
     * @return ObjectOutputStream
     */
    public static ObjectOutputStream getObjectOutputStream( final File f, final int bufferSizeInBytes, final boolean gzipped ) throws IOException
        {
        final FileOutputStream fos = new FileOutputStream( f );
        return getObjectOutputStream( fos, bufferSizeInBytes, gzipped );
        }// /method

    /**
     * Open a buffered PrintStream
     *
     * @param f                 file
     * @param bufferSizeInBytes total space to allocate for Stream  buffers
     *
     * @return BufferedWriter
     */
    public static PrintStream getPrintStream( final File f,
                                              final int bufferSizeInBytes,
                                              final Charset charset ) throws FileNotFoundException, UnsupportedEncodingException
        {
        final FileOutputStream fos = new FileOutputStream( f, false );
        final BufferedOutputStream bos = new BufferedOutputStream( fos, bufferSizeInBytes );
        return new PrintStream( bos, false, ( charset != null ? charset.name() : Charset.defaultCharset().name() ) );
        }// /method

    /**
     * Open a buffered PrintWriter
     *
     * @param f                 file
     * @param bufferSizeInBytes total space to allocate for Stream ind Writer buffers
     * @param charset           e.g.   Charset.forName( "UTF-8" );
     *
     * @return BufferedWriter
     */
    public static PrintWriter getPrintWriter( final File f,
                                              final int bufferSizeInBytes,
                                              final Charset charset ) throws FileNotFoundException
        {
        return new PrintWriter( getBufferedWriter( f, bufferSizeInBytes, ( charset != null ? charset : Charset.defaultCharset() ) ) );
        }// /method

    /**
     * test harness
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            System.out.println( changeExtension( new File( "E:/mindprod/index.html" ), "txt" ) );
            }
        }// /method
    // /methods
    }
