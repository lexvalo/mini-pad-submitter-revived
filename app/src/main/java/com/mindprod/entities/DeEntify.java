/*
 * [DeEntify.java]
 *
 * Summary: converts HTML entities back to UTF-8 characters.
 *
 * Copyright: (c) 2011-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2011-02-10 initial version
 */
package com.mindprod.entities;

import com.mindprod.commandline.CommandLine;
import com.mindprod.common18.EIO;
import com.mindprod.filter.AllButSVNDirectoriesFilter;
import com.mindprod.filter.ExtensionListFilter;
import com.mindprod.hunkio.HunkIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.System.*;

/**
 * converts HTML entities back to UTF-8 characters.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2011-02-10 initial version
 * @see DeEntify
 * @see DeEntifyStrings
 * @see Entify
 * @see EntifyStrings
 * @see Flatten
 * @since 2011-02-10
 */
public final class DeEntify extends DeEntifyStrings
    {
    private static final int FIRST_COPYRIGHT_YEAR = 2011;

    /**
     * undisplayed copyright notice.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2011-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * date this version released.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2011-02-10";

    /**
     * how to use the command line
     */
    private static final String USAGE = "\nDeentify needs a filename.html or a space-separated list of filenames, " +
                                        "with optional -s -q -v switches.";

    /**
     * embedded version string.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String VERSION_STRING = "1.0";

    /**
     * constructor, not used.
     *
     * @noinspection WeakerAccess
     */
    private DeEntify()
        {
        }

    /**
     * fix entities in one file.
     *
     * @param fileBeingProcessed the file currently being processed.
     * @param detail             0=out output at all, 1=just files changed, 2=all files.
     *
     * @throws java.io.IOException if problem reading/writing file.
     * @noinspection SameParameterValue, WeakerAccess
     */
    public static void deEntifyFile( final File fileBeingProcessed, final int detail ) throws IOException
        {
        if ( !( fileBeingProcessed.getName().endsWith( ".html" )
                || fileBeingProcessed
                        .getName().endsWith( ".htm" ) ) )
            {
            out.println( "Cannot deentify: "
                         + fileBeingProcessed.getName()
                         + "not .html file" );
            return;
            }
        final String big = HunkIO.readEntireFile( fileBeingProcessed, HunkIO.UTF8 );
        final String result = deEntifyHTML( big, ' ' );
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
        HunkIO.writeEntireFile( tempFile, result, HunkIO.UTF8 );
        // successfully created output in same directory as input,
        // Now make it replace the input file.
        HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
        // don't delete tempFile, it is renamed!
        }

    /**
     * converts awkward characters to HTML entities..
     *
     * @param args names of files to process, dirs, files, -s, *.*, no wildcards.
     */
    public static void main( String[] args )
        {
        // gather all the files mentioned on the command line.
        // either directories, files, with -s and subdirs option.
        // warning. Windows expands any wildcards in a nasty way.
        // do not use wildcards.
        // See http://mindprod.com/jgloss/wildcard.html
        out.println( "Gathering html files to deentify..." );
        final CommandLine commandLine = new CommandLine( args,
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
                deEntifyFile( file, quiet ? 0 : 1 );
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
            }
        }
    }
