/*
 * [JustNewAppvisors.java]
 *
 * Summary: Gets list of new Appvisors we don't already have.
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
 *  1.0 2015-10-18 initial version
 */
package com.mindprod.submitter;

import com.mindprod.common18.Build;
import com.mindprod.csv.CSVReader;
import com.mindprod.csv.CSVWriter;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Gets list of new Appvisors we don't already have.
 * <p/>
 * reads appvisor.csv possappvisor.csv and produces newappvisor.csv
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2015-10-18 initial version
 * @since 2015-10-18
 */
public class JustNewAppvisors
    {
    /**
     * sites from appvisor.csv
     */
    private static final HashSet<String> existingAppvisorURLs = new HashSet<>( 1000 );

    /**
     * get set of existing AppVisor sites
     *
     * @throws IOException if trouble reading appvisor.csv
     */
    private static void getAppVisorURLs() throws IOException
        {
        final CSVReader r = new CSVReader( new FileReader( new File( Build.MINDPROD_SOURCE + "/submitter/appvisor.csv"
        ) ) );
        try
            {
            while ( true )
                {
                r.skip( 1 );
                final String appvisorURL = r.get();
                if ( !existingAppvisorURLs.add( appvisorURL ) )
                    {
                    err.println( "duplicate URL in appvisor.csv " + appvisorURL + " (case-sensitive)" );
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
        }// /method

    /**
     * get Appvisor sites we do not have already
     *
     * @param args not used.
     */
    @SuppressWarnings( { "ResultOfMethodCallIgnored" } )
    public static void main( String[] args ) throws IOException
        {
        getAppVisorURLs();
        final CSVReader rn = new CSVReader( new FileReader( new File( Build.MINDPROD_SOURCE + "/submitter/possappvisor.csv" ) ) );
        final CSVWriter w = new CSVWriter( new PrintWriter( new File( Build.MINDPROD_SOURCE + "/submitter/newappvisor.csv" ) ) );
        try
            {
            while ( true )
                {
                final String site = rn.get();
                final String url = rn.get();
                rn.skipToNextLine();
                if ( existingAppvisorURLs.contains( url ) )
                    {
                    out.println( "DUP: " + site + " " + url );
                    }
                else
                    {
                    out.println( "NEW: " + site + " " + url );
                    w.put( site );
                    w.put( url );
                    w.put( "http://publisher.appvisor.com/" );
                    w.put( "" ); /* icon */
                    w.put( "AppVisor" );
                    w.nl();
                    }
                }
            }
        catch ( EOFException e )
            {
            }
        finally
            {
            rn.close();
            w.close();
            }
        }
    }
