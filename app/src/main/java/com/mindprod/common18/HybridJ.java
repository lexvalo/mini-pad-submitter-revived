/*
 * [HybridJ.java]
 *
 * Summary: Utility to fire up a JFrame application on the Swing EDT.
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
 *  1.0 2006-03-07 initial version.
 */
package com.mindprod.common18;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Utility to fire up a JFrame application on the Swing EDT.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2006-03-07
 * @since 2006-03-07
 */
public final class HybridJ
    {
    /**
     * return code to use when Application exits
     */
    private static int retCode = 0;

    /**
     * Fire up a JFrame on the Swing Thread
     *
     * @param frame             the JFrame to display
     * @param applicationWidth  width of frame
     * @param applicationHeight height of frame body
     */
    public static void fireup( final JFrame frame,
                               final int applicationWidth,
                               final int applicationHeight )
        {
        SwingUtilities.invokeLater( () -> {
            frame.setResizable( true );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.setSize( applicationWidth, applicationHeight );
            frame.validate();
            frame.setVisible( true );
            } );
        }

    /**
     * set return code to use when Application exits
     *
     * @param retCode code for System.exit
     */
    public static void setRetCode( final int retCode )
        {
        HybridJ.retCode = retCode;
        }
    }
