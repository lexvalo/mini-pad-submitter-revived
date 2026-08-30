/*
 * [CSVToUnique.java]
 *
 * Summary: Simple Diff utility for CSV files.
 *
 * Copyright: (c) 2014-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2014-04-28 original version
 *  1.1 2016-06-28 get rid of compiler warning about mixing arrays and generics.
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.common18.FNV1a64;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Simple Diff utility for CSV files.
 * <p/>
 * Use: java.exe com.mindprod.CSVToUnique first.csv second.csv ...
 * <p/>
 * You feed it a list of CSV files, and it generates files containing the records
 * that are unique to each file and also common to all the files.  The
 * original files are unchanged.  Even if inputs have duplicates, the outputs
 * will not.  If used with only one file, will remove duplicates without
 * sorting. The records will be the same original order.
 * The records in the common_all.csv file appear is all of the
 * input files not just any two of them.  If you renamed formated
 * java files as *.csv files
 * mostly with lines with with a single field, (plus fiddling to make then
 * conforming csv files) it will compare two programs, showing you which
 * lines they have in common and which are unique even if the lines are in a
 * completely different order.  Ditto for a word processing essay or text
 * files, where you want to merge two similar documents.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2016-06-28 get rid of compiler warning about mixing arrays and generics.
 * @since 2014-04-28
 */
public final class CSVToUnique
    {
    /**
     * how much space to allocate each HashSet.
     * Should be average number of lines per file + 20%
     */
    private static final int ALLOC = 1000;

    /**
     * how to use the command line
     */
    private static final String USAGE = "\nUsage: csvtounique.jar" +
                                        " first.csv second.csv .... results in unique_first.csv unique_second.csv common_all.csv";

    /**
     * used to track which files contain which records.
     * Can't properly mix generics and array.  We need an Array of HashSets.
     */
    // arrays and generic do not mik
    //  private final HashSet<Long>[] hashSets;
    private final ArrayList<HashSet<Long>> hashSets;

    /**
     * use to track which records have been emitted recently
     */
    private final HashSet<Long> recently;

    /**
     * @param filesToProcess array of csv files to compare
     * @param separatorChar  field separator character, usually ',' in North America,
     *                       ';' in Europe and sometimes '\t' for
     *                       tab.
     * @param quoteChar      char to use to enclose fields containing a separator,
     *                       usually '\"'. Use (char)0 if
     *                       you don't want a quote character.
     * @param commentChar    char to use to introduce comments.  Use (char) 0 if none.  Only one character
     *                       allowed.
     * @param encoding       encoding for input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVToUnique( final File[] filesToProcess,
                        final char separatorChar,
                        final char quoteChar,
                        final char commentChar,
                        final Charset encoding
    ) throws IOException
        {
        // actually a <Long>[]
        hashSets = new ArrayList<>( filesToProcess.length );
        // allocat hashsets to track what is in the files, one per file.
        for ( int i = 0; i < filesToProcess.length; i++ )
            {
            hashSets.add( new HashSet<>( ALLOC ) );
            }
        recently = new HashSet<>( ALLOC );
        buildHashSummaries( filesToProcess, separatorChar, quoteChar, commentChar, encoding );
        writeToUniqueFiles( filesToProcess, separatorChar, quoteChar, commentChar, encoding );
        writeCommonFile( filesToProcess, separatorChar, quoteChar, commentChar, encoding );
        } // /method

    /**
     * @param filesToProcess array of csv files to compare
     * @param separatorChar  field separator character, usually ',' in North America,
     *                       ';' in Europe and sometimes '\t' for
     *                       tab.
     * @param quoteChar      char to use to enclose fields containing a separator,
     *                       usually '\"'. Use (char)0 if
     *                       you don't want a quote character.
     * @param commentChar    char to use to introduce comments.  Use (char) 0 if none.  Only one character
     *                       allowed.
     * @param encoding       encoding for input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    private void buildHashSummaries( final File[] filesToProcess,
                                     final char separatorChar,
                                     final char quoteChar,
                                     final char commentChar,
                                     final Charset encoding
    ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // scan files and compute digests of each line to help us latere detecet duplicates.
        for ( int i = 0; i < filesToProcess.length; i++ )
            {
            final File fileBeingProcessed = filesToProcess[ i ];
            final HashSet<Long> us = hashSets.get( i );
            CSVReader r = null;
            try
                {
                r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                        separatorChar, quoteChar, commentChars, true, true /* trimQuoted */, true /* trimUnquoted */, true
                );
                }
            catch ( FileNotFoundException e )
                {
                err.println( "Cannot find file " + fileBeingProcessed.getAbsolutePath() );
                System.exit( 1 );
                }
            try
                {
                while ( true )
                    {
                    // process one line each time through the loop.
                    final String[] fields = r.getAllFieldsInLine();
                    long hash = FNV1a64.computeHash( fields );
                    us.add( hash );
                    } // end inner   while
                }
            catch ( EOFException e )
                {
                r.close();
                }
            } // end outer for
        } // /method

    /**
     * @param filesToProcess array of csv files to compare
     * @param separatorChar  field separator character, usually ',' in North America,
     *                       ';' in Europe and sometimes '\t' for
     *                       tab.
     * @param quoteChar      char to use to enclose fields containing a separator,
     *                       usually '\"'. Use (char)0 if
     *                       you don't want a quote character.
     * @param commentChar    char to use to introduce comments.  Use (char) 0 if none.  Only one character
     *                       allowed.
     * @param encoding       encoding for input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    private void writeCommonFile( final File[] filesToProcess,
                                  final char separatorChar,
                                  final char quoteChar,
                                  final char commentChar,
                                  final Charset encoding
    ) throws IOException
        {
        // read files again, this time looking up hashes to detect dups.
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // this time we put all results in one file.
        final File first = filesToProcess[ 0 ];
        final String nameWithExt = first.getName();
        final String absName = first.getAbsolutePath();
        final String commonName = absName.substring( 0, absName.length() - nameWithExt.length() )
                                  + "common_all.csv";
        final PrintWriter pw = EIO.getPrintWriter( new File( commonName ), 32 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
        recently.clear();  // put all files is same basket.
        for ( int i = 0; i < filesToProcess.length; i++ )
            {
            File fileBeingProcessed = filesToProcess[ i ];
            HashSet<Long> us = hashSets.get( i );
            CSVReader r = null;
            try
                {
                r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                        separatorChar, quoteChar, commentChars, true, true /* trimQuoted */, true /* trimUnquoted */, true
                );
                }
            catch ( FileNotFoundException e )
                {
                err.println( "Cannot find file " + fileBeingProcessed.getAbsolutePath() );
                System.exit( 1 );
                }
            try
                {
                while ( true )
                    {
                    // process one line each time through the loop.
                    final String[] fields = r.getAllFieldsInLine();
                    boolean lastFieldAComment = r.wasComment();
                    long hash = FNV1a64.computeHash( fields );
                    // look up hash in HashMaps for other files, but not this file
                    boolean inAll = true;
                    for ( HashSet<Long> h : hashSets )
                        {
                        if ( h != us && !h.contains( hash ) )
                            {
                            inAll = false;
                            break;
                            }
                        }
                    if ( inAll && !recently.contains( hash ) )
                        {
                        // this line is the first of possible dups in its own filen.
                        recently.add( hash );
                        // only we have it, include it.
                        w.nl( fields, lastFieldAComment );
                        }
                    } // end inner while
                }
            catch ( EOFException e )
                {
                r.close();
                }
            } // end outer for
        w.close();
        } // /method

    /**
     * @param filesToProcess array of csv files to compare
     * @param separatorChar  field separator character, usually ',' in North America,
     *                       ';' in Europe and sometimes '\t' for
     *                       tab.
     * @param quoteChar      char to use to enclose fields containing a separator,
     *                       usually '\"'. Use (char)0 if
     *                       you don't want a quote character.
     * @param commentChar    char to use to introduce comments.  Use (char) 0 if none.  Only one character
     *                       allowed.
     * @param encoding       encoding for input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    private void writeToUniqueFiles( final File[] filesToProcess,
                                     final char separatorChar,
                                     final char quoteChar,
                                     final char commentChar,
                                     final Charset encoding
    ) throws IOException
        {
        // read files again, this time looking up hashes to detect dups.
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        for ( int i = 0; i < filesToProcess.length; i++ )
            {
            final File fileBeingProcessed = filesToProcess[ i ];
            final HashSet<Long> us = hashSets.get( i );
            CSVReader r = null;
            try
                {
                r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                        separatorChar, quoteChar, commentChars, true, true /* trimQuoted */, true /* trimUnquoted */, true
                );
                }
            catch ( FileNotFoundException e )
                {
                err.println( "Cannot find file " + fileBeingProcessed.getAbsolutePath() );
                System.exit( 1 );
                }
            final String nameWithExt = fileBeingProcessed.getName();
            final String absName = fileBeingProcessed.getAbsolutePath();
            // convert e:/abc/def.csv --> E:/abc/unique_def.csv
            final String uniqueToName = absName.substring( 0, absName.length() - nameWithExt.length() )
                                        + "unique_" + nameWithExt;
            final PrintWriter pw = EIO.getPrintWriter( new File( uniqueToName ), 32 * 1024, encoding );
            final CSVWriter w = new CSVWriter( pw, 0 /* minimal  */, separatorChar, quoteChar, commentChar, true );
            recently.clear();
            try
                {
                while ( true )
                    {
                    // process one line each time through the loop.
                    final String[] fields = r.getAllFieldsInLine();
                    boolean lastFieldAComment = r.wasComment();
                    long hash = FNV1a64.computeHash( fields );
                    // look up hash in HashMaps for other files, but not this file
                    boolean unique = true;
                    for ( HashSet<Long> h : hashSets )
                        {
                        if ( h != us && h.contains( hash ) )
                            {
                            unique = false;
                            break;
                            }
                        }
                    if ( unique && !recently.contains( hash ) )
                        {
                        // this line is the first of possible dups in its own filen.
                        recently.add( hash );
                        // only we have it, include it.
                        w.nl( fields, lastFieldAComment );
                        }
                    } // end inner while
                }
            catch ( EOFException e )
                {
                r.close();
                w.close();
                }
            } // end outer for
        } // /method

    /**
     * Simple command line interface to CSVChangeCase.  Changes case of columns of one csv file whose name is on the
     * command line. Must have
     * extension .csv and select cols to change case on with form:  0u 2l 4t.
     *
     * @param args name of csv file to change case on
     */
    public static void main( String[] args )
        {
        if ( args.length < 1 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        File[] files = new File[ args.length ];
        for ( int i = 0; i < args.length; i++ )
            {
            String filename = args[ i ];
            if ( !filename.endsWith( ".csv" ) )
                {
                throw new IllegalArgumentException(
                        "Bad Extension\n" + USAGE );
                }
            else
                {
                files[ i ] = new File( filename );
                }
            }
        try
            {
            new CSVToUnique( files,
                    ',',
                    '\"',
                    '#',
                    CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVToUnique failed" );
            err.println();
            }
        } // /method
    } // end class
