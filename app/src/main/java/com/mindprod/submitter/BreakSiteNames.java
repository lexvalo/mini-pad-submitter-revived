/*
 * [BreakSiteNames.java]
 *
 * Summary: Insert <wbr> in site names in a CSV file.
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
 *  1.0 2015-01-26 initial version
 */
package com.mindprod.submitter;

import com.mindprod.commandline.CommandLine;
import com.mindprod.common18.EIO;
import com.mindprod.csv.CSVReader;
import com.mindprod.csv.CSVWriter;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.filter.AllButFootDirectoriesFilter;
import com.mindprod.filter.ExtensionListFilter;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import static java.lang.System.*;

/**
 * Insert <wbr> in site names in a CSV file.
 * <p/>
 * Put csv file names on command line. Split name goes in first column.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2015-01-26 initial version
 * @since 2010
 */
public class BreakSiteNames
    {
    /**
     * insert <wbr> at before caps preceeded by lower case
     *
     * @param s name to tidy
     *
     * @return tidied name
     */
    private static String insertWBR( String s )
        {
        // get rid of any existing <wbr>
        s = DeEntifyStrings.stripHTMLTags( s );
        final StringBuilder sb = new StringBuilder( s.length() + 15 );
        boolean prevuc = true;
        for ( int i = 0; i < s.length(); i++ )
            {
            char c = s.charAt( i );
            final boolean thisuc = Character.isUpperCase( c );
            if ( thisuc && !prevuc )
                {
                sb.append( "<wbr>" );
                }
            sb.append( c );
            prevuc = thisuc;
            }
        return sb.toString();
        }

    /**
     * insert breaks in one file.
     *
     * @param inFile file to process
     */
    private static void processOneFile( File inFile ) throws IOException
        {
        final CSVReader r = new CSVReader( EIO.getBufferedReader( inFile, 64 * 1024, EIO.UTF8 ),
                ',', '"', "#", false, true, true, false );
        final File tempOutFile = HunkIO.createTempFile( "tempassign", ".csv", inFile );
        final CSVWriter w = new CSVWriter( EIO.getPrintWriter( tempOutFile, 1024, EIO.UTF8 ) );
        try
            {
            while ( true )
                {
                // read  url, site, image, notes
                final String[] fields = r.getAllFieldsInLine();
                final boolean wasComment = r.wasComment();
                // ignore blank lines
                if ( fields.length == 0 )
                    {
                    continue;
                    }
                if ( fields.length > 1 || !wasComment )
                    {
                    fields[ 0 ] = insertWBR( fields[ 0 ] );
                    }
                w.nl( fields, wasComment );
                }
            }
        catch ( EOFException e )
            {
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempOutFile, inFile );
            }
        }

    /**
     * Insert <wbr> before Cap it prev letter was lower case.
     * <p/>
     * Put csv file names on command line. Split name goes in first column.
     * <p/>
     * Designed primarily for Roedy's use to research new sites.
     *
     * @param args not used.
     *
     * @throws java.io.IOException if problems reading/writing files.
     */
    public static void main( String[] args ) throws IOException
        {
        CommandLine filesToProcess = new CommandLine( args, new AllButFootDirectoriesFilter(), new ExtensionListFilter( "csv" ) );
        for ( File file : filesToProcess )
            {
            processOneFile( file );
            }
        out.println( "Done" );
        }
    }
