/*
 * [GrabAsp.java]
 *
 * Summary: Get Submitter sites from ASP website.
 *
 * Copyright: (c) 2011-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2011-05-19 initial version
 *  1.1 2012-02-13 ASP totally changed the layout of its files.
 *  1.2 2012-05-25 update number of ASP pages.
 *  1.3 2014-04-24 moved website to http://padsites.org/
 */
package com.mindprod.submitter;

import com.mindprod.common18.Build;
import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.csv.CSVWriter;
import com.mindprod.http.Chase;
import com.mindprod.http.Get;
import com.mindprod.hunkio.HunkIO;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * Get Submitter sites from ASP website.
 * <p/>
 * Slow, because does probes the Internet three times for each site..
 * results appear in E:\com\mindprod\submitter\grabasp.csv
 * for further processing.
 * Adjust PAGES before use.
 * Must run GrabAsp itself inside IntelliJ.
 * Raw results end up in E:\com\mindprod\submitter\grabasp.csv
 * <p/>
 * Use filterasp.btm to clean up results.
 * It will sort then ask you to remove sites you handled previously.
 * tidied results will end up in E:\com\mindprod\submitter\candidates.cvs
 * replacing any candidates there already.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.3 2014-04-24 moved website to http://padsites.org/
 * @since 2011-05-19
 */
public final class GrabAsp
    {
    static final String RELEASE_DATE = "2014-04-24";

    // I M P O R T A N T !
    // update PAGES  manually from   http://padsites.org/
    // adjust -fetch on command line.
    // last scanned
    // 2016-04-10
    // 2016-04-30
    // 2016-05-07
    // 2016-05-16
    // 2016-06-19
    static final String VERSION_STRING = "1.3";

    private static final int FIRST_COPYRIGHT_YEAR = 2011;

    /**
     * undisplayed copyright notice
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2011-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    private static final String PADSITE_CSV_NAME = Build.MINDPROD_SOURCE + "/submitter/grabasp.csv";

    private static final String ASP_HOST = "http://padsites.org";

    /**
     * rows that contain junk
     */
    private static final String[] rowsToIgnore = {
            "OnOrderByChange",
            "Macromedia Flash",
            "Ukrainian",
            "pad/page0",
            "<table" };

    /**
     * extracts Home URL from:
     * <td>URL</td><td><a href="/go_site.php?uin=site-003111a4b3d" rel="nofollow" target="_blank">
     */
    private static final Pattern INDIRECT_HOME_URL = Pattern.compile(
            "<td>URL</td><td><a href=\"/go_site\\.php\\?uin=site\\-(\\p{Alnum}+)\" rel=\"nofollow\"",
            Pattern.CASE_INSENSITIVE );

    /**
     * extracts Submit URL from:
     * <td>Submission&nbsp;URL</td><td><a href="/go_site.php?uin=site-003111a4b3d&amp;type=1" rel="nofollow"
     * target="_blank">
     */
    private static final Pattern INDIRECT_SUBMIT_URL = Pattern.compile(
            "<td>Submission&nbsp;URL</td><td><a href=\"/go_site\\.php\\?uin=site\\-(\\p{Alnum}+)&amp;type=",
            Pattern.CASE_INSENSITIVE );

    /**
     * extracts last modified date from
     * <td>Modified</td><td>2012-01-19 13:02:17</td>
     * on main page is shown as <td class="text" style="text-align:right">15-01-2014</td  DD-MM-YYYY
     */
    private static final Pattern MODIFIED = Pattern.compile(
            "<td>Modified</td><td>(\\p{Digit}{4}\\-\\p{Digit}{2}\\-\\p{Digit}{2}) ",
            Pattern.CASE_INSENSITIVE );

    /**
     * extracts site name from  siteinfo page
     * <a href="/siteinfo/coolscreens.html" title="More info...">CoolScreens</a>
     */
    private static final Pattern SITENAME = Pattern.compile(
            "<a href=\"/siteinfo/([!%\\(\\)\\-\\._\\p{Alnum}]+)\\.html\" title\\=\"More info\\.\\.\\.\">([ !%'\\(\\)" +
            "\\-\\._\\p{Alnum}]+)</a>",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * number of pages on the ASP website http://padsites.org. Change comment on grabasp.btm
     */
    private static int pages;

    /**
     * get url on ASP site for given page 1 .. PAGES
     *
     * @param pageNumber page number
     *
     * @return URL on asp site of that page of database.
     * @throws java.net.MalformedURLException if bad URL generated
     */
    private static URL URLForPage( int pageNumber ) throws MalformedURLException
        {
        return new URL( ASP_HOST + "/page" + ST.toLZ( pageNumber, 3 ) + "/" );
        }

    /**
     * get local file for given page 1 .. PAGES
     *
     * @param pageNumber page number
     *
     * @return local file where that page's html is stored..
     */
    private static File fileForPage( int pageNumber )
        {
        return new File( Build.MINDPROD_SOURCE + "/submitter/" + ST.toLZ( pageNumber, 3 ) + ".html" );
        }

    /**
     * fetch all ASP web pages from their site
     *
     * @throws java.io.IOException if cannot save page.
     */
    private static void getASPWebPages() throws IOException
        {
        Get g = new Get();
        for ( int pageNumber = 1; pageNumber <= pages; pageNumber++ )
            {
            final String page = g.send( URLForPage( pageNumber ), Get.UTF8 );
            HunkIO.writeEntireFile( fileForPage( pageNumber ), page );
            }
        }

    /**
     * dump out regexes
     */
    private static void proofreadRegexes()
        {
        out.println( SITENAME );
        out.println( INDIRECT_HOME_URL );
        out.println( INDIRECT_SUBMIT_URL );
        }

    /**
     * scan one page of the ASP website, extracting padsites. Slow because it chases each URL to resolve redirects.
     *
     * @param page HTML from one page from ASP site
     * @param w    CSVWrite to export found PADSites to
     *
     * @throws MalformedURLException if bad URL generated
     */
    private static void scanPage( String page, CSVWriter w ) throws MalformedURLException
        {
        int from = 0;
        rowLoop:
        while ( true )
            {
            final int start = page.indexOf( "<tr", from );
            if ( start < 0 )
                {
                break;
                }
            final int end = page.indexOf( "</tr>", start + "<tr".length() );
            if ( end < 0 )
                {
                break;
                }
            final String row = page.substring( start + "<tr".length(), end );
            from = end + "</tr>".length();
            for ( String junkWord : rowsToIgnore )
                {
                if ( row.contains( junkWord ) )
                    {
                    // ignore
                    continue rowLoop;
                    }
                }
            final String siteName;
            final String infoURLCore;
            // extract sitename
            // we want only two things from the row, name of site, and indirect URL to site.
            final Matcher s = SITENAME.matcher( row );
            if ( s.find() )
                {
                infoURLCore = s.group( 1 );
                siteName = s.group( 2 );
                }
            else
                {
                out.println( ">>>failed to find sitename : " + row );
                continue;
                }
            final Get g = new Get();
            final String infoPage = g.send( new URL( ASP_HOST + "/siteinfo/" + infoURLCore +
                                                     ".html" ), Get.UTF8 );
            final String indirectHomeURLCore;
            final Matcher h = INDIRECT_HOME_URL.matcher( infoPage );
            if ( h.find() )
                {
                indirectHomeURLCore = h.group( 1 );
                }
            else
                {
                out.println( ">>>failed to find home URL on info page : " + infoPage );
                continue;
                }
            final String indirectSubmitURLCore;
            final Matcher b = INDIRECT_SUBMIT_URL.matcher( infoPage );
            if ( b.find() )
                {
                indirectSubmitURLCore = b.group( 1 );
                }
            else
                {
                out.println( ">>>failed to find Submit URL on info page : " + infoPage );
                continue;
                }
            final String lastModified;
            final Matcher m = MODIFIED.matcher( infoPage );
            if ( m.find() )
                {
                lastModified = m.group( 1 );
                }
            else
                {
                out.println( ">>>failed to find last modified date on info page : " + infoPage );
                continue;
                }
            final Chase hh = new Chase();
            final String homeURL = hh.send( new URL( ASP_HOST + "/go_site.php?uin=site-" +
                                                     indirectHomeURLCore ) );
            final Chase bb = new Chase();
            final String submitURL = bb.send( new URL( ASP_HOST + "/go_site.php?uin=site-" +
                                                       indirectSubmitURLCore + "&type=1" ) );
            // echo to screen and well as log to file
            out.println( siteName + ", " + homeURL + ", " + submitURL + ", " + lastModified );
            // name, homeUrl, submitUrl , date
            w.put( siteName );
            w.put( homeURL );
            w.put( submitURL );
            w.put( lastModified );
            w.nl();
            } // end while
        }

    /**
     * Refresh PAGES first.
     * Results end up in E:\com\mindprod\submitter\grabasp.csv and console.
     * sort, then prune old PADSites before analysing new ones as candidates.
     * use FilterASP.btm to sort and clean up.
     *
     * @param args args[0]="-fetch" to refetch raw data from ASP website, otherwise reprocessesses previously fetched
     *             data
     *
     * @throws IOException if problems reading/writing files.
     */
    public static void main( String args[] ) throws IOException
        {
        pages = Integer.parseInt( args[ 0 ] );
        // proofreadRegexes();
        if ( args.length >= 2 && args[ 1 ].equalsIgnoreCase( "-fetch" ) )
            {
            getASPWebPages();
            }
        final CSVWriter w = new CSVWriter( EIO.getPrintWriter( new File( PADSITE_CSV_NAME ), 32 * 1024, EIO.UTF8 ) );
        for ( int pageNumber = 1; pageNumber <= pages; pageNumber++ )
            {
            final String page = HunkIO.readEntireFile( fileForPage( pageNumber ) );
            out.println( "-----------scanning page " + pageNumber + "------------------" );
            w.nl( ">>>page " + pageNumber );
            // takes a while, since has to read matching info pages.
            scanPage( page, w );
            }
        w.close();
        }
    }
