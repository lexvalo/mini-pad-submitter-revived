/*
 * [FontFactory.java]
 *
 * Summary: Modifies Java's logical font mapping. Same as com.mindprod.common18.FontFactory but uses generics.
 *
 * Copyright: (c) 2008-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2008-08-20 initial version
 *  1.1 2009-04-17 use Hashtables, add Arrows.
 */
package com.mindprod.common18;

import java.awt.Font;
import java.util.HashMap;

/**
 * Modifies Java's logical font mapping. Same as com.mindprod.common18.FontFactory but uses generics.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2009-04-17 use Hashtables, add Arrows.
 * @since 2008-08-20
 */
public class FontFactory
    {
    /**
     * font name transformations for non Windows.
     */
    private static final HashMap<String, String> otherTransforms = new HashMap<>( 17 );

    /**
     * font name transformations for Windows.
     */
    private static final HashMap<String, String> vistaTransforms = new HashMap<>( 17 );

    static
        {
        vistaTransforms.put( "Arrows", "Dialog" );   // Calibri has some arrows, but is weak.
        vistaTransforms.put( "Dialog", "Segoe UI" );
        vistaTransforms.put( "DialogInput", "Consolas" );
        vistaTransforms.put( "Monospaced", "Consolas" );
        vistaTransforms.put( "SanSerif", "Arial" );
        vistaTransforms.put( "Serif", "Constantia" );
        vistaTransforms.put( "Unicode", "Dialog" );
        }

    static
        {
        otherTransforms.put( "Arrows", "Dialog" );
        otherTransforms.put( "Unicode", "Dialog" );
        }

    /**
     * Creates a new <code>Font</code> from the specified name, style and
     * point size.  Like new Font, but modifies the mapping of logical fonts.
     * E.g. Dialog will be translated to Segoe-UI
     *
     * @param fontFamilyName the font name.  This can be a font face name or a font
     *                       family name, and may represent either a logical font or a physical
     *                       font
     *                       The family names for logical fonts are: Dialog, DialogInput,
     *                       Monospaced, Serif, or SansSerif.
     * @param style          the style constant for the <code>Font</code>
     *                       The style argument is an integer bitmask that may
     *                       be PLAIN, or a bitwise union of BOLD and/or ITALIC
     *                       (for example, ITALIC or BOLD|ITALIC).
     * @param size           the point size of the <code>Font</code>
     *
     * @return corresponding Font, with logical fonts remapped for Windows Vista to new native high res fonts.
     */
    public static Font build( String fontFamilyName, final int style, final int size )
        {
        // anti-alias fonts elsewhere.  Needs signing
        // System.setProperty( "swing.aatext", "true" );
        String os = System.getProperty( "os.name", "unknown" );
        /* we meddle with Vista and Windows 7 logical font mapping.  Potentially we could meddle with others.
            AIX
            Digital Unix
            FreeBSD
            HP UX
            Irix
            Linux
            Mac OS
            Mac OS X
            MPE/iX
            Netware 4.11
            OS/2
            Solaris
            Windows 2000
            Windows 7
            Windows 95
            Windows 98
            Windows NT
            Windows Vista
            Windows XP
        */
        final String changedFontName = ( os.equals( "Windows Vista" ) || os.equals( "Windows 7" ) ) ? vistaTransforms
                .get(
                        fontFamilyName ) :
                                       otherTransforms.get( fontFamilyName );
        if ( changedFontName != null )
            {
            fontFamilyName = changedFontName;
            }
        // In Future we could potentially also cache fonts, or weakly cache them.
        return new Font( fontFamilyName, style, size );
        }
    }
