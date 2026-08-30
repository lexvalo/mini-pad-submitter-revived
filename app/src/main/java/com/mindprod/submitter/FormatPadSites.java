/*
 * [FormatPadSites.java]
 *
 * Summary: Format a 5-column csv file of data about pad submission sites for display as an HTML table.
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
 *  1.0 2009-09-22 initial version
 */
package com.mindprod.submitter;

import com.mindprod.common18.BigDate;
import com.mindprod.common18.Build;
import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.csv.CSVReader;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.fastcat.FastCat;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;

import static java.lang.System.*;

/**
 * Format a 5-column csv file of data about pad submission sites for display as an HTML table.
 * <p/>
 * Use to prepare mindprod.com HTML versions of hassle, no-hassle or candidate pad submission sites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2009-09-22 initial version
 * @since 2009-09-22
 */
public class FormatPadSites
    {
    /**
     * url where submit PADS to ASP
     */
    public static final String APPVISOR_URL = "http://publisher.appvisor.com/";

    private static final String USAGE = "\nFormatPadSites.exe sites5.csv target.html";

    private static final String DO_NOT_EDIT = "<!-- The following table was generated on " + BigDate.localToday().toString() + " by submitter.FormatPadSites. D o   n o t   e d i t . -->\n";

    /**
     * true if want extra debugging output
     */
    private static boolean DEBUGGING = false;

    /**
     * calculate a title from the source filename
     *
     * @param fileBeingProcessed source csv file
     *
     * @return title for table
     */
    private static String calcTitle( final File fileBeingProcessed )
        {
        switch ( fileBeingProcessed.getName() )
            {
            case "hassle.csv":
                return "PAD Sites With Minor Hassle";
            case "nohassle.csv":
                return "PAD Sites with No Hassles";
            case "dead.csv":
                return "Dead PAD Sites";
            case "candidates.csv":
                return "Possible Candidate PAD Sites";
            case "appvisor.csv":
                return "PAD Sites using AppVisor";
            default:
                return "unknown";
            }
        }

    /**
     * check consistency of keywords
     *
     * @param keywordEnums enums for this padsite
     * @param image        image name, e.g. mysite.png for this padsite "" if none.
     * @param siteName     name of site for error messages.
     */
    private static void checkKeywordConsistency( final EnumSet<Keyword> keywordEnums,
                                                 final String image,
                                                 final String siteName,
                                                 final File fileBeingProcessed )
        {
        final boolean processingDead = fileBeingProcessed.getName().equals( "dead.csv" );
        if ( processingDead != Keyword.impliesDead( keywordEnums ) )
            {
            if ( processingDead )
                {
                err.println( "<<< error >>> " + EIO.getCanOrAbsPath( fileBeingProcessed )
                             + " ["
                             + siteName
                             + "] has no dead-implying keywords."
                );
                }
            else
                {
                err.println( "<<< error >>> " + EIO.getCanOrAbsPath( fileBeingProcessed )
                             + " ["
                             + siteName
                             + "] " +
                             "should be moved to dead.csv."
                );
                }
            }
        if ( !Keyword.isConsistentWithImage( keywordEnums, image ) )
            {
            if ( ST.isEmpty( image ) )
                {
                err.println( "<<< warning >>> " + EIO.getCanOrAbsPath( fileBeingProcessed )
                             + " ["
                             + siteName
                             + "] missing image that keywords imply should be there" +
                             "." );
                }
            else
                {
                err.println( "<<< warning >>> " + EIO.getCanOrAbsPath( fileBeingProcessed )
                             + " ["
                             + siteName
                             + "] image "
                             + image
                             + " present that keywords imply " +
                             "should be removed." );
                }
            }
        final boolean processingCandidate = fileBeingProcessed.getName().equals( "candidates.csv" );
        if ( !processingCandidate && keywordEnums.contains( Keyword.CANDIDATE ) )
            {
            err.println( "<<< error >>> " + EIO.getCanOrAbsPath( fileBeingProcessed )
                         + " "
                         + siteName
                         + " " +
                         "should should have its Candidate keyword removed." );
            }
        }

    /**
     * FormatPadSites csv file to HTML, list of submission sites, either hassle or nohassle, or candidates.
     *
     * @param args source and target file names
     *             .
     *
     * @throws java.io.IOException on trouble reading/writing files
     */
    public static void main( String[] args ) throws IOException
        {
        if ( args.length != 2 )
            {
            throw new IllegalArgumentException( USAGE );
            }
        final String source = args[ 0 ];
        final String target = args[ 1 ];
        out.println( "Formatting " + source + " to " + target );
        final File fileBeingProcessed = new File( source );
        final CSVReader r = new CSVReader( EIO.getBufferedReader( fileBeingProcessed, 4 * 1024, EIO.UTF8 ) );
        final PrintWriter prw = EIO.getPrintWriter( new File( target ), 4 * 1024, EIO.UTF8 );
        final String title = calcTitle( fileBeingProcessed );
        prw.print( DO_NOT_EDIT +
                   "<table class=\"standard\"><caption class=\"hidden\">\n"
                   + title + "\n"
                   + "</caption><!-- icon, lineNumber, name, submit, keywords, notes -->\n"
                   + "<colgroup>\n"
                   + "<col style=\"text-align:right\">\n"
                   + "<col style=\"text-align:right;width:50px\">\n"
                   + "<col style=\"text-align:left\">\n"
                   + "<col style=\"text-align:center\">\n"
                   + "<col style=\"text-align:left;min-width:250px\">\n"
                   + "</colgroup>\n"
                   + "<thead><tr><th colspan=\"5\" style=\"text-align:center\">"
                   + title
                   + "</th></tr>\n"
                   + "<tr><th>Logo</th>\n"
                   + "<th>#</th>\n"
                   + "<th>Home</th>\n"
                   + "<th>Submit</th>\n"
                   + "<th>Notes</th></tr></thead>\n"
                   + "<tbody>\n"
        );
        int lineNumber = 0;
        try
            {
            while ( true )
                {
                // read  url, site, image, notes
                final String[] fields = r.getAllFieldsInLine();
                // ignore blank lines
                if ( fields.length == 0 )
                    {
                    continue;
                    }
                final String siteName = fields[ 0 ];
                // name with dashes
                if ( siteName.length() == 0 )
                    {
                    err.println( "missing site name on line " + r.lineCount() + " of file " + source );
                    System.exit( 2 );
                    }
                final String sitewbr = siteName.replace( "-", "<wbr>" );
                String siteTarget = DeEntifyStrings.stripHTMLTags( siteName.replace( "-", "" ) ).toUpperCase();
                char firstChar = siteTarget.charAt( 0 );
                if ( !( 'A' <= firstChar && firstChar <= 'Z' ) )
                    {
                    siteTarget = "A" + siteTarget;
                    }
                final String hostURL = ( fields.length > 1 ) ? fields[ 1 ].replace( "&", "&amp;" ) : "";
                final String submissionURL = ( fields.length > 2 ) ? fields[ 2 ].replace( "&", "&amp;" ) : "";
                final String image = fields.length > 3 ? fields[ 3 ] : "";
                String keywords = fields.length > 4 ? fields[ 4 ] : "";
                final String notes = fields.length > 5 ? fields[ 5 ] : "";
                EnumSet<Keyword> keywordEnums = null;
                try
                    {
                    keywordEnums = Keyword.asSet( keywords );
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
                checkKeywordConsistency( keywordEnums, image, siteName, fileBeingProcessed );
                if ( keywordEnums.contains( Keyword.APPVISOR ) != submissionURL.equals( "http://publisher.appvisor.com/" ) )
                    {
                    err.println( "Inconsistent AppVisor and http://publisher.appvisor.com/ on " + siteName );
                    err.println( "submission URL:" + submissionURL );
                    }
                // write image, line number, link to site, notes.
                final FastCat sb = new FastCat( 30 );
                sb.append( "<tr class=\"alignedmiddle\"><td class=\"alignedright\" id=\"", siteTarget, "\">" );
                if ( image.length() > 0 )
                    {
                    if ( !new File( Build.MINDPROD_WEBROOT + "/image/padvendor",
                            image ).exists() )
                        {
                        err.println( "<<< error >>> " + siteName + " missing image file " + image );
                        sb.append( "Missing image file" );
                        }
                    else
                        {
                        sb.append( "<!-- macro Image padvendor/", image, " \"", siteName, "\" middle -->" );
                        }
                    }
                else
                    {
                    // just put out name without image
                    sb.append( "<span class=\"logify\">", sitewbr, "</span>" );
                    }
                sb.append( "</td><td class=\"alignedright\">", ++lineNumber, ".</td><td>" );
                sb.append( "<a href=\"", hostURL, "\">", sitewbr, "</a></td><td class=\"alignedcenter\">" );
                if ( submissionURL.equals( APPVISOR_URL ) )
                    {
                    sb.append( "<!-- macro Button submitappvisor {" );
                    }
                else
                    {
                    sb.append( "<!-- macro Button submitpadsite {" );
                    }
                sb.append( submissionURL, "} --></td><td>" );
                // we don't display the keywords directly, or tidy them, just their meanings.
                try
                    {
                    sb.append( Keyword.getCorrespondingMeanings( keywordEnums ) );
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
                catch ( Exception e )
                    {
                    err.println( "trouble on line " + r.lineCount() + " of file " + source );
                    err.println( e.getMessage() );
                    System.exit( 2 );
                    }
                if ( notes.length() > 0 )
                    {
                    sb.append( ' ' );
                    sb.append( notes );
                    }
                sb.append( "</td></tr>\n" );
                prw.print( sb.toString() );
                }
            }
        catch ( EOFException e )
            {
            if ( lineNumber == 0 )
                {
                prw.print( "<tr><td colspan=\"5\">none</td></tr>\n" );
                }
            // C L O S E
            prw.print( "</tbody></table>\n" );
            prw.close();
            r.close();
            }
        }
    }
