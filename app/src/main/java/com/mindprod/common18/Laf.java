/*
 * [Laf.java]
 *
 * Summary: Methods for selecting Look and Feel.
 *
 * Copyright: (c) 2010-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2010-03-22 initial version
 */
package com.mindprod.common18;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.AccessControlException;
import java.util.prefs.Preferences;

import static java.lang.System.*;

/**
 * Methods for selecting Look and Feel.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2010-03-22 initial version
 * @since 2010-03-22
 */
public final class Laf
    {
    // use setting up buildLookAndFeelMenu to let user to select LAF

    /**
     * get current L&F
     *
     * @return L&F currently in effect
     */
    private static UIManager.LookAndFeelInfo getCurrentLookAndFeel()
        {
        final LookAndFeel current = UIManager.getLookAndFeel();
        final String currentLAFName = current.getName();
        final String currentLAFClassName = current.getClass().getName();
        if ( currentLAFName.length() > 0 )
            {
            return new UIManager.LookAndFeelInfo( currentLAFName, currentLAFClassName );
            }
        else
            {
            return null;
            }
        }

    /**
     * fetch the persisted L&F the user has selected previously
     *
     * @return L&F user chose last time, null if none.
     */
    private static UIManager.LookAndFeelInfo getPersistedLookAndFeel()
        {
        final Preferences lafPref = Preferences.userRoot().node( "/com/mindprod/common18/laf" );
        final String persistedLAFName = lafPref.get( "lafName", "" );
        final String persistedLAFClassName = lafPref.get( "lafClassName", "" );
        if ( persistedLAFName.length() > 0 )
            {
            final UIManager.LookAndFeelInfo laf = new UIManager.LookAndFeelInfo( persistedLAFName,
                    persistedLAFClassName );
            if ( isLookAndFeelSupported( laf ) )
                {
                return laf;
                }
            else
                {
                return null;
                }
            }
        else
            {
            return null;
            }
        }

    /**
     * Get the preferred LookAndFeel, default before the user has expressed a preference.
     *
     * @return look and feel, null if none of desirable ones are supported.
     */
    private static UIManager.LookAndFeelInfo getPreferredLookAndFeel()
        {
        // sorted in order with most preferable first.
        final String preferredClassNames[] = {
                "ch.randelshofer.quaqua.QuaquaLookAndFeel",  // will it ever be in the list??
                "com.sun.java.swing.plaf.mac.MacLookAndFeel",
                "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel",
                "javax.swing.plaf.metal.MetalLookAndFeel",  // aka cross platform
                // "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
                // "com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
                // "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
                UIManager.getSystemLookAndFeelClassName() };
        // find best/first L&F that is supported.
        final UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
        for ( final String preferredClassName : preferredClassNames )
            {
            for ( UIManager.LookAndFeelInfo anInstalled : installed )
                {
                if ( preferredClassName.equals( anInstalled.getClassName() ) )
                    {
                    return new UIManager.LookAndFeelInfo( anInstalled.getName(), preferredClassName );
                    }
                }
            }
        return null;
        }

    /**
     * is this L&F supported
     *
     * @param laf to check if is supported
     *
     * @return true laf is supported.
     */
    private static boolean isLookAndFeelSupported( UIManager.LookAndFeelInfo laf )
        {
        final UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
        for ( UIManager.LookAndFeelInfo info : installed )
            {
            if ( isSameLookAndFeel( info, laf ) )
                {
                return true;
                }
            }
        return false;
        }

    /**
     * do two L&Fs match ?
     *
     * @param a first L&F
     * @param b second L&F
     *
     * @return true if both the name and className match
     */
    private static boolean isSameLookAndFeel( UIManager.LookAndFeelInfo a, UIManager.LookAndFeelInfo b )
        {
        return a == b || a != null && b != null && a.getName().equals( b.getName() ) && a.getClassName().equals( b
                .getClassName() );
        }

    /**
     * save L&F using the persistence mechanism
     *
     * @param laf L&F to persist
     */
    private static void persistLookAndFeel( UIManager.LookAndFeelInfo laf )
        {
        Preferences lafPref = Preferences.userRoot().node( "/com/mindprod/common18/laf" );
        lafPref.put( "lafName", laf.getName() );
        lafPref.put( "lafClassName", laf.getClassName() );
        }

    /**
     * make sure all Frames and Windows are repainted with the new Look and Feel.
     */
    private static void propagateLookAndFeelChange()
        {
        // propagate new L&F to all frames.
        final Frame frames[] = Frame.getFrames();
        if ( frames.length == 0 )
            {
            return;
            }
        // refresh all Frames in the app
        for ( Frame frame : frames )
            {
            SwingUtilities.updateComponentTreeUI( frame );
            Window windows[] = frame.getOwnedWindows();
            // refresh all windows and dialogs of the frame
            for ( Window window : windows )
                {
                SwingUtilities.updateComponentTreeUI( window );
                }
            }
        // Restore decoration to outermost JFrame.
        try
            {
            final JFrame outerFrame = ( JFrame ) frames[ 0 ];
            outerFrame.setVisible( false );
            outerFrame.dispose();
            outerFrame.setUndecorated( false );
            outerFrame.getRootPane().setWindowDecorationStyle( JRootPane.NONE );
            outerFrame.setVisible( true );
            }
        catch ( ClassCastException e )
            {
            // outer frame was not a JFrame. Does not have a bar to decorate.
            }
        }

    /**
     * change the current look and feel.
     * If the class is invalid, the command is ignored.
     *
     * @param lookAndFeelClassName name of Look and Feel class
     */
    private static void setLookAndFeel( String lookAndFeelClassName )
        {
        // if already set, nothing to do
        if ( UIManager.getLookAndFeel().getClass().getName().equals( lookAndFeelClassName ) )
            {
            return;
            }
        try
            {
            UIManager.setLookAndFeel( lookAndFeelClassName );
            }
        catch ( Exception exception )
            {
            err.println( "Setting Look and Feel failed" );
            err.println( exception.getMessage() );
            }
        }

    /**
     * build JMenu to allow user to choose a L&F. It must be installed in a JMenuBar.
     * USE REQUIRES APPLET TO BE SIGNED. We set system property and persist the chosen LAF.
     *
     * @return the JMenu containing all the supported Look and Feels
     */
    public static JMenu buildLookAndFeelMenu()
        {
        try
            {
            // anti-alias fonts
            System.setProperty( "swing.aatext", "true" );
            final JMenu menuLAF = new JMenu( "Look & Feel" );
            ActionListener changeLAFListener = new ActionListener()
                {
                public void actionPerformed( final ActionEvent e )
                    {
                    final JRadioButtonMenuItem rbmi = ( JRadioButtonMenuItem ) e.getSource();
                    setLookAndFeel( rbmi.getActionCommand() );
                    propagateLookAndFeelChange();
                    persistLookAndFeel( new UIManager.LookAndFeelInfo( rbmi.getText(), rbmi.getActionCommand() ) );
                    }
                };
            UIManager.LookAndFeelInfo laf = Laf.getPersistedLookAndFeel();
            if ( laf == null )
                {
                laf = Laf.getPreferredLookAndFeel();
                if ( laf == null )
                    {
                    laf = Laf.getCurrentLookAndFeel();
                    }
                }
            setLookAndFeel( laf.getClassName() );
            // we don't do a propagateLookAndFeelChange since the GUI is not realised yet.
            final ButtonGroup bg = new ButtonGroup();
            final UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
            for ( UIManager.LookAndFeelInfo info : installed )
                {
                final JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem( info.getName(),
                        isSameLookAndFeel( info, laf ) );
                menuLAF.add( rbmi );
                bg.add( rbmi );
                rbmi.setActionCommand( info.getClassName() );
                rbmi.addActionListener( changeLAFListener );
                }
            return menuLAF;
            }
        catch ( AccessControlException e )
            {
            err.println( "Look and Feel changes are not available because you refused permission." );
            return null;
            }
        }

    /**
     * test harness
     *
     * @param args not used
     *
     * @noinspection EmptyMethod
     */
    public static void main( String[] args )
        {
        // dummy
        }
    }
