/*
 * [CSV.java]
 *
 * Summary: Display some statistics about a CSV file.
 *
 * Copyright: (c) 2002-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2002-03-27 initial release
 *  1.1 2002-03-28 close
 *                 configurable separator char
 *                 no longer sensitive to line-ending convention.
 *                 uses a categorise routine to message categories for use in case clauses.
 *                 faster skipToNextLine
 *  1.2 2002-04-17 put in to separate package
 *  1.3 2002-04-17
 *  1.4 2002-04-19 fix bug if last field on line is empty, was not counting as a field.
 *  1.5 2002-04-19
 *  1.6 2002-05-25 allow choice of " or ' quote char.
 *  1.7 2002-08-29 getAllFieldsInLine
 *  1.8 2002-11-12 allow Microsoft Excel format fields that can span several lines. sponsored by Steve Hunter of
 *                 agilense.com
 *  1.9 2002-11-14 trim parameter to control whether fields are trimmed of lead/trail whitespace (blanks, Cr, Lf,
 *                 Tab etc.)
 *  2.0 2003-08-10 getInt, getLong, getFloat, getDouble
 *  2.1 2005-07-16 reorganisation, new bat files.
 *  2.2 2005-08-28 add CSVAlign and CSVPack to the suite.
 *  2.3 2005-08-28 add CSVAlign and CSVPack to the suite.
 *                 Use java com.mindprod.CSVAlign somefile.csv
 *  2.4 2007-05-20 add icon and PAD
 *  2.5 2007-11-27 tidy comments
 *  2.6 2008-02-20 IntelliJ inspector, spell corrections, tightening code.
 *  2.7 2008-05-28 add CSVTab2Comma.
 *  2.8 2008-06-04 add CSVWriter put for various primitives.
 *  2.9 2009-03-27 refactor using enums, support comments.
 *                 major rewrite. Now supports #-style
 *                 comments. More efficient RAM use. You can configure the
 *                 separator character, quote character and comment character.
 *                 You can read seeing or hiding the comments. The API was
 *                 changed to support comments.
 *  3.0 2009-06-15 lookup table to speed CSVReader
 *  3.1 2009-12-03 add CSVSort
 *  3.2 2010-02-23 add hex sort 9x+ option to CSVSort
 *  3.3 2010-11-14 change default to no comments in input file for CSVTab2Comma.
 *  3.4 2010-12-03 add CSV2SRS
 *  3.5 2010-12-11 add CSVReshape
 *  3.6 2010-12-14 add Lines2CSV
 *  3.7 2010-12-17 add CSVDeDup
 *  3.8 2010-12-31 add CSVRecode
 *  3.9 2011-01-22 add CSVTuple
 *  4.0 2011-01-23 add CSVToTable and TableToCSV
 *  4.1 2011-01-24 add CSVEntify and CSVStripEntities
 *  4.2 2011-01-25 modify all utilities so you can specify the encoding, default to UTF-8.
 *  4.3 2011-02-08 add support for sorting by field length. Add CSVCondense.
 *  4.4 2011-02-09 add getYYYYMMDD to CSVReader, improve error exceptions in CSVReader.
 *  4.5 2011-02-14 CSVToTable no longer entifies. Do separately with Entify, if needed. Add CSVTemplate.
 *  4.6 2011-02-16 rename from StripEntities to DeEntify. Add summary counts at the end of each utility.
 *  4.7 2011-02-17 add CSVChangeCase, CSVReshape now reorders the lead ## label comment.
 *  4.8 2011-02-17 CSVPack, CSVCondense, CSVTuple now tidy up ## label comments.
 *                 CSVReader has new method wasLabelComment to detect ## field labelling comments.
 *  4.9 2011-02-19 CSVAlign aligns ## comment.
 *  5.0 2011-02-21 add CSVWriter.setLineSeparator
 *  5.1 2011-02-24 fix bug, emitted """" for single field with quotelevel 2. reported by Dr. Jens Uwe Meyborn
 *  5.2 2011-02-25 new csvReader constructor parm trimUnquoted. Use Intellij to fill it in with true every place full
 *                 constructor used.
 *  5.3 2011-02-25 fix but when column comment had extra cols. left align date headers.
 *  5.4 2011-03-04 CSVToSRS: apply prelude and postlude to create a complete script that needs tweaking.
 *  5.5 2011-03-07 CSVTemplate: output file how has same extension as the template.
 *  5.6 2011-03-08 eliminate all but first duplicate in patch file. allow comments in patch file,
 *                 allow 2+ cols in patch file.
 *  5.7 2011-03-10 allow numeric sorts on empty columns.
 *  5.8 2011-03-11 CSVChangeCase fix bug in selecting the correct translation letter.
 *  5.9 2011-05-11 fix bug in multi-field reads in CSVReader
 *  6.0 2011-10-27 make CSV display stats. It used to be a dummy.
 *  6.1 2011-10-29 track comment lines and lines with tail comments separately.
 *  6.2 2011-10-30 fix fieldCount bug, which corrected bug with hideComments not working.
 *  6.3 2011-11-10 add CSVSortField and CSVDeDupField
 *  6.4 2012-01-25 redefine meaning of n option and add d option to CSVSort
 *  6.5 2012-02-19 DeDupStrategy -keepfirst -keeplast and -delete. f for family name sort option.
 *  6.6 2012-07-31 fix sort on surname van Pelt, Mc Mac
 *  6.7 2012-11-12 add CSVCommaToTab
 *  6.8 2013-01-30 CSVToTable now specify class to <tr as well
 *  6.9 2013-02-19 Change all encoding parms from string the Charset.
 *  7.0 2014-04-29 Three new utilities CSVReplaceStrings, CSVReplaceURLs and CSVtoUnique.
 *  7.1 2014-06-01 add CSVScale, encapsulate much of the IO with the EIO class.
 *  7.2 2014-08-05 add CSVToSRX for generating Funduc BE search/replace scripts.
 *  7.3 2014-08-16 add CSVStripTags for removing HTML tags.
 *  7.4 2014-09-14 CSVToTable now creates a *.htmlfrag file, and TableToCSV not accepts a *.htmlfrag
 *  7.5 2016-05-21 correct bug where CSVWrite sometimes failed to enclose fields containginng commas in quotes.
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.fastcat.FastCat;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.System.*;

/**
 * Display some statistics about a CSV file.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 7.5 2016-05-21 correct bug where CSVWrite sometimes failed to enclose fields containginng commas in quotes.
 * @since 2002-03-27
 */
public final class CSV
    {
    // todo: write CSVFill that lets you fill columns with a constant,
    // append or prefix data in a field.
    // search/replace in col? which allows remove lead or trail string.
    public static final Charset UTF8 = Charset.forName( "UTF8" );

    /**
     * encoding for iso-8859-1
     */
    public static final Charset ISO88591 = Charset.forName( "ISO-8859-1" );

    /**
     * encoding for UTF-16
     */
    public static final Charset UTF16 = Charset.forName( "UTF-16" );

    private static final int FIRST_COPYRIGHT_YEAR = 2002;

    /**
     * undisplayed copyright notice
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2002-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String RELEASE_DATE = "2016-05-21";

    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSV needs a single filename.csv on the command line.";

    /**
     * embedded version string.
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String VERSION_STRING = "7.5";

    /**
     * Dump CSV file.
     *
     * @param file          CSV file to be dumped
     * @param separatorChar field separator character, usually ',' in North America,
     *                      ';' in Europe and sometimes '\t' for
     *                      tab.
     * @param quoteChar     char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                      you don't want a quote character.
     * @param commentChars  characters that mark the start of a comment, usually "#", but can be multiple chars.
     *                      Note this is a "string" not a 'char'.
     * @param encoding      encoding of input and output.
     *
     * @throws java.io.IOException if problems reading file
     */
    private CSV( final File file, final char separatorChar, final char quoteChar, final String commentChars,
                 final Charset encoding ) throws IOException
        {
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( file, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars, false, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        int shortest = Integer.MAX_VALUE;
        int longest = 0;
        int commentLines = 0;
        int linesWithTailComments = 0;
        int shortestAtLine = 0;
        int longestAtLine = 0;
        try
            {
            while ( true )
                {
                // read one line:
                final String[] fields = r.getAllFieldsInLine();
                final int length = fields.length;
                if ( r.wasComment() )
                    {
                    // count comments in line and comment count, but not in field lengths
                    if ( length == 1 )
                        {
                        commentLines++;
                        }
                    else
                        {
                        linesWithTailComments++;
                        }
                    }
                else
                    {
                    if ( length < shortest )
                        {
                        shortest = length;
                        shortestAtLine = r.lineCount();
                        }
                    if ( length > longest )
                        {
                        longest = length;
                        longestAtLine = r.lineCount();
                        }
                    }
                }
            }
        catch ( EOFException e )
            {
            if ( shortest == Integer.MAX_VALUE )
                {
                shortest = 0;
                }
            final int lines = r.lineCount();
            final FastCat sb = new FastCat( 22 );
            sb.append( "         file: " );
            sb.append( EIO.getCanOrAbsPath( file ) );
            sb.append( "\n        bytes: " );
            sb.append( file.length() );
            sb.append( "\n        lines: " );
            sb.append( lines );
            sb.append( "\ncomment lines: " );
            sb.append( commentLines );
            sb.append( "\nlines with tail comments: " );
            sb.append( linesWithTailComments );
            if ( shortestAtLine == longestAtLine )
                {
                sb.append( "\nall lines have " + shortest + " field" );
                if ( shortest != 1 )
                    {
                    sb.append( "s." );
                    }
                else
                    {
                    sb.append( "." );
                    }
                }
            else
                {
                sb.append( "\nshortest line ", shortestAtLine, " with ", shortest, " field" );
                if ( shortest != 1 )
                    {
                    sb.append( "s." );
                    }
                else
                    {
                    sb.append( "." );
                    }
                sb.append( "\nlongest line ", longestAtLine, " with ", longest, " field" );
                if ( longest != 1 )
                    {
                    sb.append( "s." );
                    }
                else
                    {
                    sb.append( "." );
                    }
                }
            out.println( sb.toString() );
            r.close();
            }
        }

    /**
     * Simple command line interface to Dump. Dumps one csv file whose name is on the command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVDump somefile.csv
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
            new CSV( file, ',', '\"', "#", UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSV failed " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }
