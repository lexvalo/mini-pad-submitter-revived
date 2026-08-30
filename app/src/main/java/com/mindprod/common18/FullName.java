/*
 * [FullName.java]
 *
 * Summary: Parses a full name into given name, surname and title.
 *
 * Copyright: (c) 2012-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2012-07-31 initial version
 */
package com.mindprod.common18;

import com.mindprod.fastcat.FastCat;

import java.util.Arrays;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Parses a full name into given name, surname and title.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2012-07-31 initial version
 * @since 2012-07-31
 */
public class FullName
    {
    /**
     * list of possible suffixes
     */
    private static final HashSet<String> POSSIBLE_SUFFIXES =
            new HashSet<>( Arrays.asList(
                    "Esquire",
                    "III",
                    "Jr",
                    "Jr.",
                    "Sr",
                    "Sr."
            ) );

    /**
     * list of possible titles
     */
    private static final HashSet<String> POSSIBLE_TITLES =
            new HashSet<>( Arrays.asList(
                    "Admiral",
                    "Bishop",
                    "Captain",
                    "Dr.",
                    "General",
                    "Pastor",
                    "Prof",
                    "Professor",
                    "Sir",
                    "St."
            ) );

    private final String fullName;

    private String givenName;

    private String middleNames;

    /**
     * e.g. Jr.
     */
    private String suffix;

    private String surname;

    private String title;

    /**
     * constructor, also parse fullName to find title, givenName, surname
     *
     * @param fullName e.g. Prof Richard Dawkins. Just one name, possibly with title, middle name, suffix.
     */
    public FullName( final String fullName )
        {
        this.fullName = fullName;
        // if there is more than one name, drop all but first.
        String remainder = fullName;
        final String possTitle = ST.firstWord( remainder );
        if ( POSSIBLE_TITLES.contains( possTitle ) )
            {
            this.title = possTitle;
            remainder = ST.chopLeadingString( remainder, possTitle );
            }
        final String possSuffix = ST.lastWord( remainder );
        if ( POSSIBLE_SUFFIXES.contains( possSuffix ) )
            {
            this.suffix = possSuffix;
            remainder = ST.chopTrailingString( remainder, possSuffix ).trim();
            }
        final String possGivenName = ST.firstWord( remainder );
        if ( possGivenName.length() > 0 )
            {
            this.givenName = possGivenName;
            remainder = ST.chopLeadingString( remainder, possGivenName ).trim();
            }
        final String possSurname = ST.lastWord( remainder );
        if ( possSurname.length() > 0 )
            {
            this.surname = possSurname;
            remainder = ST.chopTrailingString( remainder, surname ).trim();
            }
        if ( ST.firstWord( remainder ).equals( "of" ) )
            {
            // e.g.  Xenophanes of Kolophon
            this.givenName = possSurname;
            this.surname = possGivenName;
            }
        final String possVan = ST.lastWord( remainder );
        if ( possVan.equalsIgnoreCase( "van" ) )
            {
            this.surname = possVan + " " + possSurname;
            remainder = ST.chopTrailingString( remainder, possVan ).trim();
            }
        // what's left is middle names
        if ( remainder.length() > 0 )
            {
            this.middleNames = remainder;
            }
        }

    /**
     * test harness
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        FullName f = new FullName( "Dr. Roedy Peter Frederic Salmon MacGreen Jr." );
        f.dump();
        out.println( f.getSortable() );
        }

    /**
     * debugging tool to see all parsed fields for a given name
     */
    public void dump()
        {
        out.println( "[" + fullName + "] t:" + title + " g:" + givenName + " m:" + middleNames + " s:" + surname + " " +
                     "x:" + suffix );
        }

    /**
     * get given name, null if none
     */
    public String getGivenName()
        {
        return givenName;
        }

    /**
     * get middle names, null if none
     */
    public String getMiddleNames()
        {
        return middleNames;
        }

    /**
     * get a key all in upper case to sort this name by surname/given name.
     */
    public String getSortable()
        {
        final FastCat sb = new FastCat( 11 );
        if ( surname != null )
            {
            if ( surname.startsWith( "d\u2019" ) )     // d &rsquo;
                {
                sb.append( surname.substring( 2 ).toUpperCase() );
                }
            else if ( surname.startsWith( "Mac" ) )
                {
                sb.append( "MC" );
                sb.append( surname.substring( 3 ).toUpperCase() );
                }
            else
                {
                sb.append( surname.toUpperCase() );
                }
            sb.append( " " );
            }
        if ( suffix != null )
            {
            sb.append( suffix.toUpperCase() );
            sb.append( " " );
            }
        if ( givenName != null )
            {
            sb.append( givenName.toUpperCase() );
            sb.append( " " );
            }
        if ( title != null )
            {
            sb.append( title.toUpperCase() );
            sb.append( " " );
            }
        if ( middleNames != null )
            {
            sb.append( middleNames.toUpperCase() );
            }
        return sb.toString().trim();
        }

    /**
     * get suffix e.g. Jr. null if none.
     *
     * @return the suffix
     */
    public String getSuffix()
        {
        return suffix;
        }

    /**
     * get surname name, null if none
     */
    public String getSurname()
        {
        return surname;
        }

    /**
     * get title e.g. Dr. null if none.
     *
     * @return the title
     */
    public String getTitle()
        {
        return title;
        }
    }
