/*
 * [CSVDeDup.java]
 *
 * Summary: DeDup CSV File: remove duplicate records.
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
 *  1.0 2010-12-17 initial version
 *  1.1 2011-01-25 allow you to specify encoding
 *  1.2 2012-02-19 DeDupStrategy -keepfirst -keeplast and -delete
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * DeDup CSV File: remove duplicate records.
 * <p/>
 * Use: java.exe com.mindprod.CSVDeDup somefile.csv 2 3
 * may specify 0-based columns of column to exclude when determining dups. These columns are allowed to be
 * different, and if the other columns match, subsequent matching records are discarded.
 * Comments are ignored when determining duplicates.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.2 2012-02-19 DeDupStrategy -keepfirst -keeplast and -delete
 * @since 2010
 */
public final class CSVDeDup
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVDeDup needs a single filename.csv [-keepfirst -keeplast -delete] then " +
                                        "exception columns. Don't confuse with com.mindprod.dedup.DeDup.";

    /**
     * DeDup a CSV file, constructor. Just create. There are no methods to call.
     * Dups must be sorted contiguously to be detected.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding of input and output file.
     * @param deDupStrategy      what do do with duplicates, DeDupStrategy.KEEP_FIRST, KEEP_LAST, DELETE
     * @param exceptionCols      columns that are allowed to be different, and still count as a duplicate. Cols to ignore
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVDeDup( final File fileBeingProcessed, final char separatorChar, final char quoteChar,
                     final char commentChar, final Charset encoding, final DeDupStrategy deDupStrategy,
                     final int... exceptionCols ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        final boolean[] exclude = new boolean[ 100 ];
        for ( int exceptionCol : exceptionCols )
            {
            exclude[ exceptionCol ] = true;
            }
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars, false, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        String[] prev = new String[ 0 ];
        boolean prevExists = false;
        boolean prevDifferent = true;
        boolean wasLastFieldAComment = false;
        int prevLength = 0;
        try
            {
            while ( true )
                {
                final String[] next = r.getAllFieldsInLine();
                wasLastFieldAComment = r.wasComment();
                final int nextLength = next.length - ( wasLastFieldAComment ? 1 : 0 );
                final int fieldCount = Math.min( nextLength, prevLength );
                boolean different = false;
                if ( wasLastFieldAComment && nextLength == 0 )
                    {
                    // always keep col 0 comments
                    different = true;
                    }
                else
                    {
                    triplefor:
                    {
                    for ( int i = 0; i < fieldCount; i++ )
                        {
                        if ( !exclude[ i ] && !next[ i ].equals( prev[ i ] ) )
                            {
                            different = true;
                            break triplefor;
                            }
                        }
                    // handle part of next that is longer than prev, if any
                    for ( int i = fieldCount; i < nextLength; i++ )
                        {
                        if ( !exclude[ i ] && next[ i ].length() != 0 )
                            {
                            different = true;
                            break triplefor;
                            }
                        }
                    // handle part of prev that is longer than next, if any
                    for ( int i = fieldCount; i < prevLength; i++ )
                        {
                        if ( !exclude[ i ] && prev[ i ].length() != 0 )
                            {
                            different = true;
                            break triplefor;
                            }
                        }
                    } //end triplefor
                    }
                if ( different )
                    {
                    switch ( deDupStrategy )
                        {
                        case KEEP_FIRST:
                            // can output it right away.
                            w.nl( next, wasLastFieldAComment );
                            break;
                        case KEEP_LAST:
                            if ( prevExists )
                                {
                                // output previous whether it was a dup or not.
                                w.nl( prev, wasLastFieldAComment );
                                }
                            break;
                        case DELETE:
                            if ( prevExists && prevDifferent )
                                {
                                w.nl( prev, wasLastFieldAComment );
                                }
                            break;
                        default:
                            throw new IllegalArgumentException( "Program bug. Bad deDupStrategy " + deDupStrategy );
                        }
                    prev = next;
                    prevExists = true;
                    prevLength = nextLength;
                    }
                else if ( deDupStrategy == DeDupStrategy.KEEP_LAST )
                    {
                    // keep last one in prev.
                    prev = next;
                    prevExists = true;
                    prevLength = nextLength;
                    }
                prevDifferent = different;
                }
            }
        catch ( EOFException e )
            {
            switch ( deDupStrategy )
                {
                case KEEP_FIRST:
                    break;
                case KEEP_LAST:
                    if ( prevExists )
                        {
                        w.nl( prev, wasLastFieldAComment );
                        }
                    break;
                case DELETE:
                    if ( prevExists && prevDifferent )
                        {
                        w.nl( prev, wasLastFieldAComment );
                        }
                    break;
                default:
                    throw new IllegalArgumentException( "Program bug. Bad deDupStrategy " + deDupStrategy );
                }
            out.println( r.lineCount() + " lines read, "
                         + w.getLineCount() + " lines written." );
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        }

    /**
     * Simple command line interface to CSVDeDup. Dedups one csv file whose name is on the command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVDeDup somefile.csv 2  3
     *
     * @param args name of csv file to remove excess quotes and space
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
        final DeDupStrategy deDupStrategy;
        final int firstColArg;
        if ( args.length > 1 && args[ 1 ].startsWith( "-" ) )
            {
            final String p = args[ 1 ];
            firstColArg = 2;
            if ( p.equalsIgnoreCase( "-keepfirst" ) )
                {
                deDupStrategy = DeDupStrategy.KEEP_FIRST;
                }
            else if ( p.equalsIgnoreCase( "-keeplast" ) )
                {
                deDupStrategy = DeDupStrategy.KEEP_LAST;
                }
            else if ( p.equalsIgnoreCase( "-delete" ) )
                {
                deDupStrategy = DeDupStrategy.DELETE;
                }
            else
                {
                throw new IllegalArgumentException( "Invalid DeDupStrategy\n" + USAGE );
                }
            }
        else
            {
            firstColArg = 1;
            deDupStrategy = DeDupStrategy.KEEP_FIRST;
            }
        final int[] exceptionCols = new int[ args.length - firstColArg ];
        int j = 0;
        for ( int i = firstColArg; i < args.length; i++ )
            {
            exceptionCols[ j++ ] = Integer.parseInt( args[ i ] );
            }
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar, cols that can be different
            new CSVDeDup( file, ',', '\"', '#', CSV.UTF8, deDupStrategy, exceptionCols );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVDeDup failed to dedup " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
