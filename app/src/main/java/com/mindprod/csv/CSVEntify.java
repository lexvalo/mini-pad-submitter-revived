/*
 * [CSVEntify.java]
 *
 * Summary: convert awkward UTF-9 characters in selected columns to &eacute; or &#x00a4; style HTML entities.
 *
 * Copyright: (c) 2011-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2011-01-24 initial version
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.entities.EntifyStrings;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * convert awkward UTF-9 characters in selected columns to &eacute; or &#x00a4; style HTML entities.
 * <p/>
 * Use: java.exe com.mindprod.CSVEntify somefile.csv  0 3
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2011-01-24 initial version
 * @since 2011-01-24
 */
public final class CSVEntify
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVEntify needs a single filename.csv on the command line followed by " +
                                        "0-based numeric columns to entify.";

    /**
     * convert awkward characters  in selected columns in a CSV file to entities,
     * constructor. Just create the CSVEntify object.. There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to have awkward chars converted to HTML entities.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding of the input and output file.
     * @param colsToEntify       list of columns wanted to have chars converted to entities.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVEntify( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                      final char commentChar, final Charset encoding, final int... colsToEntify ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted,
        // trimUnquoted allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars,
                true, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        try
            {
            while ( true )
                {
                // process one line each time through the loop.
                final String[] fields = r.getAllFieldsInLine();
                final int fieldCount = r.wasComment() ? fields.length - 1 : fields.length;
                // convert selected column to entities for selected columns.
                for ( int source : colsToEntify )
                    {
                    if ( source < fieldCount )
                        {
                        fields[ source ] = EntifyStrings.entifyHTML( fields[ source ] );
                        }
                    }
                w.nl( fields, r.wasComment() );
                } // end while
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines entified." );
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        }

    /**
     * Simple command line interface to CSVEntify. Entifies selected columns in one csv file whose name is on the
     * command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVEntify somefile.csv 0 1 2 3 ...
     * Output replaces input. If you want the input, make a copy first.
     *
     * @param args name of csv file to entify, followed by zero-based cols to process in desired order.
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
            throw new IllegalArgumentException( "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        final int[] colsToEntify = new int[ args.length - 1 ];
        try
            {
            for ( int i = 1; i < args.length; i++ )
                {
                colsToEntify[ i - 1 ] = Integer.parseInt( args[ i ] );
                }
            }
        catch ( NumberFormatException e )
            {
            throw new IllegalArgumentException( USAGE );
            }
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVEntify( file, ',', '\"', '#', CSV.UTF8, colsToEntify );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVEntify failed " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
