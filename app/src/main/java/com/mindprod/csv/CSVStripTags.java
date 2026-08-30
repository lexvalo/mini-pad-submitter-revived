/*
 * [CSVStripTags.java]
 *
 * Summary: remove HTML tags from each field.
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
 *  1.0 2014-08-16 initial version
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * remove HTML tags from each field.
 * <p/>
 * Use: java.exe com.mindprod.CSVStripTags somefile.csv  0 3
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2014-08-16 initial version
 * @since 2014-08-16
 */
public final class CSVStripTags
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVStripTags needs a single filename.csv on the command line followed by " +
                                        "0-based numeric columns to strip HTML tags.";

    /**
     * convert entities to UTF-8 in selected columns in a CSV file, constructor. Just create the CSVSTrapTags object..
     * There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to have entities stripped
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding of the input and output file.
     * @param colsToStrip        list of columns wanted to have entities stripped.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVStripTags( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                         final char commentChar, final Charset encoding, final int... colsToStrip ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars, true, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 64 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        try
            {
            while ( true )
                {
                // process one line each time through the loop.
                final String[] fields = r.getAllFieldsInLine();
                final int fieldCount = r.wasComment() ? fields.length - 1 : fields.length;
                // remove tags from selected columns.
                for ( int source : colsToStrip )
                    {
                    if ( source < fieldCount )
                        {
                        fields[ source ] = DeEntifyStrings.stripHTMLTags( fields[ source ] );
                        }
                    }
                w.nl( fields, r.wasComment() );
                } // end while
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines stripped." );
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        }

    /**
     * Simple command line interface to strip HTML taghes from selected columns in a CSV file.
     * Must have extension .csv <br> Use java com.mindprod.CSVStripTages somefile.csv 0 1 2 3 ...
     * Output replaces input. If you want the input, make a copy first.
     *
     * @param args name of csv file to strip, followed by zero-based cols wanted in desired order.
     */
    public static void main( final String[] args )
        {
        if ( args.length < 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException(
                    "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        final int[] colsToStrip = new int[ args.length - 1 ];
        try
            {
            for ( int i = 1; i < args.length; i++ )
                {
                colsToStrip[ i - 1 ] = Integer.parseInt( args[ i ] );
                }
            }
        catch ( NumberFormatException e )
            {
            throw new IllegalArgumentException( USAGE );
            }
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVStripTags( file, ',', '\"', '#', CSV.UTF8, colsToStrip );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVStripTages failed " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
