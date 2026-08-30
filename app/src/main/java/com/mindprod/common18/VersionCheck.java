/*
 * [VersionCheck.java]
 *
 * Summary: Check that Java version is sufficiently recent.
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
 *  1.2 1998-12-14 add isJavaVersionOK
 *  1.3 1999-08-24 add leftPad, rightPad, smarter rep. isJavaVersionOK now handles 1.3beta.
 *  1.4 2002-08-17 add quoteSQL
 *  1.5 2005-07-14 split off from Misc, allow for compilation with old compiler.
 *  1.6 2006-01-01
 *  1.7 2006-03-04 reformat with IntelliJ, provide Javadoc
 */
package com.mindprod.common18;

import java.awt.Color;
import java.awt.Container;
import java.awt.TextArea;

import static java.lang.System.*;

/**
 * Check that Java version is sufficiently recent.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.7 2006-03-04
 * @since 1997-03-23
 */
public final class VersionCheck
    {
    /**
     * true if you want extra debugging output and test code
     */
    private static final boolean DEBUGGING = false;

    /**
     * VersionCheck contains only static methods.
     */
    private VersionCheck()
        {
        }

    /**
     * Ensures Java runtime version e.g. 1.1.7 is sufficiently recent. Based on code by Dr. Tony Dahlman
     * <adahlman@jps.net>
     *
     * @param wantedMajor  java major version e.g. 1
     * @param wantedMinor  Java minor version e.g. 6
     * @param wantedBugFix Java bugfix version e.g. 9, there is no parm for the _14 level.
     *                     Sun suffers a phobia of changing versions, so uses microversions.
     *
     * @return true if JVM version running is equal to or more recent than (higher than) the level specified.
     */
    public static boolean isJavaVersionOK( int wantedMajor,
                                           int wantedMinor,
                                           int wantedBugFix )
        {
        try
            {
            try
                {
                // java.version will have form 1.1.7A, 11, 1.1., 1.1, 1.3beta,
                // 1.4.2_05 or 1.4.1-rc, 1.7.0
                // It may be gibberish. It may be undefined.
                // We have do deal with all this malformed garbage.
                // Because incompetents run the world,
                // it is not nicely formatted for us in three fields.
                String ver = System.getProperty( "java.version" );
                if ( ver == null )
                    {
                    return false;
                    }
                ver = ver.trim();
                if ( ver.length() < 2 )
                    {
                    return false;
                    }
                int dex = ver.indexOf( '.' );
                if ( dex < 0 )
                    {
                    // provide missing dot
                    ver = ver.charAt( 0 ) + '.' + ver.substring( 1 );
                    dex = 1;
                    }
                int gotMajor = Integer.parseInt( ver.substring( 0, dex ) );
                if ( DEBUGGING )
                    {
                    out.println( "major:" + gotMajor );
                    }
                if ( gotMajor < wantedMajor )
                    {
                    return false;
                    }
                if ( gotMajor > wantedMajor )
                    {
                    return true;
                    }
                // chop off major and first dot.
                ver = ver.substring( dex + 1 );
                // chop trailing "beta"
                ver = ST.chopTrailingString( ver, "beta" );
                // chop trailing "-rc"
                ver = ST.chopTrailingString( ver, "-rc" );
                // chop any trailing _nn
                dex = ver.lastIndexOf( '_' );
                if ( dex >= 0 )
                    {
                    ver = ver.substring( 0, dex );
                    }
                // chop any trailing letter as in 1.1.7A,
                // but convert 1.1.x or 1.1.X to 1.1.9
                char ch = ver.charAt( ver.length() - 1 );
                if ( !Character.isDigit( ch ) )
                    {
                    ver = ver.substring( 0, ver.length() - 1 );
                    if ( ch == 'x' || ch == 'X' )
                        {
                        ver += '9';
                        }
                    }
                // check minor version
                dex = ver.indexOf( '.' );
                if ( dex < 0 )
                    {
                    // provide missing BugFix number as in 1.2 or 1.0
                    ver += ".0";
                    dex = ver.indexOf( '.' );
                    }
                int gotMinor = Integer.parseInt( ver.substring( 0, dex ) );
                if ( DEBUGGING )
                    {
                    out.println( "minor:" + gotMinor );
                    }
                if ( gotMinor < wantedMinor )
                    {
                    return false;
                    }
                if ( gotMinor > wantedMinor )
                    {
                    return true;
                    }
                // was equal, need to examine third field.
                // check bugfix version
                ver = ver.substring( dex + 1 );
                int gotBugFix = Integer.parseInt( ver );
                if ( DEBUGGING )
                    {
                    out.println( "bugFix:" + gotBugFix );
                    }
                return ( gotBugFix >= wantedBugFix );
                }
            catch ( NumberFormatException e )
                {
                if ( DEBUGGING )
                    {
                    out.println( "number format" + e.getMessage() );
                    }
                return false;
                } // end catch
            }
        catch ( StringIndexOutOfBoundsException e )
            {
            if ( DEBUGGING )
                {
                out.println( "out of bounds:" + e.getMessage() );
                }
            return false;
            } // end catch
        } // end isJavaVersionOK

    /**
     * use in a paint routine if Java version is not ok, usually tested statically.
     *
     * @param wantedMajor  java major version e.g. 1
     * @param wantedMinor  Java minor version e.g. 1
     * @param wantedBugFix Java bugfix version e.g. 7
     * @param container    container to add an error message component.
     *
     * @return true if version is ok
     */
    public static boolean isJavaVersionOK( int wantedMajor,
                                           int wantedMinor,
                                           int wantedBugFix,
                                           Container container )
        {
        if ( isJavaVersionOK( wantedMajor, wantedMinor, wantedBugFix ) )
            {
            return true;
            }
        else
            {
            String error =
                    "Error: You need Java "
                    + wantedMajor
                    + "."
                    + wantedMinor
                    + "."
                    + wantedBugFix
                    + " or later to run this Applet.\n"
                    + "You are currently running under Java "
                    + System.getProperty( "java.version" )
                    + ".\n"
                    + "Get the latest Java from http://java.com/en/index.jsp";
            TextArea complain =
                    new TextArea( error, 3, 42, TextArea.SCROLLBARS_NONE );
            complain.setEditable( false );
            complain.setBackground( Color.white );
            complain.setForeground( Color.red );
            complain.setSize( 300, 50 );
            container.setLayout( null );
            container.add( complain );
            err.println( error );
            return false;
            }
        }
    }
