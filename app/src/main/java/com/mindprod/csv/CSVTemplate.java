/*
 * [CSVTemplate.java]
 *
 * Summary: Expands a CSV file to boilerplate in a text file.
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
 *  1.0 2011-02-14 initial version.
 *  1.1 2011-03-07 output file how has same extension as the template.
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * Expands a CSV file to boilerplate in a text file.
 * <p/>
 * Use: java.exe com.mindprod.CSVToTable xxx.csv
 * Awkward characters will appear as Entities.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2011-03-07 output file how has same extension as the template.
 * @since 2011-02-14
 */
public final class CSVTemplate
    {
    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVTemplate needs the name of a CSV file on the commandline,"
                                        + "\nfollowed optionally by the name of a template.txt file."
                                        + "\nOutput will be in xxx.txt.";

    /**
     * find spots where replace boiler place with fields
     */
    private static final Pattern PERCENT = Pattern.compile( "%(\\p{Digit}+)" );

    /**
     * Constructor to Expand a CSV file to boilerplate in a text file.
     *
     * @param fileBeingProcessed CSV file containing data to insert into the template.
     * @param writeFile          Where the expanded templates are written.
     * @param template           The string include %n to select 0-based cols, possibly multiline.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab for the output file.  It is tab for the input file.
     *                           Note this is a 'char' not a "string".
     * @param quoteChar          character used to quote fields containing awkward chars.
     * @param commentChars       characters to treat as comments.
     * @param encoding           encoding of the input and output file.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVTemplate( final File fileBeingProcessed, final File writeFile, final String template, final char separatorChar,
                        final char quoteChar, final String commentChars, final Charset encoding ) throws IOException
        {
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars,
                true, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        final PrintWriter w = EIO.getPrintWriter( writeFile, 32 * 1024, encoding );
        try
            {
            while ( true )
                {
                final String[] fields = r.getAllFieldsInLine();
                // don't change to StringBuilder or FastCat
                final StringBuffer sb = new StringBuffer( template.length() * 2 );
                final Matcher m = PERCENT.matcher( template );  // Matchers are used both for matching and finding.
                while ( m.find() )
                    {
                    assert m.groupCount() == 1 : "bug in CSVTemplate regex";
                    final int insertFieldNumber = Integer.parseInt( m.group( 1 ) );
                    final String replacement = ( insertFieldNumber < fields.length ) ? fields[ insertFieldNumber ] : "";
                    // \ needs to be coded as \\
                    m.appendReplacement( sb,
                            Matcher.quoteReplacement( replacement ) ); // also appends junk between hits.
                    }
                // we have now processed the replacements.
                m.appendTail( sb );
                w.print( sb.toString() );
                }
            }
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines templated." );
            r.close();
            w.close();
            }
        }

    /**
     * Simple command line interface to CSVToTable. Converts one CSV file to an HTML table. Must have
     * extension .csv <br> Use java com.mindprod.CSVToTable somefile.csv.
     * You may optionally provide CSS classes for each column of the table. "" means no css class for that column.
     * You can use CSVToTable constructor in your own programs.
     *
     * @param args name of csv file to remove excess quotes and space
     *
     * @throws IOException if trouble reading the template.
     */
    public static void main( String[] args ) throws IOException
        {
        if ( args.length < 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String readFilename = args[ 0 ];
        if ( !readFilename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad read file extension\n" + USAGE );
            }
        final String templateFilename = args[ 1 ];
        if ( templateFilename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad template extension, .csv not allowed.\n" + USAGE );
            }
        final int place = templateFilename.indexOf( '.' );
        if ( place < 0 )
            {
            throw new IllegalArgumentException( "template needs an extension e.g. .html .htmlfrag .txt .css" );
            }
        final File templateFile = new File( templateFilename );
        final String templateContents = HunkIO.readEntireFile( templateFile, CSV.UTF8 );
        // output has same name as input and extension as the filename.
        final String writeFilename = readFilename.substring( 0,
                readFilename.length() - 4 ) + templateFilename.substring( place );
        final File readFile = new File( readFilename );
        final File writeFile = new File( writeFilename );
        try
            {
            // file,  separatorChar,  quoteChar,  commentChar
            new CSVTemplate( readFile,
                    writeFile,
                    templateContents,
                    ',',
                    '\"',
                    "#",
                    CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVToTemplate failed to export" + EIO.getCanOrAbsPath( readFile ) );
            err.println();
            }
        } // end main
    }
