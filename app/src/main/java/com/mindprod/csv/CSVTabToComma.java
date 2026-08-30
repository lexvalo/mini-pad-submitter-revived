/*
 * [CSVTabToComma.java]
 *
 * Summary: Converts tab-separated CSV file to a comma-separated CSV file.
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
 *  2.9 2000-03-27 refactor using enums, support comments.
 *  3.0 2009-06-15 lookup table to speed CSVReader
 *  3.1 2009-12-03 add CSVSort
 *  3.2 2010-02-23 add hex sort 9x+ option to CSVSort
 *  3.3 2010-11-14 change default to no comments in input file for CSVTab2Comma.
 *  3.4 2010-12-03 add CSV2SRS
 *  3.5 2010-12-11 add CSVReshape
 *  3.6 2011-01-25 allow you to specify encoding
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
 * Converts tab-separated CSV file to a comma-separated CSV file.
 * <p/>
 * Use: java.exe com.mindprod.CSVTabToComma somefile.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.6 2011-01-25 allow you to specify encoding
 * @see CSVTabToComma
 * @since 2008-05-28
 */
public final class CSVTabToComma
    {
    /**
     * covert a tab-delimited to comma -delimited CSV file, constructor. Just create. There are no methods to call.
     * Tab-separated files no not support comments, so there is no need for a commentChars field.
     *
     * @param fileBeingProcessed CSV file be converted
     * @param separatorChar      field separator character in output usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab for the output file.  It is tab for the input file.
     *                           Note this is a 'char' not a "string".
     * @param quoteChar          character used to quote fields in output containing awkward chars.
     * @param encoding           encoding of input and output.
     *
     * @throws IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVTabToComma( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                          final Charset encoding ) throws IOException
        {
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        // are no comments in tab file
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                '\t',
                ( char ) 0 /* no " char */,
                "" /* no comment chars in tab files */,
                false /* hide comments */,
                true  /* trimQuoted */,
                true /* trimUnquoted */,
                true /* allowMultipleLineFields */
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        // there are no comments.
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, ( char ) 0, true );
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
     * in your own programs.
     *
     * @param args name of csv file to remove excess quotes and space
     */
    public static void main( String[] args )
        {
        if ( args.length != 1 )
            {
            throw new IllegalArgumentException(
                    "CSVTabToComma needs a single filename.csv on the command line." );
            }
        String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException(
                    "Bad Extension\nCSVTabToComma needs a single filename.csv on the command line." );
            }
        final File file = new File( filename );
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            // presume there are no comments in the tab file
            // fileBeingProcessed,  separatorChar,  quoteChar, encoding
            // no comments permitted in tab file no no need to specify.
            new CSVTabToComma( file, ',', '\"', CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVTabToComma failed to convert " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
