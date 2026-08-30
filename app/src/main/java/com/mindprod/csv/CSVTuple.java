/*
 * [CSVTuple.java]
 *
 * Summary: Break up a long CSV line into several shorter ones.
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
 *  1.0 2011-01-22 initial version
 *  1.1 2011-01-24 allow you to specify encoding
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
 * Break up a long CSV line into several shorter ones.
 * <p/>
 * Use: java.exe com.mindprod.CSVTuple somefile.csv   4
 * Number is how many fields you want in each output line.
 * Will lake a file like this:
 * a,b,c,d,e,f
 * and produce
 * a,b,c,d
 * a,b,c,e
 * a,b,c,f
 * Short lines are padded out to tuple fields with empty fields.
 * x,y
 * becomes
 * x,y,,,
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2010-12-24 allow you to specify encoding
 * @since 2011-01-22
 */
public final class CSVTuple
    {
    /**
     * usage message
     */
    private static final String USAGE = "\nCSVTuple needs a single filename.csv on the command line followed by the tuple" +
                                        " length.";

    /**
     * used to split Label comment fields apart
     */
    private static final Pattern SPLIT_ON_COMMA = Pattern.compile( "\\s*,\\s*" );

    /**
     * break a CSV file into tuples, constructor. Just create the object. There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param tupleLength        how many fields you want in each output record.
     * @param encoding           encoding of input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVTuple( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                     final char commentChar, final int tupleLength, final Charset encoding ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars,
                false, true /* trimQuoted */, true /* trimUnquoted */, true
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
                    fieldCount = fields.length - 1; // don't count the comment as a field
                    // comment was last, and possibly only field on line.
                    if ( r.wasLabelComment() )
                        {
                        comment = rebuildLabelComment( fields[ fieldCount ], tupleLength );
                        }
                    else
                        {
                        // just ordinary tail comment.
                        comment = fields[ fieldCount ];
                        }
                    }
                else
                    {
                    fieldCount = fields.length;
                    comment = null;
                    }
                final int recsToOutput = fieldCount - tupleLength;
                if ( recsToOutput <= 1 )
                    {
                    // output one short line padded.
                    for ( int i = 0; i < fieldCount; i++ )
                        {
                        w.put( fields[ i ] );
                        }
                    for ( int i = fieldCount; i < tupleLength; i++ )
                        {
                        w.put( "" );
                        }
                    if ( comment != null )
                        {
                        w.nl( comment );
                        }
                    else
                        {
                        w.nl();
                        }
                    }
                else
                    {
                    // long line broken into shorter ones of length tupleLength
                    for ( int i = tupleLength - 1; i < fieldCount; i++ )
                        {
                        for ( int j = 0; j < tupleLength - 1; j++ )
                            {
                            //repeat the first tupleLength -1 fields.
                            w.put( fields[ j ] );
                            }
                        // tack on a variant
                        w.put( fields[ i ] );
                        if ( comment != null )
                            {
                            w.nl( comment );
                            }
                        else
                            {
                            w.nl();
                            }
                        }
                    }
                } // end while
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " csv lines read, "
                         + w.getLineCount() + " csv tuple lines written." );
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
    private static String rebuildLabelComment( String comment, int tupleLength )
        {
        final String[] fieldnames = SPLIT_ON_COMMA.split( comment.substring( 1 ) );  // strip second #
        final FastCat sb = new FastCat( tupleLength * 2 + 2 );
        sb.append( comment.charAt( 0 ) ); // rebuild just second #
        sb.append( ' ' );
        for ( int i = 0; i < tupleLength; i++ )
            {
            sb.append( fieldnames[ i ].trim() );
            sb.append( ", " );
            }
        sb.drop();
        return sb.toString();
        }

    /**
     * Simple command line interface to CSVTuple to brake a csv file into fixed length tuples.
     * Name of file appears on the command line
     * followed by the desired length in fields of the fixed length tuples. Must have
     * extension .csv <br> Use java com.mindprod.CSVTuple somefile.csv 4
     * Output replaces input. If you want the input, make a copy first.
     *
     * @param args name of csv file r followed the desired length in fields of the fixed length tuples
     */
    public static void main( final String[] args )
        {
        if ( args.length != 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        final int tupleLength;
        try
            {
            tupleLength = Integer.parseInt( args[ 1 ] );
            }
        catch ( NumberFormatException e )
            {
            throw new IllegalArgumentException( USAGE );
            }
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVTuple( file, ',', '\"', '#', tupleLength, CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVTuple failed to break file into tuples " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        }
    }
