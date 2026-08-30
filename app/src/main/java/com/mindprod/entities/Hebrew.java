/*
 * [Hebrew.java]
 *
 * Summary: generate tables to display hebrew entities.
 *
 * Copyright: (c) 2013-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2013-07-22 initial version
 */
package com.mindprod.entities;

import com.mindprod.common18.BigDate;
import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.csv.CSVReader;
import com.mindprod.fastcat.FastCat;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import static java.lang.System.*;

/**
 * generate tables to display hebrew entities.
 * <p/>
 * Manually insert is jgloss/hebrew.html
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2013-07-22 initial version
 * @since 2013-07-22
 */
public final class Hebrew
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nHebrew fetches hebrew.csv from current directory. Outputs hebrew.html to " +
                                        "current directory.";

    private static final String DO_NOT_EDIT = "<!-- The following table was generated on " + BigDate.localToday().toString() + " by Hebrew. D o   n o t   e d i t . -->\n";

    static void head( final FastCat sb, final String title )
        {
        sb.append( DO_NOT_EDIT +
                   "<table class=\"borderless\"><caption class=\"hidden\">", title, "</caption>",
                "<thead><tr><th colspan=\"4\">", title, "</th></tr>\n",
                "<tr><th>Character</th>",
                "<th>Hex Entity</th>",
                "<th>Decimal Entity</th>",
                "<th>Description</th></tr></thead><tbody>\n" );
        }

    private static void tail( FastCat sb )
        {
        sb.append( "</tbody></table>\n" );
        }

    /**
     * Simple command line interface to Dump. Dumps one csv file whose name is on the command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVDump somefile.csv
     *
     * @param args name of csv file to remove excess quotes and space
     */
    public static void main( String[] args )
        {
        if ( args.length != 0 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        try
            {
            final CSVReader r = new CSVReader( EIO.getBufferedReader( new File( "hebrew.csv" ),
                    32 * 1024, EIO.UTF8 ),
                    ',', '\"', "#", true /* hide */, true /* trimQuoted */, true /* trimUnquoted */,
                    true /* allowMultiLineFields */
            );
            final FastCat sb = new FastCat( 3000 );
            head( sb, "Hebrew Entities" );
            char prevCode = 'L';
            try
                {
                while ( true )
                    {
                    final char code = r.getChar();
                    final String hexString = r.get();
                    final String desc = r.get();
                    r.skipToNextLine();
                    final int theCharNumber = Integer.parseInt( hexString.substring( 2 ), 16 );
                    if ( code != prevCode )
                        {
                        tail( sb );
                        head( sb, "Hebrew accents, points and punctuation" );
                        prevCode = code;
                        }
                    sb.append( "<tr>" );
                    sb.append( "<td class=\"behold\">&#x", ST.toLZHexString( theCharNumber, 4 ), ";</td>" );
                    sb.append( "<td class=\"hexentity\">&amp;#x", ST.toLZHexString( theCharNumber, 4 ),
                            ";</td>" );
                    sb.append( "<td class=\"decimalentity\">&amp;", Integer.toString( theCharNumber ),
                            ";</td>" );
                    sb.append( "<td>", desc, "</td>" );
                    sb.append( "</tr>\n" );
                    }
                }
            catch ( EOFException e )
                {
                tail( sb );
                HunkIO.writeEntireFile( new File( "hebrew.htmlfrag" ), sb.toString() );
                r.close();
                }
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "Hebrew failed" );
            err.println();
            }
        } // end main
    }
