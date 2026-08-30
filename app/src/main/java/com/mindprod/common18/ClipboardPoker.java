/*
 * [ClipboardPoker.java]
 *
 * Summary: Lets us get and put the contents of the clipboard.
 *
 * Copyright: (c) 1999-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  2.3 2009-03-16 tidy code, rename some methods.
 *  2.4 2011-01-04 move to common18. share by ISBN, Quoter. SecurityManager check.
 */
package com.mindprod.common18;

// import sun.security.util.SecurityConstants;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import static java.lang.System.*;

/**
 * Lets us get and put the contents of the clipboard.
 * <p/>
 * This class should never even be loaded if we are running as an Applet.
 * Users of this class must: import java.awt.datatransfer.*; and implement java.awt.datatransfer.ClipboardOwner with at
 * least a dummy lostOwnership method.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.4 2011-01-04 move to common18. share by ISBN, Quoter. SecurityManager check
 * @since 1999
 */
final public class ClipboardPoker
    {
    /* user/owner  of this class must implement java.awt.datatransfer.ClipboardOwner with an
      public void lostOwnership(Clipboard clipboard, Transferable contents); method.
     */

    /**
     * handle to the system clipboard for cut/paste Avoid init, unless method actually called.
     */
    private static Clipboard clipboard;

    /**
     * initialises hook to the system clipboard
     *
     * @param owner the Applet accepting clipboard commands.
     */
    private static void initClipboardHook( Component owner )
        {
        if ( clipboard == null )
            {
            Toolkit t = owner.getToolkit();
            if ( t != null )
                {
                clipboard = t.getSystemClipboard();
                } // end if
            } // end if
        } // end initClipBoard

    /**
     * get current contents of the Clipboard as a String. Returns null if any trouble, or if clip is empty. Not legal in
     * un UNSIGNED Applet.
     *
     * @param owner this Applet containing the clipboard.
     *
     * @return String contents of the clipboard, null if anything goes wrong.
     */
    public static String getClip( Component owner )
        {
        SecurityManager security = System.getSecurityManager();
        if ( security != null )
            {
            // If we are an unsigned Applet, we need permission to look at the
            // clipboard, with Clipboard.getContents, but not to set it.
            try
                {
                // security.checkPermission( SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION );
                }
            catch ( SecurityException e )
                {
                err.println( "OS refused permission to look at the clipboard." );
                return null;
                }
            }
        initClipboardHook( owner );
        if ( clipboard == null )
            {
            err.println( "Could not establish a link with the clipboard." );
            return null;
            }
        try
            {
            try
                {
                Transferable contents = clipboard.getContents( owner );
                if ( contents == null )
                    {
                    err.println( "clipboard empty" );
                    return null;
                    }
                // stringFlavor is when you want an ordinary String.
                // plainTextFlavor is for when you want a StringReader instead
                // of a String
                if ( contents.isDataFlavorSupported( DataFlavor.stringFlavor ) )
                    {
                    // Don't need to worry about a ClassCastException even if
                    // result is null.
                    String s = ( String ) contents.getTransferData( DataFlavor.stringFlavor );
                    return ( s == null || s.length() == 0 ) ? null : s;
                    } // end if
                }
            catch ( UnsupportedFlavorException e )
                {
                err.println( "Clipboard does not contain text." );
                java.awt.Toolkit.getDefaultToolkit().beep();
                return null;
                }
            }
        catch ( Exception e )
            {
            err.println( "Problem accessing the clipboard " + e.getMessage() );
            java.awt.Toolkit.getDefaultToolkit().beep();
            return null;
            }
        return null;
        } // end getClip

    /**
     * Copy data into the Clipboard. We become its owner until somebody else changes the value.
     *
     * @param s     new contents of clipboard, may be null or "". Not legal in an unsigned Applet.
     * @param owner this Applet containing the clipboard.
     */
    public static void setClip( String s, Component owner )
        {
        initClipboardHook( owner );
        if ( clipboard == null )
            {
            return;
            }
        if ( s == null )
            {
            s = "";
            }
        StringSelection contents = new StringSelection( s );
        if ( !( owner instanceof ClipboardOwner ) )
            {
            throw new IllegalArgumentException( owner.getClass().toString() + " owner of clipboard must implement " +
                                                "ClipboardOwner." );
            }
        clipboard.setContents( contents, ( ClipboardOwner ) owner );
        } // end setClip
    }
