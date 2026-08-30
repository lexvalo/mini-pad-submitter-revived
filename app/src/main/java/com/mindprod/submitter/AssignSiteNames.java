/*
 * [AssignSiteNames.java]
 *
 * Summary: Assign site names to candidates. Overrides existing names.
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
 *  1.0 2010-01-10 initial version
 */
package com.mindprod.submitter;

import com.mindprod.common18.Build;
import com.mindprod.common18.EIO;
import com.mindprod.csv.CSVReader;
import com.mindprod.csv.CSVWriter;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * Assign site names to candidates. Overrides existing names.
 * <p/>
 * Input: candidates.csv output: candidates.csv, 1 to 5 fields.  url in second column
 * Designed primarily for Roedy's use to research new sites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2010-01-10 initial version
 * @since 2010
 */
public class AssignSiteNames
    {
    /**
     * list of words often used in domain names. All must be lower case.
     */
    private static final String[] camelWords =
            {
                    // list will later be automatically sorted longest first.
                    "0",
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "about",
                    "active",
                    "add",
                    "all",
                    "allied",
                    "alpha",
                    "alt",
                    "alternative",
                    "alternatives",
                    "android",
                    "and",
                    "arcade",
                    "arcades",
                    "arch",
                    "arches",
                    "bank",
                    "best",
                    "big",
                    "bigger",
                    "blue",
                    "box",
                    "bronze",
                    "bulletin",
                    "bulletins",
                    "buy",
                    "buyer",
                    "buyers",
                    "calculator",
                    "calculators",
                    "cellular",
                    "central",
                    "chief",
                    "completely",
                    "consult",
                    "consulting",
                    "content",
                    "contents",
                    "cool",
                    "cope",
                    "corner",
                    "corners",
                    "data",
                    "deep",
                    "depot",
                    "depots",
                    "designer",
                    "designers",
                    "design",
                    "digital",
                    "dir",
                    "direct",
                    "directories",
                    "directory",
                    "discoveries",
                    "discovery",
                    "doc",
                    "dog",
                    "donkey",
                    "download",
                    "downloads",
                    "dump",
                    "easy",
                    "exchange",
                    "faq",
                    "field",
                    "file",
                    "files",
                    "filez",
                    "find",
                    "fire",
                    "flow",
                    "for",
                    "forge",
                    "forum",
                    "free",
                    "freeware",
                    "freewares",
                    "frog",
                    "fusion",
                    "game",
                    "games",
                    "garage",
                    "germany",
                    "get",
                    "go",
                    "gold",
                    "green",
                    "group",
                    "guide",
                    "have",
                    "horse",
                    "host",
                    "hot",
                    "how",
                    "hub",
                    "ice",
                    "info",
                    "information",
                    "installer",
                    "installers",
                    "internet",
                    "jet",
                    "key",
                    "keys",
                    "king",
                    "knight",
                    "knowledge",
                    "landmark",
                    "libraries",
                    "library",
                    "link",
                    "links",
                    "liquid",
                    "list",
                    "lists",
                    "load",
                    "loads",
                    "machine",
                    "machines",
                    "master",
                    "matrix",
                    "media",
                    "mega",
                    "million",
                    "mini",
                    "mobile",
                    "money",
                    "monster",
                    "move",
                    "mp3",
                    "must",
                    "my",
                    "nano",
                    "net",
                    "nets",
                    "news",
                    "newz",
                    "new",
                    "note",
                    "notes",
                    "now",
                    "o2p",
                    "office",
                    "one",
                    "online",
                    "pad",
                    "pads",
                    "page",
                    "pages",
                    "paradise",
                    "pc",
                    "pcs",
                    "pedia",
                    "pedias",
                    "phone",
                    "phones",
                    "pick",
                    "picks",
                    "pile",
                    "piles",
                    "ping",
                    "platinum",
                    "popular",
                    "press",
                    "prime",
                    "project",
                    "projects",
                    "purely",
                    "quality",
                    "rank",
                    "red",
                    "research",
                    "resource",
                    "resources",
                    "rocket",
                    "rockets",
                    "saver",
                    "savers",
                    "sea",
                    "search",
                    "searches",
                    "seek",
                    "seeks",
                    "selling",
                    "shareware",
                    "sharewares",
                    "simple",
                    "site",
                    "slash",
                    "smooth",
                    "soft",
                    "softs",
                    "software",
                    "softwares",
                    "source",
                    "sources",
                    "space",
                    "spaces",
                    "spider",
                    "spiders",
                    "standard",
                    "standards",
                    "stock",
                    "stop",
                    "store",
                    "stores",
                    "storm",
                    "storms",
                    "submit",
                    "submits",
                    "suggest",
                    "suggestion",
                    "suggestions",
                    "suggests",
                    "super",
                    "tail",
                    "tails",
                    "tech",
                    "techs",
                    "the",
                    "titanium",
                    "transform",
                    "transforms",
                    "trial",
                    "trials",
                    "url",
                    "urls",
                    "video",
                    "videos",
                    "videoz",
                    "viz",
                    "ware",
                    "wares",
                    "warez",
                    "water",
                    "web",
                    "webs",
                    "which",
                    "wide",
                    "wiki",
                    "wikis",
                    "window",
                    "windows",
                    "wing",
                    "wings",
                    "wire",
                    "wires",
                    "wise",
                    "wizard",
                    "wizards",
                    "work",
                    "works",
                    "working",
                    "world",
                    "write",
                    "xp",
                    "vista",
                    "zone",
                    "zones",
            };

    private static final Pattern SPLIT_ON_DOT = Pattern.compile( "\\." );

    static
        {
        // put longest strings first so will find longest match first
        Arrays.sort( camelWords, new LongestFirst() );
        }

    /**
     * guess a site name for this URL
     *
     * @param submissionURL URL of pad submission site.
     *
     * @return a guess for a camel caps name for the site
     */
    private static String guessSiteName( String submissionURL )
        {
        if ( !submissionURL.startsWith( "http://" ) )
            {
            submissionURL = "http://" + submissionURL;
            }
        String host;
        try
            {
            final URL u = new URL( submissionURL );
            host = u.getHost();
            }
        catch ( MalformedURLException e )
            {
            err.println( "MalformedURL" + submissionURL );
            host = submissionURL;
            }
        // split at dots
        final String[] pieces = SPLIT_ON_DOT.split( host );
        final int count = pieces.length;
        if ( count < 2 )
            {
            err.println( "URL without TLD" + submissionURL );
            return submissionURL;
            }
        else
            {
            if ( pieces[ count - 2 ].equals( "co" ) && count >= 3 )
                {
                return toCamel( pieces[ count - 3 ] );
                }
            else
                {
                return toCamel( pieces[ count - 2 ] );
                }
            }
        }

    /**
     * capitalise common words embedded in name, and remove dashes, marking the spots with caps.
     *
     * @param name name to camel-capitalise
     *
     * @return name with caps.
     */
    private static String toCamel( String name )
        {
        return toCamelCaps( toCamelDashes( name ) );
        }

    /**
     * capitalise embedded words, Camel case style
     *
     * @param name name to capitalise.
     *
     * @return name with embedded words capitalised.
     */
    private static String toCamelCaps( String name )
        {
        // capitalise any embedded words
        final StringBuilder sb = new StringBuilder( name.length() );
        boolean capitaliseNextLetter = true;
        outer:
        for ( int i = 0; i < name.length(); i++ )
            {
            // see if any toCamel words start at this position in name
            for ( String word : camelWords )
                {
                if ( name.substring( i ).startsWith( word ) )
                    {
                    // capitalise this word and the next.
                    if ( i != 0 )
                        {
                        sb.append( "-" );
                        }
                    sb.append( Character.toUpperCase( name.charAt( i ) ) );
                    sb.append( word.substring( 1 ) );
                    i += word.length() - 1;
                    capitaliseNextLetter = true;
                    continue outer;
                    }
                }
            // end inner loop without finding any matches.
            if ( capitaliseNextLetter )
                {
                if ( i != 0 )
                    {
                    sb.append( "-" );
                    }
                sb.append( Character.toUpperCase( name.charAt( i ) ) );
                capitaliseNextLetter = false;
                }
            else
                {
                sb.append( name.charAt( i ) );
                }
            }
        // end outer loop
        return sb.toString();  // add <wbr> manually later
        }

    /**
     * remove dashes separating words in name, replacing with Camel case
     *
     * @param name to Camel case
     *
     * @return name with dashes removed.
     */
    private static String toCamelDashes( String name )
        {
        // remove dashes, replacing with caps.
        final StringBuilder sb = new StringBuilder( name.length() );
        for ( int i = 0; i < name.length(); i++ )
            {
            final char c = name.charAt( i );
            if ( c == '-' )
                {
                // drop the -, append the following char capitalised
                final int next = i + 1;
                if ( next < name.length() )
                    {
                    sb.append( Character.toUpperCase( name.charAt( next ) ) );
                    // we processed one more char than usual
                    i++;
                    }
                }
            else
                {
                sb.append( c );
                }
            } // end for
        return sb.toString();
        }

    /**
     * Assign site names to candidates without them already.
     * <p/>
     * Input: candidates.csv output: candidates.csv, 1 to 5 fields.  url in second column
     * Designed primarily for Roedy's use to research new sites.
     *
     * @param args not used.
     *
     * @throws java.io.IOException if problems reading/writing files.
     */
    public static void main( String[] args ) throws IOException
        {
        out.println( "assigning site names to submitter files..." );
        final File candidateFile = new File( Build.MINDPROD_SOURCE + "/submitter/candidates.csv" );
        final CSVReader r = new CSVReader( new FileReader( candidateFile ) );
        final File tempOutFile = HunkIO.createTempFile( "tempassign", ".csv", candidateFile );
        final CSVWriter w = new CSVWriter( EIO.getPrintWriter( tempOutFile, 1024, EIO.UTF8 ) );
        try
            {
            while ( true )
                {
                // read  url, site, image, notes
                final String[] fields = r.getAllFieldsInLine();
                // ignore blank lines
                if ( fields.length == 0 )
                    {
                    continue;
                    }
                final String hostURL = ( fields.length > 1 ) ? fields[ 1 ] : "";
                final String submissionURL = ( fields.length > 2 ) ? fields[ 2 ] : "";
                // ignore sitename in field [0]
                final String siteName = guessSiteName( hostURL );
                final String image = fields.length > 3 ? fields[ 3 ] : "";
                String keywords = fields.length > 4 ? fields[ 4 ] : "";
                try
                    {
                    keywords = Keyword.tidyKeywords( keywords );
                    }
                catch ( IllegalArgumentException e )
                    {
                    err.println( e.getMessage()
                                 + " for ["
                                 + siteName
                                 + "] on line "
                                 + r.lineCount()
                                 + " of file "
                                 + EIO.getCanOrAbsPath( candidateFile ) );
                    }
                final String notes = fields.length > 5 ? fields[ 5 ] : "";
                w.put( siteName );
                w.put( hostURL );
                w.put( submissionURL );
                w.put( image );
                w.put( keywords );
                w.put( notes );
                w.nl();
                }
            }
        catch ( EOFException e )
            {
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempOutFile, candidateFile );
            }
        out.println( "Done" );
        }

    /**
     * Sort Strings by length, longest first.
     * <p/>
     * Defines an alternate sort order for String.
     */
    private static class LongestFirst implements Comparator<String>
        {
        /**
         * Sort Strings by length, longest first.
         * Defines an alternate sort order for String with JDK 1.5+ generics.
         * Compare two String Objects.
         * Compares descending string lengths.
         * Informally, returns (b-a), or +ve if a is greater than b.
         *
         * @param a first String to compare
         * @param b second String to compare
         *
         * @return +ve if a&gt;b, 0 if a==b, -ve if a&lt;b
         */
        public final int compare( String a, String b )
            {
            return b.length() - a.length();
            }
        }
    }
