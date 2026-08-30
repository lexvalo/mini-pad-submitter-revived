/*
 * [CMPAboutBox.java]
 *
 * Summary: An AWT About box that truly tells you about the program, not just the author's name.
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
 *  1.2 2010-01-16 Java version and VM version
 */
package com.mindprod.common18;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * An AWT About box that truly tells you about the program, not just the author's name.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.2 2010-01-16 Java version and VM version
 * @noinspection FieldCanBeLocal
 * @since 1999
 */
public final class CMPAboutBox extends Dialog
    {
    /**
     * true if debugging, and want extra output
     */
    private static final boolean DEBUGGING = false;

    private static final int FIRST_COPYRIGHT_YEAR = 1999;

    /**
     * height of the about box not counting frame
     */
    private static final int HEIGHT = 400;

    /**
     * width of the about box not counting frame
     */
    private static final int WIDTH = 490;

    private static final Color BLACK = Color.black;

    private static final Color DARK_GREEN = new Color( 0x008000 );

    private static final Color FOREGROUND_FOR_LABEL = new Color( 0x0000b0 );

    /**
     * for titles
     */
    private static final Color FOREGROUND_FOR_TITLE = new Color( 0xdc143c );

    private static final Color WHITE = Color.white;

    /**
     * for for titles and About buttons
     */
    private static final Font FONT_FOR_TITLE = FontFactory.build( "Dialog", Font.BOLD, 16 );

    /**
     * button to dismiss the dialog
     */
    private Button dismissButton;

    /**
     * line 1 of CMP mailing address
     */
    private Label showAddr1;

    /**
     * line 2 of CMP mailing address
     */
    private Label showAddr2;

    /**
     * program author
     */
    private Label showAuthor;

    /**
     * Canadian Mind Products
     */
    private Label showCMP;

    /**
     * version of JDK used to compile
     */
    private Label showCompiledWithJDKVersion;

    /**
     * copyright notice
     */
    private Label showCopyright;

    /**
     * download URL
     */
    private Label showDownloadURL;

    /**
     * contact email
     */
    private Label showMailTo;

    /**
     * minimum JDK version
     */
    private Label showMinJdkVersion;

    /**
     * CMP phone number
     */
    private Label showPhone;

    /**
     * program name and version
     */
    private Label showProgramVersionBuild;

    /**
     * line 1 of what program is for
     */
    private Label showPurpose1;

    /**
     * line 2 of what program is for
     */
    private Label showPurpose2;

    /**
     * yyyy-mm-dd this version released
     */
    private Label showReleaseDate;

    /**
     * running JDK version
     */
    private Label showRunningJREVersion;

    /**
     * is prgram free, shareware etc.
     */
    private Label showStatus;

    /**
     * vmName e.g. Excelsior JET  or  Java HotSpot(TM) 64-Bit Server VM
     */
    private Label showVmName;

    /**
     * Create an about box when don't have parent
     *
     * @param progname           Program name
     * @param version            Program version e.g. "1.3"
     * @param purpose1           what is this program for? line-1
     * @param purpose2           what is this program for? line-2. may be null, or "".
     * @param status             e.g. "unregistered shareware", "freeware", "commercial", "company confidential"
     * @param released           Date released e.g. "1999-12-31"
     * @param firstCopyrightYear year this code was first released
     * @param author             e.g. "Roedy Green"
     * @param masterSite         e.g. CONVERTER -- where to find most up to date ZIP
     * @param minJdkVersion      e.g. 1.1 (minimum JDK version required)
     */
    public CMPAboutBox( final String progname,
                        final String version,
                        final String purpose1,
                        final String purpose2,
                        final String status,
                        final String released,
                        final int firstCopyrightYear,
                        final String author,
                        final String masterSite,
                        final String minJdkVersion )
        {
        this( new Frame( progname + " " + version )
                /*
                 * dummy parent, won't be
                 * disposed!!
                 */,
                progname,
                version,
                purpose1,
                purpose2,
                status,
                released,
                firstCopyrightYear,
                author,
                masterSite,
                minJdkVersion );
        }

    /**
     * Create an about box
     *
     * @param parent             frame for this about box.
     * @param progname           Program name
     * @param version            Program version e.g. "1.3"
     * @param purpose1           what is this program for? line-1
     * @param purpose2           what is this program for? line-2. may be null, or "".
     * @param status             e.g. "unregistered shareware", "freeware", "commercial", "company confidential"
     * @param released           Date released e.g. "1999-12-31"
     * @param firstCopyrightYear e.g. 1996
     * @param author             e.g. "Roedy Green"
     * @param masterSite         e.g. CONVERTER -- where to find most up to date ZIP
     * @param minJdkVersion      e.g. 1.1 (minimum JDK version required)
     *
     * @noinspection WeakerAccess
     */
    public CMPAboutBox( final Frame parent,
                        final String progname,
                        final String version,
                        final String purpose1,
                        final String purpose2,
                        final String status,
                        final String released,
                        final int firstCopyrightYear,
                        final String author,
                        final String masterSite,
                        final String minJdkVersion )
        {
        super( parent, progname + " " + version, false/* not modal */ );
        guts( progname,
                version,
                purpose1,
                purpose2,
                status,
                released,
                firstCopyrightYear,
                author,
                masterSite,
                minJdkVersion );
        }

    /**
     * Shutdown the about box
     */
    private void dismiss()
        {
        // close the about box
        this.setVisible( false );
        // tell AWT to discard all pointers to the Dialog box.
        this.dispose();
        } // end dismiss

    /**
     * common parts to all creation Guts of reating an an about box
     *
     * @param progname           Program name
     * @param version            Program version e.g. "1.3"
     * @param purpose1           what is this program for? line-1
     * @param purpose2           what is this program for? line-2. may be null, or "".
     * @param status             e.g. "unregistered shareware", "freeware", "commercial", "company confidential"
     * @param released           Date released e.g. "1999-12-31"
     * @param firstCopyrightYear 1996
     * @param author             e.g. "Roedy Green"
     * @param masterSite         e.g. CONVERTER -- where to find most up to date ZIP
     * @param minJdkVersion      e.g. 1.1 (minimum JDK version required)
     */
    private void guts( final String progname,
                       final String version,
                       final String purpose1,
                       final String purpose2,
                       final String status,
                       final String released,
                       final int firstCopyrightYear,
                       final String author,
                       final String masterSite,
                       final String minJdkVersion )
        {
        // leave room for warning this frame belongs to Java applet.
        setSize( WIDTH + 16, HEIGHT + 36 );
        setLocation( 0, 0 );
        setBackground( WHITE );
        showProgramVersionBuild = new Label( progname + " " + version + " build " + Build
                .BUILD_NUMBER, Label.CENTER );
        showProgramVersionBuild.setFont( FONT_FOR_TITLE );
        showProgramVersionBuild.setForeground( FOREGROUND_FOR_TITLE );
        showProgramVersionBuild.setBackground( WHITE );
        showPurpose1 = new Label( purpose1, Label.CENTER );
        showPurpose1.setFont( FontFactory.build( "Dialog", Font.ITALIC, 12 ) );
        showPurpose1.setForeground( BLACK );
        showPurpose1.setBackground( WHITE );
        if ( purpose2 != null && purpose2.length() > 0 )
            {
            showPurpose2 = new Label( purpose2, Label.CENTER );
            showPurpose2.setFont( FontFactory.build( "Dialog", Font.ITALIC, 12 ) );
            showPurpose2.setForeground( BLACK );
            showPurpose2.setBackground( WHITE );
            }
        showStatus = new Label( status, Label.CENTER );
        showStatus.setFont( FontFactory.build( "Dialog", Font.BOLD, 12 ) );
        showStatus.setForeground( FOREGROUND_FOR_LABEL );
        showStatus.setBackground( WHITE );
        showReleaseDate = new Label( "released: " + released, Label.CENTER );
        showReleaseDate.setFont( FontFactory.build( "Dialog", Font.PLAIN, 11 ) );
        showReleaseDate.setForeground( FOREGROUND_FOR_LABEL );
        showReleaseDate.setBackground( WHITE );
        final String copyright;
        if ( firstCopyrightYear == Build.THIS_COPYRIGHT_YEAR )
            {
            copyright = Integer.toString( Build.THIS_COPYRIGHT_YEAR );
            }
        else
            {
            copyright = firstCopyrightYear + "-" + Build.THIS_COPYRIGHT_YEAR;
            }
        showCopyright = new Label( "copyright " + copyright, Label.LEFT );
        showCopyright.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showCopyright.setForeground( DARK_GREEN );
        showCopyright.setBackground( WHITE );
        showAuthor = new Label( author, Label.LEFT );
        showAuthor.setFont( FontFactory.build( "Dialog", Font.BOLD + Font.ITALIC, 11 ) );
        showAuthor.setForeground( DARK_GREEN );
        showAuthor.setBackground( WHITE );
        showCMP = new Label( "Canadian Mind Products", Label.LEFT );
        showCMP.setFont( FontFactory.build( "Dialog", Font.BOLD + Font.ITALIC, 11 ) );
        showCMP.setForeground( DARK_GREEN );
        showCMP.setBackground( WHITE );
        showAddr1 = new Label( "#101 - 2536 Wark Street", Label.LEFT );
        showAddr1.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showAddr1.setForeground( DARK_GREEN );
        showAddr1.setBackground( WHITE );
        showAddr2 = new Label( "Victoria, BC Canada V8T 4G8", Label.LEFT );
        showAddr2.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showAddr2.setForeground( DARK_GREEN );
        showAddr2.setBackground( WHITE );
        // requires: Java 1.8+
        showMinJdkVersion = new Label( "requires: Java " + minJdkVersion + "+", Label.RIGHT );
        showMinJdkVersion.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showMinJdkVersion.setForeground( DARK_GREEN );
        showMinJdkVersion.setBackground( WHITE );
        // running: Java 1.8.0_131
        showRunningJREVersion = new Label( "running: Java " + System.getProperty( "java.version", "unknown" ),
                Label.RIGHT );
        showRunningJREVersion.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showRunningJREVersion.setForeground( DARK_GREEN );
        showRunningJREVersion.setBackground( WHITE );
        //  vm: Java HotSpot(TM) 64-Bit Server VM  or
        //  vm: Excelsior JET 9.0-pro-x86
        final String vm = System.getProperty( "java.vm.name", "unknown" );
        showVmName = new Label( "vm: " + vm + ( vm.startsWith( "Excelsior" ) ? ( " " + Build.JET_VERSION ) : "" ), Label.RIGHT );
        showVmName.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showVmName.setForeground( DARK_GREEN );
        showVmName.setBackground( WHITE );
        // compiled: Java 1.8.0_131
        showCompiledWithJDKVersion = new Label( "compiled with: JDK " + Build.JDK_FULL_VERSION,
                Label.RIGHT );
        showCompiledWithJDKVersion.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showCompiledWithJDKVersion.setForeground( DARK_GREEN );
        showCompiledWithJDKVersion.setBackground( WHITE );
        showAuthor = new Label( author, Label.LEFT );
        showAuthor.setFont( FontFactory.build( "Dialog", Font.BOLD + Font.ITALIC, 11 ) );
        showAuthor.setForeground( DARK_GREEN );
        showAuthor.setBackground( WHITE );
        showCMP = new Label( "Canadian Mind Products", Label.LEFT );
        showCMP.setFont( FontFactory.build( "Dialog", Font.BOLD + Font.ITALIC, 11 ) );
        showCMP.setForeground( DARK_GREEN );
        showCMP.setBackground( WHITE );
        showAddr1 = new Label( "#101 - 2536 Wark Street", Label.LEFT );
        showAddr1.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showAddr1.setForeground( DARK_GREEN );
        showAddr1.setBackground( WHITE );
        showAddr2 = new Label( "Victoria, BC Canada V8T 4G8", Label.LEFT );
        showAddr2.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showAddr2.setForeground( DARK_GREEN );
        showAddr2.setBackground( WHITE );
        showPhone = new Label( "phone: (250) 361-9093", Label.RIGHT );
        showPhone.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showPhone.setForeground( DARK_GREEN );
        showPhone.setBackground( WHITE );
        showMailTo = new Label( "roedyg@mindprod.com", Label.LEFT );
        showMailTo.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showMailTo.setForeground( DARK_GREEN );
        showMailTo.setBackground( WHITE );
        showDownloadURL = new Label( "http://mindprod.com/products.html#" + masterSite,
                Label.RIGHT );
        showDownloadURL.setFont( FontFactory.build( "Dialog", Font.ITALIC, 10 ) );
        showDownloadURL.setForeground( DARK_GREEN );
        showDownloadURL.setBackground( WHITE );
        dismissButton = new Button( "Dismiss" );
        dismissButton.setFont( FontFactory.build( "Dialog", Font.BOLD, 15 ) );
        layoutComponents();
        // hook up About box listeners
        this.addFocusListener( new FocusAdapter()
            {
            /**
             * Handle About Dialog getting focus, give to dismiss Button.
             * @param e event giving details
             */
            public void focusGained( FocusEvent e )
                {
                dismissButton.requestFocus();
                } // end focusGained
            } // end anonymous class
        );// end addFocusListenerline
        this.addWindowListener( new WindowAdapter()
            {
            /**
             * Handle request to close about box
             * @param e event giving details of closing.
             */
            public void windowClosing( WindowEvent e )
                {
                dismiss();
                } // end WindowClosing
            } // end anonymous class
        );// end addWindowListener line
        dismissButton.addActionListener( new ActionListener()
            {
            /**
             * close down the About box when user clicks Dismiss
             */
            public void actionPerformed( ActionEvent e )
                {
                Object object = e.getSource();
                if ( object == dismissButton )
                    {
                    dismiss();
                    } // end if
                } // end actionPerformed
            } // end anonymous class
        );// end addActionListener line
        // The following code is only necessary if you want
        // any keystroke while the Dismiss button has focus
        // to simulate clicking the Dismiss button.
        dismissButton.addKeyListener( new KeyAdapter()
            {
            /**
             * Handle Dismiss button getting a function keystroke, treat like a
             * dismiss Button click
             * @param e event giving details
             */
            public void keyPressed( KeyEvent e )
                {
                dismiss();
                } // end keyPressed

            /**
             * Handle Dismiss button getting an ordinary keystroke, treat like a
             * dismiss Button click
             * @param e event giving details
             */
            public void keyTyped( KeyEvent e )
                {
                dismiss();
                } // end keyTyped
            } // end anonymous class
        );// end addKeyListener
        this.validate();
        this.setVisible( true );
        } // end constructor

    /**
     * layout the components
     */
    private void layoutComponents()
        {
        // basic layout
        // 0 1
        // 0 ---------------progname version--------------------------------- 0
        //
        // 1 ---------------------purpose1------------------------------------ 1
        // 2 ---------------------purpose2------------------------------------ 2
        //
        // 3 ---------------------status-------------------------------------- 3
        // 4 --------------released: xxxxxxxxx-------------------------------- 4
        //
        // 5 Copyright: (c) 2009-2017     ----requires:JDK 1.1+-----------------5
        // 6 Roedy Green                 --- running Java 1.06_16------------- 6
        // 7 Canadian Mind Products 7    ---- Java HotSpot(TM) 64-Bit Server   7
        // 8 #101 - 2536 Wark Street ------------------------------------------8
        // 9 Victoria, BC Canada V8T 4G8 ----phone:(250) 361-9093--------------9
        // 10 roedyg@mindprod.com------- --- http://mindprod.com/products1#COR 10
        //
        // 11 (Dismiss) 11
        // 0 1
        this.setLayout( new GridBagLayout() );
        // x y w h wtx wty anchor fill T L B R padx pady
        this.add( showProgramVersionBuild,
                new GridBagConstraints( 0,
                        0,
                        2,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets( 10, 10, 0, 10 ),
                        0,
                        0 )
        );
        this.add( showPurpose1,
                new GridBagConstraints( 0,
                        1,
                        2,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets( 5, 10, 0, 10 ),
                        0,
                        0 )
        );
        if ( showPurpose2 != null )
            {
            this.add( showPurpose2,
                    new GridBagConstraints( 0,
                            2,
                            2,
                            1,
                            0.0,
                            0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.NONE,
                            new Insets( 1, 10, 0, 10 ),
                            0,
                            0 )
            );
            }
        this.add( showStatus,
                new GridBagConstraints( 0,
                        3,
                        2,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets( 3, 10, 0, 10 ),
                        0,
                        0 )
        );
        this.add( showReleaseDate,
                new GridBagConstraints( 0,
                        4,
                        2,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets( 2, 10, 0, 10 ),
                        0,
                        0 )
        );
        this.add( showCopyright,
                new GridBagConstraints( 0,
                        5,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 10, 0, 3 ),
                        0,
                        0 )
        );
        this.add( showAuthor,
                new GridBagConstraints( 0,
                        6,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 10, 0, 3 ),
                        0,
                        0 )
        );
        this.add( showCMP,
                new GridBagConstraints( 0,
                        7,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 10, 0, 3 ),
                        0,
                        0 )
        );
        this.add( showAddr1,
                new GridBagConstraints( 0,
                        8,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 10, 0, 3 ),
                        0,
                        0 )
        );
        this.add( showAddr2,
                new GridBagConstraints( 0,
                        9,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 10, 0, 3 ),
                        0,
                        0 )
        );
        this.add( showMailTo,
                new GridBagConstraints( 0,
                        10,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 10, 0, 3 ),
                        0,
                        0 )
        );
        this.add( dismissButton,
                new GridBagConstraints( 0,
                        11,
                        2,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets( 10, 10, 10, 10 ),
                        0,
                        0 )
        );
        // second column
        this.add( showMinJdkVersion,
                new GridBagConstraints( 1,
                        5,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 3, 0, 10 ),
                        0,
                        0 )
        );
        this.add( showRunningJREVersion,
                new GridBagConstraints( 1,
                        6,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 3, 0, 10 ),
                        0,
                        0 )
        );
        this.add( showVmName,
                new GridBagConstraints( 1,
                        7,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 3, 0, 10 ),
                        0,
                        0 )
        );
        this.add( showCompiledWithJDKVersion,
                new GridBagConstraints( 1,
                        8,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 3, 0, 10 ),
                        0,
                        0 )
        );
        this.add( showPhone,
                new GridBagConstraints( 1,
                        9,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 3, 0, 10 ),
                        0,
                        0 )
        );
        this.add( showDownloadURL,
                new GridBagConstraints( 1,
                        10,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 3, 3, 0, 10 ),
                        0,
                        0 )
        );
        }

    /**
     * sample test driver
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        // nothing will happen unless debugging is true
        if ( DEBUGGING )
            {
            final Frame frame = new Frame( "About box test" );
            // need have no relation to size of About box
            frame.setSize( 450, 400 );
            MenuBar mb = frame.getMenuBar();
            if ( mb == null )
                {
                mb = new MenuBar();
                frame.setMenuBar( mb );
                }
            Menu help = mb.getHelpMenu();
            if ( help == null )
                {
                help = new Menu( "Help", /* tearoff */false );
                mb.setHelpMenu( help );
                }
            help.add( new MenuItem( "keyboard" ) );
            help.add( new MenuItem( "command line" ) );
            help.add( new MenuItem( "About" ) );
            help.addActionListener( new ActionListener()
                {
                /**
                 * Handle Menu Selection Request
                 * @param e event giving details of selection
                 */
                public void actionPerformed( ActionEvent e )
                    {
                    String command = e.getActionCommand();
                    if ( command.equals( "About" ) )
                        {
                        // Don't need to retain a reference.
                        // We do nothing more that fire it up.
                        new CMPAboutBox( frame,
                                "Sample Amanuensis",
                                "1.3",
                                "Teaches you how to interconvert the 16 basic Java types,",
                                "e.g. String to int, Long to double.",
                                "freeware",
                                "2000-01-01",
                                FIRST_COPYRIGHT_YEAR,
                                "Roedy Green",
                                "SAMPLE",
                                "1.5" );
                        } // end if
                    } // end ActionListener
                } // end anonymous class
            );// end addActionListener line
            frame.addWindowListener( new WindowAdapter()
                {
                /**
                 * Handle request to shutdown.
                 * @param e event giving details of closing.
                 */
                public void windowClosing( WindowEvent e )
                    {
                    System.exit( 0 );
                    } // end WindowClosing
                } // end anonymous class
            );// end addWindowListener line
            frame.validate();
            frame.setVisible( true );
            } // end if
        } // end main

    /**
     * bypass setBackground bug, by setting it over and over, every time addNotify gets called.
     */
    public void addNotify()
        {
        super.addNotify();
        setBackground( WHITE );
        }
    }
