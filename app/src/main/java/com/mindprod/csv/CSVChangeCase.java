/*
 * [CSVChangeCase.java]
 *
 * Summary: Change case on columns of a CSV file.
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
 *  1.0 2011-02-17 initial release
 *  1.1 2011-03-11 fix bug in selecting the correct translation letter.
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * Change case on columns of a CSV file.
 * <p/>
 * Use: java.exe com.mindprod.CSVChangeCase somefile.csv  0u 2l 4t
 * <p/>
 * 0-based list of column numbers, u=upper case l=lower case t=book title case
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2011-03-11 fix bug in selecting the correct translation letter.
 * @since 2011-02-17
 */
public final class CSVChangeCase
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nUsage: csvchangecase.jar somefile.csv  0u 2l 4t ...u=upper " +
                                        "l=lower t=title";

    /**
     * Change Case of a CSV file, constructor. Just create. There are no methods to call.
     *
     * @param fileBeingProcessed        CSV file to be packed to remove excess space and quotes.
     * @param colsToConvert             array of 0-based cols to sort on.
     * @param typeOfConversionRequested array of chars with letters s i n x to tell how to sort each column.
     * @param separatorChar             field separator character, usually ',' in North America,
     *                                  ';' in Europe and sometimes '\t' for
     *                                  tab.
     * @param quoteChar                 char to use to enclose fields containing a separator,
     *                                  usually '\"'. Use (char)0 if
     *                                  you don't want a quote character.
     * @param commentChar               char to use to introduce comments.  Use (char) 0 if none.  Only one character
     *                                  allowed.
     * @param encoding                  encoding for input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVChangeCase( final File fileBeingProcessed,
                          final int[] colsToConvert,
                          final char[] typeOfConversionRequested,
                          final char separatorChar,
                          final char quoteChar,
                          final char commentChar,
                          final Charset encoding ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars, true, true /* trimQuoted */, true /* trimUnquoted */, true
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
                // convert selected columns to desired case.
                for ( int i = 0; i < colsToConvert.length; i++ )
                    {
                    int source = colsToConvert[ i ];
                    if ( source < fieldCount )
                        {
                        switch ( typeOfConversionRequested[ i ] )
                            {
                            case 'l':
                                fields[ source ] = fields[ source ].toLowerCase();
                                break;
                            case 'u':
                                fields[ source ] = fields[ source ].toUpperCase();
                                break;
                            case 't':
                                fields[ source ] = ST.toBookTitleCase( fields[ source ] );
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "CSVChangeCase program bug: unknown typeOfConversion" );
                            }
                        }
                    }
                w.nl( fields, r.wasComment() );
                } // end while
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines with case converted." );
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        }

    /**
     * Simple command line interface to CSVChangeCase.  Changes case of columns of one csv file whose name is on the
     * command line. Must have
     * extension .csv and select cols to change case on with form:  0u 2l 4t.
     *
     * @param args name of csv file to change case on
     */
    public static void main( String[] args )
        {
        if ( args.length < 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException(
                    "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        final int colCount = args.length - 1;
        final char[] typeOfConversionRequested = new char[ colCount ];
        final int[] colsToConvert = new int[ colCount ];
        for ( int i = 0; i < colCount; i++ )
            {
            // skip first parm
            final String arg = args[ i + 1 ];
            if ( !( 2 <= arg.length() && arg.length() <= 3 ) )
                {
                // must be of form 9u or 99u
                throw new IllegalArgumentException(
                        "bad col\n" + USAGE );
                }
            colsToConvert[ i ] = Integer.parseInt( arg.substring( 0, arg.length() - 1 ) );
            final char conversionRequested = arg.charAt( arg.length() - 1 );
            if ( !ST.isLegal( conversionRequested, "lut" ) )
                {
                throw new IllegalArgumentException(
                        "Column letter must be l/u/t\n" + USAGE );
                }
            typeOfConversionRequested[ i ] = conversionRequested;
            }
        try
            {
            // file, cols, types, directions,  separatorChar,  quoteChar,  commentChar
            new CSVChangeCase( file,
                    colsToConvert,
                    typeOfConversionRequested,
                    ',',
                    '\"',
                    '#',
                    CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVChangeCase failed  " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
