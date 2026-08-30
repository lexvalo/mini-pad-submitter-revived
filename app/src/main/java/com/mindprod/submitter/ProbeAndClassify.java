/*
 * [ProbeAndClassify.java]
 *
 * Summary: Probes a list of websites to see which ones require login, require Captchas, backlinks etc.
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
 *  1.0 2009-05-17 initial version
 *  1.1 2009-12-02 hard code in filenames. strip down to host name without www. for compare.
 */
package com.mindprod.submitter;

import com.mindprod.common18.Build;
import com.mindprod.common18.EIO;
import com.mindprod.common18.Misc;
import com.mindprod.common18.ST;
import com.mindprod.csv.CSVReader;
import com.mindprod.csv.CSVWriter;
import com.mindprod.http.Get;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;
/*
   output in candidate.csv
     */

/**
 * Probes a list of websites to see which ones require login, require Captchas, backlinks etc.
 * <p/>
 * Expect 2 to  5-column candidates.csv
 * <p/>
 * Tool to look for possible future submission sites to support.
 * Designed primarily for Roedy's use to research new sites.
 * Nothing needed on command line.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2009-12-02 hard code in filenames. strip down to host name without www. for compare.
 * @since 2009-05-17
 */
@SuppressWarnings( { "FieldCanBeLocal", "WeakerAccess" } )
public final class ProbeAndClassify
    {
    /**
     * capacity estimate total nohassle + hassle + dead
     */
    public static final int INITIAL_CAPACITY = 2000;

    private static final String HREF_REGEX = "[\\d\\p{Lower}!_#/\\.\\-:\\?=&,]+";

    private static final String QUOTE_REGEX = "[\"']";

    /**
     * scan for SUBMIT button on author.php sub page.
     */
    private static final Pattern SUBMIT_SUB_FINDER = Pattern.compile( "href="
                                                                      + QUOTE_REGEX
                                                                      + "("
                                                                      + HREF_REGEX
                                                                      + ")"
                                                                      + QUOTE_REGEX
                                                                      + "[^>]*"
                                                                      + ">"
                                                                      + "submit/update", Pattern.CASE_INSENSITIVE );

    private static final String SEPARATOR_REGEX = "[^\\d\\p{Lower}]";    // not digit or lower case letter

    /**
     * scan response for sign need to login
     */
    private static final Pattern LOGIN_FINDER = Pattern.compile( SEPARATOR_REGEX
                                                                 + "(?:login"     // non-capturing group
                                                                 + "|logon"
                                                                 + "|account"
                                                                 + "|type=\"password\""
                                                                 + "|Sign In"
                                                                 + "|signin)"
                                                                 + SEPARATOR_REGEX, Pattern.CASE_INSENSITIVE );

    private static final String SPACE_REGEX = "[ \\r\\n\\t]*";

    /**
     * scan for SUBMIT button.  Will only detect if href in quotes.
     */
    private static final Pattern SUBMIT_FINDER = Pattern.compile( "href"
                                                                  + SPACE_REGEX
                                                                  + "="
                                                                  + SPACE_REGEX
                                                                  + QUOTE_REGEX
                                                                  + "("
                                                                  + HREF_REGEX
                                                                  + ")"
                                                                  + QUOTE_REGEX
                                                                  + "[^>]*"
                                                                  + ">"
                                                                  + SPACE_REGEX
                                                                  + "(?:<span>)*"
                                                                  + SPACE_REGEX
                                                                  + "(?:<img[^>]*>)*"
                                                                  + "(?:<font[^>]*>)*"
                                                                  + "(?:<strong>)*"
                                                                  + "(?:<span[^>]*>)*"
                                                                  + "(?:"
                                                                  + "Add a Software"
                                                                  + "|add program"
                                                                  + "|Add Software"
                                                                  + "|add"
                                                                  + "|Dodaj stron"
                                                                  + "|for authors"
                                                                  + "|L\u00e4gg till"
                                                                  + "|melden"
                                                                  + "|Neue"
                                                                  + "|Prefill"
                                                                  + "|Programm eintragen"
                                                                  + "|reichen"
                                                                  + "|Software addieren"
                                                                  + "|Software anmelden"
                                                                  + "|software\\s+submit"
                                                                  + "|soumettre"
                                                                  + "|Subir un Archivo"
                                                                  + "|Submit software"
                                                                  + "|Submit your software"
                                                                  + "|Submit/Update your software"
                                                                  + "|submit"
                                                                  + ")", Pattern.CASE_INSENSITIVE );

    /**
     * look for Article submit
     */
    private static final Pattern ARTICLE_FINDER = Pattern.compile( " article", Pattern.CASE_INSENSITIVE );

    /**
     * look for Article submit
     */
    private static final Pattern ARTICLE_FINDER_EXCEPTION = Pattern.compile(
            "\\Wpad\\W|You can submit your security and system software here",
            Pattern.CASE_INSENSITIVE );

    /**
     * scan response for signs you need a captcha to submit
     */
    private static final Pattern CAPTCHA_FINDER = Pattern.compile( "captcha"
                                                                   + "|security code"
                                                                   + "|validation code"
                                                                   + "|verification code"
                                                                   + "|verification"
                                                                   + "|Pr\u00fcfsumme"
                                                                   + "|code you see on the picture",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * look for proprietary category request
     */
    private static final Pattern CATEGORY_FINDER = Pattern.compile( "select category"
                                                                    + "|category:"
                                                                    + "|Kategorie:"
                                                                    + "|name=\"Category", Pattern.CASE_INSENSITIVE );

    /**
     * look for proprietary category request
     */
    private static final Pattern CATEGORY_FINDER_EXCEPTION = Pattern.compile( "Detect category from PAD-File",
            Pattern.CASE_INSENSITIVE );

    /**
     * look for some sort of form to submit to
     */
    private static final Pattern FORM_FINDER = Pattern.compile( "<form", Pattern.CASE_INSENSITIVE );

    /**
     * scan response for sign need to login
     */
    private static final Pattern LOGIN_FINDER_EXCEPTION = Pattern.compile(
            "if \\(value == 'Enter pad url'\\) \\{value =''\\}",
            Pattern.CASE_INSENSITIVE );

    /**
     * look for mandatory back link
     */
    private static final Pattern MUST_LINK_FINDER = Pattern.compile( "back link is required"
                                                                     + "|link back must be found"
                                                                     + "|Linkback is required"
                                                                     + "|linked to us"
                                                                     + "|must build a link"
                                                                     + "|must create a link"
                                                                     + "|must Provide us a back-link"
                                                                     + "|require a presence of the link"
                                                                     + "|Where the link to"
                                                                     + "|without link back URL will not be accepted"
                                                                     + "|you must add a link"
                                                                     + "|Your partners page",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * look for requirement you prefill/rekey
     */
    private static final Pattern REKEY_LINK_FINDER = Pattern.compile( "prefill",
            Pattern.CASE_INSENSITIVE );

    /**
     * look for SiteLink submit
     */
    private static final Pattern SITE_LINK_FINDER = Pattern.compile( "add a listing"
                                                                     + "|add a site"
                                                                     + "|Featured links"
                                                                     + "|Liens Publicitaires"
                                                                     + "|site URL"
                                                                     + "|submission to directory\\."
                                                                     + "|submit a site"
                                                                     + "|submit listing"
                                                                     + "|submit news"
                                                                     + "|submit site"
                                                                     + "|Submit/Update your software"
            , Pattern.CASE_INSENSITIVE );

    /**
     * keywords that apply to current candidate
     */
    static ArrayList<String> collectedKeywords;

    /**
     * Sites already in deaslist list.  Lookup by host without www.
     */
    static HashSet<String> deads;

    /**
     * Sites already in hassles list.  Lookup by host without www.
     */
    static HashSet<String> hassles;

    /**
     * Sites already in hassles list.  Lookup by host without www.
     */
    static HashSet<String> nohassles;

    /**
     * contents of web page being examined
     */
    private static String pageContents;

    /**
     * revised guess for URL of submit page
     */
    private static URL submitURL;

    /**
     * categorise ease of submitting to this site. pageContents has submit page contents.
     */
    private static void categoriseSite()
        {
        // we don't check for parked.  Use FindParked.
        if ( FORM_FINDER.matcher( pageContents ).find() )
            {
            if ( SITE_LINK_FINDER.matcher( pageContents ).find() )
                {
                markCandidate( "SiteLinks" );
                }
            else if ( ARTICLE_FINDER.matcher( pageContents ).find() && !ARTICLE_FINDER_EXCEPTION.matcher(
                    pageContents ).find() )
                {
                markCandidate( "ArticleLinks" );
                }
            else
                {
                if ( CAPTCHA_FINDER.matcher( pageContents ).find() )
                    {
                    markCandidate( "Validation" );
                    }
                if ( LOGIN_FINDER.matcher( pageContents ).find() && !LOGIN_FINDER_EXCEPTION.matcher( pageContents )
                        .find() )
                    {
                    markCandidate( "Login" );
                    }
                if ( MUST_LINK_FINDER.matcher( pageContents ).find() )
                    {
                    markCandidate( "BackLinkMandatory" );
                    }
                if ( CATEGORY_FINDER.matcher( pageContents ).find() && !CATEGORY_FINDER_EXCEPTION.matcher(
                        pageContents ).find() )
                    {
                    markCandidate( "Proprietary" );
                    }
                if ( REKEY_LINK_FINDER.matcher( pageContents ).find() )
                    {
                    markCandidate( "Rekey" );
                    }
                }
            }
        }

    /**
     * look on this page for a link to the submit page. It could be the submit page itself.
     *
     * @return true we have found the submit page.  false means abort. We don't have a submit page.
     * pageContents contains contents of submit page if found.
     * submitURL contains URL of submit page if found.
     */
    private static boolean findSubmitPage()
        {
        // is this the pad submit page or the home page?
        final String path = submitURL.getPath();
        if ( path.length() == 0 || path.equals( "/" ) )
            {
            // this is the home page.  We need to find a link to the pad submit page.
            Matcher m = SUBMIT_FINDER.matcher( pageContents );
            if ( m.find() )
                {
                // link to PAD page.
                try
                    {
                    submitURL = new URL( submitURL, m.group( 1 ) );
                    markCandidate( "Active" );
                    }
                catch ( MalformedURLException e )
                    {
                    err.println( "Malformed URL [" + submitURL + "][" + m.group( 1 ) + "]" );
                    markCandidate( "MalformedURL" );
                    e.printStackTrace( err );
                    return false;
                    }
                Get g = new Get();
                pageContents = g.send( submitURL, Get.UTF8 );
                int responseCode = g.getResponseCode();
                if ( !g.isGood() || pageContents == null || pageContents.length() == 0 )
                    {
                    markCandidate( "NotResponding" );
                    return false;
                    }
                // found submit page.  pageContents has contents of submit page we found.
                return true;
                }
            else
                {
                markCandidate( "NoSubmissionPage" );
                return false;
                }
            }
        else
            {
            // on submit page already. Trust URL given was the submit page.
            markCandidate( "Active" );
            return true;
            }
        }

    /**
     * look on this page for a link to the submit page, we start one layer deep from home page.
     *
     * @return true this URL is still a candidate,  false means abort. We don't have a submit page.
     */
    private static boolean findSubmitSubPage()
        {
        // is this the pad submit page or the home page?
        final String path = submitURL.getPath();
        if ( path.endsWith( "authors.php" ) )
            {
            // this is an intermediate page.  We need to find a link to the pad submit page.
            Matcher m = SUBMIT_SUB_FINDER.matcher( pageContents );
            if ( m.find() )
                {
                // link to PAD page.
                try
                    {
                    submitURL = new URL( submitURL, m.group( 1 ) );
                    }
                catch ( MalformedURLException e )
                    {
                    err.println( "Malformed URL [" + submitURL + "][" + m.group( 1 ) + "]" );
                    markCandidate( "MalformedURL" );
                    markCandidate( "MalformedURL" );
                    e.printStackTrace( err );
                    return false;
                    }
                Get g = new Get();
                pageContents = g.send( submitURL, Get.UTF8 );
                int responseCode = g.getResponseCode();
                if ( !g.isGood() || pageContents == null || pageContents.length() == 0 )
                    {
                    markCandidate( "NotResponding" );
                    return false;
                    }
                //  revise
                return true;
                }
            else
                {
                // wos not really an intermediate page after all
                // leave revisedSubURL as is.
                return true;
                }
            }
        else
            {
            // leave revisedSubURL as is.
            return true;
            }
        }

    /**
     * load and categorise the candidates.  candidates.csv file will be modified.
     *
     * @throws java.io.IOException on problems accessing files
     */
    private static void loadAndCategoriseCandidates( final boolean checkDups ) throws IOException
        {
        // O P E N
        out.println( "loading candidates.csv..." );
        final File candidateFile = new File( Build.MINDPROD_SOURCE + "/submitter/candidates.csv" );
        final CSVReader r = new CSVReader( new FileReader( candidateFile ) );
        final File tempOutFile = HunkIO.createTempFile( "tempprobe", ".csv", candidateFile );
        final CSVWriter w = new CSVWriter( EIO.getPrintWriter( tempOutFile, 2 * 1024, EIO.UTF8 ) );
        try
            {
            while ( true )
                {
                // R E A D
                final String[] fields = r.getAllFieldsInLine();
                // ignore blank lines
                if ( fields.length == 0 )
                    {
                    continue;
                    }
                collectedKeywords = new ArrayList<>( 100 );
                final String siteName = ( fields.length > 0 ) ? fields[ 0 ] : "";
                final String hostURLString = ( fields.length > 1 ) ? fields[ 1 ] : "";
                final String submitURLString = ( fields.length > 2 && fields[ 2 ].length() > 0 ) ? fields[ 2 ] : hostURLString;
                final String image = fields.length > 3 ? fields[ 3 ] : "";
                String keywords = fields.length > 4 ? fields[ 4 ] : "";
                final String notes = fields.length > 5 ? fields[ 5 ] : "";
                URL hostURL;
                try
                    {
                    hostURL = new URL( hostURLString );
                    submitURL = new URL( submitURLString );
                    }
                catch ( MalformedURLException e )
                    {
                    err.println( "Malformed URL [" + submitURLString + "]" );
                    e.printStackTrace( err );
                    markCandidate( "MalformedURL" );
                    hostURL = null;
                    System.exit( 2 );
                    }
                final String hostDomain = Misc.getDomain( hostURL );
                final String submitDomain = Misc.getDomain( submitURL );
                if ( checkDups && deads.contains( hostDomain ) )
                    {
                    markCandidate( "Duplicate" );
                    out.println( "dead dup: " + hostURLString );
                    continue;
                    }
                if ( checkDups && deads.contains( submitDomain ) )
                    {
                    markCandidate( "Duplicate" );
                    out.println( "dead dup: " + submitURLString );
                    continue;
                    }
                else if ( checkDups && hassles.contains( hostDomain ) )
                    {
                    markCandidate( "Duplicate" );
                    out.println( "hassle dup: " + hostURLString );
                    continue;
                    }
                else if ( checkDups && hassles.contains( submitDomain ) )
                    {
                    markCandidate( "Duplicate" );
                    out.println( "hassle dup: " + submitURLString );
                    continue;
                    }
                else if ( checkDups && nohassles.contains( hostDomain ) )
                    {
                    markCandidate( "Duplicate" );
                    out.println( "nohassle dup: " + hostURLString );
                    continue;
                    }
                else if ( checkDups && nohassles.contains( submitDomain ) )
                    {
                    markCandidate( "Duplicate" );
                    out.println( "nohassle dup: " + submitURLString );
                    continue;
                    }
                else
                    {
                    // look on submission page
                    Get g = new Get();
                    pageContents = g.send( submitURL, Get.UTF8 );
                    int responseCode = g.getResponseCode();
                    if ( !g.isGood() || pageContents == null || pageContents.length() == 0 )
                        {
                        markCandidate( "NotResponding" );
                        continue;
                        }
                    else
                        {
                        // at this point pageContents contains first cut at submit page contents.
                        if ( findSubmitPage() )
                            {
                            // submitURL as URL of submit page found.
                            if ( findSubmitSubPage() )
                                {
                                // at this point we have found the submit page and have pageContents.
                                categoriseSite();
                                }
                            }
                        }
                    }
                // append calculated categories
                for ( String kw : collectedKeywords )
                    {
                    keywords += ( " " + kw );
                    }
                try
                    {
                    // dedup keywords, canonicalise caps
                    keywords = Keyword.tidyKeywords( keywords );
                    }
                catch ( IllegalArgumentException e )
                    {
                    err.println( "Bad keyword "
                                 + e.getMessage()
                                 + " for ["
                                 + siteName
                                 + "] on line "
                                 + r.lineCount()
                                 + " of file "
                                 + EIO.getCanOrAbsPath( candidateFile ) );
                    }
                w.put( siteName );
                w.put( hostURLString );
                w.put( submitURLString );
                w.put( image );
                w.put( keywords );
                w.put( notes );
                w.nl();
                out.println( siteName + ", " + hostURLString + ", " + submitURLString + ", " + image + ", " +
                             "" + keywords + ", " +
                             notes );
                }
            }
        catch ( EOFException e )
            {
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempOutFile, candidateFile );
            }
        }

    /**
     * load sites that we have previously decided are dead We take only the URL
     *
     * @throws java.io.IOException on problems accessing files
     */
    private static void loadDeads()
            throws IOException
        {
        final CSVReader deadReader = new CSVReader( new FileReader( Build.MINDPROD_SOURCE + "/submitter/dead.csv" ) );
        out.println( "loading dead.csv..." );
        try
            {
            while ( true )
                {
                deadReader.skip( 1 );
                final String siteSubmissionURL = deadReader.get();
                final String padSubmissionURL = deadReader.get();
                if ( ST.isEmpty( siteSubmissionURL ) || ST.isEmpty( padSubmissionURL ) )
                    {
                    err.println( "empty field in dead.csv " + deadReader.lineCount() );
                    System.exit( 2 );
                    }
                try
                    {
                    deads.add( Misc.getDomain( new URL( siteSubmissionURL ) ) );
                    deads.add( Misc.getDomain( new URL( padSubmissionURL ) ) );
                    deadReader.skipToNextLine();
                    }
                catch ( MalformedURLException e )
                    {
                    err.println( "Dead has malformed URL [" + siteSubmissionURL + "/" + padSubmissionURL + "]" );
                    System.exit( 2 );
                    }
                }
            }
        catch ( EOFException e )
            {
            deadReader.close();
            }
        }

    /**
     * load sites that we have previously decided are dead We take only the URL
     *
     * @throws java.io.IOException on problems accessing files
     */
    private static void loadHassles() throws IOException
        {
        final CSVReader hassleReader = new CSVReader( new FileReader( Build.MINDPROD_SOURCE + "/submitter/hassle.csv"
        ) );
        out.println( "loading hassle.csv..." );
        try
            {
            while ( true )
                {
                hassleReader.skip( 1 );
                final String siteSubmissionURL = hassleReader.get();
                final String padSubmissionURL = hassleReader.get();
                if ( ST.isEmpty( siteSubmissionURL ) || ST.isEmpty( padSubmissionURL ) )
                    {
                    err.println( "empty field in hassles.csv " + hassleReader.lineCount() );
                    System.exit( 2 );
                    }
                try
                    {
                    hassles.add( Misc.getDomain( new URL( siteSubmissionURL ) ) );
                    hassles.add( Misc.getDomain( new URL( padSubmissionURL ) ) );
                    hassleReader.skipToNextLine();
                    }
                catch ( MalformedURLException e )
                    {
                    err.println( "hassle.csv has malformed URL [" + siteSubmissionURL + "/" + padSubmissionURL + "]" );
                    System.exit( 2 );
                    }
                }
            }
        catch ( EOFException e )
            {
            hassleReader.close();
            }
        }

    /**
     * load sites that we have previously decided are dead We take only the URL
     *
     * @throws java.io.IOException on problems accessing files
     */
    private static void loadnoHassles() throws IOException
        {
        final CSVReader nohassleReader = new CSVReader( new FileReader( Build.MINDPROD_SOURCE + "/submitter/nohassle" +
                                                                        ".csv" ) );
        out.println( "loading nohassle.csv..." );
        try
            {
            while ( true )
                {
                nohassleReader.skip( 1 );
                final String siteSubmissionURL = nohassleReader.get();
                final String padSubmissionURL = nohassleReader.get();
                if ( ST.isEmpty( siteSubmissionURL ) || ST.isEmpty( padSubmissionURL ) )
                    {
                    err.println( "empty field in nohassles.csv " + nohassleReader.lineCount() );
                    System.exit( 2 );
                    }
                try
                    {
                    nohassles.add( Misc.getDomain( new URL( siteSubmissionURL ) ) );
                    nohassles.add( Misc.getDomain( new URL( padSubmissionURL ) ) );
                    nohassleReader.skipToNextLine();
                    }
                catch ( MalformedURLException e )
                    {
                    err.println( "nohassle.csv has malformed URL [" + siteSubmissionURL + "/" + padSubmissionURL +
                                 "]" );
                    System.exit( 2 );
                    }
                }
            }
        catch ( EOFException e )
            {
            nohassleReader.close();
            }
        }

    /**
     * write out analysis of that site.
     *
     * @param keyword to describe what we discovered about the site
     */
    private static void markCandidate( final String keyword )
        {
        collectedKeywords.add( keyword );
        }

    /**
     * No parms.
     * reads and annotates candidates.csv
     * MUST CONFIGURE checkDups
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        final boolean checkDups = true;
        out.println( "CAPTCHA_FINDER: " + CAPTCHA_FINDER.toString() );
        out.println( "CATEGORY_FINDER: " + CATEGORY_FINDER.toString() );
        out.println( "CATEGORY_FINDER_EXCEPTION: " + CATEGORY_FINDER_EXCEPTION.toString() );
        out.println( "FORM_FINDER: " + FORM_FINDER.toString() );
        out.println( "LOGIN_FINDER: " + LOGIN_FINDER.toString() );
        out.println( "LOGIN_FINDER_EXCEPTION; " + LOGIN_FINDER_EXCEPTION.toString() );
        out.println( "MUST_LINK_FINDER; " + MUST_LINK_FINDER.toString() );
        out.println( "SITE_LINK_FINDER; " + SITE_LINK_FINDER.toString() );
        out.println( "SUBMIT_FINDER; " + SUBMIT_FINDER.toString() );
        out.println( "SUBMIT_SUB_FINDER; " + SUBMIT_SUB_FINDER.toString() );
        try
            {
            // domains already handled as hassle or nohassle. We don't want to reprocess them.
            deads = new HashSet<>( INITIAL_CAPACITY );
            loadDeads();
            hassles = new HashSet<>( INITIAL_CAPACITY );
            loadHassles();
            nohassles = new HashSet<>( INITIAL_CAPACITY );
            loadnoHassles();
            loadAndCategoriseCandidates( checkDups );
            out.println( "done" );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println();
            System.exit( 1 );
            }
        }
    }
