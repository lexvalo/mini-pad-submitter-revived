/*
 * [DeAmpify.java]
 *
 * Summary: converts &amp; to & in html and csv files.
 *
 * Copyright: (c) 2012-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2012-06-18 initial version
 */
package com.mindprod.amper;

import com.mindprod.commandline.CommandLine;
import com.mindprod.common18.EIO;
import com.mindprod.filter.AllButSVNDirectoriesFilter;
import com.mindprod.filter.ExtensionListFilter;
import com.mindprod.hunkio.HunkIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * converts &amp; to & in html and csv files.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2012-06-18 initial version
 * @since 2012-06-18
 */
public final class DeAmpify
    {
    /**
     * true means extra debugging output
     */
    private static final boolean DEBUGGING = false;

    /**
     * undisplayed copyright notice.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2012-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * date this version released.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2012-06-18";

    /**
     * how to use the command line
     */
    private static final String USAGE = "\n\nDeAmpify needs a filename.html or a space-separated list of filenames, " +
                                        "with optional -s -q -v switches";

    /**
     * embedded version string.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String VERSION_STRING = "1.0";

    /**
     * We search fo9r &amp;
     */
    private static final Pattern AMP = Pattern.compile( "&amp;", Pattern.LITERAL );

    /**
     * constructor, not used.
     *
     * @noinspection WeakerAccess
     */
    private DeAmpify()
        {
        }

    /**
     * convert all &amp; except ones in comments to &;.
     *
     * @param big string you want deampified.
     *
     * @return compacted string.
     * @noinspection WeakerAccess
     */
    public static String deAmpifyCommentedString( String big )
        {
        int originalLength = big.length();
        final StringBuilder sb = new StringBuilder( originalLength );
        // indexes which character we are working on
        int i = 0;
        while ( i < originalLength )
            {
            // search for start of comment
            int startCommentPlace = big.indexOf( "<!--", i );
            if ( startCommentPlace < 0 )
                {
                // no more comments, finish off this last chunk
                sb.append( deAmpifyUncommentedString( big.substring( i,
                        originalLength ) ) );
                break;
                }
            // we found the start of a comment
            // process html in front of comment, possibly empty
            sb.append( deAmpifyUncommentedString( big.substring( i, startCommentPlace ) ) );
            // find the end of comment
            int endCommentPlace = big.indexOf( "-->", startCommentPlace + "<!--".length() );
            if ( endCommentPlace < 0 )
                {
                throw new IllegalArgumentException( "missing --> on a comment" );
                }
            endCommentPlace += "-->".length();
            String comment = big.substring( startCommentPlace, endCommentPlace );
            // make sure the comments not malformed. Should be no embedded start
            // comment marker
            String commentGuts = comment.substring( "<!--".length(), comment.length() - "-->".length() );
            if ( commentGuts.contains( "<!--" ) )
                {
                throw new IllegalArgumentException( "<!-- ... --> not balanced" );
                }
            // output the comment unchanged
            if ( DEBUGGING )
                {
                out.println( "\ncomment:" + comment );
                }
            sb.append( comment );
            i = endCommentPlace;
            } // end while
        return sb.toString();
        }

    /**
     * fix amps in one file.
     *
     * @param fileBeingProcessed the file currently being processed.
     * @param detail             0=out output at all, 1=just files changed, 2=all files.
     *
     * @throws java.io.IOException if trouble reading or writing file
     * @noinspection SameParameterValue, WeakerAccess
     */
    public static void deAmpifyFile( File fileBeingProcessed,
                                     int detail ) throws IOException
        {
        if ( !( fileBeingProcessed.getName().endsWith( ".html" )
                || fileBeingProcessed.getName().endsWith( ".htm" )
                || fileBeingProcessed.getName().endsWith( ".csv" ) ) )
            {
            out.println( "Cannot deAmpify: "
                         + fileBeingProcessed.getName()
                         + "not .html .htm .csv file" );
            return;
            }
        String big = HunkIO.readEntireFile( fileBeingProcessed );
        String result = deAmpifyCommentedString( big );
        if ( result.equals( big ) )
            {
            // nothing changed. No need to write results.
            if ( detail >= 2 )
                {
                out.println( "- " + fileBeingProcessed.getName() );
                }
            return;
            }
        // generate output into a temporary file until we are sure all is ok.
        // create a temp file in the same directory as filename
        if ( detail >= 1 )
            {
            // it changed
            out.println( "* " + fileBeingProcessed.getName() );
            }
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        FileWriter emit = new FileWriter( tempFile );
        emit.write( result );
        emit.close();
        // successfully created output in same directory as input,
        // Now make it replace the input file.
        HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
        }

    /**
     * convert all &amp to &
     *
     * @param chunk the string to process
     *
     * @return tidied string
     * @noinspection WeakerAccess
     */
    public static String deAmpifyUncommentedString( String chunk )
        {
        if ( DEBUGGING )
            {
            out.println( "\nuncomment:" + chunk );
            }
        // replaceAll will efficiently return the original chunk if there are no changes.
        return AMP.matcher( chunk ).replaceAll( "&" );
        }

    /**
     * fixes ampersands in HTML files.
     *
     * @param args names of files to process, dirs, files, -s, *.*, no wildards.
     */
    public static void main( String[] args )
        {
        // gather all the files mentioned on the command line.
        // either directories, files, with -s and subdirs option.
        // warning. Windows expands any wildcards in a nasty way.
        // do not use wildcards.
        // See http://mindprod.com/jgloss/wildcard.html
        out.println( "Gathering html files to &ampify..." );
        CommandLine commandLine = new CommandLine( args,
                new AllButSVNDirectoriesFilter(),
                new ExtensionListFilter( ExtensionListFilter.COMMON_AMPER_EXTENSIONS ) );
        if ( commandLine.size() == 0 )
            {
            throw new IllegalArgumentException( "No files found to process\n" + USAGE );
            }
        final boolean quiet = commandLine.isQuiet();
        for ( File file : commandLine )
            {
            try
                {
                // -q gives no output at all, otherwise just files that changed.
                deAmpifyFile( file, quiet ? 0 : 1 );
                }
            catch ( FileNotFoundException e )
                {
                out.println( "Error: "
                             + EIO.getCanOrAbsPath( file )
                             + " not found." );
                }
            catch ( Exception e )
                {
                out.println( e.getMessage()
                             + " in file "
                             + EIO.getCanOrAbsPath( file ) );
                }
            } // end for
        } // end main
    }
