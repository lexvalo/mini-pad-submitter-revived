/*
 * [RobustJEditorPane.java]
 *
 * Summary: Robust version af JEditorPane that does not complain if rogue HTML rendered generates SecurityExceptions.
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
 *  1.0 2009-03-15
 */
package com.mindprod.submitter;

import javax.swing.JEditorPane;
import java.awt.Graphics;

import static java.lang.System.*;

/**
 * Robust version af JEditorPane that does not complain if rogue HTML rendered generates SecurityExceptions.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2009-03-15
 * @since 2009-03-15
 */
@SuppressWarnings( { "WeakerAccess" } )
public class RobustJEditorPane extends JEditorPane
    {
    @Override
    protected void paintComponent( final Graphics g )
        {
        try
            {
            super.paintComponent( g );
            }
        catch ( SecurityException e )
            {
            // just ignore errors trying to render rogue HTML
            }
        catch ( Exception e )
            {
            err.println( "serious error rendering:" + e.getMessage() );
            }
        }

    @Override
    protected void printComponent( final Graphics g )
        {
        try
            {
            super.printComponent( g );
            }
        catch ( SecurityException e )
            {
            // just ignore errors trying to render rogue HTML
            }
        catch ( Exception e )
            {
            err.println( "serious error rendering:" + e.getMessage() );
            }
        }
    }
