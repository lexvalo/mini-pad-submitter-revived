/*
 * [FastCat.java]
 *
 * Summary: Stripped down, fast replacement for StringBuilder and StringWriter to build concatenated Strings.
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
 *  1.0 2009-09-29 initial release
 *  1.1 2009-10-09 return this from append
 *  1.2 2010-01-25 fix bug in rarely used clear method.
 *  1.3 2010-02-13 convert from JDK 1.6 to JDK 1.5
 *  1.4 2011-06-08 add room, size, used methods
 *  1.5 2011-11-13 append methods now ignore null parameters
 *  1.6 2011-11-19 checkEstimate method to get advance warning an estimate is too tight or too generous.
 *  1.7 2012-11-19 add the drop method to drop last appended String. e.g. to trim last comma.
 *  1.8 2013-03-12 add toCommaList
 *  1.9 2013-04-10 add toSpaceList and toSeparatedList
 *  2.0 2014-03-22 handle null in Object..., add setPolicy to allow recover
 *  2.1 2014-04-16 new constructor to build a string all in one line
 *  2.2 2014-04-29 methods to insert separators, toSeparatedList
 *  2.3 2014-07-02 slightly different way of calculating when space is getting tight or is too generous.
 *                 setWhereTextCameFrom to help locate problems.
 *  2.4 2014-09-07 add toAndList to produce an comma list with the word and.
 */
package com.mindprod.fastcat;

import java.io.File;
import java.util.Arrays;

import static java.lang.System.*;

/**
 * Stripped down, fast replacement for StringBuilder and StringWriter to build concatenated Strings.
 * <p/>
 * For concatenating individual characters, StringBuilder will probably be faster.
 * It has an overhead of 4 bytes per piece appended, which would be 50% if the Strings were only 2-bytes long.
 * This algorithm works best with fairly long pieces. The main advantage is it is much easier to get a precise
 * estimate of how much space you will need.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.4 2014-09-07 add toAndList to produce an comma list with the word and.
 * @since 2009-09-29
 */
public class FastCat
    {
    // declarations

    /**
     * default estimated number of pieces
     */
    private static final int DEFAULT_SIZE = 1000;

    /**
     * how much extra we give to each estimate of space REQUIRED to ensure small files still get some growth.
     * This is only used when calculating tight limits, not actual size of this FastCat.
     */
    private static final int EXTRA = 20;

    private static final int FIRST_COPYRIGHT_YEAR = 2009;

    /**
     * If the estimate is 60% bigger than required, we consider it overly generous
     */
    private static final int GENEROUS_PERCENT = 160;

    /**
     * if we get an overflow, we suggest doubling the size estimate. We may later inch it back down again.
     */
    private static final int RECOVER_PERCENT = 200;

    /**
     * If the estimate is too tight or too generous, we suggest one 20% bigger than required.
     */
    private static final int SUGGESTED_PERCENT = 120;

    /**
     * If as estimate is not at least 10% bigger than required, we consider it too tight.
     */
    private static final int TIGHT_PERCENT = 110;

    /**
     * copies longer than this are done with System.arrayCopy.  Shorter with char by char copy.
     * This is the optimal value determined by experiment.
     */
    private static final int TWEAK_ARRAYCOPY_LENGTH = 2;

    /**
     * undisplayed copyright notice
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2009-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * when package was released.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2014-09-07";

    /**
     * version of package.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String VERSION_STRING = "2.4";

    /**
     * true if should abort on overflow. false if should grow and recover
     */
    private static boolean abortOnOverflow = false;

    /**
     * true if should abort on seriously excess allocation. false if should ignore excess.
     */
    private static boolean abortOnOverlyGenerous = false;

    /**
     * true if should abort on if allocation in getting tight false if should tight allocation.
     */
    private static boolean abortOnTight = false;

    /**
     * true if should report overflow on console. false if should ignore overflow.
     */
    private static boolean reportOverflow = true;

    /**
     * true if should report on serious excess allocation. false if should ignore excess.
     */
    private static boolean reportOverlyGenerous = true;

    /**
     * true if should report tight allocation. false if should ignore tight allocation.
     */
    private static boolean reportTight = true;

    /**
     * extra debegging output
     */
    private static boolean DEBUGGING = false;

    /**
     * how many pieces we have collected pending so far
     */
    private int count = 0;

    /**
     * total length of all the  pieces we have collected pending so far
     */
    private int totalLength = 0;

    /**
     * private collection of pieces we will later glue together once we know the size of the final String.
     * For RAM efficiency, we use an array, not an ArrayList.  Can be grown/replaced.
     */
    private String[] pieces;
    // /declarations

    /**
     * where text being concatentated originated from, e.g. a filename
     */
    private String whereTextCameFrom = "?";

    /**
     * no-arg constructor, defaults to 20 pieces as the estimate.
     */
    public FastCat()
        {
        this( DEFAULT_SIZE );
        }

    /**
     * constructor
     *
     * @param estNumberOfPieces estimated number of chunks you will concatenate.
     *                          If the estimate is low, you will get ArrayIndexOutOfBoundsExceptions.
     */
    public FastCat( int estNumberOfPieces )
        {
        pieces = new String[ estNumberOfPieces ];
        }
    // methods

    /**
     * append arbitrary number of strings, all in one line  new FastCat( "a", "b", "c").toString()
     * You cannot append further to this string or you will overflow.
     *
     * @param ss comma-separated list of Strings to append
     */
    public FastCat( String... ss )
        {
        this( ss.length );
        append( ss );
        }

    /**
     * abort when overflow initial allocation
     */
    private void abortOverflow()
        {
        throw new ArrayIndexOutOfBoundsException( "FastCat overflow" );
        }// /method

    /**
     * abort if initial allocation was overly generous
     */
    private void abortOverlyGenerous()
        {
        throw new ArrayIndexOutOfBoundsException( "FastCat allocation overly generous" );
        }// /method

    /**
     * abort if initial allocation was tight
     */
    private void abortTight()
        {
        throw new ArrayIndexOutOfBoundsException( "FastCat allocation tight" );
        }// /method

    /**
     * display a comment that can be used to modify a fastcats.csv file with the new suggested size.
     *
     * @param suggestedSize
     */
    private void offerCorrection( int suggestedSize )
        {
        err.println( "   Correction: " + whereTextCameFrom + ", " + suggestedSize );
        }// /method

    /**
     * We overflowed the array. possibly report, recover or abort.
     */
    private void overflow()
        {
        // this can happen in the process of running FastCat, not at a checkEstimate.
        // to we don't know where the text came from.
        if ( reportOverflow )
            {
            report( "FastCat estimate catastrophically low for " + whereTextCameFrom );
            }
        if ( abortOnOverflow )
            {
            abortOverflow();
            }
        else
            {
            recover();
            }
        }// /method

    /**
     * we don't have enough room to add another piece. Grow the array
     */
    private void recover()
        {
        final int recoverSize = count * RECOVER_PERCENT / 100 + EXTRA;
        final String[] old = pieces;
        pieces = new String[ recoverSize ];
        System.arraycopy( old, 0, pieces, 0, count );
        }// /method

    /**
     * report a problem with the estimates
     *
     * @param message what sort of problem.
     */
    private void report( String message )
        {
        // Sun JVM lets us figure out where the call came from.
        StackTraceElement e = new Throwable().getStackTrace()[ 3 ];
        // we know where in the code, but not which file was being processed, or any other user-supplied indication.
        final String whereFastCatCalled = "class.method: "
                                          + e.getClassName()
                                          + "."
                                          + e.getMethodName()
                                          + " line:"
                                          + e.getLineNumber();
        final int room = room();
        final int used = used();
        final int size = size();
        final int recoverSize = ( used + EXTRA ) * RECOVER_PERCENT / 100;
        err.println( "\n>>>> ERROR >>>> "
                     + message
                     + "\n"
                     + "                  used: "
                     + used
                     + " + room: "
                     + room
                     + " = size: "
                     + size
                     + " suggest size: "
                     + recoverSize
                     + "\n"
                     + whereFastCatCalled
        );
        offerCorrection( recoverSize );
        new Throwable().printStackTrace( err );
        // don't offer correction here. We have not finished building the string. We are also deliberately overshooting.
        // offerCorrection (recoverSize );
        }// /method

    /**
     * test harness
     *
     * @param args not used.
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            final FastCat sb = new FastCat( 7 );
            sb.append( "Hello" );
            sb.append( " " );
            sb.append( "World. " );
            sb.append( new File( "temp.txt" ) );
            sb.append( " ", "abc", "def" );
            out.println( sb.toString() );
            // prints Hello World. temp.txt abcdef
            }
        }// /method

    /**
     * Set policy how to handle overflow and excess allocation.
     * For debugging suggest N Y N Y N Y (initial setting)
     * for testing suggest Y Y Y Y Y Y
     * For local production suggest N Y N Y N Y
     * For distributed production suggest N N N N N N
     * Note, this is a static method.  This policy applies to all FastCat objects.
     *
     * @param abortOnOverflow       true if should abort with an ArrayIndexOutOfBoundsException i on overflow. false if
     *                              should grow and recover. Abort will let you be able to intercept the exception and
     *                              display additional information.
     * @param reportOverflow        true if should report overflow on console. false if should ignore overflow.
     * @param abortOnTight          true if should abort on if allocation in getting tight,
     *                              false if should tight allocation.
     * @param reportTight           true if should report tight allocation. false if should ignore tight allocation.
     * @param abortOnOverlyGenerous true if should abort with an ArrayIndexOutOfBoundsException  on seriously excess
     *                              allocation. false if should ignore excess.
     * @param reportOverlyGenerous  true if should report on serious excess allocation. false if should ignore excess.
     */
    public static void setPolicy( boolean abortOnOverflow,
                                  boolean reportOverflow,
                                  boolean abortOnTight,
                                  boolean reportTight,
                                  boolean abortOnOverlyGenerous,
                                  boolean reportOverlyGenerous )
        {
        FastCat.abortOnOverflow = abortOnOverflow;
        FastCat.reportOverflow = reportOverflow || abortOnOverflow;
        FastCat.abortOnTight = abortOnTight;
        FastCat.reportTight = reportTight || abortOnTight;
        FastCat.abortOnOverlyGenerous = abortOnOverlyGenerous;
        FastCat.reportOverlyGenerous = reportOverlyGenerous || abortOnOverlyGenerous;
        }// /method

    /**
     * append String
     *
     * @param s String to append.
     *          If s is null, nothing happens.
     *          No NullPointerException.
     *          Nothing added to the buffer.
     *
     * @return this
     */
    public FastCat append( String s )
        {
        if ( s == null || s.length() == 0 )
            {
            return this;
            }
        if ( count >= pieces.length )
            {
            overflow();
            }
        totalLength += s.length();
        pieces[ count++ ] = s;
        return this;
        }// /method

    /**
     * append arbitrary number of strings
     *
     * @param ss comma-separated list of Strings to append
     *
     * @return this
     */
    public FastCat append( String... ss )
        {
        if ( ss == null )
            {
            return this;
            }
        for ( String s : ss )
            {
            append( s );
            }
        return this;
        }// /method

    /**
     * append int
     *
     * @param i int to append.
     *
     * @return this
     */
    public FastCat append( int i )
        {
        return append( Integer.toString( i ) );
        }// /method

    /**
     * append char
     *
     * @param c char to append.
     *          If you use this method extensively, you will probably get better performance from StringBuilder.
     *
     * @return this
     */
    public FastCat append( char c )
        {
        if ( count >= pieces.length )
            {
            overflow();
            }
        final String s = String.valueOf( c );
        totalLength += s.length();
        pieces[ count++ ] = s;
        return this;
        }// /method

    /**
     * append Object
     *
     * @param o Object to append.  toString is called to acquire a String to concatenate.
     *
     * @return this
     */
    public FastCat append( Object o )
        {
        if ( o == null )
            {
            return this;
            }
        if ( count >= pieces.length )
            {
            overflow();
            }
        final String s = o.toString();
        totalLength += s.length();
        pieces[ count++ ] = s;
        return this;
        }// /method

    /**
     * append arbitrary number of Objects
     *
     * @param oo comma-separated list of Objects to to append. toString is called to acquire a String to concatenate.
     *
     * @return this
     */
    public FastCat append( Object... oo )
        {
        if ( oo == null )
            {
            return this;
            }
        if ( count + oo.length > pieces.length )
            {
            overflow();
            }
        for ( Object o : oo )
            {
            if ( o != null )
                {
                final String s = o.toString();
                if ( s.length() == 0 )
                    {
                    continue;
                    }
                totalLength += s.length();
                pieces[ count++ ] = s;
                }
            }
        return this;
        }// /method

    /**
     * Optionally check that FastCat size estimate was reasonable, not too tight, not too generous.
     * Call just prior to calling toString.  If FastCat overflows before you get around to calling
     * checkEstimate, you will get an ArrayIndexOutOfBoundsException. In that case, this method will to be involved.
     *
     * @param whereTextCameFrom where generated text came from, some text to use in the warning message printed on
     *                          err.
     *
     * @see #setWhereTextCameFrom
     */
    public void checkEstimate( final String whereTextCameFrom )
        {
        this.whereTextCameFrom = whereTextCameFrom;
        checkEstimate();
        }// /method

    /**
     * Optionally check that FastCat size estimate was reasonable, not too tight, not too generous.
     * Call just prior to calling toString.  If FastCat overflows before you get around to calling
     * checkEstimate, you will get an ArrayIndexOutOfBoundsException. In that case, this method will to be involved.
     */
    public void checkEstimate()
        {
        final int room = room();
        final int used = used();
        final int size = size();
        // these percentages are greater than 100
        final int tightLimit = ( used + EXTRA ) * TIGHT_PERCENT / 100;
        final int generousLimit = ( used + EXTRA ) * GENEROUS_PERCENT / 100;
        final int suggestedSize = ( used + EXTRA ) * SUGGESTED_PERCENT / 100;
        // don't complain if has it bang on or off by up to 5.
        // Presume aiming for bang on rather than leaving room for growth.
        if ( room > 5 )
            {
            if ( size < tightLimit )
                {
                err.println( "\n>>>> WARNING >>>> FastCat estimate tight for " + whereTextCameFrom + "\n" +
                             "                  used: " + used + " + room: " + room + " = size: " + size + " suggest " +
                             "size: " + suggestedSize );
                offerCorrection( suggestedSize );
                }
            // For default size, we don't worry if overly generous.
            else if ( room > generousLimit && size != DEFAULT_SIZE )
                {
                err.println( "\n>>>> WARNING >>>> FastCat estimate generous for " + whereTextCameFrom + "\n" +
                             "                  used: " + used + " + room: " + room + " = size: " + size + " suggest " +
                             "size: " + suggestedSize );
                offerCorrection( suggestedSize );
                }
            }
        }// /method

    /**
     * empty the concatenated String being created
     */
    public void clear()
        {
        // clear out strings so they can be garbage collected. All would work without fill, but it would waste RAM.
        // works ok even if count is 0.
        // are we are doing is setting each of the pieces to a null, thus freeing the associated string for gc
        Arrays.fill( pieces, 0, count, null );
        count = 0;
        totalLength = 0;
        }// /method

    /**
     * drops the last appended string, not the last char. If none, does nothing.
     * May be called repeatedly to peel off previously appended Strings.
     *
     * @return this
     */
    public FastCat drop()
        {
        count--;
        if ( count < 0 )
            {
            count = 0;
            }
        else
            {
            totalLength -= pieces[ count ].length();
            pieces[ count ] = null;
            }
        return this;
        }// /method

    /**
     * dump out the FastCat contents
     */
    public void dump()
        {
        err.println( " FastCat Dump used: " + used() + " length:" + length() + " size: " + size() );
        for ( int i = 0; i < count; i++ )
            {
            int len = pieces[ i ].length();
            if ( len > 90 )
                {
                err.println( i + ": " + pieces[ i ].substring( 0, 45 ) + "..." + pieces[ i ].substring( len - 45, len ) );
                }
            else
                {
                err.println( i + ": " + pieces[ i ] );
                }
            } // /for
        }// /method

    /**
     * current buffer length.
     *
     * @return current total of count of chars appended
     * @see #used()
     */
    public int length()
        {
        return totalLength;
        }// /method

    /**
     * how many unused slots are available for pieces?
     * Not same as total number of chars that could append.
     *
     * @return count of unused slots.
     */
    public int room()
        {
        return pieces.length - count;
        }// /method

    /**
     * Provide text for error messages to help locate what FastCat was working on at the time.
     * It is best to call this right afte the constructor so this string will be available
     * to embed in all error messages.
     *
     * @param whereTextCameFrom e.g a filename where text being concatenated originated
     */
    public void setWhereTextCameFrom( final String whereTextCameFrom )
        {
        this.whereTextCameFrom = whereTextCameFrom;
        }// /method

    /**
     * how many slots (used or unused) are available for pieces?
     * Not same as total number of chars that could append.
     *
     * @return count of unused/unused slots.
     * @see #length()
     * @see #used()
     */
    public int size()
        {
        return pieces.length;
        }// /method

    /**
     * Get the concatenation of all the strings appended so far, separated comma space, with the last
     * pair separated by _and_.
     * Make sure you create each leg with a single append parm.
     * In the constructor you don't need to leave space for the commas or the word " and " .
     *
     * @return all pieces collected concatenated.
     */
    public String toAndList()
        {
        if ( count == 0 )
            {
            return "";
            }
        else if ( count == 1 )
            {
            return pieces[ 0 ];
            }
        int offset = 0;
        final String separatorString = ", ";
        final String finalSeparatorString = " and ";
        final int sepLen = separatorString.length();
        final int finalSepLen = finalSeparatorString.length();
        // leave space in buffer for every piece to have a separator, except the first.
        int buffLen = totalLength + ( count - 2 ) * sepLen + finalSepLen;
        final char[] buffer = new char[ buffLen ];
        for ( int i = 0; i < count; i++ )
            {
            //  first time no sep
            if ( 1 <= i && i <= count - 2 )
                {
                // copy separator char by char to char buffer.
                for ( int j = 0; j < sepLen; j++ )
                    {
                    buffer[ offset++ ] = separatorString.charAt( j );
                    }
                }
            else if ( i == count - 1 )
                {
                // copy separator char by char to char buffer.
                for ( int j = 0; j < finalSepLen; j++ )
                    {
                    buffer[ offset++ ] = finalSeparatorString.charAt( j );
                    }
                }
            final String piece = pieces[ i ];
            final int pieceLen = piece.length();
            if ( pieceLen > TWEAK_ARRAYCOPY_LENGTH )
                {
                // copy piece to char buffer.  Uses System.arraycopy internally.
                // srcbeg srcend dst[] dstoffset
                piece.getChars( 0, pieceLen, buffer, offset );
                offset += pieceLen;
                }
            else
                {
                // copy char by char to char butter.
                for ( int j = 0; j < pieceLen; j++ )
                    {
                    buffer[ offset++ ] = piece.charAt( j );
                    }
                }
            }
        return new String( buffer );  // Would like some way to just hand buffer over to String to avoid copy.
        }// /method

    /**
     * Get the concatenation of all the strings appended so far, separated by commas
     * Make sure you create each leg with a single append parm.
     * In the constructor you don't need to leave space for the commas.
     *
     * @return all pieces collected concatenated.
     * @see #toAndList
     */
    public String toCommaList()
        {
        return toSeparatedList( ',' );
        }// /method

    /**
     * Get the concatenation of all the strings appended so far, separated by an arbitrary char
     * Make sure you create each leg with a single append parm.
     * In the constructor you don't need to leave space for the commas.
     *
     * @param separatorChar eg. '|' to get | space between elements
     *
     * @return all pieces collected concatenated.
     */
    public String toSeparatedList( char separatorChar )
        {
        if ( count == 0 )
            {
            return "";
            }
        int offset = 0;
        final char[] buffer = new char[ totalLength + count - 1 ];
        for ( int i = 0; i < count; i++ )
            {
            if ( i != 0 )
                {
                buffer[ offset++ ] = separatorChar;
                }
            final String piece = pieces[ i ];
            int pieceLen = piece.length();
            if ( pieceLen > TWEAK_ARRAYCOPY_LENGTH )
                {
                // copy piece to char buffer.  Uses System.arraycopy internally.
                piece.getChars( 0, pieceLen, buffer, offset );
                offset += pieceLen;
                }
            else
                {
                // copy char by char to char buffer.
                for ( int j = 0; j < pieceLen; j++ )
                    {
                    buffer[ offset++ ] = piece.charAt( j );
                    }
                }
            }
        return new String( buffer );  // Would like some way to just hand buffer over to String to avoid copy.
        }// /method

    /**
     * Get the concatenation of all the strings appended so far, separated by an arbitrary char
     * Make sure you create each leg with a single append parm.
     * In the constructor you don't need to leave space for the separators.
     *
     * @param separatorString eg. ", " to get comma-space between elements
     *
     * @return all pieces collected concatenated.
     */
    public String toSeparatedList( String separatorString )
        {
        if ( count == 0 )
            {
            return "";
            }
        int offset = 0;
        final int sepLen = separatorString.length();
        // leave space in buffer for every piece to have a separator, except the first.
        final char[] buffer = new char[ totalLength + ( count - 1 ) * sepLen ];
        for ( int i = 0; i < count; i++ )
            {
            if ( i != 0 )
                {
                // copy separator char by char to char buffer.
                for ( int j = 0; j < sepLen; j++ )
                    {
                    buffer[ offset++ ] = separatorString.charAt( j );
                    }
                }
            final String piece = pieces[ i ];
            final int pieceLen = piece.length();
            if ( pieceLen > TWEAK_ARRAYCOPY_LENGTH )
                {
                // copy piece to char buffer.  Uses System.arraycopy internally.
                piece.getChars( 0, pieceLen, buffer, offset );
                offset += pieceLen;
                }
            else
                {
                // copy char by char to char butter.
                for ( int j = 0; j < pieceLen; j++ )
                    {
                    buffer[ offset++ ] = piece.charAt( j );
                    }
                }
            }
        return new String( buffer );  // Would like some way to just hand buffer over to String to avoid copy.
        }// /method

    /**
     * Get the concatenation of all the strings appended so far, separated by spaces
     * Make sure you create each leg with a single append parm or there will be a space after each append.
     * In the constructor you don't need to leave space for the spaces.
     *
     * @return all pieces collected concatenated.
     */
    public String toSpaceList()
        {
        return toSeparatedList( ' ' );
        }// /method

    /**
     * Get the concatenation of all the strings appended so far.
     *
     * @return all pieces collected concatenated.
     */
    public String toString()
        {
        int offset = 0;
        final char[] buffer = new char[ totalLength ];
        for ( int i = 0; i < count; i++ )
            {
            final String piece = pieces[ i ];
            int length = piece.length();
            if ( length > TWEAK_ARRAYCOPY_LENGTH )
                {
                // copy piece to char buffer.  Uses System.arraycopy internally.
                piece.getChars( 0, length, buffer, offset );
                offset += length;
                }
            else
                {
                // copy char by char to char butter.
                for ( int j = 0; j < length; j++ )
                    {
                    buffer[ offset++ ] = piece.charAt( j );
                    }
                }
            }
        return new String( buffer );  // Would like some way to just hand buffer over to String to avoid copy.
        }// /method

    /**
     * how many used slots, slots containing pieces?
     * Not same as total number of chars appended.
     *
     * @return count of used slots.
     * @see #length()
     * @see #size()
     */
    public int used()
        {
        return count;
        }// /method
    // /methods
    }
