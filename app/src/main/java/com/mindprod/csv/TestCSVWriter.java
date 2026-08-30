/*
 * [TestCSVWriter.java]
 *
 * Summary: Test CSVWriter to write a csv file.
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
 *  1.0 2010-12-31 initial version.
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;

import java.io.File;
import java.io.IOException;

import static java.lang.System.*;

/**
 * Test CSVWriter to write a csv file.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2010-12-31 initial version
 * @since 2010-12-31
 */
public final class TestCSVWriter
    {
    /**
     * Test driver, commandh line ignored
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        try
            {
            // write out a test file
            // There are other terser constructors you could have used.
            // To specify the encoding, build it into the BufferedWriter.
            CSVWriter csv =
                    new CSVWriter( EIO.getPrintWriter( new File( "C:/temp/test.csv" ), 10 * 1024, EIO.UTF8 ),
                            2, /* quotelevel   (ttTest with all four values! )
                     * -1 = like 0, but add an extra space after each separator/comma,
                     *  0 = minimal quotes, only around fields containing quotes or separators.
                     *  1 = quotes also around fields containing spaces.
                     *  2 = quotes around all fields, whether or not they contain commas, quotes or spaces.
                     */
                            ',',  /* separatorChar between fields */
                            '\"', /* quoteChar to surround fields containing commas */
                            '#',  /* char that starts comments */
                            true /*  trim fields of lead and trailing blanks */ );
            // each put is a single field.
            csv.put( "abc" );
            csv.put( "def" );
            csv.put( "g h i" );
            csv.put( "jk,l" );
            csv.put( "m\"n\'o " );
            csv.nl();
            csv.put( "m\"n\'o " );
            csv.put( "    " );
            csv.put( "a" );
            csv.put( "x,y,z" );
            csv.put( "x;y;z" );
            csv.put( "stringwithemebbed#char" );
            csv.nl( "a comment" );
            csv.put( true ); // output a boolean
            csv.put( 123 ); // output an integer
            csv.put( 1. / 3., 2 ); // output rounded to two decimal places.
            csv.put( "" );
            csv.nl();
            final String[] groupOfFields = new String[] { "abc", "def", "last one" };
            csv.nl( groupOfFields, false ); // outputs three separate fields on a single line
            csv.nl( groupOfFields, true ); // outputs two fields on line, with third treated as comment.
            csv.close();
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println();
            }
        }
    }
