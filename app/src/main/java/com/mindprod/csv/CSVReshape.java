/*
 * [CSVReshape.java]
 *
 * Summary: reshape a CSV file reordering, duplicating or removing fields.
 *
 * Copyright: (c) 2010-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  3.5 2010-12-11 add CSVReshape initial version
 *  3.6 2011-01-25 allow you to specify encoding
 *  3.7 2011-02-17 now reorders the lead ## label comment, and leaves embedded col0 comments untouched.
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.fastcat.FastCat;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * reshape a CSV file reordering, duplicating or removing fields.
 * <p/>
 * Use: java.exe com.mindprod.CSVReshape somefile.csv   0 3 1 1
 * (0-based column numbers desired in output, if column in out past the end, will be empty)
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.7 2011-02-17 now reorders the lead ## label comment, and leaves embedded col0 comments untouched.
 * @since 2010-12-11
 */
public final class CSVReshape
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVReshape needs a single filename.csv on the command line followed by " +
                                        "0-based numeric columns desired.";

    /**
     * used to split Label comment fields apart
     */
    private static final Pattern SPLIT_ON_COMMA = Pattern.compile( "\\s*,\\s*" );

    /**
     * Reshape a CSV file, constructor. Just create the CSVReshape object.. There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding of input and output.
     * @param colsWanted         list of columns wanted in the output file in order.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVReshape( final File fileBeingProcessed,
                       final char separatorChar,
                       final char quoteChar,
                       final char commentChar,
                       final Charset encoding,
                       final int... colsWanted ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars, false, true /* trimQuoted */, true /* trimUnquoted */, true
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
                final int fieldCount;
                final String comment;
                if ( r.wasComment() )
                    {
                    // don't count the comment as a field
                    fieldCount = fields.length - 1;
                    // col 0 or tail comment.
                    // comment was last, and possibly only field on line.
                    if ( r.wasLabelComment() )
                        {
                        // reorder  ## label comment, usually first in file.
                        comment = rebuildLabelComment( fields[ fieldCount ], colsWanted );
                        }
                    else
                        {
                        comment = fields[ fieldCount ];
                        }
                    }
                else
                    {
                    fieldCount = fields.length;
                    comment = null;
                    }
                // output the the fields in desired order.
                for ( int source : colsWanted )
                    {
                    if ( source >= fieldCount )
                        {
                        if ( fieldCount > 0 )
                            {
                            // don't bother with dummy fields for an empty or pure comment line.
                            w.put( "" );  // request was off past the end.
                            }
                        }
                    else
                        {
                        w.put( fields[ source ] );  // was actual data field available.
                        }
                    }
                // fields copied, now tack on comment, no matter what column it is r.
                if ( comment != null )
                    {
                    w.nl( comment );
                    }
                else
                    {
                    w.nl();
                    }
                } // end while
            }
        catch ( EOFException
                e )
            {
            out.println( r.lineCount() + " lines reshaped." );
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        }

    /**
     * comment has two lead comment chars, one stripped off, and are used to label the fields.
     * Reshape it so it labels the new fields.
     *
     * @param comment field names separated by commas, with lead comment char.
     *
     * @return reordered comment
     */
    private static String rebuildLabelComment( String comment, int[] colsWanted )
        {
        final String[] fieldnames = SPLIT_ON_COMMA.split( comment.substring( 1 ) );  // strip second #
        final int labelledFieldnameCount = fieldnames.length;
        final FastCat sb = new FastCat( colsWanted.length * 2 + 2 );
        sb.append( comment.charAt( 0 ) ); // rebuild just second #
        sb.append( ' ' );
        for ( int source : colsWanted )
            {
            if ( source < labelledFieldnameCount )
                {
                sb.append( fieldnames[ source ].trim() );
                sb.append( ", " );
                }
            // don't put dummy field for unlabelled tail fields.
            }
        sb.drop();
        return sb.toString();
        }

    /**
     * Simple command line interface to Reshape. Reshapes one csv file whose name is on the command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVReshape somefile.csv 0 1 2 3 ...
     * Output replaces input. If you want the input, make a copy first.
     *
     * @param args name of csv file to reshape, followed by zero-based cols wanted in desired order.
     */
    public static void main( final String[] args )
        {
        if ( args.length < 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        final int[] colsWanted = new int[ args.length - 1 ];
        try
            {
            for ( int i = 1; i < args.length; i++ )
                {
                colsWanted[ i - 1 ] = Integer.parseInt( args[ i ] );
                }
            }
        catch ( NumberFormatException e )
            {
            throw new IllegalArgumentException( USAGE );
            }
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVReshape( file, ',', '\"', '#', CSV.UTF8, colsWanted );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVReshape failed to reshape " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
