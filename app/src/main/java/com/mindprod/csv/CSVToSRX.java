/*
 * [CSVToSRX.java]
 *
 * Summary: Converts a 2-column CSV file to a Funduc Search Replace BE SRX Script.
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
 *  1.0 2014-08-05 initial version, cloned from CSVToSRS
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.entities.EntifyStrings;
import com.mindprod.fastcat.FastCat;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * Converts a 2-column CSV file to a Funduc Search Replace BE SRX Script.
 * <p/>
 * Use: java.exe com.mindprod.CSVToSRX pairs.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2014-08-05 initial version, cloned from CSVToSRS
 * @since 2014-08-05
 */
public final class CSVToSRX
    {
    // heading for file. No need for BOM. Generated File must be \n not \r\n.
    private static final String PRELUDE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                          "<fs_sr_script>\n" +
                                          "<fs_sr_options process_binary_files=\"0\"/>\n";

    // tail end of file.
    private static final String POSTLUDE = "<fs_file_list_info search_subdir=\"1\" sort_file_names=\"0\" sort_ascending=\"1\">\n" +
                                           "<mask>*.html</mask>\n" +
                                           "<path>C:\\temp\\</path>\n" +
                                           "</fs_file_list_info>\n" +
                                           "</fs_sr_script>\n";

    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVToSRX needs a single *.csv file on the command line. Output will be in " +
                                        "*.srx.";

    /**
     * Constructor to prepare a Funduc BE SRX search/replace script from a CSV file.
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
    public CSVToSRX( final File fileBeingProcessed, final char separatorChar, final char quoteChar, final String commentChars,
                     final Charset encoding ) throws IOException
        {
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars,
                true, true /* trimQuoted */, true /* trimUnquoted */, true );
        String srsFilename = EIO.getCanOrAbsPath( fileBeingProcessed );
        // replace .csv with .srs
        srsFilename = ST.chopTrailingString( srsFilename, ".csv" ) + ".srx";
        final File srsFile = new File( srsFilename );
        final PrintWriter w = EIO.getPrintWriter( srsFile, 16 * 1024, CSV.UTF8 ); // no matter what the input encoding, always export UTF-8
        w.print( PRELUDE );
        try
            {
            while ( true )
                {
                final String from = r.get();
                final String to = r.get();
                r.skipToNextLine();
                FastCat sb = new FastCat( 8 );
                sb.append( "<fs_sr_info case_sensitive=\"1\">\n" );
                // must be on single line to avoid appending chars to search/replace strings.
                sb.append( "<search_string>", EntifyStrings.entifyXML( from ), "</search_string>\n" );
                sb.append( "<replace_string>", EntifyStrings.entifyXML( to ), "</replace_string>\n" );
                sb.append( "</fs_sr_info>\n" );
                w.print( sb.toString() );
                }
            }
        catch ( EOFException e )
            {
            w.print( POSTLUDE );
            out.println( r.lineCount() + " lines converted to an SRX script." );
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
            new CSVToSRX( file, ',', '\"', "#", CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVToSRX failed to convert " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
