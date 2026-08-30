/*
 * [LinesToCSV.java]
 *
 * Summary: Converts text file of one field per line to a comma-separated CSV file.
 *
 * Copyright: (c) 2008-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2010-12-14 initial version
 *  1.1 2011-01-25 allow you to specify encoding
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * Converts text file of one field per line to a comma-separated CSV file.
 * <p/>
 * Use: java.exe com.mindprod.LinesToCSV somefile.txt  3
 * How many lines should be grouped together on one line of the CSV file.
 * Output is somefile.csv.  somefile.txt is left unchanged.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2011-01-25 allow you to specify encoding
 * @since 2010-12-14
 */
public final class LinesToCSV
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nLinesToCSV needs a single filename.txt on the command line following by the " +
                                        "grouping number.";

    /**
     * convert a txt file to CSV, constructor. Just create. There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param grouping           how many lines to collect together as one csv line, one field per line.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab for the output file.  It is tab for the input file.
     *                           Note this is a 'char' not a "string".
     * @param quoteChar          character used to quote fields containing awkward chars.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     *                           Note this is a 'char' not a "string". For output file. No comment presumed in input file.
     * @param encoding           encoding of input and output file.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public LinesToCSV( final File fileBeingProcessed, final int grouping, final char separatorChar, final char quoteChar,
                       final char commentChar, final Charset encoding ) throws IOException
        {
        // O P E N
        final BufferedReader r = EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding );
        String outFilename = EIO.getCanOrAbsPath( fileBeingProcessed );
        int place = outFilename.lastIndexOf( '.' );
        if ( place < 0 )
            {
            outFilename = outFilename + ".csv";
            }
        else
            {
            outFilename = outFilename.substring( 0, place ) + ".csv";
            }
        final File outFile = new File( outFilename );
        final PrintWriter pw = EIO.getPrintWriter( outFile, 64 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        int rCount = 0;
        int count = grouping;
        try
            {
            String thisLine;
            while ( ( thisLine = r.readLine() ) != null )
                {
                rCount++;
                w.put( thisLine );
                count--;
                if ( count == 0 )
                    {
                    w.nl();
                    count = grouping;
                    }
                }
            }
        catch ( EOFException e )
            {
            }
        finally
            {
            out.println( rCount + " text lines read, "
                         + w.getLineCount() + " csv lines written" );
            r.close();
            w.close();
            }
        }

    /**
     * Simple command line interface to LinesToCSV.  Converts text file of one field per line to a comma-separated
     * CSV file.
     * java.exe com.mindprod.LinesToCSV somefile.txt  3
     * How many lines should be grouped together on one line of the CSV file.
     * Output is somefile.csv.  somefile.txt is left unchanged.
     * You can use LinesToCSV constructor in your own programs.
     *
     * @param args name of csv file to remove excess quotes and space
     */
    public static void main( String[] args )
        {
        if ( args.length != 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        String filename = args[ 0 ];
        int grouping = Integer.parseInt( args[ 1 ] );
        if ( !filename.endsWith( ".txt" ) )
            {
            throw new IllegalArgumentException( "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new LinesToCSV( file, grouping, ',', '\"', '#', CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "LinesToCSV failed to convert " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
