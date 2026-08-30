/*
 * [CSVAlign.java]
 *
 * Summary: align a CSV File into columns.
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
 *  3.6 2011-01-13 add -v verbose option
 *  3.7 2011-01-19 also align the ## comment.
 *  3.8 2011-02-25 fix but when column comment had extra cols. left align date headers.
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
import java.util.ArrayList;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * align a CSV File into columns.
 * <p/>
 * Use: java.exe com.mindprod.CSVAlign somefile.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.8 2011-02-25 fix but when column comment had extra cols. left align date headers.
 * @since 1998
 */
public final class CSVAlign
    {
    /**
     * always use windows line separator on any platform since csv is a windows format file.
     */
    private static final String lineSeparator = "\r\n";

    /**
     * how to use the command line
     */
    private static final String USAGE = "\nCSVAlign needs a single filename.csv on the command line with possible -v " +
                                        "switch.";

    /**
     * used to split Label comment fields apart
     */
    private static final Pattern SPLIT_ON_COMMA = Pattern.compile( "\\s*,\\s*" );

    /**
     * ColumnDescriptors for each column as an ArrayList for pass1
     */
    private ArrayList<ColumnDescriptor> colDescriptorList;

    /**
     * ColumnDescriptors for each column as an array for pass2
     */
    private ColumnDescriptor[] colDescriptors;

    /**
     * where we accumulate an entire aligned line.  Cannot be made local.
     */
    private FastCat sb;

    /**
     * The width of an entire aligned line, less the line separator.
     */
    private int alignedLineWidth;

    /**
     * how many pending leading spaces are outstanding.
     * -1 = nothing outstanding.
     * 0 = comma
     * 1 = comma then one space.
     * m.
     */
    private int pending = -1;

    /**
     * align a CSV file, constructor. Just create. There are no methods to call.
     *
     * @param file        CSV file to be aligned in columns.
     * @param separator   field separator character, usually ',' in North America, ';' in Europe and sometimes '\t' for
     *                    tab.
     * @param quote       char to use to enclose fields containing a separator, usually '\"' . Use (char)0 if
     *                    you don't want a quote character.
     * @param commentChar char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param verbose     true if want extra status listing.
     * @param encoding    encoding of input and output.
     *
     * @throws IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVAlign( final File file, final char separator, final char quote, final char commentChar,
                     final boolean verbose, final Charset encoding ) throws IOException
        {
        // read file to get max widths
        pass1( file, separator, quote, commentChar, verbose, encoding );
        // calculate the aggregate width of all the aligned fields.
        calcStartCols();
        // read file again this time writing, widening fields to align.
        pass2( file, separator, quote, commentChar, encoding );
        }

    /**
     * prepare an awkward string for output by enclosing it in " and doubling internal "
     *
     * @param s     output field to prepare
     * @param quote char to use to enclose fields containing a separator, usually '\"'
     *
     * @return string enclosed in quotes with internal quotes doubled.
     */
    private static String prepareAwkward( String s, char quote )
        {
        if ( s.indexOf( quote ) < 0 )
            {
            return quote + s + quote;
            }
        else
            {
            // StringBuilder is better than FastCat for char by char work.
            StringBuilder sb = new StringBuilder( s.length() + 10 );
            sb.append( quote );
            for ( int i = 0, n = s.length(); i < n; i++ )
                {
                char c = s.charAt( i );
                if ( c == quote )
                    {
                    sb.append( quote );
                    sb.append( quote );
                    }
                else
                    {
                    sb.append( c );
                    }
                } // end for
            sb.append( quote );
            return sb.toString();
            } // end else
        }

    /**
     * calculate the with of the line after we have aligned it.
     */
    private void calcStartCols()
        {
        int size = colDescriptorList.size();
        colDescriptors = colDescriptorList.toArray( new ColumnDescriptor[ size ] );
        colDescriptorList = null;
        alignedLineWidth = 0;
        for ( ColumnDescriptor cd : colDescriptors )
            {
            cd.startCol = alignedLineWidth;
            /* allow one for comma and one for space */
            alignedLineWidth += cd.maxWidth + 2;
            }
        }

    /**
     * append a field to the wholeLine left justified
     *
     * @param field       field to add, with awkward encoding in place if any.
     * @param columnWidth width of the column
     */
    private void leftJustify( String field, int columnWidth )
        {
        if ( pending >= 0 )
            {
            sb.append( ',' );
            sb.append( ST.spaces( pending ) );
            }
        sb.append( field );
        // if there is a subsequent field we have a , plus spaces to pad, plus
        // space.
        pending = columnWidth - field.length() + 1;
        }

    /**
     * pass1 of aligning a CSV file, get the maximum column widths
     *
     * @param file          CSV file to be aligned in columns.
     * @param separatorChar field separator character, usually ',' in North America,
     *                      ';' in Europe and sometimes '\t' for
     *                      tab.
     * @param quoteChar     char to use to enclose fields containing a separator, usually '\"'
     * @param commentChar   char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param verbose       true if want extra status information
     * @param encoding      encoding of input and output file.
     *
     * @throws IOException if problems reading
     */
    private void pass1( final File file,
                        final char separatorChar,
                        final char quoteChar,
                        final char commentChar,
                        final boolean verbose,
                        final Charset encoding ) throws IOException
        {// first pass read
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, allowMultipleLineFields
        // on first pass ignore comments.
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( file, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars, true, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        colDescriptorList = new ArrayList<>( 40 );
        try
            {
            while ( true )
                {
                final String[] fields = r.getAllFieldsInLine();
                boolean wasComment = r.wasComment();
                for ( int colIndex = 0; colIndex < fields.length; colIndex++ )
                    {
                    if ( !wasComment || colIndex != fields.length - 1 )
                        {
                        // not tail comment.
                        if ( colIndex >= colDescriptorList.size() )
                            {
                            colDescriptorList.add( new ColumnDescriptor() );
                            }
                        ColumnDescriptor col = colDescriptorList.get( colIndex );
                        String field = fields[ colIndex ];
                        int width = field.length();
                        final boolean loneEmptyField = fields.length == ( wasComment ? 2 : 1 ) && fields[ 0 ].trim()
                                                                                                          .length()
                                                                                                  == 0;
                        // does this field need surrounding quotes?
                        boolean isAwkward = col.isAwkward
                                            || loneEmptyField
                                            || field.indexOf( separatorChar ) >= 0
                                            || field.indexOf( quoteChar ) >= 0
                                            || field.indexOf( commentChar ) >= 0;
                        if ( isAwkward )
                            {
                            // extra col for lead/trail quote.
                            width += 2;
                            // extra col for each doubled quote
                            for ( int i = 0; i < field.length(); i++ )
                                {
                                if ( field.charAt( i ) == quoteChar )
                                    {
                                    width++;
                                    }
                                }
                            }
                        if ( !col.isAwkward && isAwkward )
                            {
                            col.isAwkward = true;
                            // earlier non-awkward chars will be displayed in quotes.
                            col.maxWidth += 2;
                            if ( verbose )
                                {
                                out.println( "    Note : col " + colIndex + " quoted because of line " + r.lineCount
                                        () + " : " + field );
                                }
                            }
                        if ( width > col.maxWidth )
                            {
                            col.maxWidth = width;
                            }
                        if ( !ST.isLegal( field, "0123456789.+-" ) )
                            {
                            col.isNumeric = false;
                            }
                        if ( field.length() == "yyyy-mm-dd".length() && field.charAt( 4 ) == '-' && field.charAt( 7 )
                                                                                                    == '-' )
                            {
                            // left align col headers of dates.
                            col.isNumeric = false;
                            }
                        }
                    }
                }
            }
        catch ( EOFException e )
            {
            // expected
            }
        r.close();
        }

    /**
     * pass2 of aligning a CSV file, expand fields to the max width
     *
     * @param fileBeingProcessed CSV file to be aligned in columns.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding of input and output file.
     *
     * @throws IOException if problems reading/writing file
     */
    private void pass2( final File fileBeingProcessed,
                        final char separatorChar,
                        final char quoteChar,
                        final char commentChar,
                        final Charset encoding ) throws IOException
        {
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, allowMultipleLineFields
        // on second pass, consider comments.
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, allowMultipleLineFields
        final CSVReader r = new CSVReader(
                EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars, false, true, true, true
        );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter w = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        // we don't write to a CSVWriter, since it collapses fields.
        try
            {
            while ( true )
                {
                final String[] fields = r.getAllFieldsInLine();
                final boolean wasComment = r.wasComment();
                sb = new FastCat( fields.length * 4 );
                pending = -1;
                // may have short line.
                for ( int colIndex = 0; colIndex < fields.length; colIndex++ )
                    {
                    if ( wasComment && colIndex == fields.length - 1 )
                        {
                        if ( colIndex == 0 )
                            {
                            // comment on line by itself, on left
                            sb.append( commentChar );
                            final String comment = fields[ 0 ];
                            if ( !( comment.length() > 0 && comment.charAt( 0 ) == commentChar ) )
                                {
                                // keep ## together, else separate comment body by space.
                                sb.append( ' ' );
                                sb.append( comment );
                                }
                            else
                                {
                                sb.append( rebuildLabelComment( comment ) );
                                }
                            }
                        else
                            {
                            // tail comment, put out past end of all fields, all aligned.
                            sb.append( ST.spaces( alignedLineWidth - sb.length() + 1 ) );
                            sb.append( commentChar );
                            sb.append( ' ' );
                            sb.append( fields[ colIndex ] );
                            }
                        }
                    else
                        {
                        final ColumnDescriptor col = colDescriptors[ colIndex ];
                        String field = fields[ colIndex ];
                        if ( col.isNumeric )
                            {
                            // right justify, can't be awkward
                            // spaces, field, comma, space
                            rightJustify( field, col.maxWidth );
                            }
                        else
                            {
                            // left justify
                            // field, comma, space, spaces
                            if ( col.isAwkward )
                                {
                                // also handles loneEmptyField
                                field = prepareAwkward( field, quoteChar );
                                }
                            leftJustify( field, col.maxWidth );
                            }
                        }
                    } // end for
                w.write( ST.trimTrailing( sb.toString() ) );
                w.write( lineSeparator );
                } // end while
            } // end try
        catch ( EOFException e )
            {
            out.println( r.lineCount() + " lines aligned." );
            r.close();
            // swap tempFile output and input now that output is safely created.
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
    String rebuildLabelComment( String comment )
        {
        final String[] fieldnames = SPLIT_ON_COMMA.split( comment.substring( 1 ) );  // strip second #
        final FastCat sb2 = new FastCat( fieldnames.length * 3 + 2 );
        sb2.append( comment.charAt( 0 ) ); // rebuild just second #
        sb2.append( ' ' );
        for ( int i = 0; i < fieldnames.length; i++ )
            {
            if ( i >= colDescriptors.length )
                {
                sb2.append( fieldnames[ i ].trim() );
                }
            else
                {
                final ColumnDescriptor cd = colDescriptors[ i ];
                int padding = cd.startCol - sb2.length() - 1 /* account for lead # applied later */;
                if ( padding > 0 )
                    {
                    sb2.append( ST.spaces( padding ) );
                    padding = 0;
                    }
                if ( cd.isNumeric )
                    {
                    final int width = cd.maxWidth + padding; /* possibly negative padding */
                    sb2.append( ST.leftPad( fieldnames[ i ].trim(), width, false ) );
                    }
                else
                    {
                    sb2.append( fieldnames[ i ].trim() );
                    }
                }
            sb2.append( ", " );
            }
        return sb2.toString().substring( 0, sb2.length() - 2 ); // chop off final ,
        }

    /**
     * append a field to the wholeLine right justified
     *
     * @param field       field to add, with awkward encoding in place if any.
     * @param columnWidth width of the column
     */
    private void rightJustify( String field, int columnWidth )
        {
        if ( pending >= 0 )
            {
            sb.append( ',' );
            sb.append( ST.spaces( pending ) );
            }
        int spaces = columnWidth - field.length();
        sb.append( ST.spaces( spaces ) );
        sb.append( field );
        pending = 1;/* comma plus space, if there is a subsequent field */
        }

    /**
     * Simple command line interface to CSVAlign one file whose name is on the command line. Must have
     * extension .csv <br> Use java com.mindprod.CSVAlign somefile.csv
     *
     * @param args name of csv file to align in columns.
     */
    public static void main( String[] args )
        {
        if ( !( 1 <= args.length && args.length <= 2 ) )
            {
            throw new IllegalArgumentException( USAGE );
            }
        boolean verbose = false;
        String filename = "";
        for ( String arg : args )
            {
            if ( arg.equals( "-v" ) )
                {
                verbose = true;
                }
            else
                {
                filename = arg;
                }
            }
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException( "Bad extension.\n" + USAGE );
            }
        final File file = new File( filename );
        try
            {
            // file , separatorChar, quoteChar, commentChar , verbose
            new CSVAlign( file, ',', '\"', '#', verbose, CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVAlign failed to align " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        } // end main
    }

/**
 * Facts about a single column in the CSV file, used internally by CSVAlign
 * <p/>
 * created with Intellij Idea
 *
 * @author Roedy Green, Canadian Mind Products
 */
final class ColumnDescriptor
    {
    /**
     * true if one or more fields in this column require surrounding quotes. On output, all fields in an awkward column
     * will get surrounding quotes. If a field contains , or quote it needs the surrounding quotes.
     */
    public boolean isAwkward = false;

    /**
     * if true all fields contain only digits dot, plus and minus. Assume column is numeric until proven otherwise by
     * finding a non-numeric char.
     */
    public boolean isNumeric = true;

    /**
     * widest field in the column. Not including comma or space. If a column is "awkward" (needs surrounding quotes),
     * then the
     * length includes the quotes and any internal quoting.
     */
    public int maxWidth = 0;

    /**
     * 0-based char column where this column starts
     */
    public int startCol = 0;
    }
