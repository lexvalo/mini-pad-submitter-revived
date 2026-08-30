/*
 * [TestCSVReader.java]
 *
 * Summary: Test CSVReader to read a csv file.
 *
 * Copyright: (c) 2002-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2010-12-31 initial version
 */
package com.mindprod.csv;

import com.mindprod.hunkio.HunkIO;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.System.*;

/**
 * Test CSVReader to read a csv file.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2011-10-29 allow test with/without comments, read field at a time or line at a time
 * @since 2010-12-31
 */
public final class TestCSVReader
    {
    /**
     * true if should test processing hiding comments
     */
    private static final boolean HIDE_COMMENTS = true;

    /**
     * true if should test processing a line at a time
     */
    private static final boolean READ_A_LINE_AT_A_TIME = true;

    /**
     * Test driver,  Command line ignored.
     *
     * @param args not used
     *
     * @noinspection InfiniteLoopStatement
     */
    public static void main( String[] args )
        {
        out.println( "Testing with HIDE_COMMENTS = " + HIDE_COMMENTS + " and READ_A_LINE_AT_A_TIME = " +
                     READ_A_LINE_AT_A_TIME );
        try
            {
            String all = HunkIO.readEntireFile( new File( "test.csv" ) );
            out.println( "test file looks like this:" );
            out.println( all );
            // read test file test.csv in current dir, and dump it out.
            // There are other terser constructors you could have used.
            // To specify encoding, build it into the BufferedReader.
            // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted,
            // trimUnquoted allowMultipleLineFields
            CSVReader csv = new CSVReader( new BufferedReader( new FileReader( "test.csv" ) ),
                    ',',  /* separatorChar between fields */
                    '\"', /* quoteChar to surround fields containing commas */
                    "#",  /* char that starts comments */
                    HIDE_COMMENTS /* hide comments */,
                    true /* trimQuoted */,
                    true /* trimUnquoted */,
                    true /* allow multiline fields multiline */
            );
            try
                {
                while ( true )
                    {
                    if ( READ_A_LINE_AT_A_TIME )
                        {
                        String[] fields = csv.getAllFieldsInLine();
                        out.print( fields.length + " fields " );
                        for ( int i = 0; i < fields.length; i++ )
                            {
                            out.print( "[" + i + "]={" + fields[ i ] + "} " );
                            }
                        out.println( "wasComment: " + csv.wasComment() );
                        }
                    else
                        {
                        final String s = csv.get();
                        // we could also use more specific get methods.
                        // csv.getAllFieldsInLine();  (does not need a following csv.skipToNextLine() )
                        // csv.skip();
                        // csv.skipToNextLine();
                        // csv.getBoolean();
                        // csv.getDouble()
                        // csv.getFloat()
                        // csv.getInt()
                        // csv.getLong()
                        // csv.getYYYYMMDD()
                        // csv.lineCount()
                        // csv.wasComment()
                        if ( s == null )
                            {
                            out.println( "(end of line)" );
                            }
                        else if ( csv.wasComment() )
                            {
                            out.println( " <" + s + ">" );
                            }
                        else
                            {
                            out.println( "  [" + s + "]" );
                            }
                        }
                    }
                }
            catch ( EOFException e )
                {
                // normal termination.
                csv.close();
                }
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println();
            }
        }
    }
