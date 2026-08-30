/*
 * [TidyKeywords.java]
 *
 * Summary: DeDups and sorts the keywords in a csv file for each site.
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
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.System.*;

/**
 * DeDups and sorts the keywords in a csv file for each site.
 * <p/>
 * Use to prepare mindprod.com HTML versions of hassle, no-hassle or candidate pad submission sites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2010-02-14 initial version
 * @since 2010-02-14
 */
public class TidyKeywords
    {
    private static final String USAGE = "\nTidyKeywords.exe sites5col.csv ";

    /**
     * FormatPadSites csv file to HTML, list of submission sites, either hassle or nohassle, or candidates.
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
        try
            {
            final CSVReader r = new CSVReader( EIO.getBufferedReader( sourceFile, 8 * 1024, EIO.UTF8 ) );
            final File tempOutFile = HunkIO.createTempFile( "tempdup", ".csv", sourceFile );
            final CSVWriter w = new CSVWriter( EIO.getPrintWriter( tempOutFile, 8 * 1024, EIO.UTF8 ) );
            w.nl( "P A D   S U B M I S S I O N   S I T E S" );
            final Keyword[] allKeywords = Keyword.values();
            Arrays.sort( allKeywords, new KeywordsAlphabetically() );
            // legend of what all the codes mean.  Website never sees these codes, just the expanded meanings.
            for ( Keyword k : allKeywords )
                {
                w.nl( k.getCanonical() + ": " + k.getUndecoratedMeaning() );
                }
            w.nl( "# name, homeUrl, submitUrl, logo, keywords, notes" );
            try
                {
                while ( true )
                    {
                    // read  url, site, image, keywords, notes
                    // old comments hidden
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
                HunkIO.deleteAndRename( tempOutFile, sourceFile );
                }
            }
        catch ( FileNotFoundException e )
            {
            err.println( "missing file " + EIO.getCanOrAbsPath( sourceFile ) );
            System.exit( 2 );
            }
        out.println( "TidyKeywords done" );
        }
    }
