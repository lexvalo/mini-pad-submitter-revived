/*
 * [CMPAboutJBox.java]
 *
 * Summary: A Swing About box that truly tells you about he program, not just the author's name.
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
 *  1.3 2008-11-12 add JDK version to About box
 */
package com.mindprod.common18;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A Swing About box that truly tells you about he program, not just the author's name.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.3 2008-11-12 add JDK version to About box
 * @noinspection FieldCanBeLocal
 * @see com.mindprod.common18.CMPAboutBox
 * @since 2008
 */
public final class CMPAboutJBox extends JDialog
    {
    /**
     * true if want debugging output
     */
    private static final boolean DEBUGGING = false;

    private static final int FIRST_COPYRIGHT_YEAR = 2008;

    /**
     * height of the about box not counting frame
     */
    private static final int HEIGHT = 320;

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
     * trigger dismiss of about box
     */
    private JButton dismissButton;

    /**
     * first line of CMP mailing address
     */
    private JLabel showAddr1;

    /**
     * second line of CMP mailing address
     */
    private JLabel showAddr2;

    /**
     * program author
     */
    private JTextArea showAuthor;

    /**
     * Canadian Mind Products
     */
    private JLabel showCMP;

    /**
     * copyright
     */
    private JLabel showCopyright;

    /**
     * download url
     */
    private JLabel showDownloadURL;

    /**
     * contact email
     */
    private JLabel showMailTo;

    /**
     * min JDK version required
     */
    private JLabel showMinJdkVersion;

    /**
     * cmp phone
     */
    private JLabel showPhone;

    /**
     * program name and version
     */
    private JLabel showProgramVersionBuild;

    /**
     * first line of purpose
     */
    private JLabel showPurpose1;

    /**
     * second line of purpose
     */
    private JLabel showPurpose2;

    /**
     * date released yyyy-mm-dd
     */
    private JLabel showReleaseDate;

    /**
     * JDK version running now
     */
    private JLabel showRunningJREVersion;

    /**
     * freeware, shareware etc.
     */
    private JLabel showStatus;

    /**
     * name of VM
     */
    private JLabel showVmName;

    /**
     * version of JDK used to compile
     */
    private JLabel showCompiledWithJDKVersion;

    /**
     * Create an about box, no parent
     *
     * @param progname           Program name
     * @param version            Program version e.g. "1.3"
     * @param purpose1           what is this program for? line-1
     * @param purpose2           what is this program for? line-2. may be null, or "".
     * @param status             e.g. "unregistered shareware", "freeware", "commercial", "company confidential"
     * @param released           Date released e.g. "1999-12-31"
     * @param firstCopyrightYear year program was first released
     * @param author             program's author
     * @param masterSite         anchor on products page e.g. CONVERTER -- where to find most up to date ZIP
     * @param minJdkVersion      e.g. 1.1 (minimum JDK version required)
     *
     * @noinspection UnusedDeclaration
     */
    public CMPAboutJBox( final String progname,
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
        // dummy frame won't be properly disposed
        this( new JFrame( progname + " " + version + " build " + Build.BUILD_NUMBER ),
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
     * @param firstCopyrightYear year program was first released
     * @param author             program's author
     * @param masterSite         anchor on products page e.g. CONVERTER -- where to find most up to date ZIP
     * @param minJkdVersion      e.g. 1.1 (minimum JDK version required)
     */
    public CMPAboutJBox( final Frame parent,
                         final String progname,
                         final String version,
                         final String purpose1,
                         final String purpose2,
                         final String status,
                         final String released,
                         final int firstCopyrightYear,
                         final String author,
                         final String masterSite,
                         final String minJkdVersion )
        {
        super( parent,
                progname + " " + version + " build " + Build.BUILD_NUMBER,
                false
        );
        guts( progname,
                version,
                purpose1,
                purpose2,
                status,
                released,
                firstCopyrightYear,
                author,
                masterSite,
                minJkdVersion );
        }

    /**
     * Shutdown the about box
     */
    private void dismiss()
        {
        this.dispose();
        } // end dismiss

    /**
     * Create an about box
     *
     * @param progname           Program name
     * @param version            Program version e.g. "1.3"
     * @param purpose1           what is this program for? line-1
     * @param purpose2           what is this program for? line-2. may be null, or "".
     * @param status             e.g. "unregistered shareware", "freeware", "commercial", "company confidential"
     * @param released           Date released e.g. "1999-12-31"
     * @param firstCopyrightYear e.g. 1996
     * @param author             who wrote the program.
     * @param masterSite         anchor on products page e.g. CONVERTER -- where to find most up to date ZIP
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
        setSize( WIDTH + 22, HEIGHT + 32 );
        setLocation( 0, 0 );
        Container contentPane = this.getContentPane();
        contentPane.setBackground( WHITE );
        showProgramVersionBuild =
                new JLabel( progname + " " + version + " build " + Build.BUILD_NUMBER,
                        JLabel.CENTER );
        showProgramVersionBuild.setFont( FONT_FOR_TITLE );
        showProgramVersionBuild.setForeground( FOREGROUND_FOR_TITLE );
        showProgramVersionBuild.setBackground( WHITE );
        showPurpose1 = new JLabel( purpose1, JLabel.CENTER );
        showPurpose1.setFont( FontFactory.build( "Dialog", Font.ITALIC, 12 ) );
        showPurpose1.setForeground( BLACK );
        showPurpose1.setBackground( WHITE );
        if ( purpose2 != null && purpose2.length() > 0 )
            {
            showPurpose2 = new JLabel( purpose2, JLabel.CENTER );
            showPurpose2.setFont( FontFactory.build( "Dialog", Font.ITALIC, 12 ) );
            showPurpose2.setForeground( BLACK );
            showPurpose2.setBackground( WHITE );
            }
        showStatus = new JLabel( status, JLabel.CENTER );
        showStatus.setFont( FontFactory.build( "Dialog", Font.BOLD, 12 ) );
        showStatus.setForeground( FOREGROUND_FOR_LABEL );
        showStatus.setBackground( WHITE );
        showReleaseDate = new JLabel( "released: " + released, JLabel.CENTER );
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
        showCopyright = new JLabel( "copyright " + copyright, JLabel.LEFT );
        showCopyright.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showCopyright.setForeground( DARK_GREEN );
        showCopyright.setBackground( WHITE );
        // allow in to go multiline and have embedded \n
        showAuthor = new JTextArea( author );
        showAuthor.setEditable( false );
        showAuthor.setBorder( BorderFactory.createEmptyBorder() );
        showAuthor.setFont( FontFactory.build( "Dialog", Font.BOLD + Font.ITALIC, 11 ) );
        showAuthor.setForeground( DARK_GREEN );
        showAuthor.setBackground( WHITE );
        showCMP = new JLabel( "Canadian Mind Products", JLabel.LEFT );
        showCMP.setFont( FontFactory.build( "Dialog", Font.BOLD + Font.ITALIC, 11 ) );
        showCMP.setForeground( DARK_GREEN );
        showCMP.setBackground( WHITE );
        showAddr1 = new JLabel( "#101 - 2536 Wark Street", JLabel.LEFT );
        showAddr1.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showAddr1.setForeground( DARK_GREEN );
        showAddr1.setBackground( WHITE );
        showAddr2 = new JLabel( "Victoria, BC Canada V8T 4G8", JLabel.LEFT );
        showAddr2.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showAddr2.setForeground( DARK_GREEN );
        showAddr2.setBackground( WHITE );
        // requires: Java 1.8+
        showMinJdkVersion = new JLabel( "requires: Java " + minJdkVersion + "+", JLabel.RIGHT );
        showMinJdkVersion.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showMinJdkVersion.setForeground( DARK_GREEN );
        showMinJdkVersion.setBackground( WHITE );
        // running: Java 1.8.0_131
        showRunningJREVersion = new JLabel( "running: Java " + System.getProperty( "java.version", "unknown" ),
                JLabel.RIGHT );
        showRunningJREVersion.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showRunningJREVersion.setForeground( DARK_GREEN );
        showRunningJREVersion.setBackground( WHITE );
        //  vm: Java HotSpot(TM) 64-Bit Server VM  or
        //  vm: Excelsior JET 9.0-pro-x86
        final String vm = System.getProperty( "java.vm.name", "unknown" );
        showVmName = new JLabel( "vm: " + vm + ( vm.startsWith( "Excelsior" ) ? ( " " + Build.JET_VERSION ) : "" ), JLabel.RIGHT );
        showVmName.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showVmName.setForeground( DARK_GREEN );
        showVmName.setBackground( WHITE );
        // compiled: Java 1.8.0_131
        showCompiledWithJDKVersion = new JLabel( "compiled with: JDK " + Build.JDK_FULL_VERSION,
                JLabel.RIGHT );
        showCompiledWithJDKVersion.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showCompiledWithJDKVersion.setForeground( DARK_GREEN );
        showCompiledWithJDKVersion.setBackground( WHITE );
        showPhone = new JLabel( "phone: (250) 361-9093", JLabel.RIGHT );
        showPhone.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showPhone.setForeground( DARK_GREEN );
        showPhone.setBackground( WHITE );
        showMailTo = new JLabel( "roedyg@mindprod.com", JLabel.LEFT );
        showMailTo.setFont( FontFactory.build( "Dialog", Font.ITALIC, 11 ) );
        showMailTo.setForeground( DARK_GREEN );
        showMailTo.setBackground( WHITE );
        showDownloadURL = new JLabel( "http://mindprod.com/products.html#" + masterSite, JLabel.RIGHT );
        showDownloadURL.setFont( FontFactory.build( "Dialog", Font.ITALIC, 10 ) );
        showDownloadURL.setForeground( DARK_GREEN );
        showDownloadURL.setBackground( WHITE );
        dismissButton = new JEButton( "Dismiss" );
        dismissButton.requestFocus();
        dismissButton.setToolTipText( "Dismiss About dialog" );
        // add components:
        layoutComponents( contentPane );
        // hook up About box listeners
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
        this.validate();
        this.setVisible( true );
        } // end constructor

    /**
     * layout the components
     *
     * @param contentPane pane of the surrounding JFrame.
     */
    private void layoutComponents( final Container contentPane )
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
        contentPane.setLayout( new GridBagLayout() );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( showProgramVersionBuild,
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
        contentPane.add( showPurpose1,
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
            contentPane.add( showPurpose2,
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
        contentPane.add( showStatus,
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
        contentPane.add( showReleaseDate,
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
        contentPane.add( showCopyright,
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
        contentPane.add( showAuthor,
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
        contentPane.add( showCMP,
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
        contentPane.add( showAddr1,
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
        contentPane.add( showAddr2,
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
        contentPane.add( showMailTo,
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
        contentPane.add( dismissButton,
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
        contentPane.add( showMinJdkVersion,
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
        contentPane.add( showRunningJREVersion,
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
        contentPane.add( showVmName,
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
        contentPane.add( showCompiledWithJDKVersion,
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
        contentPane.add( showPhone,
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
        contentPane.add( showDownloadURL,
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
        if ( DEBUGGING )
            {
            final JFrame frame = new JFrame( "About box test" );
            frame.setSize( WIDTH, HEIGHT + 26 );
            JMenuBar mb = new JMenuBar();
            frame.setJMenuBar( mb );
            JMenu help = new JMenu( "Help", /* tearoff */false );
            help.getAccessibleContext().setAccessibleDescription( "Help" );
            mb.add( help );
            help.add( new JMenuItem( "keyboard" ) );
            help.add( new JMenuItem( "command line" ) );
            JMenuItem about = new JMenuItem( "About" );
            about.setActionCommand( "About" );
            help.add( about );
            about.addActionListener( new ActionListener()
                {
                /**
                 * Handle Menu Selection Request
                 * @param e event giving details of selection
                 */
                public void actionPerformed( ActionEvent e )
                    {
                    new CMPAboutJBox( frame,
                            "Sample Amanuensis",
                            "1.0",
                            "Teaches you how to interconvert the 16 basic Java types,",
                            "e.g. String to int, Long to double.",
                            "freeware",
                            "2000-01-01",
                            FIRST_COPYRIGHT_YEAR,
                            "Roedy Green",
                            "SAMPLE",
                            "1.8" );
                    } // end ActionListener
                } // end anonymous class
            );// end addActionListener line
            frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
            frame.validate();
            frame.setVisible( true );
            } // end if
        } // end main
    }
