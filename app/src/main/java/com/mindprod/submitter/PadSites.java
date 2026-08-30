/*
 * [PadSites.java]
 *
 * Summary: enum for various padsite csv collections. Used by DetectDupPadSites.
 *
 * Copyright: (c) 2016-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2016-08-06 initial version
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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * enum for various padsite csv collections. Used by DetectDupPadSites.
 * <p/>
 * used to dedup both on name without <wbr> and on the home domain
 * Designed primarily for Roedy's use to validate new sites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2016-08-06 initial version
 * #see DetectDupPadSites
 * @since 2016-08-06
 */
public enum PadSites
    {
        ALLSITES( false,  /* .list */ true  /* mandatory */ ),
        APPVISOR( true, /* .isMultiColumn */ true ),
        CANDIDATES( true, false ),
        DEAD( true, true ),
        HASSLE( true, true ),
        NEWSITES( false, true ),
        NOHASSLE( true, true ),
        POSSAPPVISOR( true, false );

    static final EnumSet<PadSites> CSVS = EnumSet.of( APPVISOR, CANDIDATES, DEAD, HASSLE, NOHASSLE, POSSAPPVISOR );

    /**
     * sites that violate the naming conventions
     */
    private static final HashSet<String> HOME_EXCEPTIONS = new HashSet<>( Arrays.asList(
            "appvisor.com",
            "cnet.com",
            "download-lair.com",
            "hs-lab.com.ua",
            "softpedia.com",
            "softlist.ws",
            "z-down.com" ) );

    /**
     * count of errors found
     */
    private static int errors = 0;

    /**
     * true if the backing file for this must exist
     */
    private final boolean isMandatory;

    private final boolean isMultiColumn;

    private final HashSet<String> names = new HashSet<>( 1000 );

    private final HashSet<String> homes = new HashSet<>( 1000 );

    /**
     * construtor
     *
     * @param isMultiColumn true if csv, false if *.list 1 column
     * @param isMandatory   true if backing file is mandatory
     */
    PadSites( final boolean isMultiColumn, final boolean isMandatory )
        {
        this.isMultiColumn = isMultiColumn;
        this.isMandatory = isMandatory;
        }

    /**
     * how many errors since unem loaded
     */
    static int errorCount()
        {
        return errors;
        }

    /**
     * detect any home dups in these two PadSite lists
     *
     * @param a first list
     * @param b second list
     */
    static void findDupsInHomes( final PadSites a, final PadSites b )
        {
        for ( String itema : a.homes )
            {
            b.homes.stream().filter( itema::equals ).forEach( itemb ->
            {
            err.println( "Home " + itema + " duplicated in " + a.name().toLowerCase() + " and " + b.name().toLowerCase() + "\n" );
            } );
            }
        }

    /**
     * detect any site name dups in these two PadSite lists
     *
     * @param a first list
     * @param b second list
     */
    static void findDupsInNames( final PadSites a, final PadSites b )
        {
        for ( String itema : a.names )
            {
            b.names.stream().filter( itema::equals ).forEach( itemb ->
            {
            err.println( "Sitename " + itema + " duplicated in " + a.name().toLowerCase() + " and " + b.name().toLowerCase() + "\n" );
            } );
            }
        }

    /**
     * Enusere all allements of one set are contained in another
     *
     * @param contained the small list that must be contained in the big list
     * @param container big list that must contain the small list
     */
    static void mustContain( final PadSites contained, final PadSites container )
        {
        contained.names.stream().filter( item -> !container.names.contains( item ) ).forEach( item ->
        {
        err.println( "Sitename " + item + " in " + contained.name().toLowerCase() + " but not " + container.name().toLowerCase() + "\n" );
        } );
        }

    /**
     * load HashMaps from isMultiColumn and list files.
     */
    void load()
        {
        try
            {
            final File file = new File( Build.MINDPROD_SOURCE + "/submitter/" + name().toLowerCase() + ( isMultiColumn ? ".csv" : ".list" ) );
            if ( !file.exists() )
                {
                if ( this.isMandatory() )
                    {
                    err.println( "missing file " + file );
                    System.exit( 1 );
                    }
                else
                    {
                    return; /* empty file */
                    }
                }
            final CSVReader r = new CSVReader( new FileReader( file ) );
            try
                {
                // detect dups within each PadSite list.
                while ( true )
                    {
                    // get padsite name
                    final String siteName = r.get();
                    if ( siteName == null )
                        {
                        err.println( "null sitename in " + file + " at line " + r.lineCount() + "\n" );
                        break;
                        }
                    String condensedSiteName = DeEntify.stripHTMLTags( siteName ).toLowerCase();
                    condensedSiteName = condensedSiteName.replace( "-", "" );
                    condensedSiteName = condensedSiteName.replace( ".", "" );
                    if ( names.contains( condensedSiteName ) )
                        {
                        errors++;
                        err.println( "Error: duplicate siteName " + siteName +
                                     " in " + file + " (case-sensitive) at line " + r.lineCount() + "\n" );
                        }
                    else
                        {
                        names.add( condensedSiteName );
                        }
                    // get padsite home domain
                    if ( isMultiColumn )
                        {
                        final String home = r.get();
                        final String condensedHome = Misc.getDomain( new URL( home ) ).toLowerCase();
                        if ( homes.contains( condensedHome ) && !HOME_EXCEPTIONS.contains( condensedHome ) )
                            {
                            errors++;
                            err.println( "Error: duplicate home " + home + " in " + file + " (case-sensitive) at line " + r.lineCount() + "\n" );
                            }
                        else
                            {
                            homes.add( condensedHome );
                            }
                        // check Site name and home URL are consistent
                        String protoSite = ST.chopLeadingString( ST.chopLeadingString( home.toLowerCase(), "http://" ), "https://" );
                        protoSite = protoSite.replace( "-", "" );
                        protoSite = protoSite.replace( ".", "" );
                        if ( !protoSite.contains( ST.trimTrailing( condensedSiteName, "dash" ) ) )
                            {
                            errors++;
                            err.println( "Error: site name " + siteName + " does not match home url " + home + " in " + file + " at line " + r.lineCount() + "\n" );
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
                }
            }
        catch ( IOException e )
            {
            err.println( "Error: reading " + name().toLowerCase() + "\n" );
            }
        }

    /**
     * return true if the backing file for this must exist
     *
     * @return true if file must exist
     */
    public boolean isMandatory()
        {
        return isMandatory;
        }
    // /methods
    }
