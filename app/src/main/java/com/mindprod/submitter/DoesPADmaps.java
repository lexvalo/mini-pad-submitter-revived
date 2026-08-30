/*
 * [DoesPADmaps.java]
 *
 * Summary: Looks at site to see if they support PADmaps or PADRING.
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
 *  1.0 2010-02-14 initial version
 */
package com.mindprod.submitter;

import com.mindprod.common18.EIO;
import com.mindprod.csv.CSVReader;
import com.mindprod.csv.CSVWriter;
import com.mindprod.http.Get;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * Looks at site to see if they support PADmaps or PADRING.
 * <p/>
 * Processes *.csv file on command line, e.g. candidates.csv or hassle.csv
 * <p/>
 * Use to prepare mindprod.com HTML versions of hassle, no-hassle or candidate pad submission sites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2010-02-14 initial version
 * @since 2010-02-14
 */
public class DoesPADmaps
    {
    private static final String USAGE = "\nDoesPADmaps sites6col.csv";

    /**
     * finds word PADmap anywhere in page in any case.
     */
    private static final Pattern PADMAPS_PATTERN = Pattern.compile( "PADmap", Pattern.CASE_INSENSITIVE );

    /**
     * finds word PADRING anywhere in page in any case.
     */
    private static final Pattern PADRING_PATTERN = Pattern.compile( "PADRING", Pattern.CASE_INSENSITIVE );

    /**
     * FormatPadSites csv file to HTML, list of submission sites, either hassle or nohassle, or candidates.
     * Put name of file to edit on command line.
     *
     * @param args source and target file names.
     *
     * @throws java.io.IOException on trouble reading/writing files
     */
    public static void main( String[] args ) throws IOException
        {
        if ( args.length != 1 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String source = args[ 0 ];
        final File sourceFile = new File( source );
        final CSVReader r = new CSVReader( new FileReader( sourceFile ) );
        final File tempOutFile = HunkIO.createTempFile( "temppadmaps", ".csv", sourceFile );
        final CSVWriter w = new CSVWriter( EIO.getPrintWriter( tempOutFile, 1024, EIO.UTF8 ) );
        try
            {
            while ( true )
                {
                // read  url, site, image, keywords, notes
                final String[] fields = r.getAllFieldsInLine();
                // ignore blank lines
                if ( fields.length == 0 )
                    {
                    continue;
                    }
                final String siteName = ( fields.length > 0 ) ? fields[ 0 ] : "";
                final String hostURL = ( fields.length > 1 ) ? fields[ 1 ] : "";
                final String submissionURL = ( fields.length > 2 ) ? fields[ 2 ] : "";
                final String image = fields.length > 3 ? fields[ 3 ] : "";
                String keywords = fields.length > 4 ? fields[ 4 ] : "";
                final String notes = fields.length > 5 ? fields[ 5 ] : "";
                out.println( "checking " + submissionURL );
                final Get get = new Get();
                final String page = get.send( new URL( submissionURL ), Get.UTF8 );
                final int responseCode = get.getResponseCode();
                final String responseMessage = get.getResponseMessage();
                if ( get.isGood() && page != null && page.length() != 0 )
                    {
                    if ( PADMAPS_PATTERN.matcher( page ).find() )
                        {
                        if ( keywords.contains( "PADmaps" ) )
                            {
                            out.println( "     " + submissionURL + " already marked as supporting PADmaps" );
                            }
                        else
                            {
                            out.println( "   + " + submissionURL + " also supports PADmaps. Keyword added." );
                            keywords = keywords + " " + "PADmaps";
                            }
                        }
                    else if ( keywords.contains( "PADmaps" ) )
                        {
                        err.println( "     - " + submissionURL + " does not appear to support PADmaps but it marked " +
                                     "as doing so. Keyword removed." );
                        Keyword.removeKeyword( keywords, Keyword.PADMAPS );
                        }
                    if ( PADRING_PATTERN.matcher( page ).find() )
                        {
                        if ( keywords.contains( "PADRING" ) )
                            {
                            out.println( "     " + submissionURL + " already marked as supporting PADRING" );
                            }
                        else
                            {
                            out.println( "   + " + submissionURL + " also supports PADRING. Keyword added." );
                            keywords = keywords + " " + "PADRING";
                            }
                        }
                    else if ( keywords.contains( "PADRING" ) )
                        {
                        err.println( "     - " + submissionURL + " does not appear to support PADRING but it marked " +
                                     "as doing so. Keyword removed." );
                        Keyword.removeKeyword( keywords, Keyword.PADRING );
                        }
                    }
                else
                    {
                    err.println( "could not fetch page. responseCode: " + responseCode + " responseMessage:" +
                                 responseMessage );
                    }
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
                                 + source );
                    }
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
            // successfully created output in same directory as input,
            // Now make it replace the input file.
            HunkIO.deleteAndRename( tempOutFile, sourceFile );
            }
        out.println( "Done" );
        }
    }
