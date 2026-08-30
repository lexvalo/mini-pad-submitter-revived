/*
 * [CSVToSRS.java]
 *
 * Summary: Converts a 2-column CSV file to a first cut at a Funduc Search Replace SRS Script.
 *
 * Copyright: (c) 2010-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  3.4 2010-12-03 add CSV2SRS
 *  3.5 2011-01-25 allow you to specify encoding
 *  3.6 2011-03-02 apply prelude and postlude to create a complete script that needs tweaking.
 *  3.7 2011-11-11 insert BOM at start of generated *.SRS file.
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * Converts a 2-column CSV file to a first cut at a Funduc Search Replace SRS Script.
 * <p/>
 * Use: java.exe com.mindprod.CSVToSRS pairs.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.7 2011-11-11 insert BOM at start of generated *.SRS file.
 * @since 2008-05-28
 */
public final class CSVToSRS
    {
    /**
     * a BOM as it appears internally
     */
    private static final char BOM = 0xfeff;

    private static final String PRELUDE = BOM + "[Script for Search and Replace]\r\n" +
                                          "--title goes here--\r\n" +
                                          "[Options]\r\n" +
                                          "Search_subdir=1\r\n" +
                                          "Prompt_flag=0\r\n" +
                                          "Max Display Chars=512\r\n" +
                                          "Show Progress Dialog=1\r\n" +
                                          "Count across files=0\r\n" +
                                          "Line Prefix=Line %3ld - \r\n" +
                                          "Binary Prefix=Offset 0x%-6lx - \r\n" +
                                          "Replace Processing=0\r\n" +
                                          "Process Binary Files=1\r\n" +
                                          "Buffer Size=102400\r\n" +
                                          "Num Buffers To Process=0\r\n" +
                                          "Output_File=\r\n" +
                                          "Show_Files=1\r\n" +
                                          "Backup Path=\r\n" +
                                          "Before Hit=<\r\n" +
                                          "After Hit=>\r\n" +
                                          "Max Reg Expr=300\r\n" +
                                          "Write to Backup Dir=0\r\n" +
                                          "Unzip Dir=C:\\Temp\\\r\n" +
                                          "Show Files Without Hits=0\r\n" +
                                          "Display Replace String=0\r\n" +
                                          "Display File Stats=1\r\n" +
                                          "Show File Date and Size=0\r\n" +
                                          "Reverse Filters=0\r\n" +
                                          "Min Size Filter=0\r\n" +
                                          "Max Size Filter=0\r\n" +
                                          "Min Date Filter=\r\n" +
                                          "Max Date Filter=\r\n" +
                                          "Skip Files Mask=0\r\n" +
                                          "Ignore Attributes=55\r\n" +
                                          "Keep file time stamp=0\r\n" +
                                          "One hit=0\r\n" +
                                          "Sort File Names=0\r\n" +
                                          "Sort Ascending=1\r\n" +
                                          "Append to output file=0";

    private static final String POSTLUDE = "[Path]\r\n" +
                                           "C:\\temp\\*.txt\r\n" +
                                           "[End of Search and Replace Script]";

    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVToSRS needs a single *.csv on the command line. Output will be in " +
                                        "*.srs.";

    /**
     * Constructor to preapare a Funduc SRS search/replace script from a CSV file.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab for the output file.  It is tab for the input file.
     *                           Note this is a 'char' not a "string".
     * @param quoteChar          character used to quote fields containing awkward chars.
     * @param commentChars       chars to treat at comment lead-ins.
     * @param encoding           encoding of input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVToSRS( final File fileBeingProcessed, final char separatorChar, final char quoteChar, final String commentChars,
                     final Charset encoding ) throws IOException
        {
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars,
                true, true /* trimQuoted */, true /* trimUnquoted */, true );
        String srsFilename = EIO.getCanOrAbsPath( fileBeingProcessed );
        // replace .csv with .srs
        srsFilename = srsFilename.substring( 0, srsFilename.length() - 4 ) + ".srs";
        final File srsFile = new File( srsFilename );
        final PrintWriter w = EIO.getPrintWriter( srsFile, 16 * 1024, CSV.UTF8 ); // no matter what the input encoding, always export UTF-8
        w.println( PRELUDE );
        try
            {
            while ( true )
                {
                final String from = r.get();
                final String to = r.get();
                r.skipToNextLine();
                w.println( "[Search]" );  //[Search /x] would give a case-insensitive replace /w would do a whole
                // word replace.
                w.println( from );
                w.println( "[Replace]" );
                w.println( to );
                }
            }
        catch ( EOFException e )
            {
            w.println( POSTLUDE );
            out.println( r.lineCount() + " lines converted to an SRS script." );
            r.close();
            w.close();
            }
        }

    /**
     * Simple command line interface to CSVToSRS. Prepares one csv file whose name is on the command line to the guts of
     * a Funduc Search/Replace script.. Must have
     * extension .csv <br> Use java com.mindprod.CSVToSRS somefile.csv. You can use CSVToSRS constructor
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
        final String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException(
                    "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVToSRS( file, ',', '\"', "#", CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVToSRS failed to convert " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
