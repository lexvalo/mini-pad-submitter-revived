/*
 * [CSVPack.java]
 *
 * Summary: pack a CSV File as densely as possible, removing excess space and commas.
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
 *  3.6 2011-01-25 allows you to specify the encoding
 *  3.7 2011-02-17 tidy up the label comment.
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
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
 * pack a CSV File as densely as possible, removing excess space and commas.
 * <p/>
 * Unlike CSVCondense, it does not remove blank lines.
 * <p/>
 * Use: java.exe com.mindprod.CSVPack somefile.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.7 2011-02-17 tidy up the label comment
 * @since 1998
 */
public final class CSVPack
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVPack needs a single filename.csv on the command line.";

    /**
     * used to split Label comment fields apart
     */
    private static final Pattern SPLIT_ON_COMMA = Pattern.compile( "\\s*,\\s*" );

    /**
     * pack a CSV file, constructor. Just create. There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding of input and output.
     *
     * @throws IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVPack( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                    final char commentChar, final Charset encoding ) throws IOException
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
                // read field by field, not line by line.
                String s = r.get();
                // null means end of line
                if ( r.wasComment() )
                    {
                    // was a comment
                    if ( r.wasLabelComment() )
                        {
                        s = rebuildLabelComment( s );
                        }
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
            out.println( r.lineCount() + " lines packed." );
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
    private static String rebuildLabelComment( String comment )
        {
        final String[] fieldnames = SPLIT_ON_COMMA.split( comment.substring( 1 ) );  // strip second #
        final FastCat sb = new FastCat( fieldnames.length * 2 + 2 );
        sb.append( comment.charAt( 0 ) ); // rebuild just second #
        sb.append( ' ' );
        for ( String fieldname : fieldnames )
            {
            sb.append( ST.condense( fieldname ) );
            sb.append( ", " );
            }
        sb.drop();
        return sb.toString();
        }

    /**
     * Simple command line interface to CSVPack. Packs one csv file whose name is on the command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVPack somefile.csv
     *
     * @param args name of csv file to remove excess quotes and space
     */
    public static void main( String[] args )
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
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVPack( file, ',', '\"', '#', CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVPack failed to pack " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
