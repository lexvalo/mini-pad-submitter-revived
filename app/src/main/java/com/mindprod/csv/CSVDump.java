/*
 * [CSVDump.java]
 *
 * Summary: display a csv file with fields separated with |.
 *
 * Copyright: (c) 1998-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * display a csv file with fields separated with |.
 * <p/>
 * Use: java.exe com.mindprod.CSVDump somefile.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.6 2011-01-25 allow you to specify encoding
 * @since 1998
 */
public final class CSVDump
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVDump needs a single filename.csv on the command line.";

    /**
     * Dump CSV file.
     *
     * @param fileBeingProcessed CSV file to be dumped
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChars       characters that mark the start of a comment, usually "#", but can be multiple chars.
     *                           Note this is a "string" not a 'char'.
     * @param encoding           encoding of input and output.
     *
     * @throws IOException if problems reading file
     */
    @SuppressWarnings( "WeakerAccess" )
    public CSVDump( final File fileBeingProcessed, final char separatorChar, final char quoteChar, final String commentChars,
                    final Charset encoding ) throws IOException
        {
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars, false, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        try
            {
            while ( true )
                {
                // read one line:
                final String[] fields = r.getAllFieldsInLine();
                for ( String field : fields )
                    {
                    out.print( "| " );
                    out.print( field );  // comment treated like any other field.
                    out.print( ' ' );
                    }
                if ( r.wasComment() )
                    {
                    out.println( "<<" );
                    }
                else
                    {
                    out.println( "|" );
                    }
                }
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines dumped." );
            r.close();
            }
        }

    /**
     * Simple command line interface to Dump. Dumps one csv file whose name is on the command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVDump somefile.csv
     *
     * @param args name of csv file to remove excess quotes and space
     */
    public static void main
    ( String[] args )
        {
        if ( args.length != 1 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        try
            {
            new CSVDump( file, ',', '\"', "#", CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVDump failed " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
