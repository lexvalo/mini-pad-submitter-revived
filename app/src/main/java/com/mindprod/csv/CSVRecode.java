/*
 * [CSVRecode.java]
 *
 * Summary: Recode a column of a CSV file with a list of from:to pairs.
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
 *  1.0 2010-12-30 initial version
 *  1.1 2011-01-25 allow you to specify encoding
 *  1.2 2011-03-08 eliminate all but first duplicate in patch file. allow comments in patch file,
 *                 allow 2+ cols in patch file.
 *  1.3 2011-10-13 fix bug, would get two records if patched two fields.
 *  1.4 2011-10-16 ignore trailing / when matching
 *  1.5 2016-05-05 rename from CSVPatch to CSVRecode
 *  1.6 2016-07-01 add -miss parameter
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
import java.util.HashMap;

import static java.lang.System.*;

/**
 * Recode a column of a CSV file with a list of from:to pairs.
 * <p/>
 * Use: java.exe com.mindprod.CSVRecode somefile.csv  fromto.csv  0 3  (columns to patch)
 * Typically used to update URLs in a column with recently discovered replacements.
 * Both columns and the files must use a consistent &amp; and &amp;amp; plain/entity  convention for &amp; in URLs if
 * you are replacing URLs in HTML text.
 * In contrast CSVReplaceURLs modifies HTML text.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.6 2016-07-01 add -miss parameter
 * @see com.mindprod.csv.CSVReplaceURLs
 * @since 2010-12-30
 */
public final class CSVRecode
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVRecode needs filename_to_patch.csv file_of_from_to_pairs.csv  [-miss xx] 0 3 ... " +
                                        "(0-based cols to patch)";

    private static boolean DEBUGGING = false;

    /**
     * patch a CSV fileToPatch, constructor. Just create. There are no methods to call.
     *
     * @param fileBeingProcessed CSV fileToPatch to be packed to remove excess space and quotes.
     * @param fileOfPairs        CSV file of pairs from,to (extra cols ignored)
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding of input and output.
     * @param miss               value to use when there is no match.  null means leave as is.
     * @param colsToPatch        list of columns that should be patched, 0-based.
     *
     * @throws java.io.IOException if problems reading/writing fileToPatch
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVRecode( final File fileBeingProcessed, final File fileOfPairs, final char separatorChar,
                      final char quoteChar, final char commentChar, final Charset encoding,
                      final String miss, final int... colsToPatch ) throws IOException
        {
        if ( DEBUGGING )
            {
            out.println( "miss: " + miss );
            }
        // hold translate from:to pairs in RAM
        final HashMap<String, String> translator = new HashMap<>( 2048 );
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader inPairs = new CSVReader( EIO.getBufferedReader( fileOfPairs, 64 * 1024, encoding ),
                separatorChar,
                quoteChar,
                commentChars,
                true
                /* hide comments */,
                true
                /* trimQuoted */,
                true
                /* trimUnquoted */,
                true
                /* allow multi-line */
        );
        int pairCount;
        try
            {
            while ( true )
                {
                String from = inPairs.get();
                // ignore blank line
                if ( from == null )
                    {
                    continue;
                    }
                // we match ignoring trailing /
                from = ST.trimTrailing( from, '/' );
                final String to = inPairs.get();
                if ( to == null || from.length() == 0 )
                    {
                    throw new IllegalArgumentException( "pairs file " + fileOfPairs.toString() + " must contain at " +
                                                        "least two fields (from,to) on each line" );
                    }
                inPairs.skipToNextLine();
                final String old = translator.put( from, to );
                if ( old != null )
                    {
                    err.println( "duplicate pair " + from + " -> " + old + " and " + from + " -> " + to );
                    }
                }
            }
        catch ( EOFException e )
            {
            pairCount = inPairs.lineCount();
            inPairs.close();
            }
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar,
                quoteChar,
                commentChars,
                false
                /* process comments */,
                true
                /* trimQuoted */,
                true
                /* trimUnquoted */,
                true
                /* allow multi-line */
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 64 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw,
                0
                /* minimal  */,
                separatorChar,
                quoteChar,
                commentChar,
                true
                /* trim */ );
        int fieldsPatched = 0;
        try
            {
            while ( true )
                {
                final String[] fields = r.getAllFieldsInLine();
                final boolean lastFieldWasComment = r.wasComment();
                // patch each of the columns requested.
                for ( int col : colsToPatch )
                    {
                    if ( col < fields.length )
                        {
                        // we insist on exact match, except for trailing /
                        final String from = ST.trimTrailing( fields[ col ], '/' );
                        // currently insists that xxx.html, ?xxx, #xxxx also match.
                        // todo: should not insist on match.
                        // should keep tail if new has none.
                        // should replace tail if new has tail.
                        // match based
                        final String newValue = translator.get( from );
                        if ( newValue != null )
                            {
                            fields[ col ] = newValue;
                            fieldsPatched++;
                            }
                        else if ( miss != null )
                            {
                            // not in lookup table.
                            fields[ col ] = miss;
                            fieldsPatched++;
                            }
                        // else leave old value alone
                        //  not in lookup table.
                        // most of the time we make no change. The value won't be in the HashMap.
                        }
                    }
                w.nl( fields, lastFieldWasComment );
                }
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines read, " + pairCount + " pairs read, " +
                         fieldsPatched + " fields patched, " + w.getLineCount() + " lines written." );
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        } // /method

    /**
     * Simple command line interface to CSVRecode. Patches one csv file whose name is on the command line with a list
     * of from to pairs in another csv file.  You specify which columns to translate from to to.
     * Both files must have the extension .csv
     *
     * @param args name of csv file to patch, from-to pairs file, columns to patch.
     */
    public static void main( String[] args )
        {
        if ( args.length < 3 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String fileToPatchName = args[ 0 ];
        if ( !fileToPatchName.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad filenametopatch Extension\n" + USAGE );
            }
        final String fileOfPatchesName = args[ 1 ];
        if ( !fileOfPatchesName.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad fromtopairs Extension\n" + USAGE );
            }
        // filename_to_patch.csv file_of_from_to_pairs.csv  [-miss xx] 0 3 (0-based cols to patch)";
        String miss = null;
        int bypass = 2;
        if ( args.length < 3 )
            {
            throw new IllegalArgumentException( "Not enough parameters\n" + USAGE );
            }
        if ( args[ 2 ].equals( "-miss" ) )
            {
            if ( args.length < 4 )
                {
                throw new IllegalArgumentException( "Not enough parameters\n" + USAGE );
                }
            miss = args[ 3 ];
            bypass = 4;
            }
        final int[] colsToPatch = new int[ args.length - bypass ];
        try
            {
            for ( int i = bypass; i < args.length; i++ )
                {
                // we are copying and converting to int.
                colsToPatch[ i - bypass ] = Integer.parseInt( args[ i ] );
                }
            }
        catch ( NumberFormatException e )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final File fileToPatch = new File( fileToPatchName );
        final File fileOfPatches = new File( fileOfPatchesName );
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVRecode( fileToPatch, fileOfPatches, ',', '\"', '#', CSV.UTF8, miss, colsToPatch );
            }
        catch ( IOException e )
            {
            err.println();
            err.println( "IO problem: " + e.getMessage() );
            e.printStackTrace( err );
            err.println( "CSVRecode failed to patch " + EIO.getCanOrAbsPath( fileToPatch ) );
            if ( !fileToPatch.canRead() )
                {
                err.println( "Cannot read fileToPatch: " + fileToPatch.getAbsolutePath() );
                }
            if ( !fileToPatch.canWrite() )
                {
                // canWrite true implies file exists.
                err.println( "Cannot write fileToPatch:" + fileToPatch.getAbsolutePath() );
                }
            if ( !fileOfPatches.canRead() )
                {
                err.println( "Cannot read fileOfPatches: " + fileOfPatches.getAbsolutePath() );
                }
            err.println();
            }
        } // /method
    }
