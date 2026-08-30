/*
 * [Common18.java]
 *
 * Summary: Common methods for JDK 1.8+.
 *
 * Copyright: (c) 2005-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  2.9 2012-11-06 fix bug in nullSafeStringCompare. It was ignoring case.
 *  3.0 2014-04-29 add quoteForReplace to quote Regex replacement Strings,
 *                 stripNaughtyCharacters, reorderLetters, deDupLetters
 *  3.1 2016-02-22 add last and tail methods
 */
package com.mindprod.common18;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.System.*;

/**
 * Common methods for JDK 1.8+.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.1 2015-11-02 add last and tail methods
 * @since 2005
 */
public final class Common18
    {
    private static final int FIRST_COPYRIGHT_YEAR = 2005;

    /**
     * undisplayed copyright notice
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2005-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * when package released.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2016-02-22";

    /**
     * embedded version string.
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String VERSION_STRING = "3.1";

    /**
     * used to format in Zulu time
     */
    private static final SimpleDateFormat ZULUSDF = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );

    private static final boolean DEBUGGING = false;

    /**
     * Attempt to set the LAF, depending on OS and JDK being suitable. Might require signing.
     *
     * @param os           name of OS, null means it does not matter.
     * @param wantedMajor  java major version e.g. 1
     * @param wantedMinor  Java minor version e.g. 6
     * @param wantedBugFix Java bugfix version e.g. 14
     * @param lafClassName class name of laf to set if os matches and JDK version is sufficiently advanced.
     *
     * @return true the setting took, false if failed.
     */
    private static boolean setLaf( String os, int wantedMajor,
                                   int wantedMinor,
                                   int wantedBugFix,
                                   String lafClassName )
        {
        if ( os != null && !System.getProperty( "os.name", "unknown" ).equals( os ) )
            {
            return false;
            }
        if ( !VersionCheck.isJavaVersionOK( wantedMajor, wantedMinor, wantedBugFix ) )
            {
            return false;
            }
        try
            {
            UIManager.setLookAndFeel( lafClassName );
            }
        catch ( UnsupportedLookAndFeelException e )
            {
            if ( DEBUGGING )
                {
                err.println( "fail: UnsupportedLookAndFeelException" + lafClassName + e.getMessage() );
                }
            return false;
            }
        catch ( IllegalAccessException e )
            {
            if ( DEBUGGING )
                {
                err.println( "fail: IllegalAccessException" + lafClassName + e.getMessage() );
                }
            return false;
            }
        catch ( InstantiationException e )
            {
            if ( DEBUGGING )
                {
                err.println( "fail: InstantiationException" + lafClassName + e.getMessage() );
                }
            return false;
            }
        catch ( ClassNotFoundException e )
            {
            if ( DEBUGGING )
                {
                err.println( "fail: ClassNotFoundException" + lafClassName + e.getMessage() );
                }
            return false;
            }
        catch ( Exception e )
            {
            if ( DEBUGGING )
                {
                err.println( "fail: Exception" + lafClassName + e.getMessage() );
                }
            return false;
            }
        if ( DEBUGGING )
            {
            err.println( "Choosing L&F " + lafClassName );
            }
        return true;
        }

    /**
     * TEST harness
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        out.println( "Common18 should not be run standalone." );
        }

    /**
     * set the look and feel for a Swing App, based on OS and JDK version..
     */
    public static void setLaf()
        {
        if ( setLaf( "Mac OS X", 1, 4, 0, "ch.randelshofer.quaqua.QuaquaLookAndFeel" ) )
            {
            // will only work if user has installed the 1.7 MB quaqua.jar himself.
            return;
            }
        if ( setLaf( "Mac OS X", 1, 3, 0, "com.sun.java.swing.plaf.mac.MacLookAndFeel" ) )
            {
            return;
            }
        if ( setLaf( "Mac OS", 1, 3, 0, UIManager.getSystemLookAndFeelClassName() ) )
            {
            return;
            }
        if ( setLaf( "Linux", 1, 5, 0, "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" ) )
            {
            return;
            }
        if ( setLaf( "Windows 7", 1, 3, 0, UIManager.getCrossPlatformLookAndFeelClassName() ) )
            {
            return;
            }
        if ( setLaf( "Windows Vista", 1, 3, 0, UIManager.getCrossPlatformLookAndFeelClassName() ) )
            {
            return;
            }
        if ( setLaf( "Windows XP", 1, 3, 0, UIManager.getCrossPlatformLookAndFeelClassName() ) )
            {
            return;
            }
        if ( setLaf( "Windows 2000", 1, 3, 0, UIManager.getCrossPlatformLookAndFeelClassName() ) )
            {
            return;
            }
        if ( setLaf( null, 1, 6, 0, "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel" ) )
            {
            // won't take for anything less than 1.6.0
            return;
            }
        setLaf( null, 1, 3, 0, UIManager.getSystemLookAndFeelClassName() );
        // if even that failed, give up. leave original L&F in place.
        }

    /**
     * convert to Zulu time format
     *
     * @param timestamp milliseconds since 1970-01-01 00:00
     *
     * @return string formatted as 2008-06-22T14:51:18.62Z
     */
    public static String toZulu( long timestamp )
        {
        // Zulu is UTC 24-hour time, no DST.
        final TimeZone utc = TimeZone.getTimeZone( "UTC" );
        ZULUSDF.setTimeZone( utc );
        final String milliformat = ZULUSDF.format( new Date( timestamp ) );
        // convert from milliseconds to centiseconds
        // by chopping off last digit
        return milliformat.substring( 0, 22 ) + 'Z';
        }
    }
