/*
 * [Submitter.java]
 *
 * Summary: GUI to submit PAD files to various distribution websites.
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
 * Version: 26.3
 */
package com.mindprod.submitter;

import com.mindprod.common18.Build;
import com.mindprod.common18.CMPAboutJBox;
import com.mindprod.common18.FontFactory;
import com.mindprod.common18.JEButton;
import com.mindprod.common18.ST;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.http.Get;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static java.lang.System.*;

/**
 * GUI to submit PAD files to various distribution websites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 26.3 2017-03-30 drop http://paulspicks.com/
 * @since 2007
 */
@SuppressWarnings( { "FieldCanBeLocal" } )
public final class Submitter extends JFrame implements Runnable
    {
    /**
     * allow user to resusbumit the same url within a week.  Normally false. True during debugging.
     */
    private static final boolean PERMIT_RESUBMIT = false;

    private static final int WINDOW_HEIGHT = 500;

    private static final int WINDOW_WIDTH = 680;

    private static final int FIRST_COPYRIGHT_YEAR = 2007;

    /**
     * Sites to submit to, loaded once from sites.txt (next to the jar) instead of
     * being a hardcoded enum - edit that file to add, remove, or change sites.
     */
    private static final java.util.List<Site> SITES = Site.loadAll();

    /**
     * how many websites we submit to.
     */
    private static final int HOW_MANY_WEBSITES = SITES.size();

    /**
     * How long to wait for response from site to to finish rendering. Does not count time for initial response.
     * It may need time to load images, style sheets etc.
     */
    private static final int MILLIS_TO_ADMIRE = 6000;

    /**
     * not displayed copyright
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2007-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * when this version was released
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String RELEASE_DATE = "2017-03-30";

    /**
     * fake pad name to use an as example, no lead /
     */
    private static final String SAMPLE_PAD_URL = "hypotheticalprogram.xml";

    /**
     * fake website URL to use an as example, no trailing /
     */
    private static final String SAMPLE_WEBSITE_URL = "http://mypretendwebsite.com/pad";

    private static final String TITLE_STRING = "Mini PAD Submitter";

    /**
     * embedded version string
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String VERSION_STRING = "26.3 Revived";

    /**
     * background colour, pale green to match website
     */
    private static final Color BACKGROUND_FOR_BODY = Build.BACKGROUND_FOR_BLENDING;

    /**
     * background colour for instructions.  Default grey is too dark
     */
    private static final Color BACKGROUND_FOR_INSTRUCTIONS = new Color( 0xf8f8f8 );

    /**
     * instruction background colour when submitting
     */
    private static final Color BACKGROUND_FOR_WORKING = new Color( 0x005e6e/* dark cyan */ );

    /**
     * something went wrong colour
     */
    private static final Color FOREGROUND_FOR_ALERT = new Color( 0xdc143c/* crimson */ );

    /**
     * something went wrong alternate colour
     */
    private static final Color FOREGROUND_FOR_ALERT_ALT = Color.RED;

    /**
     * instruction normal color
     */
    private static final Color FOREGROUND_FOR_INSTRUCTIONS = new Color( 0x339911 );

    /**
     * foreground colour for title
     */
    private static final Color FOREGROUND_FOR_LABEL = Color.BLUE;

    /**
     * for titles
     */
    private static final Color FOREGROUND_FOR_TITLE = new Color( 0xdc143c );

    /**
     * URL colour
     */
    private static final Color FOREGROUND_FOR_URL = new Color( 0x442222 );

    /**
     * instruction colour when submitting
     */
    private static final Color FOREGROUND_FOR_WORKING = new Color( 0xccffcc/* light cyan */ );

    /**
     * instructions font
     */
    private static final Font FONT_FOR_INSTRUCTIONS = FontFactory.build( "Dialog", Font.PLAIN, 12 );

    /**
     * title font
     */
    private static final Font FONT_FOR_TITLE = FontFactory.build( "Dialog", Font.BOLD, 15 );

    /**
     * for title second line
     */
    private static final Font FONT_FOR_TITLE2 = FontFactory.build( "Dialog", Font.PLAIN, 14 );

    /**
     * URL font
     */
    private static final Font FONT_FOR_URLS = FontFactory.build( "Dialog", Font.PLAIN, 14 );

    /**
     * contentPane of the JFrame
     */
    private Container contentPane;

    /**
     * button to submit URL to various sites
     */
    private JButton submitButton;

    /**
     * aux instructions on how to use program
     */
    private JLabel instructions2;

    /**
     * label for pad name
     */
    private JLabel padFileLabel;

    /**
     * title for app
     */
    private JLabel title;

    /**
     * title, second line
     */
    private JLabel title2;

    /**
     * label for website URL
     */
    private JLabel websiteURLLabel;

    /**
     * control scrolling of the response field
     */
    private JScrollPane scroller;

    /**
     * text response from the website, with HTML stripped out
     */
    private JTextArea responsePage;

    /**
     * instructions on how to use program
     */
    private JTextField instructions;

    /**
     * name of the pad to submit e.g. entities.xml
     */
    private JTextField padFile;

    /**
     * instructions on how to use program
     */
    private JTextField response;

    /**
     * URL of the website directory http://mindprod.com/pad
     */
    private JTextField websiteURL;

    /**
     * where in registry we persist our history. key = value padname.xml = timestamp long for each submission. website =
     * http:\\mindprod.com\pad
     */
    private Preferences userPrefs;

    /**
     * the complete URL of the pad e.g. http://mindprod.com/pad/entitities.xml
     */
    private String fullPADURLString;

    /**
     * directory to dump the log, null if suppress log.*
     */
    private String logDir;

    /**
     * true if using alternate alert colour
     */
    private boolean usingAlt;

    /**
     * Constructor.
     *
     * @param logDir directory to dump the log, null if suppress log.
     */
    public Submitter( String logDir )
        {
        super( TITLE_STRING + " " + VERSION_STRING );
        if ( logDir == null
             || logDir.length() == 0
             || logDir.equals( "null" )
             || logDir.equals( "default" )
             || logDir.equalsIgnoreCase( "noLog" ) )
            {
            logDir = null;
            }
        this.logDir = logDir;
        try
            {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }
        catch ( Exception ignored )
            {
            }
        contentPane = getContentPane();
        userPrefs = Preferences.userNodeForPackage( Submitter.class );
        usingAlt = false;
        buildMenu();
        buildComponents();
        layoutComponents();
        hookListeners();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( WINDOW_WIDTH, WINDOW_HEIGHT );
        this.validate();
        this.setVisible( true );
        }

    /**
     * displays an alert message
     *
     * @param text string to display as alert message.
     */
    private void alert( String text )
        {
        assert text.trim().equals( text ) : "untrimmed alert text";
        // In case message is same as already there, we toggle the colour
        // to make it clear there is a "new" message.
        if ( text.equals( instructions.getText() ) )
            {
            usingAlt = !usingAlt;
            instructions.setForeground( usingAlt
                                        ? FOREGROUND_FOR_ALERT_ALT
                                        : FOREGROUND_FOR_ALERT );
            // no need to setText
            }
        else
            {
            instructions.setText( text );
            instructions.setForeground( FOREGROUND_FOR_ALERT );
            usingAlt = false;
            }
        }

    /**
     * build all the Swing components.
     */
    private void buildComponents()
        {
        contentPane.setBackground( BACKGROUND_FOR_BODY );
        title = new JLabel( TITLE_STRING + " " + VERSION_STRING );
        title.setFont( FONT_FOR_TITLE );
        title.setForeground( FOREGROUND_FOR_TITLE );
        title2 = new JLabel(
                "released:" +
                RELEASE_DATE +
                " 2026 Community Edition"
        );
        title2.setFont( FONT_FOR_TITLE2 );
        title2.setForeground( FOREGROUND_FOR_TITLE );
        websiteURLLabel = new JLabel( "Web Dir URL:", JLabel.RIGHT );
        websiteURLLabel.setForeground( FOREGROUND_FOR_LABEL );
        final String defaultWebsite;
        if ( userPrefs != null )
            {
            defaultWebsite = userPrefs.get( "website", SAMPLE_WEBSITE_URL );
            }
        else
            {
            defaultWebsite = SAMPLE_WEBSITE_URL;
            }
        websiteURL = new JTextField( defaultWebsite, 50 );
        websiteURL.setFont( FONT_FOR_URLS );
        websiteURL.setForeground( FOREGROUND_FOR_URL );
        websiteURL.setMargin( new Insets( 3, 2, 3, 2 ) );
        websiteURL.setToolTipText(
                "URL of directory or your website where you upload PAD xml files e.g. "
                + SAMPLE_WEBSITE_URL
        );
        padFileLabel = new JLabel( "PAD xml file:", JLabel.RIGHT );
        padFileLabel.setForeground( FOREGROUND_FOR_LABEL );
        padFile = new JTextField( SAMPLE_PAD_URL, 50 );
        padFile.setFont( FONT_FOR_URLS );
        padFile.setForeground( FOREGROUND_FOR_URL );
        padFile.setMargin( new Insets( 3, 2, 3, 2 ) );
        padFile.setToolTipText( "name of your uploaded PAD xml file e.g. "
                                + SAMPLE_PAD_URL );
        submitButton = new JEButton( "Submit" );
        submitButton.setToolTipText( "Submit this PAD xml to "
                                     + HOW_MANY_WEBSITES
                                     + " sites" );
        instructions = new JTextField( "To register your pad xml at "
                                       + HOW_MANY_WEBSITES
                                       + " distribution websites, enter the URL of your uploaded pad and click submit.",
                120
        );
        instructions.setFont( FONT_FOR_INSTRUCTIONS );
        instructions.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
        instructions.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
        instructions.setEditable( false );
        instructions.setMargin( new Insets( 2, 2, 2, 2 ) );
        instructions2 = new JLabel(
                "Don\u2019t scroll away from or minimise this application when it is actively submitting." );
        instructions2.setFont( FONT_FOR_INSTRUCTIONS );
        instructions2.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
        instructions2.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
        responsePage = new JTextArea();
        responsePage.setLineWrap( true );
        responsePage.setWrapStyleWord( true );
        // htmlDocument = new HTMLDocument();
        //  htmlDocument.setBase();  done later
        //  responsePage.setDocument( htmlDocument );
        // responsePage.setContentType( "text/html" );
        responsePage.setForeground( Color.BLACK ); // does not seem to work to set default CSS foreground.
        responsePage.setBackground( Color.WHITE );
        responsePage.setFont( FONT_FOR_INSTRUCTIONS );
        responsePage.setMargin( new Insets( 2, 2, 2, 2 ) );
        // contain the responsePage in JScrollPane.
        scroller = new JScrollPane( responsePage,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        // control the speed effect of the wheelmouse
        scroller.getVerticalScrollBar().setUnitIncrement( 16 );
        response = new JTextField( "", 120 );
        response.setFont( FONT_FOR_INSTRUCTIONS );
        response.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
        response.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
        response.setEditable( false );
        response.setMargin( new Insets( 2, 2, 2, 2 ) );
        }

    private void buildMenu()
        {
        final JMenuBar menubar = new JMenuBar();
        setJMenuBar( menubar );
        final JMenu menuHelp = new JMenu( "Help" );
        menubar.add( menuHelp );
        final JMenuItem aboutItem = new JMenuItem( "About" );
        menuHelp.add( aboutItem );
        aboutItem.addActionListener( new ActionListener()
            {
            public void actionPerformed( ActionEvent e )
                {
                // open about frame
                new CMPAboutJBox( Submitter.this,
                        TITLE_STRING,
                        VERSION_STRING,
                        "Submits an ASP PAD XML program description to "
                        + HOW_MANY_WEBSITES
                        + "websites.",
                        "",
                        "freeware",
                        RELEASE_DATE,
                        FIRST_COPYRIGHT_YEAR,
                        "Roedy Green",
                        "SUBMITTER",
                        "1.7"
                );
                }
            } );
        }

    /**
     * hook up the listeners
     */
    private void hookListeners()
        {
        submitButton.addActionListener( new ActionListener()
            {
            public void actionPerformed( ActionEvent e )
                {
                submit();
                }
            } );
        }

    /**
     * is the pad we are considering submitting valid?
     *
     * @return true if pad is valid
     */
    private boolean isPadValid()
        {
        String websiteURLString = websiteURL.getText().trim();
        String padFileString = padFile.getText().trim();
        if ( websiteURLString.length() == 0 )
            {
            alert( "You must fill in the website URL before hitting submit." );
            return false;
            }
        if ( padFileString.length() == 0 )
            {
            alert( "You must fill in the PAD URL before hitting submit." );
            return false;
            }
        if ( !websiteURLString.startsWith( "http://" ) && !websiteURLString.startsWith( "https://" ) )
            {
            alert( "The website URL ["
                   + websiteURLString
                   + "] must begin with http:// or https://" );
            return false;
            }
        if ( !padFileString.endsWith( ".xml" ) )
            {
            alert( "The PAD URL [" + padFileString + "] must end with .xml" );
            return false;
            }
        if ( websiteURLString.indexOf( '\\' ) >= 0 )
            {
            alert( "The website URL ["
                   + websiteURLString
                   + "] must not contain any \\ characters; use / instead." );
            return false;
            }
        if ( padFileString.indexOf( '\\' ) >= 0
             || padFileString.indexOf( '/' ) >= 0 )
            {
            alert( "The PAD URL ["
                   + padFileString
                   + "] must not contain any \\ or / characters." );
            return false;
            }
        if ( websiteURLString.equalsIgnoreCase( SAMPLE_WEBSITE_URL ) )
            {
            alert( "You must enter the URL of YOUR website before hitting submit." );
            return false;
            }
        if ( padFileString.equalsIgnoreCase( SAMPLE_PAD_URL ) )
            {
            alert( "You must enter the URL of YOUR pad on YOUR website before hitting submit." );
            return false;
            }
        final URL url;
        try
            {
            url = new URL( websiteURLString + '/' + padFileString );
            }
        catch ( MalformedURLException e )
            {
            alert( "Your URL ["
                   + websiteURLString
                   + '/'
                   + padFileString
                   + "] is malformed." );
            return false;
            }
        // we use Get instead of Probe because we want to test the length of the result, not just existence.
        final Get get = new Get();
        // no parms needed
        final String padText = get.send( url, Get.UTF8 );
        final int padResponseCode = get.getResponseCode();
        // later could check fields in the pad document
        if ( !get.isGood() || padText == null || padText.length() == 0 )
            {
            alert( "The PAD must already be uploaded to your website. responsecode:" + padResponseCode );
            return false;
            }
        if ( padText.length() < 5000 )
            {
            alert( "The uploaded PAD xml file should be 5000+ character long. It is only "
                   + padText.length()
                   + "." );
            return false;
            }
        // Later could check fields in the pad document
        // or extract field to use is submitting to trickier sites.
        final String lowerPadURLString = padFileString.toLowerCase();
        final long lastSubmitted;
        if ( userPrefs != null )
            {
            lastSubmitted = userPrefs.getLong( lowerPadURLString, 0 );  // key is pad name, value is timestamp.
            }
        else
            {
            lastSubmitted = 0;
            }
        final long now = System.currentTimeMillis();
        if ( lastSubmitted > now - ( 1000L * 60 * 60 * 24 * 7 ) )
            {
            // done before
            alert( padFileString + " already submitted within the last week." );
            if ( !PERMIT_RESUBMIT )
                {
                return false;
                }
            }
        // persist fact we are submitting this pad to all sites, just two fields.
        try
            {
            if ( userPrefs != null )
                {
                userPrefs.putLong( lowerPadURLString, now ); // key is pad name, value is usual Java timestamp long
                userPrefs.put( "website", websiteURLString );  // don't persist this until got a good one.
                // will persist the pad name later.
                userPrefs.flush();
                }
            }
        catch ( BackingStoreException e )
            {
            err.println( "Cannot save Preferences." );
            }
        // passed all tests, let it go
        return true;
        }

    /**
     * layout fields using GridBagLayout
     */
    private void layoutComponents()
        {
        // ---0-------------1------- -----2---
        // --title1---  -- title2 ------------ 0
        // ~webdir ---weburl------------------ 1
        //  ~pad     padurl   ---------submit  2
        // -----------instructions-----------  3
        // ----------scroller----------------  4
        // ----------response----------------  5
        // ----------instructions2-----------  6
        contentPane.setLayout( new GridBagLayout() );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( title,
                new GridBagConstraints( 0,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets( 10, 10, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( title2,
                new GridBagConstraints( 1,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets( 10, 5, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( websiteURLLabel,
                new GridBagConstraints( 0,
                        1,
                        1,
                        1,
                        1.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 5, 10, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( websiteURL,
                new GridBagConstraints( 1,
                        1,
                        1,
                        1,
                        95.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 5, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( padFileLabel,
                new GridBagConstraints( 0,
                        2,
                        1,
                        1,
                        1.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 5, 10, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( padFile,
                new GridBagConstraints( 1,
                        2,
                        1,
                        1,
                        95.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 5, 5, 5 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( submitButton,
                new GridBagConstraints( 2,
                        2,
                        1,
                        1,
                        1.0,
                        0.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets( 5, 5, 5, 10 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( instructions,
                new GridBagConstraints( 0,
                        3,
                        3,
                        1,
                        100.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 10, 5, 10 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( scroller
                /* contains responsePage */,
                new GridBagConstraints( 0,
                        4,
                        3,
                        1,
                        100.0,
                        100.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 10, 5, 10 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( response,
                new GridBagConstraints( 0,
                        5,
                        3,
                        1,
                        100.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 10, 5, 10 ),
                        0,
                        0 )
        );
        // x y w h wtx wty anchor fill T L B R padx pady
        contentPane.add( instructions2,
                new GridBagConstraints( 0,
                        6,
                        3,
                        1,
                        100.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets( 5, 10, 10, 10 ),
                        0,
                        0 )
        );
        }

    /**
     * Log the HTML response from the site
     *
     * @param site         which site.
     * @param siteResponse HTML from the site.
     */
    private void logSiteResponse( final Site site, final String siteResponse )
        {
        // for log.html files
        try
            {
            final BufferedWriter log = openLog( fullPADURLString, site.getName() );
            if ( log != null )
                {
                log.write( siteResponse );
                log.close();
                }
            }
        catch ( IOException e )
            {
            err.println( "logging not functioning" );
            }
        }

    /**
     * @param fullPADURLString URL of pad we are submitting
     * @param siteName         name of site we are submitting to
     *
     * @return BufferedWriter to put HTML output for this submission, or null to suppress log.
     * @throws java.io.FileNotFoundException if trouble creating output file
     */
    private BufferedWriter openLog( String fullPADURLString, String siteName ) throws FileNotFoundException
        {
        // log to a file of the form quoter_FileDownload.log.html
        // http://mindprod.com/pad/quoter.xml --> quoter
        String padName;
        if ( fullPADURLString.length() < 4 )
            {
            padName = "unknown";
            }
        else
            {
            padName = fullPADURLString.substring( 0, fullPADURLString.length() - 4 );
            int place = padName.lastIndexOf( "/" );
            padName = padName.substring( place + 1 );
            }
        // O P E N
        if ( logDir == null )
            {
            return null;
            }
        else
            {
            final FileOutputStream fos = new FileOutputStream( new File( logDir,
                    padName + "_" + siteName + ".log.html" ), false /* append */ );
            final OutputStreamWriter osw = new OutputStreamWriter( fos );
            return new BufferedWriter( osw, 20000/* buffsize in chars */ );
            }
        }

    /**
     * submit URL to HOW_MANY_WEBSITES sites
     */
    private void submit()
        {
        submitButton.setEnabled( false );
        tidyWebsiteURL();
        tidyPadFilename();
        if ( isPadValid() )
            {
            fullPADURLString = websiteURL.getText().trim() + '/' + padFile.getText().trim();
            // submit to the HOW_MANY_WEBSITES sites
            instructions.setForeground( FOREGROUND_FOR_WORKING );
            instructions.setBackground( BACKGROUND_FOR_WORKING );
            response.setForeground( FOREGROUND_FOR_WORKING );
            response.setBackground( BACKGROUND_FOR_WORKING );
            new Thread( this ).start();
            }
        else
            {
            // if screwed up let user have another shot.
            submitButton.setEnabled( true );
            }
        }

    /**
     * tidy padFile  field
     */
    private void tidyPadFilename()
        {
        String padFileString = padFile.getText().trim();
        if ( padFileString.length() == 0 )
            {
            // We can't do anything with it.
            return;
            }
        padFileString = ST.trimLeading( padFileString, '/' );
        padFileString = ST.trimLeading( padFileString, '\\' );
        if ( !padFileString.endsWith( ".xml" ) )
            {
            padFileString += ".xml";
            }
        padFile.setText( padFileString );
        }

    /**
     * tidy websiteURL field
     */
    private void tidyWebsiteURL()
        {
        String websiteURLString = websiteURL.getText().trim();
        if ( websiteURLString.length() == 0 )
            {
            // We can't do anything with it.
            return;
            }
        if ( !websiteURLString.startsWith( "http://" ) && !websiteURLString.startsWith( "https://" ) )
            {
            websiteURLString = "http://" + websiteURLString;
            }
        if ( websiteURLString.endsWith( "/" ) )
            {
            websiteURLString = ST.trimTrailing( websiteURLString, '/' );
            }
        websiteURL.setText( websiteURLString );
        }

    /**
     * @param args optional parm, directory to put logs.
     */
    public static void main( String args[] )
        {
        final String logDir = ( args.length >= 1 ) ? args[ 0 ] : null;
        SwingUtilities.invokeLater( () -> new Submitter( logDir ) );
        } // end main

    /**
     * separate thread to handle submit loop
     */
    public void run()
        {
        if ( logDir == null )
            {
            out.println( "logging is turned off" );
            }
        else
            {
            out.println( "log files will appear in " + logDir );
            }
        out.println( "" );
        out.println( "-------------------------------" );
        out.println( "" );
        out.println( ">>>> SUBMITTING " + fullPADURLString );
        out.println( "" );
        responsePage.setText( "" );
        for ( Site site : SITES )
            {
            assert instructions != null : "instructions component not yet built.";
            instructions.setText( "Submitting to " + site.getName() + "." );
            String siteResponse = site.submit( fullPADURLString );
            if ( siteResponse == null )
                {
                siteResponse = "no response";
                }
            final int siteResponseCode = Site.getResponseCode();
            final String siteResponseMessage = Site.getResponseMessage();
            // render document relative to the website where the response came from.
            // htmlDocument.setBase( site.getBaseURL() );
            response.setText( "Response from: " + site.getName() + " >>>" + siteResponseCode + "<<< " +
                              siteResponseMessage );
            // for rolling log.
            out.println( "Response from: " + site.getName() + " >>>" + siteResponseCode + "<<< " +
                         siteResponseMessage );
            logSiteResponse( site, siteResponse );
            // display response from site with HTML stripped out. Do last so logs available on crash.
            responsePage.setText( ST.condense( DeEntifyStrings.flattenHTML( siteResponse,
                    ' ' ) ) );  // strips comments, tags, javascript
            try
                {
                // Sleep to give time to admire responsePage,
                // Does not include time for initial response.
                Thread.sleep( MILLIS_TO_ADMIRE );
                }
            catch ( InterruptedException e )
                {
                // nothing
                }
            } // end for
        SwingUtilities.invokeLater( new Runnable()
            {
            public void run()
                {
                instructions.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
                instructions.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
                instructions.setText( "D o n e !  Enter the URL of another pad xml file and click submit." );
                response.setText( "D o n e !  " + response.getText() );
                response.setForeground( FOREGROUND_FOR_INSTRUCTIONS );
                response.setBackground( BACKGROUND_FOR_INSTRUCTIONS );
                submitButton.setEnabled( true );
                }
            } );
        }
    }
