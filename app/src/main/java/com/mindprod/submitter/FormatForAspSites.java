/*
 * [FormatForAspSites.java]
 *
 * Summary: format CSV info on asp PADs to html.
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

import com.mindprod.common18.BigDate;
import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.csv.CSVReader;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.entities.EntifyStrings;
import com.mindprod.fastcat.FastCat;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.System.*;

/**
 * format CSV info on asp PADs to html.
 * <p/>
 * Use to prepare mindprod.com HTML versions of hassle, no-hassle or candidate pad submission sites.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2016-05-22 initial version
 * @since 2016-05-22
 */
public class FormatForAspSites
    {
    private static final String USAGE = "\nFormatForAspSites.exe";

    private static final String DO_NOT_EDIT = "<!-- The following table was generated on " + BigDate.localToday().toString() + " by submitter.FormatForAspSites. D o   n o t   e d i t . -->\n";

    private static void appendLongURL( final String url, final FastCat sb )
        {
        if ( ST.isEmpty( url ) )
            {
            sb.append( "<td></td>\n" );
            }
        else
            {
            // trim a very long submissionURL
            sb.append( "<td><a href=\"", url, "\">" );
            if ( url.length() > 60 )
                {
                String t = DeEntifyStrings.deEntifyHTML( url, ' ' );
                t = t.substring( 0, 50 );
                t = EntifyStrings.entifyHTML( t );
                sb.append( t, "&hellip;" );
                }
            else
                {
                sb.append( url );
                }
            sb.append( "</a></td>\n" );
            }
        }

    private static void appendStatus( final String status, final FastCat sb )
        {
        switch ( status )
            {
            case "":
                sb.append( "<td class=\"tick\"></td>\n" );
                break;
            case "dead":
                sb.append( "<td class=\"x\"></td>\n" );
                break;
            case "dup":
                sb.append( "<td class=\"dup\"></td>\n" );
                break;
            case "parked":
                sb.append( "<td class=\"parked\"></td>\n" );
                break;
            case "redirect":
                sb.append( "<td class=\"redirect\"></td>\n" );
                break;
            default:
                sb.append( "<td>", status, "</td>\n" );
            }
        }

    private static void emitTableHeader( final PrintWriter prw )
        {
        prw.print( DO_NOT_EDIT +
                   "<table class=\"standard\"><caption class=\"hidden\">\n"
                   + "Status of ASP PADSites\n"
                   + "</caption><!-- PadsiteName, lineNumber, home, status, submit, status, homeredir, submitredir -->\n"
                   + "<colgroup>\n"
                   + "<col style=\"text-align:right\">\n"
                   + "<col style=\"text-align:right;width:50px\">\n"
                   + "<col style=\"text-align:left\">\n"
                   + "<col style=\"text-align:center\">\n"
                   + "<col style=\"text-align:left\">\n"
                   + "<col style=\"text-align:center\">\n"
                   + "<col style=\"text-align:center\">\n"
                   + "<col style=\"text-align:center\">\n"
                   + "</colgroup>\n"
                   + "<thead><tr><th colspan=\"8\" style=\"text-align:center\">"
                   + "Status of ASP PADSites"
                   + "</th></tr>\n"
                   + "<tr><th>PadSite</th>\n"
                   + "<th>#</th>\n"
                   + "<th>Status</th>\n"
                   + "<th style=\"min-width:330px;width:330px\">Home</th>\n"
                   + "<th>Status</th>\n"
                   + "<th style=\"min-width:500px;width:500px\">Submit</th>\n"
                   + "<th style=\"min-width:330px;width:330px\">HomeRedir</th>\n"
                   + "<th style=\"min-width:500px;width:500px\">SubmitRedir</th>\n"
                   + "</tr></thead><tbody>\n"
        );
        }

    public static void emitTableFooter( final PrintWriter prw, final int lineNumber )
        {
        if ( lineNumber == 0 )
            {
            prw.print( "<tr><td colspan=\"5\">none</td></tr>\n" );
            }
        // C L O S E
        prw.print( "</tbody></table>\n" );
        }

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
        final File rfile = new File( "E:/com/mindprod/submitter/forasp.csv" );
        final CSVReader r = new CSVReader( EIO.getBufferedReader( rfile, 4 * 1024, EIO.UTF8 ) );
        final PrintWriter prw = EIO.getPrintWriter( new File( "E:/mindprod/jgloss/include/forasp.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        emitTableHeader( prw );
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
                if ( fields.length < 3 )
                    {
                    // submissionURL and status are optional
                    err.println( "missing field(s) on line " + r.lineCount() + " of file " + rfile );
                    System.exit( 2 );
                    }
                final String padsiteName = fields[ 0 ];
                if ( padsiteName.length() == 0 )
                    {
                    err.println( "missing site name on line " + r.lineCount() + " of file " + rfile );
                    System.exit( 2 );
                    }
                final String statusForHome = fields[ 1 ];
                final String homeURL = fields[ 2 ].replace( "&", "&amp;" );
                final String statusForSubmit = fields.length > 3 ? fields[ 3 ] : "";
                final String submitURL = fields.length > 4 ? fields[ 4 ].replace( "&", "&amp;" ) : "";
                final String homeRedir = fields.length > 5 ? fields[ 5 ].replace( "&", "&amp;" ) : "";
                final String submitRedir = fields.length > 6 ? fields[ 6 ].replace( "&", "&amp;" ) : "";
                // write image, line number, link to site, notes.
                final FastCat sb = new FastCat( 35 );
                // PadSite
                sb.append( "<tr><td>", padsiteName, "</td>\n" );
                // LineNumber
                sb.append( "<td class=\"alignedright\">", ++lineNumber, ".</td>\n" );
                // statusForHost
                appendStatus( statusForHome, sb );
                // homeURL
                appendLongURL( homeURL, sb );
                // statusForSubmit
                appendStatus( statusForSubmit, sb );
                //  submissionURL
                appendLongURL( submitURL, sb );
                // homeRedir
                appendLongURL( homeRedir, sb );
                // submitRedir
                appendLongURL( submitRedir, sb );
                sb.append( "</tr>\n" );
                // out.println( "used: " + sb.used()  );
                prw.print( sb.toString() );
                }  // end while
            }
        catch ( EOFException e )
            {
            emitTableFooter( prw, lineNumber );
            prw.close();
            r.close();
            }
        catch ( Exception e )
            {
            err.println( "trouble on line " + r.lineCount() + " of file " + rfile
            );
            err.println( e.getMessage() );
            System.exit( 2 );
            }
        }
    }
