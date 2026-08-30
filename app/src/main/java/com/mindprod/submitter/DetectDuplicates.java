/*
 * [DetectDuplicates.java]
 *
 * Summary: Ensure various lists of sites are consistent.
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
 *  1.0 2009-12-17 initial version
 */
package com.mindprod.submitter;

import static com.mindprod.submitter.PadSites.*;
import static java.lang.System.*;

/**
 * Ensure various lists of sites are consistent.
 * <p/>
 * formerly Consistency, DetectDupPadSites
 * <p/>
 * DeDups on site name, stripped of <wbr>, not domain name.
 * Tool to help verify code and lists. Not needed for submitting.
 * Designed primarily for Roedy's use to validate new sites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2009-12-17 initial version
 * @since 2009
 */
public class DetectDuplicates
    {
    /**
     * CSV files must be tidied prior to use.
     *
     * @param args not used.
     */
    @SuppressWarnings( { "ResultOfMethodCallIgnored" } )
    public static void main( String[] args )
        {
        out.println( "checking consistency of submitter files..." );
        out.println( "We presume submissionsite has already been run to regenerate allsites.list, " +
                     "and you have manually updated use.txt." );
        // read csv files
        for ( PadSites p : PadSites.values() )
            {
            p.load();
            }
        // look for both name and home dups in CSV files
        outer:
        for ( PadSites p : CSVS )
            {
            for ( PadSites q : CSVS )
                {
                if ( q.ordinal() >= p.ordinal() )
                    {
                    continue outer;
                    }
                PadSites.findDupsInNames( p, q );
                PadSites.findDupsInHomes( p, q );
                }
            }
        // look for problems with *.list files
        mustContain( NEWSITES, NOHASSLE );
        mustContain( ALLSITES, NOHASSLE );
        mustContain( NOHASSLE, ALLSITES );
        mustContain( NEWSITES, ALLSITES );
        int errors = PadSites.errorCount();
        if ( errors == 0 )
            {
            out.println( "no errors found" );
            }
        else
            {
            err.println( errors + " errors found" );
            }
        out.println( "Done" );
        System.exit( errors );
        }// /method
    }
