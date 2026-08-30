/*
 * [CSVScale.java]
 *
 * Summary: Scale values in selected column up or down by a multiplier.
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
 *  1.0 2014-06-01 initial version
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import static java.lang.System.*;

/**
 * Scale values in selected column up or down by a multiplier.
 * <p/>
 * Use: java.exe com.mindprod.CSVscale somefile.csv 0.01  0 3
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2014-06-01 initial version
 * @since 2014-06-01
 */
public final class CSVScale
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVScale needs a single filename.csv on the command line followed by " +
                                        " a scale factor, e.g. 10 or 0.01 followed by 0-based numeric columns to scale.";

    private static final DecimalFormat[] df = {
            new DecimalFormat( "###,##0" ) /* 0 */,
            new DecimalFormat( "###,##0.0" ) /* 1 */,
            new DecimalFormat( "###,##0.00" ) /* 2 */,
            new DecimalFormat( "###,##0.000" ) /* 3 */,
            new DecimalFormat( "###,##0.0000" ) /* 4 */,
            new DecimalFormat( "###,##0.00000" ) /* 5 */,
            new DecimalFormat( "###,##0.000000" ) /* 6 */ };

    /**
     * Scale values in selected column up or down by a multiplier.
     * constructor. Just create the CSVScale. There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to have awkward chars converted to HTML entities.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding,          how input and output CSV files are encoded.
     * @param scale              scaleFactor
     * @param colsToScale        list of columns wanted to have chars converted to entities.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVScale( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                     final char commentChar, final Charset encoding, final double scale, final int... colsToScale ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted,
        // trimUnquoted allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 32 * 1024, encoding ),
                separatorChar, quoteChar, commentChars,
                true, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        final int extraDecimalPlaces = -( int ) Math.floor( Math.log10( scale ) );
        // if scaled bigger, can drop decimal places. If scaled smaller, must add decimal places.
        try
            {
            while ( true )
                {
                // process one line each time through the loop.
                final String[] fields = r.getAllFieldsInLine();
                final int fieldCount = r.wasComment() ? fields.length - 1 : fields.length;
                // convert selected column to entities for selected columns.
                for ( int source : colsToScale )
                    {
                    if ( source < fieldCount )
                        {
                        try
                            {
                            final String field = fields[ source ];
                            final int place = field.lastIndexOf( '.' );
                            final int oldDecimalPlaces;
                            if ( place < 0 )
                                {
                                oldDecimalPlaces = 0;
                                }
                            else
                                {
                                oldDecimalPlaces = field.length() - place - 1;
                                }
                            final int newDecimalPlaces = Math.min( Math.max( 0, oldDecimalPlaces + extraDecimalPlaces ), 6 );
                            fields[ source ] = df[ newDecimalPlaces ].format( Double.parseDouble( field ) * scale );
                            }
                        catch ( NumberFormatException e )
                            {
                            err.println( ">>> Error: Could not scale field ["
                                         + fields[ source ]
                                         + "] on line "
                                         + r.lineCount()
                                         + " in file "
                                         + EIO.getCanOrAbsPath( fileBeingProcessed ) );
                            }
                        }
                    }
                w.nl( fields, r.wasComment() );
                } // end while
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines scaled." );
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
        if ( args.length < 3 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad Extension\n" + USAGE );
            }
        final File fileToProcess = new File( filename );
        final double scale = Double.parseDouble( args[ 1 ] );
        final int[] colsToScale = new int[ args.length - 2 ];
        try
            {
            for ( int i = 2; i < args.length; i++ )
                {
                colsToScale[ i - 2 ] = Integer.parseInt( args[ i ] );
                }
            }
        catch ( NumberFormatException e )
            {
            throw new IllegalArgumentException( USAGE );
            }
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVScale( fileToProcess, ',', '\"', '#', EIO.UTF8, scale, colsToScale );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVScale failed " + EIO.getCanOrAbsPath( fileToProcess ) );
            err.println();
            }
        } // end main
    }
