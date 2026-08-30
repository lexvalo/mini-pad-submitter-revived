/*
 * [CSVSort.java]
 *
 * Summary: Sort a CSV file, on multiple columns with case-sensitive, case-insensitive and numeric fields.
 *
 * Copyright: (c) 2009-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
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
 *  3.6 2010-12-14 add LinesToCSV
 *  3.7 2010-12-17 fix bug in descending sorts.
 *  3.8 2011-01-25 allow you to specify encoding
 *  3.9 2011-02-08 add support for sorting by field length.
 *  4.0 2011-03-10 allow numeric sort on empty columns.
 *  4.1 2012-01-25 redefine meaning of n option and add d option. You may need to change some n to d in your scripts.
 *  4.2 2012-02-07 treat empty numeric columns as 0.
 *  4.3 2012-02-11 add -f for sorting by family name, i.e. surname/given name.
 *  4.4 2012-02-15 refine algorithm for sorting by family name.
 *  4.5 2012-02-20 fix bug. -d option was being improperly refused.
 *  4.6 2012-07-31 fix sort on surname van Pelt, Mc Mac
 *  4.7 2014-05-14 allow $ as sort type
 */
package com.mindprod.csv;

import com.mindprod.common18.EIO;
import com.mindprod.common18.FullName;
import com.mindprod.common18.ST;
import com.mindprod.hunkio.HunkIO;
// import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.System.*;

/**
 * Sort a CSV file, on multiple columns with case-sensitive, case-insensitive and numeric fields.
 * <p/>
 * Use: java.exe com.mindprod.CSVSort somefile.csv 0s+ 2i- 5n+ 7d+ 3x+ 4l+
 * 0-based list of column numbers, s=string i=case-insensitive string, d=(double,float) n=number(int,
 * long) x=hex l=length of field +=ascending -=descending
 * Empty cols are presumed to contain &quot;&quot; or &quot;0&quot;.
 * <p/>
 * This program has an unusual genesis. On 2009-12-03 I had a detailed dream about documenting
 * and writing a sort for CSV files. When I woke up I was surprised at how logical the dream was.
 * Usually my computer dreams, on waking, turn out to be complete nonsense.  I wrote the program
 * just as I planned it in the dream.  All went unusually smoothly. About the only change was provision
 * for ascending and descending keys.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.7 2014-05-14 allow $ as sort type
 * @since 2009-12-03
 */
public final class CSVSort
    {
    // declarations

    /**
     * true if want extra debug output
     */
    private static final boolean DEBUGGING = false;

    /**
     * how to use the command line
     */
    private static final String USAGE = "\nUsage: csvsort.jar somefile.csv 0s+ 2i- 7d+ 6$+ 5n+ 3x+ 4l+ 8f- .." +
                                        ".\n"
                                        + "s=case-sensitive string i=case-insensitive string, d=(double," +
                                        "float) $=dollar n=number(int,long)  x=hex (\\u 0x ff) l=length of field f=family name";

    /**
     * true if sort on col is ascending
     */
    private final boolean[] isAscendings;

    /**
     * type of sort for each column
     */
    private final char[] sortTypes;

    /**
     * how many columns to sort
     */
    private final int countOfColsToSort;

    /**
     * which 0-based cols to sort in order
     */
    private final int[] sortCols;

    /**
     * which line number we are parsing
     */
    private int lineNumber;
    // /declarations
    // methods

    /**
     * Sort a CSV file, constructor. Just create. There are no methods to call.
     *
     * @param fileBeingProcessed CSV file to be packed to remove excess space and quotes.
     * @param sortCols           array of 0-based cols to sort on.
     * @param sortTypes          array of chars with letters s i n x to tell how to sort each column.
     * @param isAscendings       array of sort direction, true=ascending, false = descending.
     * @param separatorChar      field separator character, usually ',' in North America,
     *                           ';' in Europe and sometimes '\t' for
     *                           tab.
     * @param quoteChar          char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                           you don't want a quote character.
     * @param commentChar        char to use to introduce comments.  Use (char) 0 if none.  Only one character allowed.
     * @param encoding           encoding for input and output.
     *
     * @throws java.io.IOException if problems reading/writing file
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public CSVSort( final File fileBeingProcessed,
                    final int[] sortCols,
                    final char[] sortTypes,
                    final boolean[] isAscendings,
                    final char separatorChar,
                    final char quoteChar,
                    final char commentChar,
                    final Charset encoding ) throws IOException
        {
        this.countOfColsToSort = sortCols.length;
        this.sortCols = sortCols;
        this.sortTypes = sortTypes;
        this.isAscendings = isAscendings;
        final String commentChars = ( commentChar == 0 ) ? "" : String.valueOf( commentChar );
        final int estimatedLines = ( int ) fileBeingProcessed.length() / 50;
        final ArrayList<com.mindprod.csv.CSVSort.SortableCSVRow> allRows = new ArrayList<>(
                estimatedLines );
        final ArrayList<String> pendingComments = new ArrayList<>( 11 );
        final ArrayList<String> topComments = new ArrayList<>( 11 );
        // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
        // allowMultipleLineFields
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 64 * 1024, encoding ),
                separatorChar, quoteChar, commentChars,
                false, true /* trimQuoted */, true /* trimUnquoted */, true
        );
        // read everything into allRows ArrayList
        lineNumber = 0;
        // are we parsing comments at the top of the file?, which are frozen there,
        // rather than following the following data record.
        boolean inTopComments = true;
        try
            {
            while ( true )
                {
                String[] fields = r.getAllFieldsInLine();
                lineNumber++;
                if ( r.wasComment() )
                    {
                    if ( fields.length == 1 )
                        {
                        // was comment on a line by itself.
                        // save them up to prepend to next real record.
                        if ( inTopComments )
                            {
                            topComments.add( fields[ 0 ] );
                            }
                        else
                            {
                            pendingComments.add( fields[ 0 ] );
                            }
                        }
                    else
                        {
                        // just a tail comment
                        inTopComments = false;
                        final String[] leadComments;
                        if ( pendingComments.size() > 0 )
                            {
                            leadComments = pendingComments.toArray( new String[ pendingComments.size() ] );
                            pendingComments.clear();
                            }
                        else
                            {
                            leadComments = new String[ 0 ];
                            }
                        final String[] dataFields = new String[ fields.length - 1 ];
                        System.arraycopy( fields, 0, dataFields, 0, fields.length - 1 );
                        allRows.add( new SortableCSVRow( dataFields, leadComments, fields[ fields.length - 1 ] ) );
                        }
                    }
                else
                    {
                    // no comment on tail of this data line.
                    inTopComments = false;
                    final String[] leadComments;
                    if ( pendingComments.size() > 0 )
                        {
                        leadComments = pendingComments.toArray( new String[ pendingComments.size() ] );
                        pendingComments.clear();
                        }
                    else
                        {
                        leadComments = new String[ 0 ];
                        }
                    allRows.add( new SortableCSVRow( fields, leadComments, null ) );
                    }
                }
            }
        catch ( EOFException e )
            {
            r.close();
            }
        // pendingComments might contain footer comments.
        // in-ram sort
        Collections.sort( allRows );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final PrintWriter pw = EIO.getPrintWriter( tempFile, 32 * 1024, encoding );
        final CSVWriter w = new CSVWriter( pw, 0 /* minimal */, separatorChar, quoteChar, commentChar, true );
        for ( String topComment : topComments )
            {
            w.nl( topComment );
            }
        for ( SortableCSVRow sr : allRows )
            {
            sr.emit( w ); // also outputs embedded comments.
            }
        for ( String footComment : pendingComments )
            {
            w.nl( footComment );
            }
        out.println( lineNumber + " lines sorted" );
        w.close();  // closes pw indirectly
        HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
        }// /method

    /**
     * extract surname from field, might have multiple names, lead given name.
     *
     * @param name complete name
     *
     * @return Surname
     */
    private static String extractSurname( String name )
        {
        name = name.toLowerCase();
        // could be a comma separated list or and-separated or &-separated.
        int p = name.indexOf( ',' );
        if ( p >= 0 )
            {
            name = name.substring( 0, p );
            }
        p = name.indexOf( '&' );
        if ( p >= 0 )
            {
            name = name.substring( 0, p );
            }
        p = name.indexOf( " and " );
        if ( p >= 0 )
            {
            name = name.substring( 0, p );
            }
        p = name.indexOf( " with " );
        if ( p >= 0 )
            {
            name = name.substring( 0, p );
            }
        name = name.trim();
        // trim off trailing [author] etc.
        int len = name.length();
        if ( len > 0 && name.charAt( len - 1 ) == ']' )
            {
            p = name.lastIndexOf( '[' );
            if ( p >= 0 )
                {
                name = name.substring( 0, p );
                name = name.trim();
                }
            }
        return new FullName( name ).getSortable();
        }// /method

    /**
     * Simple command line interface to CSVSort, Sorts one csv file whose name is on the command line. Must have
     * extension .csv and sort cols of form:  0s+ 2i- 4n+ 5x+
     *
     * @param args name of csv file to sort, followed by cols to sort.
     */
    public static void main( String[] args )
        {
        if ( args.length < 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        String filename = args[ 0 ];
        if ( !filename.endsWith( ".csv" ) )
            {
            throw new IllegalArgumentException(
                    "Bad Extension\n" + USAGE );
            }
        final File file = new File( filename );
        final int keyCount = args.length - 1;
        final char[] sortTypes = new char[ keyCount ];
        final int[] sortCols = new int[ keyCount ];
        final boolean[] isAscendings = new boolean[ keyCount ];
        for ( int i = 0; i < keyCount; i++ )
            {
            // skip first parm
            final String arg = args[ i + 1 ];
            if ( !( 3 <= arg.length() && arg.length() <= 4 ) )
                {
                // must have form 9s+ or 99s+
                throw new IllegalArgumentException(
                        "bad sort col\n" + USAGE );
                }
            sortCols[ i ] = Integer.parseInt( arg.substring( 0, arg.length() - 2 ) );
            final char charSortType = arg.charAt( arg.length() - 2 );
            if ( !ST.isLegal( charSortType, "sid$nxlf" ) )
                {
                throw new IllegalArgumentException(
                        "Sort column letter must be s/i/d/$/n/x/l/f\n" + USAGE );
                }
            sortTypes[ i ] = charSortType;
            final char direction = arg.charAt( arg.length() - 1 );
            if ( !ST.isLegal( direction, "+-" ) )
                {
                throw new IllegalArgumentException(
                        "Sort column letter direction must be +/-\n" + USAGE );
                }
            isAscendings[ i ] = direction == '+';
            }
        try
            {
            // file, cols, types, directions,  separatorChar,  quoteChar,  commentChar
            new CSVSort( file, sortCols, sortTypes, isAscendings, ',', '\"', '#', CSV.UTF8 );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "CSVSort failed to sort " + EIO.getCanOrAbsPath( file ) );
            err.println();
            }
        }// /method

    // /methods
    // inner class
    class SortableCSVRow implements Comparable<SortableCSVRow>
        {
        /**
         * data fields of the row
         */
        private final String[] fields;

        /**
         * comment lines prior to the this row, without lead #
         */
        private final String[] leadComments;

        /**
         * comment line on tail ent of line.  null if so such comment, without lead #
         */
        private final String tailComment;

        /**
         * key fields we sort by, String (si) or Double objects (n) or Long object (x)
         * Not directly Comparable.  Cannot use Comparable<T> since no common T.
         */
        private final Comparable[] keys;

        /**
         * constructor
         *
         * @param fields       List of fields to sort on
         * @param leadComments comment lines prior to the this row, without lead #
         * @param tailComment  line on tail ent of line.  null if so such comment, without lead #
         */
        SortableCSVRow( String[] fields, String[] leadComments, String tailComment )
            {
            this.fields = fields;
            this.leadComments = leadComments;
            this.tailComment = tailComment;
            // build list of keys wi will sort on in convenient form.
            // arrays and generics do not mix so cannot have Comparable<Object>[].
            this.keys = new Comparable[ CSVSort.this.countOfColsToSort ];
            for ( int i = 0; i < CSVSort.this.countOfColsToSort; i++ )
                {
                final int selectCol = CSVSort.this.sortCols[ i ];
                switch ( CSVSort.this.sortTypes[ i ] )
                    {
                    case 's':
                        if ( selectCol < this.fields.length )
                            {
                            this.keys[ i ] = this.fields[ selectCol ];
                            }
                        else
                            {
                            this.keys[ i ] = "";
                            }
                        break;
                    case 'i':
                        if ( selectCol < this.fields.length )
                            {
                            // precondition key so can use fast case-sensitive compare.
                            this.keys[ i ] = this.fields[ selectCol ].toLowerCase();
                            }
                        else
                            {
                            this.keys[ i ] = "";
                            }
                        break;
                    case '$':
                        try
                            {
                            if ( selectCol < this.fields.length )
                                {
                                final String field = ST.trimLeading( this.fields[ selectCol ], '$' );
                                if ( field.length() > 0 )
                                    {
                                    this.keys[ i ] = new Double( field );
                                    }
                                else
                                    {
                                    this.keys[ i ] = 0.0d;
                                    }
                                }
                            else
                                {
                                this.keys[ i ] = 0.0d;
                                }
                            }
                        catch ( NumberFormatException e )
                            {
                            throw new IllegalArgumentException( "Invalid double field: [" + fields[ selectCol ] + "] " +
                                                                "on line: " + lineNumber );
                            }
                        break;
                    case 'd':
                        try
                            {
                            if ( selectCol < this.fields.length )
                                {
                                final String field = this.fields[ selectCol ];
                                if ( field.length() > 0 )
                                    {
                                    this.keys[ i ] = new Double( field );
                                    }
                                else
                                    {
                                    this.keys[ i ] = 0.0d;
                                    }
                                }
                            else
                                {
                                this.keys[ i ] = 0.0d;
                                }
                            }
                        catch ( NumberFormatException e )
                            {
                            throw new IllegalArgumentException( "Invalid double field: [" + fields[ selectCol ] + "] " +
                                                                "on line: " + lineNumber );
                            }
                        break;
                    case 'n':
                        try
                            {
                            if ( selectCol < this.fields.length )
                                {
                                String field = this.fields[ selectCol ];
                                if ( field.length() > 0 )
                                    {
                                    // store as Long via boxing.
                                    this.keys[ i ] = Long.parseLong( field );
                                    }
                                else
                                    {
                                    this.keys[ i ] = 0L;
                                    }
                                }
                            else
                                {
                                this.keys[ i ] = 0L;
                                }
                            }
                        catch ( NumberFormatException e )
                            {
                            throw new IllegalArgumentException( "Invalid numeric (int/long) field: [" + fields[
                                    selectCol ] + "] on line: " + lineNumber );
                            }
                        break;
                    case 'x':
                        try
                            {
                            if ( selectCol < this.fields.length )
                                {
                                String field = this.fields[ selectCol ];
                                field = ST.chopLeadingString( field, "0x" );
                                field = ST.chopLeadingString( field, "\\u" );
                                // store as Long via boxing.
                                if ( field.length() > 0 )
                                    {
                                    this.keys[ i ] = Long.parseLong( field, 16 /* radix */ );
                                    }
                                else
                                    {
                                    this.keys[ i ] = 0L;
                                    }
                                }
                            else
                                {
                                this.keys[ i ] = 0L;
                                }
                            }
                        catch ( NumberFormatException e )
                            {
                            throw new IllegalArgumentException( "Invalid hex field: [" + fields[ selectCol ] + "] on " +
                                                                "line: " + lineNumber );
                            }
                        break;
                    case 'l':
                        if ( selectCol < this.fields.length )
                            {
                            //noinspection UnnecessaryBoxing
                            this.keys[ i ] = this.fields[ selectCol ].length();
                            }
                        else
                            {
                            this.keys[ i ] = 0;
                            }
                        break;
                    case 'f':
                        if ( selectCol < this.fields.length )
                            {
                            // precondition so can use fast case-sensitive compare
                            this.keys[ i ] = ( extractSurname( this.fields[ selectCol ] ) + ' ' + this.fields[
                                    selectCol ] ).toLowerCase();
                            if ( DEBUGGING )
                                {
                                out.println( this.keys[ i ] + " : " + this.fields[ selectCol ] );
                                }
                            }
                        else
                            {
                            this.keys[ i ] = "";
                            }
                        break;
                    default:
                        throw new IllegalArgumentException( "Program bug: invalid sortType: " + sortTypes[ i ] );
                    }
                }
            }

        /**
         * output this row to a CSVWriter, including comments
         *
         * @param w handle to CSVWriter
         */
        void emit( CSVWriter w )
            {
            for ( String leadComment : leadComments )
                {
                w.nl( leadComment );
                }
            for ( String field : fields )
                {
                w.put( field );
                }
            if ( tailComment != null )
                {
                w.nl( tailComment );
                }
            else
                {
                w.nl();
                }
            }

        /**
         * Sort on arbitrary list of columns.
         * Defines default the sort order for SortableCSVRow Objects.
         * Compare this SortableCSVRow with another SortableCSVRow with JDK 1.5+ generics.
         * Compares key then key2 then key3.
         * Informally, returns (this-other) or +ve if this is more positive than other.
         *
         * @param other other SortableCSVRow to compare with this one
         *
         * @return +ve if this&gt;other, 0 if this==other, -ve if this&lt;other
         */
        public final int compareTo( /** @NotNull **/ SortableCSVRow other )
            {
            int diff;
            // sort over keys till find a non-tie.
            for ( int i = 0; i < CSVSort.this.countOfColsToSort; i++ )
                {
                // sortType irrelevent
                // compareTo will use a compareTo method corresponding to type of this.keys[i]
                // All we know is that keys is Comparable. We do not know precisely
                // which type it is. So we cannot use generics to specify it an run time.
                // The run time virtual method scheme sorts it out during execution.
                // declared an Comparebles, but could be String, Double, Long.
                // rejects compareTo<Comparable>
                diff = this.keys[ i ].compareTo( other.keys[ i ] );
                if ( !CSVSort.this.isAscendings[ i ] )
                    {
                    diff = -diff;
                    }
                if ( diff != 0 )
                    {
                    return diff;
                    }
                }
            return 0;
            }
        }
    }
