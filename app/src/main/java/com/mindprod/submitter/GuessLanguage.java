/*
 * [GuessLanguage.java]
 *
 * Summary: Guesses the language used at a site.
 *
 * Copyright: (c) 2014-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2014-07-15 initial version
 */
package com.mindprod.submitter;

import com.mindprod.common18.EIO;
import com.mindprod.csv.CSVReader;
import com.mindprod.csv.CSVWriter;
import com.mindprod.http.Get;
import com.mindprod.hunkio.HunkIO;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static java.lang.System.*;

/**
 * Guesses the language used at a site.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2014-07-15 initial version
 * @since 2014-07-15
 */
public class GuessLanguage
    {
    /**
     * takes a 9-column list of padsites, eg. candidates.csv hassle.csv
     */
    private static final String USAGE = "\nGuessLanguage sites6col.csv";
    // methods

    /**
     * recognise the language
     *
     * @param page lower case page.
     *
     * @return Namef of the language
     */
    private static String recogniseLanguage( String page )
        {
        if ( page.contains( "catégories" )
             || page.contains( "rechercher" )
             || page.contains( "logiciel" ) )
            {
            return "French";
            }
        // German
        else if ( page.contains( "nueue" )
                  || page.contains( "kostenlose" )
                  || page.contains( "über" )
                  || page.contains( "unseren" ) )
            {
            return "German";
            }
        // Russian
        else if ( page.contains( "новые" )
                  || page.contains( "программы" )
                  || page.contains( "добавить" )
                  || page.contains( "программу" )
                  || page.contains( "разработке" )
                  || usesCharsForLanguage( page, 75, 0x04f1, 0x044f ) )
            {
            return "Russian";
            }
        // Portuguese
        else if ( page.contains( "programinha" )
                  || page.contains( "várias" )
                  || page.contains( "gratuito" )
                  || page.contains( "sua" )
                  || page.contains( "fotos" )
                  || page.contains( "você" )
                  || page.contains( "fotografia" ) )
            {
            return "Portuguese";
            }
        // Italian
        else if ( page.contains( "grafica" )
                  || page.contains( "programmazione" )
                  || page.contains( "tutto" )
                  || page.contains( "tutti" )
                  || page.contains( " e " ) )
            {
            return "Italian";
            }
        // Turkish
        else if ( page.contains( "giriş" )
                  || page.contains( "kullanıcı" )
                  || page.contains( "şifremi" )
                  || page.contains( "Uygulama" ) )
            {
            return "Turkish";
            }
        // Chinese
        else if ( usesCharsForLanguage( page, 75, 0x4300, 0xf9cc ) )
            {
            return "Chinese";
            }
        // Greek
        else if ( usesCharsForLanguage( page, 75, 0x0370, 0x03ff ) )
            {
            return "Greek";
            }
        // Arabic
        else if ( usesCharsForLanguage( page, 75, 0x0600, 0x06ff ) )
            {
            return "Arabic";
            }
        // Thai
        else if ( usesCharsForLanguage( page, 75, 0x0e00, 0x0eff ) )
            {
            return "Thai";
            }
        // Japanese
        else if ( page.contains( "�?�" )
                  || page.contains( "ロ" )
                  || page.contains( "�?�" )
                  || page.contains( "る" ) )
            {
            return "Japanese";
            }
        // English, most pages in other languages still have some English, so test last
        else if ( page.contains( " the " )
                  || page.contains( " is " )
                  || page.contains( " on " )
                  || page.contains( "submit" )
                  || page.contains( "freeware" )
                  || page.contains( "shareware" )
                  || page.contains( "contact" )
                  || page.contains( "login" )
                  || page.contains( "error" )
                  || page.contains( "soon" )
                  || page.contains( "index" ) )
            {
            return "English";
            }
        else
            {
            return "Unknown";
            }
        }// /method

    /**
     * Does this page contain more than 50% of chars of this range, excluding ASCII
     *
     * @param page lower case contents of page to test
     * @param bar  how high a percentage of chars in range do we need to get to count as a hit.
     * @param low  lowest char
     * @param high highest char
     *
     * @return true if have a matche
     */
    private static boolean usesCharsForLanguage( String page, int bar, int low, int high )
        {
        int match = 0;
        int nonMatch = 0;
        for ( char i = 0; i < page.length(); i++ )
            {
            if ( 0 <= i && i <= 255 )
                { /* nothing, probably html or stray English */ }
            else if ( low <= i && i <= high )
                {
                match++;
                }
            else
                {
                nonMatch++;
                }
            }
        return match > 10 && ( match * 100 ) / ( match + nonMatch ) >= bar;
        }// /method

    /**
     * FormatPadSites csv file to HTML, list of submission sites, either hassle or nohassle, or candidates.
     * Put name of file to edit.html on command line.
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
        final CSVReader r = new CSVReader( EIO.getBufferedReader( sourceFile, 64 * 1024, EIO.UTF8 ) );
        // we modify list of sites adding keywords, write to temp file.
        final File tempOutFile = HunkIO.createTempFile( "temppadsites", ".csv", sourceFile );
        final CSVWriter w = new CSVWriter( EIO.getPrintWriter( tempOutFile, 1024, EIO.UTF8 ) );
        out.println( "Guessing languages of sites in " + EIO.getCanOrAbsPath( sourceFile ) );
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
                if ( keywords.contains( "Stalls" ) || keywords.contains( "Parked" ) || keywords.contains( "NotResponding" ) )
                    {
                    continue;
                    }
                out.println( "probing " + siteName + " " + submissionURL );
                final Get get = new Get();
                String page = get.send( new URL( submissionURL ), Get.UTF8 );
                final int responseCode = get.getResponseCode();
                final String responseMessage = get.getResponseMessage();
                if ( get.isGood() && page != null && page.length() != 0 )
                    {
                    final String language = recogniseLanguage( page.toLowerCase() );
                    if ( !language.equals( "English" ) )
                        {
                        out.println( language + "    " + submissionURL );
                        }
                    if ( !( language.equals( "English" ) || language.equals( "Unknown" ) ) )
                        {
                        keywords = keywords + " " + language;
                        try
                            {
                            // dedup, sort.
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
                        }
                    }
                else
                    {
                    err.println( "Could not fetch page: " + siteName + " " + submissionURL + " responseCode: " + responseCode + " responseMessage:" +
                                 responseMessage );
                    }
                // write, changed or not
                w.put( siteName );
                w.put( hostURL );
                w.put( submissionURL );
                w.put( image );
                w.put( keywords );
                w.put( notes );
                w.nl();
                }  // end loop
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
        }// /method
    // /methods
    }
