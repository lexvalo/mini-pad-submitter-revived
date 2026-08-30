/*
 * [Crack.java]
 *
 * Summary: Analyses a submit form to extract information needed to automate submission.
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
 *  4.3 2009-04-11 add AivSoft, FilesGuard, FreeFileSeek, GetAllSoft, HotFileDownload,
 *                 Seek4Software, SharewarePower, Soft-db, Softake, Softholm, SoftLookup
 *  4.4 2009-05-11 remove DL Daddy, add Softwarelode, Digimodes, Download-by
 *  4.5 2009-05-19 remove TrialFiles, add Publish-Me, AlphaDownloads , DownloadChoice, SoftwareArchiveIsGreat,
 *                 Download5000, DownloadArsivi, DownloadShareware, DownloadStation,
 *                 EliasSoftDownloads, FreeSoftwareSharewareDownloads,
 *                 FreeShareWeb, FreewareArchiv, Freeware1,
 *                 FreeSoftwareApps, Goooggle, SafeFreeDownloads, SafeFreeSoftware, SafeFreeSoftwareDowload
 *  4.6 2009-05-19 remove WebAcclaim, add PadfileInfo, PadFM, PeachSeed, ShareApple, Vandino, Webzf
 *  4.7 2009-06-06 add Geeez, GeneralShareware, Newsoft2006, pc24hours,
 *                 ResourceDB, RetailerDeals, Share32, SharewareBay, SharewareKing, SoftAllWare, BobSoft, SoftLow
 *  4.8 2009-07-11 remove BobSoft, EnterHelp, Softake
 *                 add Top4Download, SoftwareMass, SoftwareSizzle, SuperDownloads, TrialWare, TryingBuying, WestDownload
 *  4.9 2009-07-30 remove BestSoftware, add SubmitPadFile, ProgrammersHeaven
 *  5.0 2009-09-22 remove DownloadWast, Seek4Software. CSV files to track hassle and hassle-free.
 *                 Prober discards sites already processed.
 *                 add GeneralFreeware, Enterhelp, Bobsoft, SoftCrown, Softmerge
 *  5.1 2009-10-25 remove Techwoods, add SuperWebHunt, ABDownloads, Downloads2K, Soft321.
 *  5.2 2009-11-03 remove Sharewareville. Add Windows7Download, SoftwareDownloads
 *  5.3 2009-11-21 remove http://www.allapp.com/Submit-Software/
 *                 add http://www.5moons.net/submit.php
 *                 http://www.8844download.com/submit.htm
 *                 http://www.affiliate-referrals.net/submit.php (DLDaddy)
 *                 http://www.goodownload.com/submit.html
 *                 http://www.resourcefill.com/submit.php
 *                 http://www.uniqueidea.net/download/submit.asp
 *                 http://www.acidfiles.com/submit.html
 *  5.4 2009-12-02 remove FileVolution, add Download11, SoftCab, AfDown, DeltaLoad, DesktopShareware, 12buzz,
 *                 GetSharewareForFree, FreePadDatabase, FreewareTown
 *  5.5 2009-12-11 add 11 sites: DownloadDir, EuSoftNet, FreeSoftwarePrograms, IMfreeware, dlTube,
 *                 MySoftwareList, NewDownload, SoftwareCrown, PeterBurgess, SearchSomeSoft, SearchAllSoft
 *  5.6 2009-12-13 add 10 sites: 4software2Download, SharewareCheap, SoftwareRatings,
 *                 ShopLagom, SmallFreeware, Soft4Sale, SoftMobile, SoftwareMatrix, SystemUtils, WorldSoftwareArchive
 *  5.7 2009-12-17 add 16 sites: 123Freesoft, 4software2Download, DownloadYourSoftware, EzySoft, FastShareware,
 *                 FileEdge, FilePicks, FileProfile, LoadFree, , ObtainSoft, ReviewWorld, Download4a,
 *                 DownloadExpo, DownloadHeaven, DownloadPile, EasyFileDownloads
 *  5.8 2009-12-18 add 10 sites: FilePile, FilesStore, FindBestSoft, FineDownloads,
 *                 FreewareDump, FreewareSoft, MetaDownloads, PocketPCSoftwareDownloads, Run2, SafeFreeSoftware
 *  5.9 2009-12-19 add 15 sites: SharewareDump, Sharewareville, Smilestone, SoftDir, SoftwareDetails,
 *                 SoftwareKB, SoftwarePyramid, SoftWeb, TechWoods, Telecharger, TopSharewareDownloads,
 *                 VideoSoftwareDirect,
 *                 WinColors, WindFile, BigSoftwareBox
 */
package com.mindprod.submitter;

import com.mindprod.common18.ST;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.http.Get;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * Analyses a submit form to extract information needed to automate submission.
 * <p/>
 * Rough and ready.
 * Designed primarily for Roedy's use to research new sites.
 * Scans the submit page and generates most of the code needed to add support for
 * yet another submission site.
 * Takes siteName, home and submitURL with our without trailing commas. Space separated
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2009-12-19 original
 * @since 2009
 */
public class Crack
    {
    //todo: avoid /submit/submit when base ends in ./submit sometimes.

    /**
     * true if want additional output to help figure out why a site won't analyse.
     */
    private static final boolean DEBUGGING = false;

    /**
     * regex to go inside [] to match all punctuation chars, not counting space
     * <p/>
     * !"#$%&'()*+,-./:;<=>?[\]^_`{|}~     regeq quoted
     */
    private static final String ALL_PUNCT = "!\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?\\[\\\\\\]\\^_`\\{\\|\\}~";

    /**
     * regex to go inside [] for all punct chars but >
     */
    private static final String ALL_PUNCT_BUT_GT = ALL_PUNCT + "&&[^>]";

    /**
     * finds parm in form ,
     * e.g. <Input Name="psubmit" Type="Submit" Value="Submit PAD File">
     * <Input Type="text" Value="http://" Size="45" Name="padurl">
     * <Input Type="text" Value="" Size="10" Name="regnow">
     * <input Type="text" Value="http://" Size="45" Name="padurl" class="input01" style="width:90%">
     * <input type="image" src="http://www.downbroad.com/images/submit.jpg" style="margin: 0 auto; text-align:
     * center;" name="" value="Submit" />
     * <input type="hidden" name="form" value="submitting"/>
     * <input type="text" size="46" name="xmlurl"
     * value="http://www.PAD File URL">
     * <input type="submit" name="B1" value="Submit"
     * style="font-family: Verdana">&nbsp;&nbsp; <input type="reset" name="B2" value="Reset"
     * style="font-family: Verdana">
     * <p/>
     * allow anything inside except a >
     */
    private static final Pattern PARM_FINDER = Pattern.compile(
            "<(?:input|textarea|select)[\\s\\p{Alnum}"
            + ALL_PUNCT_BUT_GT
            + "]+>", Pattern.CASE_INSENSITIVE
    );

    /**
     * regex to go inside [ ] for all chars but " and '
     */
    private static final String ALL_PUNCT_BUT_QUOTES = ALL_PUNCT + "&&[^'\"]";

    /**
     * finds value in parm
     * all punct except "' , ideally should eat Russian values etc. too.
     */
    private static final Pattern VALUE_FINDER = Pattern.compile(
            "value=['\"]([ \\p{Alnum}" +
            ALL_PUNCT_BUT_QUOTES +
            "]+)['\"]", Pattern.CASE_INSENSITIVE
    );

    /**
     * words in the action field that indicate we definitely do not the submit form
     */
    private static final String[] BAD_ACTION_INDICATORS = {
            // must be pure lower case to compare
            "google",
            "search",
            "show",
            "wp-comments",
    };

    /**
     * words in the action field that indicate we have the submit form
     */
    private static final String[] GOOD_ACTION_INDICATORS = {
            // must be pure lower case to compare
            "",
            "add_software",
            "cgi-sys/formmail.pl",
            "check-pad",
            "checkadd",
            "edt_pad_location",
            "insertpad",
            "pad-check",
            "submit",
            "submit-pad-file.html",
            "submit_pad_file.php",
            "submit-software.php",
            "www.downbroad.com/com/soft/admin_ex/index.php",
    };

    /**
     * words in parameter name that indicate this is the padURL param
     */
    private static final String[] PAD_URL_INDICATORS = {
            // must be pure lower case for compare.
            "cale_pad",
            "edt_pad_location",
            "frm[url]",
            "manualsubmissionurl",
            "manyurls",
            "notes",
            "p",
            "pad",
            "pad_file",
            "pad-file",
            "pad_file_box",
            "pad[url]",
            "pad-url",
            "pad_url",
            "padfile",
            "padfileurl",
            "pad_file_url",
            "padfilename",
            "padlocation",
            "padurl",
            "padurls",
            "paffile",
            "so",
            "submit_pad[url]",
            "submitpadfileform[padfileurl]",
            "url",
            "url_pad",
            "url_pad_file",
            "textboxurl",
            "validateurl",
            "web",
            "xmlpad",
            "xmlpath",
            "xmlurl"
    };
    //  <form method="post" action="http://filedir.com/submit/form/">

    /**
     * usual x=y pair parms
     */
    private static final ArrayList<String> parmPairs = new ArrayList<>( 11 );

    /**
     * find action post/get
     * e.g. action="http://freesafesoft.com/search.php?pindex=1&showimage=on">    or just plain action>
     */
    private static final Pattern ACTION_FINDER = Pattern.compile(
            "action=[\"']?([\\p{Alnum}&\\-\\./:=\\?_~]*)[\"']?|action",
            Pattern.CASE_INSENSITIVE );

    /**
     * finds end of form on the page
     */
    private static final Pattern END_FORM_FINDER = Pattern.compile( "</form>", Pattern.CASE_INSENSITIVE );

    /**
     * finds form on the page and extracts the action.
     * e.g.
     * <form action="http://www.downbroad.com/com/soft/admin_ex/index.php" name="yao"  method="post" >
     * <form name="SubmitPAD" method="post" action="http://www.PadRepository.com/SubmitPAD.htm?action=submit">
     * <form action="check-pad.html" method="post">
     * <p/>
     * allow anything inside except a >
     */
    private static final Pattern FORM_FINDER = Pattern.compile( "<form (.*?)>", Pattern.CASE_INSENSITIVE );

    /**
     * finds method in <form tag
     */
    private static final Pattern METHOD_FINDER = Pattern.compile( "method=['\"]?(post|get)['\"]?",
            Pattern.CASE_INSENSITIVE );

    /**
     * finds name in parm name="xxx", where name can be empty.
     * e.g. <input type="hidden" name="form" value="submitting"/>
     */
    private static final Pattern NAME_FINDER = Pattern.compile( "name=['\"]([\\p{Alnum}\\$\\._\\-\\[/\\]]*)[\"']",
            Pattern.CASE_INSENSITIVE );

    /**
     * primitive split of encoded pairs of parms
     */
    private static final Pattern PARM_SPLITTER = Pattern.compile( "[\\?=&]" );

    /**
     * action with lead / and possible path.
     */
    private static String absoluteAction;

    /**
     * aux parms from the url
     */
    private static String[] auxParmPairs;

    /**
     * camel case human name for site
     */
    private static String humanName;

    /**
     * true if method="POST" false if "GET"
     */
    private static boolean isViaPost;

    /**
     * url of the manual submission page
     */
    private static URL manualSubmissionURL;

    /**
     * contents of web page for manual submit that we analyse
     */
    private static String page;

    /**
     * find and extract information needed to fake a post on the web page
     *
     * @throws java.net.MalformedURLException if bad action url
     */
    private static void analyse() throws MalformedURLException
        {
        if ( DEBUGGING )
            {
            out.println( page );
            }
        int totalFormsFound = 0;
        int plausibleFormsFound = 0;
        final Matcher startFormFinder = FORM_FINDER.matcher( page );
        while ( startFormFinder.find() )
            {
            totalFormsFound++;
            final String formContents = startFormFinder.group( 1 );
            final Matcher af = ACTION_FINDER.matcher( formContents );
            if ( af.find() )
                {
                String action = af.groupCount() > 0 ? af.group( 1 ) : "";
                if ( DEBUGGING )
                    {
                    out.println();
                    out.println(
                            "------------------------------------------------------------------------------------------" );
                    out.println();
                    out.println( "found form" );
                    }
                final String lcAction = analyseAction( action );
                analyseMethod( formContents );
                if ( isActionOfInterest( lcAction ) )
                    {
                    plausibleFormsFound++;
                    analyseForm( startFormFinder.end() );
                    }
                else if ( DEBUGGING )
                    {
                    out.println( "ignoring form " + lcAction );
                    }
                }
            else
                {
                out.println( "no action found, presuming default " );
                analyseAction( "" );
                analyseMethod( formContents );
                plausibleFormsFound++;
                analyseForm( startFormFinder.end() );
                }
            } // end form-finder loop
        out.println( totalFormsFound + " total <forms found " + plausibleFormsFound + " plausible <forms found" );
        }

    /**
     * analyse the action parm of the <form
     *
     * @param action, action from form
     *
     * @return action in lower case
     * @throws java.net.MalformedURLException if bad action url
     */
    private static String analyseAction( String action )
            throws MalformedURLException
        {
        if ( action.length() == 0 )
            {
            // no action. It defaults to  name of submitter file.
            action = manualSubmissionURL.getPath();
            }
        if ( DEBUGGING )
            {
            out.println( "found action: " + action );
            }
        final String lcAction = ST.trimLeading( action.toLowerCase(), '/' );
        // prepend / and possible lead path
        URL actionURL = new URL( manualSubmissionURL, action );
        absoluteAction = actionURL.getPath();
        if ( DEBUGGING )
            {
            out.println( "action:" + absoluteAction );
            }
        // might be some get-style parms glued on the end of the action.
        final String getEncodedAuxParms = actionURL.getQuery();
        auxParmPairs = ( getEncodedAuxParms != null && getEncodedAuxParms.length() != 0 ) ? PARM_SPLITTER.split(
                getEncodedAuxParms ) : null;
        return lcAction;
        }

    /**
     * analyse one form in the HTML
     *
     * @param startOffset where the body of the form starts, just after <form > tag
     */
    private static void analyseForm( final int startOffset )
        {
        final Matcher endFormFinder = END_FORM_FINDER.matcher( page );
        endFormFinder.region( startOffset, page.length() );
        if ( !endFormFinder.find() )
            {
            out.println( "can't find </form>" );
            }
        else
            {
            final Matcher parmFinder = PARM_FINDER.matcher( page );
            parmFinder.region( startOffset, endFormFinder.start() );
            // parmFinder is limited to form bounds
            analyseParms( parmFinder );
            generateJavaCode();
            }
        }

    /**
     * analyse the method parm of the <form
     *
     * @param formContents where to look for action
     */
    private static void analyseMethod( String formContents )
        {
        if ( DEBUGGING )
            {
            out.println( "searching for get/post method in " + formContents );
            }
        final Matcher methodFinder = METHOD_FINDER.matcher( formContents );
        if ( methodFinder.find() )
            {
            if ( DEBUGGING )
                {
                out.println( "found method: " + methodFinder.group( 1 ) );
                }
            isViaPost = methodFinder.group( 1 ).toLowerCase().equals( "post" );
            }
        else
            {
            // no method specified, defaults to GET
            isViaPost = false;
            }
        }

    /**
     * analyse the <input parms in the html form
     *
     * @param parmFinder the Matcher pointing ready to do a find of all parms
     */
    private static void analyseParms( final Matcher parmFinder )
        {
        parmPairs.clear();
        while ( parmFinder.find() )
            {
            final String parm = parmFinder.group( 0 );
            if ( DEBUGGING )
                {
                out.println( "found parm: " + parm );
                }
            final Matcher nameFinder = NAME_FINDER.matcher( parm );
            final String name = nameFinder.find() ? nameFinder.group( 1 ) : "";
            if ( DEBUGGING )
                {
                out.println( "found name:" + name );
                }
            final Matcher valueFinder = VALUE_FINDER.matcher( parm );
            final String value = valueFinder.find() ? valueFinder.group( 1 ) : "";
            if ( DEBUGGING )
                {
                out.println( "found value:" + value );
                }
            // don't bother to send nameless fields.
            if ( name.length() != 0 )
                {
                parmPairs.add( name );
                parmPairs.add( value );
                }
            }
        }

    /**
     * generate rough Java code for Submitter to implement this submission site
     */
    private static void generateJavaCode()
        {
        out.println();
        final String enumName = humanName.toUpperCase();
        out.println();
        out.println( "-----" );
        out.println();
        // generate line like:
        //  ABSOLUTELYFREESHAREWARE( "AbsolutelyFreeSoftware", "http://freeware.hs-lab.com.ua/submit.php",
        // "/checkadd.php" )
        out.println( enumName
                     + " ( \""
                     + humanName
                     + "\", \""
                     + manualSubmissionURL.toString()
                     + "\", \""
                     + absoluteAction
                     + "\" )" );
        out.println( "{" );
        out.println( "/**\n"
                     + "* Simulate manual submit\n"
                     + "* @param pad URL of the pad xml file we are submitting.\n"
                     + "*/" );
        if ( auxParmPairs == null || auxParmPairs.length == 0 )
            {
            out.println( "String submit( String pad )\n"
                         + "{\n"
                         + "return submitVia"
                         + ( isViaPost ? "Post" : "Get" )
                         + "( " );
            generateJavaCodeForParmPairs();
            }
        else
            {
            out.println( "String submit( String pad )\n"
                         + "{\n"
                         + "final Post post = new Post();\n"
                         + "// has both get and post style parms\n"
                         + "post.setParms(" );
            generateJavaCodeForAuxParmPairs();
            out.println( "\n"
                         + "post.setPostParms(" );
            generateJavaCodeForParmPairs();
            out.println( "final URL actionURL = getActionURL();\n"
                         + "final String result = post.send( actionURL.getHost(), -1  , actionURL.getPath(), " +
                         "Post.UTF8 );\n"
                         + "responseCode = post.getResponseCode();\n"
                         + "responseMessage = post.getResponseMessage();\n"
                         + "return result;\n" );
            }
        out.println( "}" );
        out.println( "}," );
        out.println();
        out.println( "-----" );
        out.println();
        out.println(
                humanName
                + ", "
                + manualSubmissionURL.getProtocol()
                + "://"
                + manualSubmissionURL.getHost()
                + ", "
                + manualSubmissionURL.toString()
                + ", "
                + humanName.toLowerCase()
                + ".png,,"
        );
        out.println();
        out.println( "-----" );
        out.println();
        }

    /**
     * generate Java code for the Get-style parms auxiliary to the POST
     */
    private static void generateJavaCodeForAuxParmPairs()
        {
        if ( ( auxParmPairs.length & 1 ) != 0 )
            {
            out.println( "must have even number of aux parms " + auxParmPairs.length );
            }
        int last = auxParmPairs.length - 2;
        for ( int i = 0; i <= last; i += 2 )
            {
            out.println( "\"" + auxParmPairs[ i ] + "\", \"" + auxParmPairs[ i + 1 ] + "\"" + ( i == last ? " );" :
                                                                                                "," ) );
            // leave comma off last line, and put ); instead
            }
        }

    /**
     * generate Java in form of comma list of pairs for the parameters.
     */
    private static void generateJavaCodeForParmPairs()
        {
        final String[] pairs = parmPairs.toArray( new String[ parmPairs.size() ] );
        assert ( pairs.length & 1 ) == 0 : "must have even number of parms";
        int last = pairs.length - 2;
        int padParmCount = 0;
        for ( int i = 0; i <= last; i += 2 )
            {
            final String name = pairs[ i ];
            final String value = pairs[ i + 1 ];
            if ( isThisParmForPad( name, value ) )
                {
                out.println( "\"" + name + "\", pad" + ( i == last ? " );" : "," ) );
                padParmCount++;
                }
            else
                {
                out.println( "\"" + name + "\", \"" + value + "\"" + ( i == last ? " );" : "," ) );
                }
            }
        if ( padParmCount != 1 )
            {
            out.println( "Error! " + padParmCount + "  pad parameters in the generated code above. There should be " +
                         "exactly 1." );
            out.println();
            }
        }

    /**
     * get complete HTML for a submit page from current url.
     * Leaves the result in page.
     */
    private static void getHTML()
        {
        final Get get = new Get();
        page = get.send( manualSubmissionURL, Get.UTF8 );
        final int responseCode = get.getResponseCode();
        final String responseMessage = get.getResponseMessage();
        if ( responseCode != 200 )
            {
            out.println( "could not fetch page. responseCode: " + responseCode + " responseMessage:" +
                         responseMessage );
            }
        }

    /**
     * Determine if this form is one for submitting pads.
     *
     * @param lcAction action from the form, in lower case.
     *
     * @return true if this action looks like one used to submit a PAD
     */
    private static boolean isActionOfInterest( String lcAction )
        {
        // determine if this Pad is of interest.
        lcAction = ST.trimLeading( lcAction, '/' );
        for ( String actionIndicator : BAD_ACTION_INDICATORS )
            {
            if ( lcAction.contains( actionIndicator ) )
                {
                return false;
                }
            }
        for ( String actionIndicator : GOOD_ACTION_INDICATORS )
            {
            if ( lcAction.contains( actionIndicator ) )
                {
                return true;
                }
            }
        return false;
        }

    /**
     * Does this parameter refer to the pad?
     *
     * @param name  name of pad field ( not id )
     * @param value default value
     *
     * @return true if this refers to the pad URL.
     */
    private static boolean isThisParmForPad( String name, String value )
        {
        name = name.trim();
        value = value.trim();
        // out.println( "testing " + name + "=" + value );
        if ( value.startsWith( "http:" ) )
            {
            return true;
            }
        else
            {
            final String nameLc = name.toLowerCase();
            for ( String padURLIndicator : PAD_URL_INDICATORS )
                {
                if ( nameLc.equals( padURLIndicator ) )
                    {
                    return true;
                    }
                }
            }
        return false;
        }

    /**
     * proofread regexes
     */
    private static void proofread()
        {
        out.println( "ACTION_FINDER: " + ACTION_FINDER.toString() );
        out.println( "END_FORM_FINDER: " + END_FORM_FINDER.toString() );
        out.println( "FORM_FINDER: " + FORM_FINDER.toString() );
        out.println( "METHOD_FINDER: " + METHOD_FINDER.toString() );
        out.println( "NAME_FINDER: " + NAME_FINDER.toString() );
        out.println( "PARM_FINDER: " + PARM_FINDER.toString() );
        out.println( "PARM_SPLITTER: " + PARM_SPLITTER.toString() );
        out.println( "VALUE_FINDER: " + VALUE_FINDER.toString() );
        }

    /**
     * analyse humanname, homeurl, submiturl
     *
     * @param args siteName and submitURL.
     *
     * @throws MalformedURLException if submitter URL is bad.
     */
    public static void main( String[] args ) throws MalformedURLException
        {
        if ( DEBUGGING )
            {
            proofread();
            }
        try
            {
            if ( args.length < 3 )
                {
                throw new IllegalArgumentException( "Command line needs siteName, home and submitURL" );
                }
            humanName = DeEntifyStrings.stripHTMLTags( ST.trimTrailing( args[ 0 ], ',' ) );
            manualSubmissionURL = new URL( ST.trimTrailing( args[ 2 ], ',' ) );
            if ( DEBUGGING )
                {
                out.println( "Fetching submit page..." );
                }
            getHTML();
            if ( DEBUGGING )
                {
                out.println( "Analysing submit page..." );
                }
            if ( page != null )
                {
                analyse();
                }
            }
        catch ( MalformedURLException e )
            {
            out.println( "malformed URL" );
            }
        }
    }
