/*
 * [CSVCommaToTab.java]
 *
 * Summary: Converts comma-separated CSV file to a tab-separated CSV file.
 *
 * Copyright: (c) 2012-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  6.7 2012-11-12 add CSVCommaToTab
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * Converts comma-separated CSV file to a tab-separated CSV file.
 * <p/>
 * Use: java.exe com.mindprod.CSVCommaToTab somefile.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 6.7 2012-11-12 add CSVCommaToTab
 * @see CSVTabToComma
 * @since 2012-11-12
 */
public final class CSVCommaToTab
    {
    /**
     * covert a tab-delimited to comma-delimited CSV file, constructor. Just create. There are no methods to call.
     * Tab-separated files do not support comments. Any comments in input file will be stripped.
     *
     * @param fileBeingProcessed CSV file to be converted to tab form
     * @param separatorChar      field separator character in input, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab for the input file.  It is tab for the input file.
     *                           Note this is a 'char' not a "string".
     * @param quoteChar          character used to quote input fields containing awkward chars in input.
     * @param commentChars       chars to use to introduce comments in input.  Use "" if none. Multiple chars allowed.
     *                           Comments are stripped from tab output.
     * @param encoding           encoding of input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVCommaToTab( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                          final String commentChars, final Charset encoding ) throws IOException
        {
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        // we are reading and stripping comments.
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar,
                quoteChar,
                commentChars,
                true /* hide comments */,
                true /* trimQuoted */,
                true /* trimUnquoted */,
                true
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, '\t', ( char ) 0, ( char ) 0, true );
        try
            {
            while ( true )
                {
                final String s = r.get();
                // null means end of line
                if ( r.wasComment() )
                    {
                    w.nl( s );
                    r.skipToNextLine();
                    }
                else
                    {
                    // null will start a new line.
                    w.put( s );
                    }
                }
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " tabbed lines expanded." );
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        }

    /**
     * Simple command line interface to CSVTabToComma, Converts tabs to commas is csv file whose name is on the
     * command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVTabToComma somefile.csv .  You can use CSVTabToComma constructor
     * in your own programs. It will remove comments in the process.
     *
     * @param args name of csv file to remove excess quotes and space
     */
    public static void main( String[] args )
        {
        if ( args.length != 1 )
            {
            throw new IllegalArgumentException(
                    "CSVCommaToTab needs a single filename.csv on the command line." );
            }
        String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException(
                    "Bad Extension\nCSVCommaToTab needs a single filename.csv on the command line." );
            }
        final File file = new File( filename );
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVCommaToTab( file, ',', '\"', "#", CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVCommaToTab failed to convert " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
