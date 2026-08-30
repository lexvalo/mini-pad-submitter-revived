/*
 * [JEButton.java]
 *
 * Summary: A JButton with some default attributes.
 *
 * Copyright: (c) 2007-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2007-08-25 Created with IntelliJ IDEA.
 */
package com.mindprod.common18;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
import java.awt.Font;

/**
 * A JButton with some default attributes.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2007-08-25 Created with IntelliJ IDEA.
 * @noinspection WeakerAccess
 * @since 2007-08-25
 */
public final class JEButton extends JButton
    {
    /**
     * border for a button
     */
    private static final Border border;

    /**
     * bold font for a button
     */
    private static final Font boldFont = FontFactory.build( "Arrows", Font.BOLD, 16 );

    /**
     * plain font for a button
     */
    private static final Font plainFont = FontFactory.build( "Arrows", Font.PLAIN, 14 );

    static
        {
        final Border innerBorder =
                BorderFactory.createEmptyBorder( 2, 5, 2, 5 );
        final Border outerBorder = BorderFactory.createRaisedBevelBorder();
        border = BorderFactory.createCompoundBorder( outerBorder, innerBorder );
        }

    /**
     * constructor
     *
     * @param text string to label the JButton
     */
    public JEButton( String text )
        {
        this( text, true );
        }

    /**
     * constructor
     *
     * @param text string to label the JButton
     * @param bold true if you want bold font.
     */
    public JEButton( String text, boolean bold )
        {
        super( text );
        this.setFocusPainted( false );
        this.setBorder( border );
        this.setFont( bold ? boldFont : plainFont );
        // setMargin does not work, when you have a Border, because
        // the margin is a implemented as a species of Border.
        // Leave foreground and background alone to get gradient in metal L&F.
        // Client should do a setToolTipText, possibly a requestFocus
        }
    }
