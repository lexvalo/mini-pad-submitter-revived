/*
 * [TestCSVLoad.java]
 *
 * Summary: Test load a csv file into a HashMap.
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
 *  1.0 2012-04-05 initial version
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.*;

/**
 * Test load a csv file into a HashMap.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2012-04-05 initial version
 * @since 2012-04-05
 */
public final class TestCSVLoad
    {
    /**
     * Test driver, command line ignored
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        final HashMap<String, String> lookup = new HashMap<>( 1000 );
        try
            {
            // There are other terser constructors you could have used.
            // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted,
            // trimUnquoted allowMultipleLineFields
            final CSVReader csv = new CSVReader(
                    EIO.getBufferedReader( new File( "C:/temp/test.csv" ), 32 * 1024, CSV.UTF8 ),
                    ',',  /* separatorChar between fields */
                    '\"', /* quoteChar to surround fields containing commas */
                    "#",  /* char that starts comments */
                    true /* hide comments */,
                    true /* trimQuoted */,
                    true /* trimUnquoted */,
                    true /* allow multiline fields multiline */
            );
            try
                {
                while ( true )
                    {
                    final String key = csv.get();
                    final String value = csv.get();
                    csv.skipToNextLine();
                    lookup.put( key, value );
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
        // dump out the HashMap
        for ( Map.Entry<String, String> e : lookup.entrySet() )
            {
            out.println( e );
            }
        }
    }
