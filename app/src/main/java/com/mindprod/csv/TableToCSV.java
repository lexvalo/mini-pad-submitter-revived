/*
 * [TableToCSV.java]
 *
 * Summary: Extracts rows in CSV tables to CSV form. Extracts data from all tables in the input. Output in xxx.csv.
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
 *  1.1 2011-01-25 allow you to specify encoding
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.hunkio.HunkIO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * Extracts rows in CSV tables to CSV form. Extracts data from all tables in the input. Output in xxx.csv.
 * <p/>
 * Use: java.exe com.mindprod.TableToCSV xxxx.html
 * It also strips tags and converts entities back to UTF-8 characters.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2011-01-25 allow you to specify encoding
 * @since 2011-01-23
 */
public final class TableToCSV
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nTableToCSV needs the name of an *.html or *.htmlfrag file on the commandline, " +
                                        "nothing else. Output will be in xxx.csv.";

    /**
     * Constructor to convert an HTML table to CSV. Strips out entities and tags.
     *
     * @param file          CSV file to be packed to remove excess space and quotes.
     * @param separatorChar field separator character, usually ',' in North America,
     *                      ';' in Europe and sometimes '\t' for
     *                      tab for the output file.  It is tab for the input file.
     *                      Note this is a 'char' not a "string".
     * @param quoteChar     character used to quote fields containing awkward chars.
     * @param commentChar   character to treat as comments.
     * @param encoding      encoding of the input and output file.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public TableToCSV( final File file, final char separatorChar, final char quoteChar, final char commentChar,
                       final Charset encoding ) throws IOException
        {
        String outFilename = EIO.getCanOrAbsPath( file );
        outFilename = ST.chopTrailingString( outFilename, ".htmlfrag" );
        outFilename = ST.chopTrailingString( outFilename, ".html" );
        outFilename += ".csv";
        final File outFile = new File( outFilename );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( outFile, 32 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        // read the entire html file into RAM.
        String big = HunkIO.readEntireFile( file, encoding );
        int from = 0;
        // our parser is forgiving, works even if </td> </tr> missing.
        while ( true )
            {
            // find <tr
            final int trStart = big.indexOf( "<tr", from );
            if ( trStart < 0 )
                {
                break;
                }
            from = trStart + 3;
            final int trEnd = big.indexOf( '>', from );
            if ( trEnd < 0 )
                {
                break;
                }
            while ( true )
                {
                // search for <td>...</td>
                final int tdStart = big.indexOf( "<td", from );
                if ( tdStart < 0 )
                    {
                    break;
                    }
                from = tdStart + 3;
                final int tdEnd = big.indexOf( '>', from );
                if ( tdEnd < 0 )
                    {
                    break;
                    }
                from = tdEnd + 1;
                final int startField = tdEnd + 1;
                final int slashTdStart = big.indexOf( "</td", from );
                final int lookaheadTd = big.indexOf( "<td", from );
                final int lookaheadSlashTr = big.indexOf( "</tr", from );
                final int lookaheadTr = big.indexOf( "<tr", from );
                int endField = Integer.MAX_VALUE;
                if ( slashTdStart >= 0 && slashTdStart < endField )
                    {
                    endField = slashTdStart;
                    }
                if ( lookaheadTd >= 0 && lookaheadTd < endField )
                    {
                    endField = lookaheadTd;
                    }
                if ( lookaheadSlashTr >= 0 && lookaheadSlashTr < endField )
                    {
                    endField = lookaheadSlashTr;
                    }
                if ( lookaheadTr >= 0 && lookaheadTr < endField )
                    {
                    endField = lookaheadTr;
                    }
                if ( endField == Integer.MAX_VALUE )
                    {
                    break;
                    }
                from = endField + 3;
                final int slashTdEnd = big.indexOf( '>', from );
                if ( slashTdEnd < 0 )
                    {
                    break;
                    }
                String field = big.substring( startField, endField );
                field = DeEntifyStrings.flattenHTML( field, ' ' );
                w.put( field );
                from = slashTdEnd + 1;
                final int lookTd = big.indexOf( "<td", from );
                final int lookTr = big.indexOf( "<tr", from );
                if ( lookTr >= 0 && lookTr < lookTd || lookTd < 0 )
                    {
                    break;
                    }
                }
            w.nl();
            }
        out.println( w.getLineCount() + " rows extracted from table to csv" );
        w.close();
        }

    /**
     * Simple command line interface to TableToCSV. Converts one  HTML file to a CSV file, extracting tables,
     * with entities stripped.
     * Must have extension .html <br> Use java com.mindprod.TableToCSV somefile.html .  You can use TableToCSV
     * constructor
     * in your own programs.
     *
     * @param args name of csv file to remove excess quotes and space
     */
    public static void main( String[] args )
        {
        if ( args.length != 1 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        String filename = args[ 0 ];
        if ( !( filename.endsWith( ".html" ) || filename.endsWith( ".htmlfrag" ) ) )
            {
            throw new IllegalArgumentException( "Bad Extension. Input must be a .html file.\n" + USAGE );
            }
        final File file = new File( filename );
        try
            {
            // file, separatorChar, quoteChar, commentChar, encoding
            new TableToCSV( file, ',', '\"', '#', CSV.UTF8 );
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
