/*
 * [CheckPermission.java]
 *
 * Summary: Ensure user has granted permission to do something dangerous in a signed Applet.
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
 *  1.0 2011-10-31 initial version
 */
package com.mindprod.common18;

import java.awt.Color;
import java.awt.Container;
import java.awt.TextArea;
import java.security.Permission;

import static java.lang.System.*;

/**
 * Ensure user has granted permission to do something dangerous in a signed Applet.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2011-10-31 initial version
 * @since 2011-10-31
 */
public final class CheckPermission
    {
    /**
     * CheckPermission contains only static methods.
     */
    private CheckPermission()
        {
        }

    /**
     * Check if user has granted us permission to do something "dangerous", by policy file or general grant.
     * It displays a human-comprehensible error massage if we don't have permission both on the console
     * and on the browser Applet screen. Does not emit any error messages.
     *
     * @param permission Permission for "dangerous" activity we want to do, e.g.
     *                   new PropertyPermission( "user.language", "read,write")
     *                   new PropertyPermission( ""swing.aatext", "write")  to set LAF
     *                   new SocketPermission("www.sun.com", "connect");
     *
     * @return true if we have permission
     */
    public static boolean doWeHavePermissionTo( Permission permission )
        {
        try
            {
            final SecurityManager sm = System.getSecurityManager();
            if ( sm == null )
                {
                // is no security Manager, so we can do whatever we want.
                return true;
                }
            else
                {
                sm.checkPermission( permission, sm.getSecurityContext() );
                return true;
                }
            }
        catch ( SecurityException e )   // usually an AccessControlException
            {
            return false;
            }
        }

    /**
     * Check if user has granted us permission to do something "dangerous", by policy file or general grant.
     * It displays a human-comprehensible error massage if we don't have permission both on the console
     * and on the browser Applet screen. If you call this in an Application it will return false.
     *
     * @param whatWeWantToDo describe in end-user language what we want to do.
     * @param permission     Permission for "dangerous" activity we want to do, e.g.
     *                       new PropertyPermission( "user.language", "read,write")
     *                       new PropertyPermission( ""swing.aatext", "write")  to set LAF
     *                       new SocketPermission("www.sun.com", "connect");
     * @param container      container to add an error message component, usually the main application frame.
     *
     * @return true if we have permission
     */
    public static boolean doWeHavePermissionTo( String whatWeWantToDo, Permission permission, Container container )
        {
        if ( doWeHavePermissionTo( permission ) )
            {
            return true;
            }
        else
            {
            final String error =
                    "Error: You refused to grant this digitally signed program permission to "
                    + whatWeWantToDo
                    + ".\nWithout permission it cannot continue.\nTechnically, the program wants "
                    + permission.toString()
                    + " permission.";
            final TextArea complain = new TextArea( error, 5, 100, TextArea.SCROLLBARS_NONE );
            complain.setEditable( false );
            complain.setBackground( Color.white );
            complain.setForeground( Color.red );
            complain.setSize( 300, 100 );
            container.setLayout( null );
            container.add( complain );
            err.println( error );
            return false;
            }
        }
    }
