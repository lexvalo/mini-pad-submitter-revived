/*
 * [CSVWriter.java]
 *
 * Summary: Write CSV (Comma Separated Value) files.
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
 *  4.5 2011-02-21 add setLineSeparator at the request of Jens Meyborn of ppi media.
 *  4.6 2011-02-24 fix bug, emitted """" for single field with quotelevel 2. reported by Dr. Jens Uwe Meyborn
 *  4.7 2016-05-21 correct bug where CSVWrite sometimes failed to enclose fields containginng commas in quotes.
 */
package com.mindprod.csv;

import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Write CSV (Comma Separated Value) files.
 * <p/>
 * This format is used my Microsoft Word and Excel. Fields are separated by
 * commas, and enclosed in quotes if they contain commas or quotes. Embedded quotes are doubled. Embedded spaces do not
 * normally require surrounding quotes. The last field on the line is not followed by a comma. Null fields are
 * represented by two commas in a row.
 * <p/>
 * Must be combined with your own code or used by one of the standalone CSV utilities.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.7 2016-05-21 correct bug where CSVWrite sometimes failed to enclose fields containginng commas in quotes.
 * @since 2002-03-27
 */
public final class CSVWriter
    {
    /**
     * true if want debugging output
     */
    private static final boolean DEBUGGING = false;

    /**
     * formatter to output doubles with explicit number of places.
     */
    private static final DecimalFormat df = new DecimalFormat( "##0.000" );

    /**
     * true if write should trim lead/trail whitespace from fields before writing them.
     */
    private final boolean trim;

    /**
     * char to mark the start of a comment, usually ; or #
     */
    private final char commentChar;

    /**
     * quote character, usually '\"' '\'' for SOL used to enclose fields containing a separator character.
     */
    private final char quoteChar;

    /**
     * field separator character, usually ',' in North America, ';' in Europe and sometimes '\t' for tab.
     */
    private final char separatorChar;

    /**
     * how much extra quoting you want
     * -1 = like 0, but add an extra space after each separator/comma,
     * 0 = minimal quotes, only around fields containing quotes or separators.
     * 1 = quotes also around fields containing spaces.
     * 2 = quotes around all fields, whether or not they contain commas, quotes or spaces.
     */
    private final int quoteLevel;

    /**
     * PrintWriter where CSV fields will be written.
     */
    private PrintWriter pw;

    /**
     * line separator to use. We use Windows style for all platforms since csv is a Windows format file.
     */
    private String lineSeparator = "\r\n";

    /**
     * true if first and only field on line is "", and we have not emitted a "" for it yet.
     */
    private boolean pendingLoneEmptyField;

    /**
     * true if there has was a field previously written to this line, meaning there is a comma pending to be written.
     */
    private boolean wasPreviousField = false;

    /**
     * count of lines written
     */
    private int lineCount;

    /**
     * Simplified convenience Constructor to write a CSV file, defaults to quotelevel 1, comma separator , trim
     *
     * @param pw Buffered PrintWriter where fields will be written
     */
    public CSVWriter( final PrintWriter pw )
        {
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        this( pw, 1, ',', '\"', '#', true );
        }

    /**
     * Detailed constructor to write a CSV file.
     *
     * @param pw            Buffered PrintWriter where fields will be written
     * @param quoteLevel    -1 = like 0, but add an extra space after each separator/comma,
     *                      0 = minimal quotes, only around fields containing quotes or separators.
     *                      1 = quotes also around fields containing spaces.
     *                      2 = quotes around all fields, whether or not they contain commas, quotes or spaces.
     * @param separatorChar field separator character, usually ',' in North America,
     *                      ';' in Europe and sometimes '\t' for
     *                      tab.  Note this is a 'char' not a "string".
     * @param quoteChar     char to use to enclose fields containing a separator, usually '\"'. Use (char)0 if
     *                      you don't want a quote character. Note this is a 'char' not a "string".
     * @param commentChar   char to prepend on any comments you write.  usually ; or #. Note this is a 'char' not a
     *                      "string". Recommend you use # even if there are no comments so that # will be quoted
     *                      for when file is used later with # as the commend char.
     * @param trim          true if writer should trim leading/trailing whitespace (e.g. blank, cr, Lf,
     *                      tab) before writing
     *                      the field.
     */
    public CSVWriter( final PrintWriter pw,  /* with implied encoding */
                      final int quoteLevel,
                      final char separatorChar,
                      final char quoteChar,
                      final char commentChar,
                      final boolean trim )
        {
        this.pw = pw;
        this.quoteLevel = quoteLevel;
        this.separatorChar = separatorChar;
        this.quoteChar = quoteChar;
        this.commentChar = commentChar;
        this.trim = trim;
        }

    /**
     * Close the PrintWriter.
     */
    public void close()
        {
        if ( pw != null )
            {
            pw.close();
            pw = null;
            }
        }

    /**
     * get count of how many lines written so far.
     *
     * @return count of lines written so far
     */
    public int getLineCount()
        {
        return lineCount;
        }

    /**
     * Write a new line in the CVS output file to demark the end of record.
     */
    public void nl()
        {
        if ( pw == null )
            {
            throw new IllegalArgumentException(
                    "attempt to write to a closed CSVWriter" );
            }
        /* don't write last pending comma on the line */
        if ( pendingLoneEmptyField )
            {
            // single empty field on line, displayed as "".  Can't show single empty field just with commas
            pw.write( quoteChar );
            pw.write( quoteChar );
            }
        pw.write( lineSeparator );
        wasPreviousField = false;
        pendingLoneEmptyField = false;
        lineCount++;
        }

    /**
     * Write a comment followed by new line in the CVS output file to demark the end of record.
     *
     * @param comment comment string containing any chars.  Lead comment character will be applied automatically.
     *                If you want two of them, you must include one yourself.
     */
    public void nl( final String comment )
        {
        if ( pw == null )
            {
            throw new IllegalArgumentException(
                    "attempt to write to a closed CSVWriter" );
            }
        if ( wasPreviousField )
            {
            if ( pendingLoneEmptyField )
                {
                // single empty field on line, displayed as "".  Can't show single empty field just with commas
                pw.write( quoteChar );
                pw.write( quoteChar );
                }
            // no comma, just extra space.
            pw.write( ' ' );
            }
        if ( commentChar != ( char ) 0 )
            {
            pw.write( commentChar );  // start comment with space # space
            }
        if ( !( comment.length() > 0 && comment.charAt( 0 ) == commentChar ) )
            {
            // keep ## together, else separate comment body by space.
            pw.write( ' ' );
            }
        pw.write( comment.trim() );
        pw.write( lineSeparator );
        wasPreviousField = false;
        pendingLoneEmptyField = false;
        lineCount++;
        }

    /**
     * Write a comment followed by new line in the CVS output file to demark the end of record.
     *
     * @param fields              array of strings to output. Last field may be a comment. Typically from
     *                            getAllFieldsInLine.
     * @param lastFieldWasComment if true, mean last field in the array was a comment.
     */
    public void nl( final String[] fields, final boolean lastFieldWasComment )
        {
        if ( lastFieldWasComment )
            {
            for ( int i = 0; i < fields.length - 1; i++ )
                {
                put( fields[ i ] );
                }
            nl( fields[ fields.length - 1 ] );
            }
        else
            {
            for ( String field : fields )
                {
                put( field );
                }
            nl();
            }
        }

    /**
     * Write a variable number of Strings, without a terminal lf
     *
     * @param fields array of strings to output.
     */
    public void put( final String... fields )
        {
        for ( String field : fields )
            {
            put( field );
            }
        }

    /**
     * Write one csv field to the file, followed by a separator unless it is the last field on the line. Lead and
     * trailing blanks will be removed.
     *
     * @param i The int to write. Any additional quotes or embedded quotes will be provided by put.
     */
    public void put( final int i )
        {
        put( Integer.toString( i ) );
        }

    /**
     * Write one csv field to the file, followed by a separator unless it is the last field on the line. Lead and
     * trailing blanks will be removed.
     *
     * @param c The char to write. Any additional quotes or embedded quotes will be provided by put.
     */
    public void put( final char c )
        {
        put( String.valueOf( c ) );
        }

    /**
     * Write one boolean field to the file, followed by a separator unless it is the last field on the line. Lead and
     * trailing blanks will be removed.
     *
     * @param b The boolean to write. Any additional quotes or embedded quotes will be provided by put.
     */
    public void put( final boolean b )
        {
        put( b ? "true" : "false" );
        }

    /**
     * Write one csv field to the file, followed by a separator unless it is the last field on the line. Lead and
     * trailing blanks will be removed.
     *
     * @param l The long to write. Any additional quotes or embedded quotes will be provided by put.
     */
    public void put( final long l )
        {
        put( Long.toString( l ) );
        }

    /**
     * Write one csv field to the file, followed by a separator unless it is the last field on the line. Lead and
     * trailing blanks will be removed.
     *
     * @param d The double to write. Any additional quotes or embedded quotes will be provided by put.
     */
    public void put( final double d )
        {
        put( Double.toString( d ) );
        }

    /**
     * Write one csv field to the file, followed by a separator unless it is the last field on the line. Lead and
     * trailing blanks will be removed.
     *
     * @param f The float to write. Any additional quotes or embedded quotes will be provided by put.
     */
    public void put( final float f )
        {
        put( Float.toString( f ) );
        }

    /**
     * Write one csv field to the file, followed by a separator unless it is the last field on the line. Lead and
     * trailing blanks will be removed.   Don't use this method to write comments.
     *
     * @param s The string to write. Any additional quotes or embedded quotes will be provided by put. Null means start
     *          a new line.
     *
     * @see #nl(String)
     */
    public void put( String s )
        {
        if ( pw == null )
            {
            throw new IllegalArgumentException( "attempt to write to a closed CSVWriter" );
            }
        if ( s == null )
            {
            nl();
            return;
            }
        if ( trim )
            {
            s = s.trim();
            }
        if ( wasPreviousField )
            {
            pw.write( separatorChar );
            if ( quoteLevel == -1 )
                {
                pw.write( ' ' );
                }
            pendingLoneEmptyField = false;
            }
        else
            {
            // first field on line
            pendingLoneEmptyField = s.trim().length() == 0;
            }
        final boolean hasEmbeddedQuote = s.indexOf( quoteChar ) >= 0;
        // do we need to surround the field in quotes?
        final boolean surround;
        switch ( quoteLevel )
            {
            case -1: // like 0, but add an extra space after each separator/comma,
            case 0: // minimal quotes, only around fields containing separators, quotes or separators or comments
                surround =
                        s.indexOf( separatorChar ) >= 0
                        || hasEmbeddedQuote
                        || s.indexOf( commentChar ) >= 0;
                break;
            case 1: //quotes also around fields containing spaces.
                surround = s.indexOf( ' ' ) >= 0
                           || s.indexOf( separatorChar ) >= 0
                           || hasEmbeddedQuote
                           || s.indexOf( commentChar ) >= 0;
                break;
            case 2: //quotes around all fields, whether or not they contain commas, quotes or spaces.
                surround = true;
                break;
            default:
                throw new IllegalArgumentException( "Invalid quoteLevel " + quoteLevel );
            }
        if ( surround )
            {
            /* need surrounding quotes */
            pw.write( quoteChar );
            if ( hasEmbeddedQuote )
                {
                // must process contents of field char by char, doubling quotes
                for ( int i = 0, n = s.length(); i < n; i++ )
                    {
                    char c = s.charAt( i );
                    if ( c == quoteChar )
                        {
                        pw.write( quoteChar );
                        pw.write( quoteChar );
                        }
                    else
                        {
                        pw.write( c );
                        }
                    }
                }
            else
                {
                /* no embedded quotes, can writee field as a lump   */
                pw.write( s );
                }
            pw.write( quoteChar );
            pendingLoneEmptyField = false;
            }
        else
            {
            /* ordinary case, no surrounding quotes needed, might be empty or blank */
            pw.write( s );
            }
        /* make a note to print trailing comma later, if there in another field following */
        wasPreviousField = true;
        }

    /**
     * Write one csv field to the file, followed by a separator unless it is the last field on the line. Lead and
     * trailing blanks will be removed.
     *
     * @param d      The double to write. Any additional quotes or embedded quotes will be provided by put.
     * @param places lets you explicitly control how max places past the decimal to output.
     */
    public void put( final double d, final int places )
        {
        df.setMaximumFractionDigits( places );
        // df.setMinimumFractionDigits( places );  if wanted to force extra 0s on end..
        put( df.format( d ) );
        }

    /**
     * Set the line separator used to demark where one line ends and the next begins.
     * The default is "\r\n" because CSV is a Microsoft format.
     * <p/>
     * Note the spelling sep<strong>a</strong>rator not sep<strong>e</strong>rator.
     *
     * @param lineSeparator the new desired line separator String.
     *                      null gets the OS default e.g. "\n" for Unix, "\r\n" for Windows.
     */
    public void setLineSeparator( String lineSeparator )
        {
        this.lineSeparator = lineSeparator == null ? System.getProperty( "line.separator" ) : lineSeparator;
        }
    }
