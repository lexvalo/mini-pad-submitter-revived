/*
 * [CSVDeDupField.java]
 *
 * Summary: Sorts the letters WITHIN field of a given column in a CSV file, then removes duplicates.
 *
 * Copyright: (c) 2011-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2011-11-10 initial version
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;

import java.io.File;
import java.io.IOException;

import static java.lang.System.*;

/**
 * Sorts the letters WITHIN field of a given column in a CSV file, then removes duplicates.
 * <p/>
 * Does not reorder records.
 * <p/>
 * Use: java.exe com.mindprod.CSVDeDupField  somefile.csv  1 5...(0-based columns)
 * For selected column does &quot;aCBBB&quot; &rArr; &quot;BCa&quot;,
 * &quot;DAB&quot;   * &rArr; &quot;ABD&quot;,
 * &quot; &quot;Z X&quot; &rArr; &quot;XZ&quot;,
 * &quot;   &quot; &rArr; &quot;&quot;
 * e.g, CSVDepFieldField 1
 * coverts a file like this:
 * # option letters for processing exception files.
 * space.html,  &quot;E R  S AS&quot;
 * bellweather.html, abcDEF
 * abundance.html,EDQA
 * activedit.html
 * space.html,  &quot;E R  S AS&quot;
 * addnotify.html,Z
 * to
 * # optionn letters for processing exception files.
 * space.html, AERS  # (removes blanks and duplicates)
 * bellweather.html, DEFabc # (case-sensitive sort)
 * abundance.html,AEDQ
 * activedit.html
 * addnotify.html,Z
 * empty, blank or missing columns are ok.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2011-11-10 initial version
 * @see com.mindprod.csv.CSVSort
 * @see CSVSortField
 * @since 2009-12-03
 */
public final class CSVDeDupField
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nUsage: csvdeDupfield.jar somefile.csv  1 5...\n"
                                        + "Needs one or more 0-based column numbers to tell in which column to " +
                                        "process.";

    /**
     * @param args name of csv file to sort, followed by cols to sort.
     */
    public static void main( String[] args )
        {
        if ( args.length < 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException(
                    "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        final int colCount = args.length - 1;
        final int[] columnsToDeDup = new int[ colCount ];
        for ( int i = 0; i < colCount; i++ )
            {
            // skip first parm
            columnsToDeDup[ i ] = Integer.parseInt( args[ i + 1 ] );
            }
        try
            {
            out.print( "CSVDeDupField " );
            // file, cols, types, directions,  separatorChar,  quoteChar,  commentChar
            new CSVSortField( true /* deDup */, file, columnsToDeDup, ',', '\"', '#', CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVDeDupField failed to sort " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
