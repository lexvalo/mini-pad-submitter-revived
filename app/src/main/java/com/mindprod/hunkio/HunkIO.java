/*
 * [HunkIO.java]
 *
 * Summary: read/write a file in one large hunk.
 *
 * Copyright: (c) 2002-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.3 2005-06-18 add more consistent bat files.
 *  1.4 2006-01-14 readEntire file now works with multi-byte
 *                 encodings.
 *  1.5 2006-01-25 readFile/writeFile with non-default
 *                 encoding, raw read/write entire file.
 *  1.6 2007-05-24 add icon, pad, Intellij inspector.
 *  1.7 2009-05-03 create PrintWriterPlus vs PrintWriterPortable
 *  1.8 2013-02-19 convert all encoding parms from String to Charset, all file parms form String to File
 *  1.9 2014-05-17 fix bug, failed to chop tail end of file when writing a shorter file than previously.
 */
package com.mindprod.hunkio;

import com.mindprod.common18.EIO;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * read/write a file in one large hunk.
 * <p/>
 * Variants to read/write bytes, strings with default encoding and strings with specified encoding.
 * No main method.
 * We don't use java.nio.charset.Charset because it requires JDK 1.4+.
 * See also the more elaborate filetransfer package.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.9 2014-05-17 fix bug, failed to chop tail end of file when writing a shorter file than previously.
 * @see com.mindprod.common18.EIO
 * @since 2002-07-12
 */
public final class HunkIO
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
     * true to include the debugging harness code
     */
    private static final boolean DEBUGGING = false;

    private static final int FIRST_COPYRIGHT_YEAR = 2002;

    /**
     * undisplayed copyright notice
     *
     * @noinspection UnusedDeclaration
     */
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2002-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2014-05-17";

    /**
     * embedded version string.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String VERSION_STRING = "1.9";
    // /declarations

    /**
     * all methods are static, so hidden constructor
     */
    private HunkIO()
        {
        }
    // methods

    /**
     * Create a temporary file, Slightly smarter version of File.createTempFile
     *
     * @param prefix beginning letters of filename
     * @param suffix ending letters of filename. e.g. ".temp". null means ".tmp",
     *               including any dot.
     * @param near   directory where to put file, or file to place this temp file near in the same directory.
     *               That way, you can later eficiently rename the temp file to the original temp file.
     *               The safe way to update a file is to write a temporary replacement, delete the original
     *               and rename the temp to the original name.
     *               If null, just puts it anywhere File.createTempFile wants to put it.
     *               e.g. C:\Users\Roedy\AppData\Local\Temp\tempfindparkedxenu161647099906557308.csv
     *
     * @return A temporary file. It will not automatically delete on program completion. Ideally, pick a name and
     * place where such deadwood will be found and deleted later. e.g.  C:/temp/temp99999.tmp
     * Windows does not automatically mark temp files in any special way.
     * @throws java.io.IOException when cannot create file.
     * @see deleteAndRename
     */
    public static File createTempFile( final String prefix,
                                       final String suffix,
                                       final File near ) throws IOException
        {
        if ( near != null )
            {
            if ( near.isDirectory() )
                {
                return File.createTempFile( prefix, suffix, near );
                }
            else if ( near.isFile() )
                {
                String parent = near.getParent();
                if ( parent != null )
                    {
                    File dir = new File( parent );
                    if ( dir.isDirectory() )
                        {
                        return File.createTempFile( prefix, suffix, dir );
                        }
                    }
                }
            }
        // put in C:\Users\Roedy\AppData\Local\Temp\  default temp dir.
        return File.createTempFile( prefix, suffix );
        }
    // /method

    /**
     * Convert a tempFile to a permanent file reasonably atomically.
     * Deletes permanentFile and renames tempFile to permanentFile.
     *
     * @param tempFile      temporary file with new contents.
     * @param permanentFile permanent file to hold the new contents.
     *
     * @throws IOException if some trouble deleting or renaming.
     * @see createTempFile
     */
    public static void deleteAndRename( File tempFile, File permanentFile ) throws IOException
        {
        if ( permanentFile.exists() && !permanentFile.delete() )  // delete returns true if it succeeded
            {
            throw new IOException( "Unable to delete the old file " + EIO.getCanOrAbsPath( permanentFile ) + " we are recreating." );
            }
        if ( !tempFile.renameTo( permanentFile ) )   // renameto return true if rename succeeded
            {
            throw new IOException( "Unable to rename the temporary output " + EIO.getCanOrAbsPath( tempFile ) + " to the old file name " + EIO.getCanOrAbsPath( permanentFile ) );
            }
        }// /method
    // /method

    /**
     * debugging harness for HunkIO.
     *
     * @param args not used.
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            try
                {
                writeEntireFile( new File( "C:/temp/temp.txt" ),
                        "abcedefghijklmnopqrstuvwxyz",
                        EIO.UTF8 );
                String result = readEntireFile( new File( "C:/temp/temp.txt" ), EIO.UTF8 );
                out.println( result );
                }
            catch ( IOException e )
                {
                err.println( e.getMessage() );
                }
            }
        } // /method
    // /method

    /**
     * Get all text in a file.
     *
     * @param fromFile file where to get the text.
     *
     * @return all the text in the File.
     * @throws IOException when cannot access file.
     * @noinspection WeakerAccess
     */
    public static String readEntireFile( File fromFile ) throws IOException
        {
        // decode with default encoding and return entire file as one big string
        return new String( readEntireFileAsBytes( fromFile ) );
        } // end readEntireFile
    // /method

    /**
     * Get all text in a file.
     *
     * @param fromFile file where to get the text.
     * @param encoding name of the encoding to use to translate the bytes in the file into chars e.g. "windows-1252"
     *                 "UTF-8" "UTF-16"
     *
     * @return all the text in the File.
     * @throws IOException when cannot access file.
     * @noinspection WeakerAccess
     */
    public static String readEntireFile( File fromFile,
                                         Charset encoding ) throws IOException
        {
        // decode with encoding and return entire file as one big string
        return new String( readEntireFileAsBytes( fromFile ), encoding );
        } // end readEntireFile
    // /method

    /**
     * read file of bytes without conversion
     *
     * @param fromFile file to read
     *
     * @return byte array representing whole file
     * @throws IOException when cannot access file.
     * @noinspection WeakerAccess
     */
    public static byte[] readEntireFileAsBytes( File fromFile ) throws IOException
        {
        final int size = ( int ) fromFile.length();
        final RandomAccessFile raf = new RandomAccessFile( fromFile, "r" );
        // R E A D
        final byte[] rawContents = new byte[ size ];
        final int bytesRead = raf.read( rawContents );
        if ( bytesRead != size )
            {
            throw new IOException( "error: problems reading file " + fromFile );
            }
        // C L O S E
        raf.close();
        return rawContents;
        }

    /**
     * Write all the text in a file
     *
     * @param toFile file where to write the text
     * @param text   Text to write
     *
     * @throws IOException when cannot access file.
     * @noinspection WeakerAccess
     */
    public static void writeEntireFile( File toFile,
                                        String text ) throws IOException
        {
        // default encoding
        final byte[] b = text.getBytes();
        writeEntireFileAsBytes( toFile, b );
        }

    /**
     * Write all the text in a file
     *
     * @param toFile   file where to write the text, embedded line endings will be left as is.
     * @param text     Text to write
     * @param encoding name of the encoding to use to translate chars to bytes e.g. "windows-1252" "UTF-8" "UTF-16"
     *
     * @throws IOException                          when cannot access file.
     * @throws java.io.UnsupportedEncodingException If the named encoding is not supported
     * @noinspection WeakerAccess
     */
    public static void writeEntireFile( File toFile,
                                        String text,
                                        Charset encoding ) throws IOException
        {
        // default encoding
        final byte[] b = text.getBytes( encoding );
        writeEntireFileAsBytes( toFile, b );
        } // /method

    /**
     * read file of bytes without conversion
     *
     * @param toFile   file to write to
     * @param contents bytes to write to file
     *
     * @throws IOException when cannot access file.
     * @noinspection WeakerAccess
     */
    public static void writeEntireFileAsBytes( File toFile, byte[] contents ) throws IOException
        {
        final RandomAccessFile raf = new RandomAccessFile( toFile, "rw" );   // plain "w" illegal, oddly
        // might currently be empty or too long
        raf.setLength( contents.length );
        raf.write( contents );
        // C L O S E
        raf.close();
        }    // /method
    // /methods
    }
