/*
 * [CSVReplaceURLs.java]
 *
 * Summary: bulk replaces URLs in local website files.
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
 *  1.0 2008-07-21 initial version.
 *  1.1 2014-04-14 check for old URLS both with and without a trailing /.
 *  1.2 2014-04-28 borrowed from brokenlinks package for use in csv package.
 *                 simplify with regexes.
 *                 case-insensitive searches.
 *                 matches without amper strings and files
 *                 safety quoting of both from and to urls.
 *  1.3 2015-04-18 ignore # comments
 */
package com.mindprod.csv;

import com.mindprod.amper.Amper;
import com.mindprod.common18.ST;
import com.mindprod.hunkio.HunkIO;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * bulk replaces URLs in local website files.
 * <p/>
 * Ignores comments.
 * Presumes html files have been treated with Amper.
 * Control file usually created by BrokenLinks.
 * In contrast, CSVRecode modifies CSV files.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.3 2015-04-18 ignore # comments
 * @see CSVRecode
 * @since 2010-11-17
 */
public class CSVReplaceURLs
    {
    private static final String USAGE = "\nCSVReplaceURLs needs a single csv file on the command line containing fromURL, toURL, file, file... replacements.";

    /**
     * true if want extra debugging output
     */
    private static boolean DEBUGGING = false;

    /**
     * name of CSV file is on command line.
     * File contains CSV format multiple records of form:
     * fromURL, toURL, pagefile.html, page2file.html ...
     * And it will replace all instances of fromURL with toURL on the mentioned pages.
     * There is no "all pages" option.
     * <p/>
     * This is similar to CSVReplaceString except:
     * 1. the replacement only replaces strings in the context of an URL in html
     * 2. The match works whether & is coded as & or &amp;
     * 3. The match is relaxed.  It will work whether the Strings are missing or have extra trailing /.
     * 4. it looks inside {...}  href="...".
     * 5. it works if URLs have trailing #XXX, or trailing ?XXX which is not replaced.
     * 6. the match is case-insensitive.
     *
     * @param args arg[0] is name of csv file containing a list of the replacements to make.
     */
    public static void main( String[] args )
        {
        if ( args.length != 1 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        out.println( "ReplaceURLs replacing redirected URLs..." );
        try
            {
            int successes = 0;
            int failures = 0;
            final File csv = new File( args[ 0 ] );
            // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
            // allowMultipleLineFields
            CSVReader r = new CSVReader( new BufferedReader( new FileReader( csv ), 1024 * 64 ),
                    ',' /* comma delimited */,
                    '\"' /* statard quote */,
                    "#" /* optional # comments */,
                    true /* hide comments */,
                    true /* trimQuoted */,
                    true /* trimUnquoted */,
                    true /* multiline */ );
            try
                {
                while ( true )
                    {
                    String oldURL = r.get();
                    String newURL = r.get();
                    // bypass blank lines
                    if ( oldURL == null || newURL == null )
                        {
                        continue;
                        }
                    if ( newURL.lastIndexOf( "//" ) >= "https://".length() )
                        {
                        // collapse any internal stray double // down to /, but not http:// or https://
                        final int place = newURL.indexOf( "://" );
                        if ( place == "http".length() || place == "https".length() )
                            {
                            newURL = newURL.substring( 0, place + 3 ) + newURL.substring( place + 3 ).replace( "//",
                                    "/" );
                            }
                        }
                    // don't check for equal.  It gets too complicated with all the possible wrinkles.
                    out.println( "    Patching " + oldURL );
                    out.println( "         --> " + newURL );
                    // Xenu sometimes adds or removes the trailing / on the old reference.
                    // We must look for it in both forms:
                    boolean needToCheckWithSlashes = oldURL.endsWith( "/" )
                                                     || !( oldURL.endsWith( ".html" ) || oldURL.contains( ".html#" ) );
                    final String oldURLWithoutSlash = ST.chopTrailingString( oldURL, "/" );
                    // compose a regex with the URL to be replaced in the middle of it.
                    // e.g. (href="|\{)\Qhttp://pcpitstop.com/store/pcmatic.asp\E/?(#|"|\?|})
                    final String regex = "(href=\"|\\{)"
                                         + Pattern.quote( Amper.ampifyUncommentedString( oldURLWithoutSlash ) )
                                         + ( needToCheckWithSlashes ? "/?" : "" )
                                         + "(#|\"|\\?|})";
                    if ( DEBUGGING )
                        {
                        out.println( regex );
                        }
                    final Pattern p = Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
                    // loop patching each of the list of files where the URL is known to exist
                    while ( true )
                        {
                        // filename to patch
                        final String htmlFilename = r.get();
                        if ( htmlFilename == null )
                            {
                            // end of line
                            break;
                            }
                        final File htmlFile = new File( htmlFilename );
                        final String oldContents = HunkIO.readEntireFile( htmlFile );
                        final Matcher m = p.matcher( Amper.ampifyPossiblyScriptedString( oldContents ) );
                        final String replacement = "$1" + ST.quoteForReplace( Amper.ampifyUncommentedString( newURL ) ) + "$2";
                        if ( DEBUGGING )
                            {
                            out.println( replacement );
                            }
                        // we find and replace all urls in one go.
                        final String newContents = m.replaceAll( replacement );
                        // changes to one url in one file
                        if ( newContents.equals( oldContents ) )
                            {
                            err.println(
                                    "\n<><>Warning<><> because the old URL could not be found, could not replace\n    ["
                                    + oldURL
                                    + "]\n    with ["
                                    + newURL
                                    + "]\n    in ["
                                    + htmlFilename
                                    + "]\n"
                            );
                            failures++;
                            }
                        else
                            {
                            HunkIO.writeEntireFile( htmlFile, newContents );
                            successes++;
                            }
                        } // end from loop
                    } // end outer loop
                }
            catch ( EOFException e )
                {
                }
            finally
                {
                if ( r != null )
                    {
                    r.close();
                    }
                }
            out.println( successes + " URLs successfully replaced. " + failures + " URLs failed to replace." );
            }
        catch ( IOException e )
            {
            err.println( "<><>Error<><> file problems. " + e.getMessage() );
            }
        }
    }
