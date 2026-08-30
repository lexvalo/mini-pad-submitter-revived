/*
 * [Amper.java]
 *
 * Summary: amper, converts invalid & to &amp; in html.
 *
 * Copyright: (c) 1999-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.1 2006-03-05
 *  1.2 2007-03-26 fix bug in StripEntities. Was not doing &#xffff; properly.
 *  1.3 2007-04-07 recover from crash. Tidy code.
 *  1.4 2007-05-10 add icon, PAD file.
 *  1.5 2007-06-29 add -q command line support. New CommandLine interface.
 *  1.6 2008-08-03 change detail parameter so that you can request three levels of detail, rather than two.
 *  1.7 2012-01-25 now handles HTML5 entities. It now leaves any unusual entities as is.
 *  1.8 2012-02-09 fix bug. Now handles even very longest HTML5 entities. No longer extends DeEntifyStrings.
 *  1.9 2012-06-18 allow you to ampify .htm and .csv files
 *  2.0 2012-11-03 deal text inside <script is no longer ampified.
 *                 new methods ampifyPossiblyScriptedString(String) ampifyPossiblyCommentedString(String)
 *                 deprecated ampifyCommented.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * amper, converts invalid & to &amp; in html.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.0 2012-11-03 deal text inside <script is no longer ampified.
 * new methods ampifyPossiblyScriptedString(String) ampifyPossiblyCommentedString(String)
 * deprecated ampifyCommented.
 * @noinspection WeakerAccess
 * @since 1999
 */
public final class Amper
    {
    /**
     * Longest an HTML 5 entity can be, at least in our tables, including the lead & and trail ;.
     */
    static final int LONGEST_HTML5_ENTITY = "&CounterClockwiseContourIntegral;".length();

    /**
     * pattern to detect entity less lead & with trail ;   alpha, &#x hex or &# numeric
     */
    private static final Pattern ENTITY_PATTERN = Pattern.compile( "\\p{Alnum}{2," + ( LONGEST_HTML5_ENTITY - 2 ) +
                                                                   "};|#x[0-9a-fA-F]{1,8};|#\\p{Digit}{1,10};" );

    /**
     * true if want extra debug output
     */
    private static final boolean DEBUGGING = false;

    private static final int FIRST_COPYRIGHT_YEAR = 1999;

    /**
     * undisplayed copyright notice.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 1999-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * string that marks the start of a comment
     */
    private static final String MARKER_FOR_COMMENT_BEGIN = "<!--";

    /**
     * string that marks the end of a comment
     */
    private static final String MARKER_FOR_COMMENT_END = "-->";

    /**
     * string that marks the start of a script
     */
    private static final String MARKER_FOR_SCRIPT_BEGIN = "<script";

    /**
     * string that marks the end of a script
     */
    private static final String MARKER_FOR_SCRIPT_END = "</script>";

    /**
     * date this version released.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2012-11-03";

    /**
     * how to use the command line
     */
    private static final String USAGE = "\nAmper needs a filename.html or a space-separated list of filenames, " +
                                        "with optional -s -q -v switches";

    /**
     * embedded version string.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String VERSION_STRING = "2.0";

    /**
     * constructor, not used.
     *
     * @noinspection WeakerAccess
     */
    private Amper()
        {
        }

    /**
     * convert all & except ones in comments to &amp;.
     *
     * @param big string possibly containing & and comments, but no <scripts
     *
     * @return compacted string.
     * @noinspection WeakerAccess
     * @see #ampifyPossiblyScriptedString(String)
     * @see #ampifyPossiblyCommentedString(String)
     * @see #ampifyUncommentedString(String)
     * @deprecated renamed to ampifyPossiblyCommentedString . You probably really want
     * ampifyPossiblyScriptedString.
     */
    public static String ampifyCommented( String big )
        {
        return ampifyPossiblyCommentedString( big );
        }

    /**
     * fix amps in one file.
     *
     * @param fileBeingProcessed the file currently being processed.
     * @param detail             0=out output at all, 1=just files changed, 2=all files.
     *
     * @throws IOException if trouble reading or writing file
     * @noinspection SameParameterValue, WeakerAccess
     * @see #ampifyPossiblyScriptedString(String)
     * @see #ampifyPossiblyCommentedString(String)
     * @see #ampifyUncommentedString(String)
     */
    public static void ampifyFile( File fileBeingProcessed,
                                   int detail ) throws IOException
        {
        // in case called without using filter, double check extenion.
        final String extension = EIO.getExtension( fileBeingProcessed );
        boolean found = false;
        for ( String possExtension : ExtensionListFilter.COMMON_AMPER_EXTENSIONS )
            {
            if ( extension.equals( possExtension ) )
                {
                found = true;
                break;
                }
            }
        if ( !found )
            {
            out.println( "Cannot amp: "
                         + EIO.getCanOrAbsPath( fileBeingProcessed )
                         + " not a safe extension." );
            return;
            }
        String big = HunkIO.readEntireFile( fileBeingProcessed );
        String result = ampifyPossiblyScriptedString( big );
        if ( result.equals( big ) )
            {
            // nothing changed. No need to write results.
            if ( detail >= 2 )
                {
                out.println( "- " + EIO.getCanOrAbsPath( fileBeingProcessed ) );
                }
            return;
            }
        // generate output into a temporary file until we are sure all is ok.
        // create a temp file in the same directory as filename
        if ( detail >= 1 )
            {
            // it changed
            out.println( "* " + EIO.getCanOrAbsPath( fileBeingProcessed ) );
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
     * convert all & except ones in comments to &amp;.
     *
     * @param big string possibly containing & and comments, but no <scripts
     *
     * @return tidied string.
     * @noinspection WeakerAccess
     * @see #ampifyPossiblyScriptedString(String)
     * @see #ampifyUncommentedString(String)
     */
    public static String ampifyPossiblyCommentedString( String big )
        {
        int originalLength = big.length();
        final StringBuilder sb = new StringBuilder( originalLength + 100 );
        // indexes which character we are working on
        int i = 0;
        while ( i < originalLength )
            {
            // search for start of comment
            int startCommentPlace = big.indexOf( MARKER_FOR_COMMENT_BEGIN, i );
            if ( startCommentPlace < 0 )
                {
                // no more comments, finish off this last chunk
                sb.append( ampifyUncommentedString( big.substring( i,
                        originalLength ) ) );
                break;
                }
            // we found the start of a comment
            // process html in front of comment, possibly empty
            sb.append( ampifyUncommentedString( big.substring( i,
                    startCommentPlace ) ) );
            // find the end of comment
            int endCommentPlace =
                    big.indexOf( MARKER_FOR_COMMENT_END, startCommentPlace + MARKER_FOR_COMMENT_BEGIN.length() );
            if ( endCommentPlace < 0 )
                {
                throw new IllegalArgumentException( "missing " + MARKER_FOR_COMMENT_END + " on a comment" );
                }
            endCommentPlace += MARKER_FOR_COMMENT_END.length();
            String commentText = big.substring( startCommentPlace, endCommentPlace );
            // make sure the comments not malformed. Should be no embedded start
            // comment marker
            String commentGuts = commentText.substring( MARKER_FOR_COMMENT_BEGIN.length(),
                    commentText.length() - MARKER_FOR_COMMENT_END.length() );
            if ( commentGuts.contains( MARKER_FOR_COMMENT_BEGIN ) )
                {
                throw new IllegalArgumentException( MARKER_FOR_COMMENT_BEGIN +
                                                    " ... " +
                                                    MARKER_FOR_COMMENT_END +
                                                    " nested or not balanced" );
                }
            // output the comment unchanged
            sb.append( commentText );
            i = endCommentPlace;
            } // end while
        return sb.toString();
        }

    /**
     * convert all & except ones in comments or inside <script to &amp;.
     * & could in url or could be and operator.
     *
     * @param big string possibly containing & and comments and <scripts
     *
     * @return tidied string.
     * @noinspection WeakerAccess
     * @see #ampifyPossiblyCommentedString(String)
     * @see #ampifyUncommentedString(String)
     */
    public static String ampifyPossiblyScriptedString( String big )
        {
        int originalLength = big.length();
        final StringBuilder sb = new StringBuilder( originalLength + 100 );
        // indexes which character we are working on
        int i = 0;
        while ( i < originalLength )
            {
            // search for start of <script
            int startScriptPlace = big.indexOf( MARKER_FOR_SCRIPT_BEGIN, i );
            if ( startScriptPlace < 0 )
                {
                // no more scripts, finish off this last chunk
                sb.append( ampifyPossiblyCommentedString( big.substring( i,
                        originalLength ) ) );
                break;
                }
            // we found the start of a <script
            // process html in front of <script, possibly empty
            sb.append( ampifyPossiblyCommentedString( big.substring( i,
                    startScriptPlace ) ) );
            // find the end of script
            int endScriptPlace =
                    big.indexOf( MARKER_FOR_SCRIPT_END, startScriptPlace + MARKER_FOR_SCRIPT_END.length() );
            if ( endScriptPlace < 0 )
                {
                throw new IllegalArgumentException( "missing " +
                                                    MARKER_FOR_SCRIPT_END );
                }
            endScriptPlace += MARKER_FOR_SCRIPT_END.length();
            String scriptText =
                    big.substring( startScriptPlace, endScriptPlace );
            // make sure the <scripts not malformed. Should be no embedded start  marker
            String scriptGuts = scriptText.substring( MARKER_FOR_SCRIPT_BEGIN.length(),
                    scriptText.length() - MARKER_FOR_SCRIPT_END.length() );
            if ( scriptGuts.contains( MARKER_FOR_SCRIPT_BEGIN ) )
                {
                throw new IllegalArgumentException( MARKER_FOR_SCRIPT_BEGIN +
                                                    " ... " +
                                                    MARKER_FOR_SCRIPT_END +
                                                    " nested or not balanced" );
                }
            // output the script unchanged
            sb.append( scriptText );
            i = endScriptPlace;
            } // end while
        return sb.toString();
        }

    /**
     * convert all & to &amp; unless it has been done already. Leaves existing
     * entities as is.
     *
     * @param chunk the string to process
     *
     * @return tidied string
     * @noinspection WeakerAccess
     * * @see #ampifyPossiblyScriptedString(String)
     * @see #ampifyPossiblyCommentedString(String)
     */
    public static String ampifyUncommentedString( String chunk )
        {
        // do a quick check. If chunk contains no &, we have nothing to do,
        // guaranteed
        if ( !chunk.contains( "&" ) )
            {
            return chunk;
            }
        final int originalLength = chunk.length();
        final StringBuilder sb = new StringBuilder( originalLength + 20 );
        int i = 0;
        while ( i < originalLength )
            {
            int ampPlace = chunk.indexOf( "&", i );
            if ( ampPlace < 0 )
                {
                // all done, copy over the remaining chunk.
                sb.append( chunk.substring( i, originalLength ) );
                // don't need to increment i
                break;
                }
            // we found an &
            // copy over stuff before the & we just found
            sb.append( chunk.substring( i, ampPlace ) );
            i = ampPlace;
            // is it an &amp; or &lt; or some other entity already?
            // get string without lead & but with trailing ; if it exists.
            final String candidate = chunk.substring( i + 1, Math.min( i + LONGEST_HTML5_ENTITY, originalLength ) );
            final Matcher m = ENTITY_PATTERN.matcher( candidate );
            // quick test.  Just check pattern starting just after &
            if ( m.lookingAt() )
                {
                // this was an entity already, leave it alone.
                sb.append( '&' );
                }
            else
                {
                // convert & to &amp;
                sb.append( "&amp;" );
                }
            i++;
            } // end while
        return sb.toString();
        }

    /**
     * fixes ampersands in HTML files.
     *
     * @param args names of files to process, dirs, files, -s, *.*, no wildcards.
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            out.println( ENTITY_PATTERN.toString() );
            }
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
                ampifyFile( file, quiet ? 0 : 1 );
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
