/*
 * [Localise.java]
 *
 * Summary: Miscellaneous static methods localising an app to a particular platform under JDK 1.5+.
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
 *  1.0 2009-05-03 initial version, just localise(String)
 */
package com.mindprod.common18;

/**
 * Miscellaneous static methods localising an app to a particular platform under JDK 1.5+.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2009-05-03 initial version, just localise(String)
 * @see com.mindprod.common18.Localise
 * @see com.mindprod.hunkio.PrintWriterPortable
 * @since 2009-05-03
 */
@SuppressWarnings( { "WeakerAccess" } )
public class Localise
    {
    /**
     * lineSeparator using now.
     */
    protected static String lineSeparator = System.getProperty( "line.separator" );

    /**
     * converts \n  in String to configurable  local line separator
     * This is a more efficient implementation of com.mindprod.common18.Localise for JDK 1.5+.
     *
     * @param s string with possible \ns embedded.
     *
     * @return string with \n converted to \r\n for Windows, \a for Mac, \n for Unix etc.
     * @see com.mindprod.common18.Localise#localise(String)
     */
    public static String localise( String s )
        {
        assert !s.contains( "\r" ) : "Localising already localised String";
        // See if we can avoid work of building a new string.
        // No point in testing a long string.  Almost certainly it will need work.
        if ( lineSeparator.equals( "\n" ) || s.length() < 100 && s.indexOf( '\n' ) < 0 )
            {
            return s;
            }
        // StringBuilder is better than FastCat for char by char work
        final StringBuilder sb = new StringBuilder( s.length() + 100 );
        for ( int i = 0; i < s.length(); i++ )
            {
            final char c = s.charAt( i );
            if ( c == '\n' )
                {
                sb.append( lineSeparator );
                }
            else
                {
                sb.append( c );
                }
            }
        return sb.toString();
        }

    /**
     * Use a line separator other than the system default.
     *
     * @param lineSeparator Sequence to separate lines. usually Windows: "\r\n" Unix: "\n" or Mac: "\r". But could be
     *                      anything.
     */
    public static void setLineSeparator( String lineSeparator )
        {
        Localise.lineSeparator = lineSeparator;
        }

    /**
     * converts \r\n  in String to \n.
     * There is a more efficient implementation at unlocalise at com.mindprod.common18.Localise for JDK 1.5+.
     *
     * @param s string with possible \ns embedded.
     *
     * @return string with \n converted to \r\n for Windows, \a for Mac, \n for Unix etc.
     * @see com.mindprod.common18.Localise.localise(String)
     */
    public static String unlocalise( String s )
        {
        // See if we can avoid work of building a new string.
        // No point in testing a long string.  Almost certainly it will need work.
        if ( s.length() < 100 && s.indexOf( '\r' ) < 0 )
            {
            return s;
            }
        final StringBuilder sb = new StringBuilder( s.length() );
        for ( int i = 0; i < s.length(); i++ )
            {
            final char c = s.charAt( i );
            if ( c != '\r' )
                {
                sb.append( c );
                }
            }
        return sb.toString();
        }
    }
