/*
 * [CSVToTable.java]
 *
 * Summary: Converts a CSV file to the guts of an HTML table. Output appears in xxx.html.
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
 *  1.0 2011-01-23 initial version.
 *  1.1 2011-01-25 allow optional css classes on command line, encoding.
 *  1.2 2011-02-14 no longer entify. Do separately with Entify.
 *  1.3 2013-01-30 now specify class to <tr as well
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.fastcat.FastCat;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import static java.lang.System.*;

/**
 * Converts a CSV file to the guts of an HTML table. Output appears in xxx.html.
 * <p/>
 * Use: java.exe com.mindprod.CSVToTable xxx.csv
 * Awkward characters will appear as Entities.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.3 2013-01-30 now specify class to <tr as well
 * @since 2011-01-23
 */
public final class CSVToTable
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVToTable needs the name of a CSV file on the commandline," +
                                        "\nfollowed optionally by css classes for <tr and each <td column.\nOutput " +
                                        "will be in xxx.htmlfrag.";

    private static DecimalFormat DF = new DecimalFormat( "#,##0" );

    /**
     * Constructor to convert a CSV file to an HTML table.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab for the output file.  It is tab for the input file.
     *                           Note this is a 'char' not a "string".
     * @param quoteChar          character used to quote fields containing awkward chars.
     * @param commentChars       characters to treat as comments.
     * @param encoding           encoding of the input and output file.
     * @param cssClasses         css Classes for <tr and <td columns of HTML table (optional)
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVToTable( final File fileBeingProcessed, final char separatorChar, final char quoteChar, final String commentChars,
                       final Charset encoding, final String... cssClasses ) throws IOException
        {
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ), separatorChar, quoteChar, commentChars,
                true, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        String tableFilename = EIO.getCanOrAbsPath( fileBeingProcessed );
        tableFilename = tableFilename.substring( 0, tableFilename.length() - 4 ) + ".htmlfrag";
        final File tableFile = new File( tableFilename );
        final PrintWriter w = EIO.getPrintWriter( tableFile, 16 * 1024, encoding );
        try
            {
            while ( true )
                {
                final String[] fields = r.getAllFieldsInLine();
                // we don't apply the <table or </table>  or <tbody /tbody
                final FastCat sb = new FastCat( fields.length * 5 + 4 );
                if ( 0 < cssClasses.length && cssClasses[ 0 ].length() > 0 )
                    {
                    sb.append( "<tr class=\"" );
                    sb.append( cssClasses[ 0 ] );
                    sb.append( "\">" );
                    }
                else
                    {
                    sb.append( "<tr>" );
                    }
                for ( int i = 0; i < fields.length; i++ )
                    {
                    final String field = fields[ i ];
                    int j = i + 1;
                    if ( j < cssClasses.length && cssClasses[ j ].length() > 0 )
                        {
                        sb.append( "<td class=\"" );
                        sb.append( cssClasses[ j ] );
                        sb.append( "\">" );
                        }
                    else
                        {
                        sb.append( "<td>" );
                        }
                    if ( ST.isNumeric( field ) )
                        {
                        final long x = Long.parseLong( field );
                        sb.append( DF.format( x ) );
                        }
                    else
                        {
                        sb.append( field );
                        }
                    sb.append( "</td>" );
                    }
                sb.append( "</tr>\n" );
                w.print( sb.toString() );
                }
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " csv lines converted to table rows." );
            r.close();
            w.close();
            }
        }

    /**
     * Simple command line interface to CSVToTable. Converts one CSV file to an HTML table. Must have
     * extension .csv <br> Use java com.mindprod.CSVToTable somefile.csv.
     * You may optionally provide CSS classes for each column of the table. "" means no css class for that column.
     * You can use CSVToTable constructor in your own programs.
     *
     * @param args name of csv file to remove excess quotes and space
     */
    public static void main( String[] args )
        {
        if ( args.length < 1 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        final String[] cssClasses = new String[ args.length - 1 ];
        System.arraycopy( args, 1, cssClasses, 0, args.length - 1 );
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVToTable( file, ',', '\"', "#", CSV.UTF8, cssClasses );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVToTable failed to export" + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
