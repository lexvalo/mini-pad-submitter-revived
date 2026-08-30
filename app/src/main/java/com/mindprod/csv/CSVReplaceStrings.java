/*
 * [CSVReplaceStrings.java]
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
 */
package com.mindprod.csv;

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
 * Presumes html files have been treated with Amper.
 * Control file usually created by BrokenLinks.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.2 2014-04-28 borrowed from brokenlinks package for use in csv package.
 * @since 2010-11-17
 */
public class CSVReplaceStrings
    {
    private static final String USAGE = "\nCSVReplaceString needs a single csv file on the command line containing fromString, toString, file, file... replacements.";

    /**
     * name of CSV file is on command line.
     * File contains CSV format multiple records of form:
     * fromstring, tostring, pagefile.html, page2file.html ...
     * And it will replace all instances of fromstring with tostring on the mentioned pages.
     * If you put a lead regex: on the from string, both string will be treated as regexes.
     * There is no "all pages" option.
     *
     * @param args arg[0] is name of csv file containing a list of the replacements to make.
     */
    public static void main( String[] args )
        {
        if ( args.length != 1 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        out.println( "CSVReplaceStrings replacing Strings in selected files..." );
        try
            {
            int successes = 0;
            int failures = 0;
            final File csv = new File( args[ 0 ] );
            // reader, separatorChar, quoteChar, commentChars, hideComments, trimQuoted, trimUnquoted,
            // allowMultipleLineFields
            CSVReader r = new CSVReader( new BufferedReader( new FileReader( csv ), 1024 * 64 ),
                    ','
                    /* comma delimited */,
                    '\"',
                    ""
                    /* no comments */,
                    true,
                    true
                    /* trimQuoted */,
                    true
                    /* trimUnquoted */,
                    true );
            try
                {
                // loop throup replace requsts
                while ( true )
                    {
                    String oldString = r.get();
                    String newString = r.get();
                    // bypass blank lines
                    if ( oldString == null || newString == null )
                        {
                        continue;
                        }
                    out.println( "    Patching " + oldString );
                    out.println( "          to " + newString );
                    final boolean usingRegex;
                    // pattern to search for to replace
                    final Pattern p;
                    if ( oldString.startsWith( "regex:" ) )
                        {
                        usingRegex = true;
                        oldString = ST.chopLeadingString( oldString, "regex:" );
                        // normally should not be lead regex: on newstring , but just in case:
                        newString = ST.chopLeadingString( newString, "regex:" );
                        p = Pattern.compile( oldString );
                        // ideally should validate the Pattern.
                        }
                    else
                        {
                        usingRegex = false;
                        p = null;
                        }
                    // loop through multiple filenames on file
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
                        final String newContents;
                        if ( usingRegex )
                            {
                            Matcher m = p.matcher( oldContents );
                            newContents = m.replaceAll( newString );
                            }
                        else
                            {
                            // replace replaces all instances, non-regex
                            newContents = oldContents.replace( oldString, newString );
                            }
                        // changes to one url in one file
                        if ( newContents.equals( oldContents ) )
                            {
                            err.println(
                                    "\n<><>Warning<><> because the old string could not be found, could not replace\n    ["
                                    + oldString
                                    + "]\n    with ["
                                    + newString
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
                        } // end inner loop
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
            out.println( successes + " Strings successfully replaced. " + failures + " String failed to replace." );
            }
        catch ( IOException e )
            {
            err.println( "<><>Error<><> file problems. " + e.getMessage() );
            err.println( "Order of control file fields: from, to, file1, file2..." );
            System.exit( 2 );
            }
        }
    // /method
    } // end classn
