/*
 * [CSVSortField.java]
 *
 * Summary: Sorts the letters WITHIN field of a given column in a CSV file. Does not reorder records.
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
import com.mindprod.common18.ST;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;

import static java.lang.System.*;

/**
 * Sorts the letters WITHIN field of a given column in a CSV file. Does not reorder records.
 * <p/>
 * Use: java.exe com.mindprod.CSVSortField  somefile.csv  1 5 (0-based columns)
 * <p/>
 * For selected column does: e.g.
 * &quot;DAB&quot; &rArr; &quot;ABD&quot;,
 * &quot;aCBBB&quot; &rArr; &quot;BBBCa&quot;,
 * &quot;Z X&quot; &rArr; &quot;XZ&quot;,
 * &quot; &quot; &rArr; &quot;&quot;
 * <p/>
 * e.g, CSVSort Field 1
 * coverts a file like this:
 * # option letters for processing exception files.
 * bellweather.html, abcDEF
 * abundance.html,EDQA
 * activedit.html
 * &quot;E R  S AS&quot;
 * addnotify.html,Z
 * to
 * # optionn letters for processing exception files.
 * bellweather.html, DEFabc # (case-sensitive sort)
 * abundance.html,AEDQ
 * activedit.html
 * space.html, AERS  # (removes blanks, but not dups)
 * addnotify.html,Z
 * empty, blank or missing columns are ok.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2011-11-10 initial version
 * @see CSVSort
 * @since 2009-12-03
 */
public final class CSVSortField
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nUsage: csvsortfield.jar somefile.csv  1 5...n"
                                        + "Needs a one or more 0-based column numbers to tell in which columns to " +
                                        "process.";

    /**
     * Sort/and optionally deup fieldns in a CSV file, constructor. Just create. There are no methods to call.
     * This code is shared by CSVDeDup.main
     *
     * @param deDup              Do we want the letters deDuped after they are sorted?
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     *                           tab.
     * @param columnsToProcess   Columns whose letters should be internallyy sorted within the field.
     * @param separatorChar      separator char to use for read/write.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding for input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     * @see CSVDeDup#main(String[])
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVSortField( final boolean deDup,
                         final File fileBeingProcessed,
                         final int[] columnsToProcess,
                         final char separatorChar,
                         final char quoteChar,
                         final char commentChar,
                         final Charset encoding ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar,
                quoteChar,
                commentChars,
                false
                /* hide comments */,
                true
                /* trimQuoted */,
                true
                /* trimUnquoted */,
                true
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        try
            {
            while ( true )
                {
                // process one line each time through the loop.
                final String[] fields = r.getAllFieldsInLine();
                final int fieldCount = r.wasComment() ? fields.length - 1 : fields.length;
                // convert selected columns to desired case.
                for ( final int columnToProcess : columnsToProcess )
                    {
                    if ( columnToProcess < fieldCount )
                        {
                        if ( deDup )
                            {
                            // sort and dedup
                            fields[ columnToProcess ] = ST.deDupLetters( fields[ columnToProcess ] );
                            }
                        else
                            {
                            // just sort
                            fields[ columnToProcess ] = ST.reorderLetters( fields[ columnToProcess ] );
                            }
                        }
                    }
                w.nl( fields, r.wasComment() );
                } // end while
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines processed." );
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        }

    /**
     * reorder the letters in a String in ascending order, case-sensitively, and then remove dups.
     * e.g. aCBBB" --> "BCa", "DAB" --> "ABD", " "Z X" --> "XZ", "   " --> ""
     *
     * @param field field to sort.
     *
     * @return field with letters composing it sorted in ascending order. Blanks squeezed out, but not dups.
     * @see #reorderLetters(String)
     * @see ST#sort(String)
     */
    public static String deDupLetters( String field )
        {
        final String squished = ST.squish( field );
        if ( squished.length() <= 1 )
            {
            return field;
            }
        else
            {
            final char[] ca = squished.toCharArray();
            Arrays.sort( ca );  // case-sensitive sort letter by letter
            // most of the time, it will already be deDuped, check just in case.
            int dups = 0;
            for ( int i = 1; i < ca.length; i++ )
                {
                if ( ca[ i ] == ca[ i - 1 ] )
                    {
                    dups++;
                    }
                }
            if ( dups == 0 )
                {
                // no dups found, we are done.
                return String.valueOf( ca );
                }
            // we have to tediously dedup but at least we know the precise size of the final result ahead of time.
            final char[] deDuped = new char[ ca.length - dups ];
            if ( ca.length > 0 )
                {
                // always copy first char if there is one.
                deDuped[ 0 ] = ca[ 0 ];
                int j = 1;
                for ( int i = 1; i < ca.length; i++ )
                    {
                    if ( ca[ i ] != ca[ i - 1 ] )
                        {
                        deDuped[ j++ ] = ca[ i ];
                        }
                    }
                }
            return String.valueOf( deDuped );
            }
        }

    /**
     * Simple command line interface to CSVSort, Sorts one csv file whose name is on the command line. Must have
     * extension .csv and sort cols of form:  0s+ 2i- 4n+ 5x+
     *
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
        final int[] columnsToSort = new int[ colCount ];
        for ( int i = 0; i < colCount; i++ )
            {
            // skip first parm
            columnsToSort[ i ] = Integer.parseInt( args[ i + 1 ] );
            }
        try
            {
            out.print( "CSVSortField " );
            // deDup, File, cols, types, directions,  separatorChar,  quoteChar,  commentChar
            new CSVSortField( false, file, columnsToSort, ',', '\"', '#', CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( " failed to sort " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
