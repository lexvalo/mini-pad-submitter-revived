/*
 * [Misc.java]
 *
 * Summary: Simple convenience methods used often by CMP utilities.
 *
 * Copyright: (c) 1997-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 1997-03-23 initial.
 *  1.1 1998-11-10 add dates
 *  1.2 1998-12-14 add isJavaVersionOX
 *  1.3 1999-08-24 add leftPad, rightPad, smarter rep.
 *                 isJavaVersionOK now handles 1.3beta.
 *  1.4 2002-08-17 add quoteSQL
 *  1.5 2005-07-14 trimmed down. moved to com.mindprod.common18
 *                 splitting off ST Limiters VersionCheck
 *  1.6 2006-01-02 added thisYear
 *  1.7 2012-11-02 add nullToEmpty
 *  1.8 2014-04-21 add getExtension, EIO.getCoreName, contains
 *  1.9 2016-05-21 move deleteAndRename to com.mindprod.hunkio.HunkIO
 */
package com.mindprod.common18;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;

import static java.lang.System.*;

/**
 * Simple convenience methods used often by CMP utilities.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.9 2016-05-21 move deleteAndRename to com.mindprod.hunkio.HunkIO
 * @since 2003-05-15
 */
public final class Misc
    {
    // declarations

    /**
     * true if you want extra debugging output and test code
     */
    private static final boolean DEBUGGING = false;
    // /declarations

    /**
     * Misc contains only static methods.
     */
    private Misc()
        {
        }
    // methods

    /**
     * /**
     * makeshift system beep if awt.Toolkit.beep is not available. Works also in JDK 1.02.
     */
    public static void beep()
        {
        out.print( "\007" );
        out.flush();
        }// /method

    /**
     * Compare two floats. Used is sort Comparators
     *
     * @param a first number
     * @param b second number
     *
     * @return a > b => +1; a == b  => 0; a < b  => -1.
     */
    public static int compare( float a, float b )
        {
        if ( a > b )
            {
            return 1;
            }
        else if ( a < b )
            {
            return -1;
            }
        else
            {
            return 0;
            }
        }// /method

    /**
     * Compare two doubles. Used is sort Comparators
     *
     * @param a first number
     * @param b second number
     *
     * @return a > b => +1; a == b  => 0; a < b  => -1.
     */
    public static int compare( double a, double b )
        {
        if ( a > b )
            {
            return 1;
            }
        else if ( a < b )
            {
            return -1;
            }
        else
            {
            return 0;
            }
        }// /method

    /**
     * Compare two longs. Used is sort Comparators
     *
     * @param a first number
     * @param b second number
     *
     * @return a > b => +1; a == b  => 0; a < b  => -1.
     */
    public static int compare( long a, long b )
        {
        if ( a > b )
            {
            return 1;
            }
        else if ( a < b )
            {
            return -1;
            }
        else
            {
            return 0;
            }
        }// /method

    /**
     * Compare two ints. Used is sort Comparators
     *
     * @param a first number
     * @param b second number
     *
     * @return a > b => +1; a == b  => 0; a < b  => -1.
     */
    public static int compare( int a, int b )
        {
        if ( a > b )
            {
            return 1;
            }
        else if ( a < b )
            {
            return -1;
            }
        else
            {
            return 0;
            }
        }// /method

    /**
     * Determines if a gives array of String contains some particular string.
     * Like a miniature version of Set.contains.
     * i.e. is the query in the list?
     * Works with a simple linear search, much like ArrayList.contains.
     *
     * @param possibilities array of possible strings
     * @param query         string to test
     *
     * @return true if query matches one of the string in the array.
     * @see Misc#isQuerySubstringOfAnyPossibility
     * @see Misc@isAnyPossibilitySubstringOfQuery
     */
    public static boolean contains( final String[] possibilities, final String query )
        {
        for ( String possibility : possibilities )
            {
            if ( possibility.equals( query ) )
                {
                return true;
                }
            }
        // no match on any
        return false;
        }// /method

    /**
     * get just the domain part of this URL,  www trimmed. trimmed of the host http://. and filename. includes TLD.
     *
     * @param u URL to extract host/domain from
     *
     * @return domain e.g. mindprod.com  or mindprod.com.au with www. stripped
     */
    public static String getDomain( URL u )
        {
        // see main test harness below to see sample outputs.
        String domain = u.getHost().toLowerCase();
        // the whole thing e.g. www.mindprod.co.uk
        // chop off everything but the last word before the TLD,
        // however www.burns.co.uk -> burns.co.uk
        // ditto com.au, com.br,
        if ( domain.length() < 2 )
            {
            return domain;
            }
        String choppedDomain = domain.substring( 0, domain.length() - 2 );
        if ( choppedDomain.endsWith( ".co." ) || choppedDomain.endsWith( ".com." ) )
            {
            // e.g. ends in in .co.uk or .com.fr
            // e.g.  mindprod^.com.au
            final int secondLastDot = domain.lastIndexOf( ".", domain.length() - 4 );
            // e.g. www^.mindprod.com.au
            final int thirdLastDot = domain.lastIndexOf( ".", secondLastDot - 1 );
            if ( thirdLastDot < 0 )
                {
                return domain;
                }
            else
                {
                return domain.substring( thirdLastDot + 1 );
                }
            }
        else
            {
            // mindprod^.com
            final int lastDot = domain.lastIndexOf( "." );
            if ( lastDot <= 0 )
                {
                return domain;
                }
            else
                {
                // www^.mindprod.com
                final int secondLastDot = domain.lastIndexOf( ".", lastDot - 1 );
                if ( secondLastDot < 0 )
                    {
                    return domain;
                    }
                else
                    {
                    return domain.substring( secondLastDot + 1 );
                    }
                }
            }
        }// /method

    /**
     * get the home url corresponding to this URL
     *
     * @param u URL to extract host name from, e.g. http://mindprod.com/jgloss/jgloss.html#TOP
     *
     * @return e.g. http://mindprod.com  corresponding home
     * @throws MalformedURLException should never happen
     */
    public static URL getHomeURL( URL u ) throws MalformedURLException
        {
        return new URL( u.getProtocol(), u.getHost(), -1, "" ); // must use "" not null for empty field.
        }// /method

    /**
     * find Frame/JFrame enclosing a Component/Container/Dialog. Returns null if can't find one.
     * Useful when you need to pass the enclosing Frame to a JDialog constructor.
     *
     * @return Frame, will typically be a javax.swing.JFrame
     */
    public static Frame getParentFrame( Component child )
        {
        Container c = child.getParent();
        while ( c != null )
            {
            if ( c instanceof Frame )
                {
                return ( Frame ) c;
                }
            c = c.getParent();
            }
        return null;
        }// /method

    /**
     * Returns true if any of the possibility strings is a substring of the query.
     *
     * @param possibilities array of possible strings
     * @param query         string to test
     *
     * @return true if if any of the possibility strings is a substring of the query.
     * @see Misc#contains
     * @see Misc#isQuerySubstringOfAnyPossibility
     */
    public static boolean isAnyPossibilitySubstringOfQuery( final String[] possibilities, final String query )
        {
        for ( String possibility : possibilities )
            {
            if ( query.contains( possibility ) )
                {
                return true;
                }
            }
        // no match on any
        return false;
        }// /method

    /**
     * Is the query is a substring of any of the possibility strings.
     *
     * @param possibilities array of possible strings
     * @param query         string to test
     *
     * @return true if the query is a substring of any of the possibility strings.
     * @see Misc#contains
     * @see Misc@isAnyPossibilitySubstringOfQuery
     */
    public static boolean isQuerySubstringOfAnyPossibility( final String[] possibilities, final String query )
        {
        for ( String possibility : possibilities )
            {
            if ( possibility.contains( query ) )
                {
                return true;
                }
            }
        // no match on any
        return false;
        }// /method

    /**
     * Load a properties file, but not into a Property hashTable, into an array that does not disturb property order.
     * Returns array of string pairs. Closes the given inputstream. Property file might look like something like this: #
     * com.mindprod.inwords.InWords.properties must live in inwords.jar. # Describes languages supported to translate
     * numbers to words. # Fully qualified classname, (without .class)=name on menu (embedded blanks ok) # Everything is
     * case-sensitive. com.mindprod.inwords.Indonesian=Bahahasa Indonesia com.mindprod.inwords.BritishEnglish=British
     * com.mindprod.inwords.Dutch=Dutch com.mindprod.inwords.Esperanto=Esperanto com.mindprod.inwords
     * .AmericanEnglish=North
     * American Vector com.mindprod.inwords.Norwegian=Norwegian com.mindprod.inwords.Swedish=Swedish #-30-
     *
     * @param fis InputStream from which the properties can be read.
     *
     * @return a matrix of properties, keyword one column and value in the other.
     * @throws IOException if cannot read file
     */
    public static String[][] loadProperties( InputStream fis ) throws IOException
        {
        // make them big to start, we will shrink them later to fit.
        String[] left = new String[ 1000 ];
        String[] right = new String[ 1000 ];
        int count = 0;
        // we don't use Properties.load since that would scramble the order.
        StreamTokenizer s =
                new StreamTokenizer( new BufferedReader( new InputStreamReader(
                        fis ) ) );
        // treat space, alpha, numbers and most punctuation as ordinary char
        s.wordChars( ' ', '_' );
        s.commentChar( '#' );
        s.whitespaceChars( '=', '=' );// ignore equal, just separates fields
        s.eolIsSignificant( true );
        while ( true )
            {
            s.nextToken();
            if ( s.ttype == StreamTokenizer.TT_EOF )
                {
                break;
                }
            if ( s.ttype == StreamTokenizer.TT_EOL )
                {
                continue;
                }
            left[ count ] = s.sval.trim();
            s.nextToken();
            right[ count ] = s.sval.trim();
            count++;
            } // end for
        fis.close();
        // prune back arrays to size
        String[][] result = new String[ 2 ][ count ];
        System.arraycopy( left, 0, result[ 0 ], 0, count );
        System.arraycopy( right, 0, result[ 1 ], 0, count );
        return result;
        }// /method

    /**
     * Debug harness
     *
     * @param args not used
     */
    public static void main( String[] args ) throws MalformedURLException
        {
        if ( DEBUGGING )
            {
            out.println( ">" + new File( "E:/mindprod/jgloss" ).getName() + "<" );  //   >jgloss<
            out.println( ">" + new File( "E:/mindprod/jgloss/x.html" ).getName() + "<" );  // >x.html<
            out.println( getDomain( new URL( "http://www.xxx.mindprod.com" ) ) ); //  mindprod.com
            out.println( getDomain( new URL( "http://mindprod.com" ) ) ); //   mindprod.com
            out.println( getDomain( new URL( "http://mindprod.com/jgloss" ) ) ); // mindprod.com
            out.println( getDomain( new URL( "http://mindprod.co.uk" ) ) );  //  mindprod.co.uk
            out.println( getDomain( new URL( "http://type.mindprod.co.uk" ) ) );  // mindprod.co.uk
            out.println( getDomain( new URL( "https://t-shirtwholesaler.com/dept/shirts.html" ) ) ); // t-shirtwholesaler.com
            }
        }// /method

    /**
     * Tidy the URL encoding, simplified.
     *
     * @param URL URLString to encode. May already be encoded.
     *
     * @return encoded String
     */
    public static String miniURLEncode( String URL )
        {
        // Can't use FastCat. It require 1.5
        final StringBuilder sb = new StringBuilder( URL.length() + 20 );
        for ( int i = 0; i < URL.length(); i++ )
            {
            char c = URL.charAt( i );
            switch ( c )
                {
                case ' ':
                case '{':
                case '}':
                case '|':
                    sb.append( '%' );
                    sb.append( ST.toLZHexString( c, 2 ).toUpperCase() );
                    break;
                default:
                    // leave as is
                    sb.append( c );
                }
            }
        return sb.toString();
        }// /method

    /**
     * represent null as a special string
     *
     * @param nullRepresentation String to represent null
     * @param s                  String to represent
     *
     * @return normally s, but nullString if s is null
     */
    public static String nullAs( final String nullRepresentation, final String s )
        {
        if ( s == null )
            {
            return nullRepresentation;
            }
        else
            {
            return s;
            }
        }// /method

    /**
     * represent null as as an empty string ""
     *
     * @param s String to represent
     *
     * @return normally s, but " if s is null
     * @see ST#isEmpty(String)
     */
    public static String nullToEmpty( final String s )
        {
        if ( s == null )
            {
            return "";
            }
        else
            {
            return s;
            }
        }// /method

    /**
     * parse a string to produce a boolean, often from a SET variable or a boolean macro parameter o9 a csv file.
     *
     * @param s            one of "yes", "true", "Y", "T", "1" ,"+" for true
     *                     or null, "",  "no", "false", "N", "F", "0", "-" for false, case-insensitive.
     * @param defaultValue if the value is null, return this value
     *
     * @return whether the string represents true or false.
     * @throws java.lang.NumberFormatException if s is not an int.
     */
    public static boolean parseBoolean( final String s, final boolean defaultValue )
        {
        if ( ST.isEmpty( s ) )
            {
            return defaultValue;
            }
        else
            {
            final String cleaned = s.trim().toLowerCase();
            switch ( cleaned )
                {
                case "yes":
                case "true":
                case "y":
                case "t":
                case "1":
                case "+":
                    return true;
                case "no":
                case "false":
                case "n":
                case "f":
                case "0":
                case "-":
                    return false;
                default:
                    throw new NumberFormatException( "Boolean value " + cleaned + " must be one of true(\"yes\", \"true\", \"Y\", \"T\", \"1\", \"+\") "
                                                     + "or false:(\"no\", \"false\", \"N\", \"F\", \"0\", \"-\", \"\", null)" );
                }
            }
        }// /method

    /**
     * parse a string to produce int , often from a SET variable or a boolean macro parameter o9 a csv file.
     *
     * @param s            int string
     * @param defaultValue if the value is null, return this value
     *
     * @return value
     */
    public static int parseInt( final String s, final int defaultValue ) throws NumberFormatException
        {
        if ( ST.isEmpty( s ) )
            {
            return defaultValue;
            }
        else
            {
            return Integer.parseInt( s.trim() );
            }
        }// /method

    /**
     * parse a string to produce an int, often from a SET variable
     */
    public static int parseInt( String s )
        {
        if ( s == null )
            {
            return 0;
            }
        else
            {
            return Integer.parseInt( s );
            }
        }

    /**
     * alternate to signum for use in compare. Not a true signum, since it returns ints other than +-1. Where there is
     * any possibility of overflow, you should compare two longs with < rather than subtraction. In Pentium assembler
     * you could implement this algorithm with following code:
     * <p/>
     * <pre>
     *  diff = edx:eax result = eax
     *  mov ebx,eax
     *  shl eax,1
     *  or  eax,ebx
     *  slr eax,1
     *  or  eax,edx
     *  which would take 5 cycles, 2 more that lohi.  However, JET did even
     * better,
     *  with code essentially this using a clever trick to implement piotr.
     *   lea    ecx,0(eax,eax)  ; shifts lo left by doubling, keeps copy of
     * lo
     *   or     eax,ecx
     *   shr    eax,1
     *   or     eax,edx
     *  This is 4 cycles, still one more than lohi. Why was Piotr so much
     * faster
     * on JET?
     *  Piotr has no pipeline-confounding jumps. Further, the lo then high
     * operands actually
     *  come from the ram-based stack. Piotr nicely separates the accesses
     * giving plenty of  motive
     *  for preemptive fetch of hi. lohi insists on having them both
     * upfront,
     * so it has to wait
     *  for memory access. Piotr does not have to wait.
     *  Modern CPUs hurry up and wait for RAM most of the time.
     * </pre>
     *
     * @param diff number to be collapsed to an int preserving sign and zeroness. usually represents the difference of
     *             two long.
     *
     * @return sign of diff, some -ve int, 0 or some -ve int.
     * created with Intellij Idea
     * author Peter Kobzda
     */
    public static int signum( long diff )
        {
        return ( int ) ( diff >>> 32 ) | ( ( int ) diff | ( int ) diff << 1 ) >>> 1;
        }// /method

    /**
     * Collapse magnitude down to +1, 0 or -1 .
     *
     * @param diff number to test.
     *
     * @return positive => +1; 0 => 0; negative => -1.
     */
    public static int signum( int diff )
        {
        return diff > 0 ? 1 : ( diff < 0 ? -1 : 0 );
        }

    /**
     * Collapse magnitude down to +1, 0 or -1 .
     *
     * @param diff number to test.
     *
     * @return positive => +1; 0 => 0; negative => -1.
     */
    public static int signum( double diff )
        {
        if ( diff > 0 )
            {
            return +1;
            }
        if ( diff < 0 )
            {
            return -1;
            }
        return 0;
        }// /method

    /**
     * Collapse magnitude down to +1, 0 or -1 .
     *
     * @param diff number to test.
     *
     * @return positive => +1; 0 => 0; negative => -1.
     */
    public static int signum( float diff )
        {
        if ( diff > 0 )
            {
            return 1;
            }
        else if ( diff < 0 )
            {
            return -1;
            }
        else
            {
            return 0;
            }
        }// /method

    /**
     * Get this day e.g. 1=1st of month using default TimeZone
     *
     * @return dd 1=first day of month.
     */
    public static int thisDayOfMonth()
        {
        return new GregorianCalendar().get( GregorianCalendar.DAY_OF_MONTH );
        }// /method

    /**
     * Get this month e.g. 1=January using default TimeZone
     *
     * @return mm 1=Jan
     */
    public static int thisMonth()
        {
        return new GregorianCalendar().get( GregorianCalendar.MONTH ) + 1;
        }// /method

    /**
     * Get this year e.g. 2008 using default TimeZone
     *
     * @return yyyy
     */
    public static int thisYear()
        {
        return new GregorianCalendar().get( GregorianCalendar.YEAR );
        }// /method

    /**
     * call just before System.exit( 0 ) to find out if there was more than one thread.
     */
    public static void trackLastThread()
        {
        final int activeCount = Thread.activeCount();
        if ( activeCount <= 1 )
            {
            return;
            }
        final Thread[] ta = new Thread[ activeCount ];
        Thread.enumerate( ta );
        // list just the user threads, not the daemons
        int userCount = 0;
        for ( Thread t : ta )
            {
            if ( !t.isDaemon() )
                {
                userCount++;
                }
            }
        if ( userCount <= 1 )
            {
            return;
            }
        for ( Thread t : ta )
            {
            if ( !t.isDaemon() )
                {
                out.println( "U S E R   T H R E A D" );
                out.println( t.toString() + " : " + t.getState() );
                final StackTraceElement[] s = t.getStackTrace();
                for ( StackTraceElement e : s )
                    {
                    out.println( e.toString() );
                    }
                out.println();
                }
            }
        }// /method

    /**
     * tell you where in the code you are, class/method/line
     *
     * @param depth Set to 1 if you call youAreHere directly.
     *              Set to 2 if you call it indirectly via a piece of code that
     *              does something with the String it generates, and you
     *              really want the location of its caller.
     */
    public static String youAreHere( int depth )
        {
        final Throwable t = new Throwable();
        final StackTraceElement[] es = t.getStackTrace();
        final StackTraceElement e = es[ depth ];
        return "at "
               + e.getClassName()
               + "."
               + e.getMethodName()
               + " line:"
               + e.getLineNumber();
        }// /method
    // /methods
    }
