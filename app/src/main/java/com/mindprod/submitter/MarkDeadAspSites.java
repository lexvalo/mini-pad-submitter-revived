/*
 * [MarkDeadAspSites.java]
 *
 * Summary: Mark urls in forasp.csv if they ard dead with the keyword "dead".
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
 *  1.0 2016-05-22 initial version
 */
package com.mindprod.submitter;

import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.csv.CSVReader;
import com.mindprod.csv.CSVWriter;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Mark urls in forasp.csv if they ard dead with the keyword "dead".
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2016-05-26 initial version
 * @since 2016-05-26
 */
public class MarkDeadAspSites
    {
    private static final String USAGE = "\nMarkDeadAspSites.exe";

    /**
     * FormatPadSites csv file to HTML, list of submission sites, either hassle or nohassle, or candidates.
     *
     * @param args source and target file names
     *             .
     *
     * @throws IOException on trouble reading/writing files
     */
    public static void main( String[] args ) throws IOException
        {
        if ( args.length != 0 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final File deadsFile = new File( "D:/bl/forasp/brokenlinks.csv" );
        // errorcode, url (main or submit)
        final CSVReader deads = new CSVReader( EIO.getBufferedReader( deadsFile, 4 * 1024, EIO.UTF8 ) );
        HashSet<String> bad = new HashSet<>( 200 );
        try
            {
            while ( true )
                {
                deads.skip( 1 );
                final String deadUrl = deads.get();
                deads.skipToNextLine();
                bad.add( ST.trimTrailing( deadUrl, '/' ) );
                }
            }
        catch ( EOFException e )
            {
            deads.close();
            }
        final File rfile = new File( "E:/com/mindprod/submitter/forasp.csv" );
        //  padsite, homeStatus, homeURL, submitStatus, submitURL, homeRedir, submitRedir
        final CSVReader r = new CSVReader( EIO.getBufferedReader( rfile, 4 * 1024, EIO.UTF8 ) );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", rfile );
        CSVWriter w =
                new CSVWriter( EIO.getPrintWriter( tempFile, 10 * 1024, EIO.UTF8 ),
                        -1, /* quotelevel
                 * -1 = like 0, but add an extra space after each separator/comma,
                 *  0 = minimal quotes, only around fields containing quotes or separators.
                 *  1 = quotes also around fields containing spaces.
                 *  2 = quotes around all fields, whether or not they contain commas, quotes or spaces.
                 */
                        ',',  /* separatorChar between fields */
                        '\"', /* quoteChar to surround fields containing commas */
                        '#',  /* char that starts comments */
                        true /*  trim fields of lead and trailing blanks */ );
        int marks = 0;
        try
            {
            w.nl( "# padsite, homeStatus, homeURL, submitStatus, submitURL, homeRedir, submitRedir" );
            while ( true )
                {
                // read  url, site, image, notes
                final String[] fields = r.getAllFieldsInLine();
                // ignore blank lines
                if ( fields.length == 0 )
                    {
                    continue;
                    }
                if ( fields.length < 3 )
                    {
                    err.println( "missing field(s) on line " + r.lineCount() + " of file " + rfile );
                    System.exit( 2 );
                    }
                final String siteName = fields[ 0 ];
                if ( siteName.length() == 0 )
                    {
                    err.println( "missing site name on line " + r.lineCount() + " of file " + rfile );
                    System.exit( 2 );
                    }
                String homeStatus = fields[ 1 ];
                final String homeURL = fields[ 2 ];
                String submitStatus = fields.length > 3 ? fields[ 3 ] : "";
                final String submitURL = fields.length > 4 ? fields[ 4 ] : "";
                String homeRedir = fields.length > 5 ? fields[ 5 ] : "";
                String submitRedir = fields.length > 6 ? fields[ 6 ] : "";
                if ( bad.contains( ST.trimTrailing( homeURL, '/' ) ) && !homeStatus.contains( "dead" ) )
                    {
                    homeStatus += " dead";
                    marks++;
                    }
                if ( bad.contains( ST.trimTrailing( submitURL, '/' ) ) && !submitStatus.contains( "dead" ) )
                    {
                    submitStatus += " dead";
                    marks++;
                    }
                w.put( siteName );
                w.put( homeStatus );
                w.put( homeURL );
                w.put( submitStatus );
                w.put( submitURL );
                w.put( homeRedir );
                w.put( submitRedir );
                w.nl();
                }  // end while
            }
        catch ( EOFException e )
            {
            // C L O S E
            w.close();
            r.close();
            HunkIO.deleteAndRename( tempFile, rfile );
            }
        catch ( Exception e )
            {
            err.println( "trouble on line " + r.lineCount() + " of file " + rfile );
            err.println( e.getMessage() );
            System.exit( 2 );
            }
        out.println( marks + " urls marked. " + bad.size() + " urls to mark" );
        }
    }
