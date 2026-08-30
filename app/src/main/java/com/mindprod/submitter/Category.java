/*
 * [Category.java]
 *
 * Summary: Enumerate the various files where padsites are stored.
 *
 * Copyright: (c) 2015-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2015-08-22 initial version
 */
package com.mindprod.submitter;

import com.mindprod.common18.Build;
import com.mindprod.common18.Misc;
import com.mindprod.common18.ST;
import com.mindprod.csv.CSVReader;
import com.mindprod.entities.DeEntify;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Enumerate the various files where padsites are stored.
 * <p/>
 * Designed primarily for Roedy's use to validate padsite files.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2015-08-22 initial version
 * @since 2015-08-22
 */
public enum Category
    {
        DEAD( "dead.csv", true, false ),
        HASSLE( "hassle.csv", true, false ),
        APPVISOR( "appvisor.csv", true, false ),
        NOHASSLE( "nohassle.csv", true, false ),
        CANDIDATES( "candidates.csv", true, false ),
        ALLSITES( "allsites.list", false, true ),
        NEWSITES( "newsites.list", false, true );

    /**
     * true if want extra debugging output
     */
    private static final boolean DEBUGGING = false;

    /**
     * where csv padsite are
     */
    final String file;

    /**
     * allow dups in this file with other files.
     */
    final boolean permitDups;

    /**
     * does this file have a domain as well as site name
     */
    final boolean hasDomain;

    /**
     * Names of sites with <wbr> stripped, for one file
     */
    private final HashSet<String> siteNames = new HashSet<>( 1000 );

    /**
     * site domains with lead http:// stripped, for one file
     */
    private final HashSet<String> domainNames = new HashSet<>( 1000 );

    Category( String file, boolean hasDomain, boolean permitDups )
        {
        this.file = file;
        this.hasDomain = hasDomain;
        this.permitDups = permitDups;
        }

    private static void crossCheck( Category c1, Category c2 )
        {
        // cross check two categories, displaying all dups.
        for ( String siteName1 : c1.siteNames )
            {
            if ( c2.siteNames.contains( siteName1 ) )
                {
                err.println( "duplicate siteName in " + c1.file + "/" + c2.file + " : " + siteName1 + " (case-sensitive)" );
                }
            }
        if ( c1.hasDomain && c2.hasDomain )
            {
            for ( String domainName1 : c1.domainNames )
                {
                if ( c2.domainNames.contains( domainName1 ) )
                    {
                    err.println( "duplicate domainName in " + c1.file + "/" + c2.file + " : " + domainName1
                                 + " (case-sensitive)" );
                    }
                }
            }
        }

    /**
     * ensure all padsite is inner also occur in outer
     * display error messeges.
     *
     * @param inner all elts of this must be in outer
     * @param outer all elts of inner must be in this
     */
    private static void ensureSubSet( Category inner, Category outer )
        {
        for ( String siteName : inner.siteNames )
            {
            if ( !outer.siteNames.contains( siteName ) )
                {
                err.println( "Site " + siteName + " in " + inner.file + " missing from " + outer.file );
                }
            }
        }
    /*
    Cross check    contents of      two files
    */

    /**
     * read one file full of padsite refs. Internally dedup it.
     *
     * @throws IOException if trouble reading appvisor.csv
     */
    private void loadFile() throws IOException
        {
        final CSVReader r = new CSVReader( new FileReader( new File( Build.MINDPROD_SOURCE + "/submitter/", file
        ) ) );
        try
            {
            while ( true )
                {
                final String siteName = DeEntify.stripHTMLTags( r.get() );
                if ( !siteNames.add( siteName ) )
                    {
                    err.println( "duplicate siteName in " + file + " : " + siteName + " (case-sensitive)" );
                    }
                if ( hasDomain )
                    {
                    final String domain = Misc.getDomain( new URL( r.get() ) );
                    if ( !domainNames.add( domain ) )
                        {
                        err.println( "duplicate domain in " + file + " : " + siteName + " : " + domain + " (case-sensitive)" );
                        }
                    final String simplifiedSiteName = ST.trimJunk( siteName.toLowerCase(), "-" );
                    final String simplifiedDomain = ST.trimJunk( domain.toLowerCase(), "-." );
                    if ( !simplifiedDomain.contains( simplifiedSiteName ) )
                        {
                        err.println( "in " + file + " siteName " + siteName + " mismatches domain " + domain );
                        }
                    }
                r.skipToNextLine();
                }
            }
        catch ( EOFException e )
            {
            }
        finally
            {
            r.close();
            if ( DEBUGGING )
                {
                out.println( siteNames.size() + "/" + domainNames.size() + " padsites loaded from " + file );
                }
            }
        }// /method

    /**
     * check all files for dups
     *
     * @param args not used.
     */
    @SuppressWarnings( { "ResultOfMethodCallIgnored" } )
    public static void main( String[] args )
        {
        try
            {
            out.println( "checking for dups in submitter files..." );
            out.println(
                    "We presume submissionsite has already been run to regenerate allsites.list, " +
                    "and you have manually updated use.txt."
            );
            // check for dups within files and load up all values.
            for ( Category c : Category.values() )
                {
                c.loadFile();
                }
            // cross check all files for dups
            for ( Category c1 : Category.values() )
                {
                if ( c1.permitDups )
                    {
                    continue;
                    }
                for ( Category c2 : Category.values() )
                    {
                    // avoid comparing with self and comparing already compared
                    if ( c1.ordinal() >= c2.ordinal() || c2.permitDups )
                        {
                        continue;
                        }
                    crossCheck( c1, c2 );
                    }
                }
            // check files that should duplicate
            ensureSubSet( NEWSITES, ALLSITES );
            ensureSubSet( ALLSITES, NOHASSLE );
            ensureSubSet( NOHASSLE, ALLSITES );
            }
        catch ( IOException e )
            {
            err.println();
            e.printStackTrace( err );
            err.println( "problem with file I/O" );
            err.println();
            }
        out.println( "Done" );
        }// /method
    // /methods
    }
