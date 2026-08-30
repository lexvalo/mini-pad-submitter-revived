/*
 * [CSVCondense.java]
 *
 * Summary: Remove blank lines and condense multiple spaces to one within fields. Trim lead and trail blanks on fields.
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
 *  1.0 2011-02-08 initial version
 *  1.1 2011-02-17 tidy up the label comment
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
 * Remove blank lines and condense multiple spaces to one within fields. Trim lead and trail blanks on fields.
 * <p/>
 * Preserves comments.
 * In contrast CSVPack does not remove blank lines.
 * <p/>
 * Use: java.exe com.mindprod.CSVCondense somefile.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2011-02-17 tidy up the label comment
 * @since 2011-02-08
 */
public final class CSVCondense
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVCondense needs a single filename.csv on the command line.";

    /**
     * used to split Label comment fields apart
     */
    private static final Pattern SPLIT_ON_COMMA = Pattern.compile( "\\s*,\\s*" );

    /**
     * Condense a CSV file, constructor. Just create. There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding of input and output file.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVCondense( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                        final char commentChar, final Charset encoding ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar,
                quoteChar,
                commentChars,
                false
                /* hideComments */,
                true
                /* trimQuoted */,
                true
                /* trimUnquoted */,
                true
                /* multiline */
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        try
            {
            while ( true )
                {
                final String[] next = r.getAllFieldsInLine();
                if ( next.length != 0 )
                    {
                    final boolean wasLastFieldAComment = r.wasComment();
                    for ( int i = 0; i < next.length; i++ )
                        {
                        next[ i ] = ST.condense( next[ i ] ); // condense comment too.
                        }
                    if ( r.wasLabelComment() )
                        {
                        next[ next.length - 1 ] = rebuildLabelComment( next[ next.length - 1 ] );
                        }
                    w.nl( next, wasLastFieldAComment );
                    }
                }
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines condensed." );
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
            sb.append( "," );
            }
        sb.drop();
        return sb.toString();
        }

    /**
     * Simple command line interface to CSVCondense  Condenses csv file whose name is on the command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVCondense  somefile.csv
     *
     * @param args name of csv file to remove excess space and blank lines
     */
    public static void main( final String[] args )
        {
        if ( args.length < 1 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar, cols that can be different
            new CSVCondense( file, ',', '\"', '#', CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVCondense failed to condense " + EIO.getCanOrAbsPath( file ) );
            err.println();
            System.exit( 2 );
            }
        } // end main
    }
