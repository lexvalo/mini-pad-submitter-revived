/*
 * [CommandLine.java]
 *
 * Summary: Process the command line passed to main to find all the files and directories mentioned on it.
 *
 * Copyright: (c) 2003-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.4 2005-06-18 cleaner bat files
 *  1.5 2005-07-07 displays stats, with
 *                 recursive indenting. converted to Java 1.5 syntax
 *  1.6 2006-01-01
 *  1.7 2006-01-01
 *  1.8 2006-03-13 rewrite with for:each JDK 1.5
 *  1.9 2007-01-01
 *  2.0 2007-01-01
 *  2.1 2007-01-01
 *  2.2 2007-06-29 Commandline is Iterable, isQuiet, now iterate CommandLine directly.
 *  2.3 2007-08-27 add JunkFilter
 *  2.4 2009-02-27 send output to err instead of out so will not contaminate batch data to out.
 *  2.5 2009-02-28 CommandLine now uses considerably less RAM by caching the list of files on disk.
 *                 You no longer need specify estimatedFiles.
 *                 Split off into its own package.
 *  2.6 2011-01-10 v verbose option
 */
package com.mindprod.commandline;

import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.fastcat.FastCat;
import com.mindprod.filter.AllButSVNDirectoriesFilter;
import com.mindprod.filter.ExtensionListFilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;

import static java.lang.System.*;

/**
 * Process the command line passed to main to find all the files and directories mentioned on it.
 * <p/>
 * They have he form: file.txt \mydir aRelativedir/subdir *.*
 * The  -s option means, include subdirectories of any mentioned directories thereafter everything is
 * relative to the current directory, not to what preceeded on the line.
 * -s implies full recursion, not just immediate subdirs.
 * WARNING: -s must PRECEDE dirs to be recursed.
 * <p/>
 * The -q (quiet) option means suppress listing of file processed.
 * The -v (verbose) option means request extra status info.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.6 2011-01-10 verbose option.
 * @noinspection WeakerAccess
 * @since 2003-03-06
 */
public final class CommandLine implements Iterable<File>
    {
    /**
     * true if you want extra debugging output and TEST code.
     */
    private static final boolean DEBUGGING = false;

    private static final int FIRST_COPYRIGHT_YEAR = 2003;

    /**
     * undisplayed copyright notice
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2003-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * when package released.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2011-01-10";

    /**
     * embedded version string.
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String VERSION_STRING = "2.6";

    /**
     * filter to let only directories through we want.
     */
    private final FilenameFilter dirFilter;

    /**
     * filter to let only files through we want.
     */
    private final FilenameFilter fileFilter;

    /**
     * parameters from the command line, dir and filenames, -s -q.
     */
    private final String[] args;

    /**
     * cache where file names are stored
     */
    File cacheFile;

    /**
     * where we save the file names for later processing
     */
    Saver saver;

    /**
     * quiet = true to suppress count messages.
     */
    private boolean quiet = false;

    /**
     * verbose  = true to request application provide extra status information.  Will be ignored by bost apps.
     */
    private boolean verbose = false;

    /**
     * how many files we have selected.
     */
    private int size;

    /**
     * count lines we emit, so we can avoid dup
     */
    private int lines;

    /**
     * constructor.
     *
     * @param args       arguments as they were passed to main, or any similar array of strings. Directory names,
     *                   file names, "*.*", -s, -q, result of command line expansion of wildcards to individual
     *                   files. Any null or empty args will be ignored.  This allows you to process some args yourself
     *                   then nullify them so CommandLine will ignore them.
     * @param dirFilter  a FileFilter to weed out any directories you don't want. It's accept method should return
     *                   true for good directories. It should accept directories and reject files. No file will be
     *                   accepted unless the directory it in is passes this muster.
     * @param fileFilter FilenameFilter to weed out files you don't want. It's accept method should return true for
     *                   any files you want. It should accept files and reject directories. No file will be accepted
     *                   unless it passes this muster, even if specifically named.
     *                   single filenames, directory names, dot, dot dot. If you use wildcards such as *.* a*.*
     *                   abc?.html they expanded BY THE JAVA RUNTIME, to a list of directories and files. This is
     *                   rarely what you want. *.* is effectively a one deep recursion into all directories, even
     *                   without -s!! "*.*" however, will be treated like . The problem is, CommandLine can't tell
     *                   apart a list of directories and files you want processed, from one Windows expanded from *.*
     *                   or h*.*. Using an extension, on your wildcard e.g. *.html will tend to keep directories out.
     *                   It then returns the files in the form of an Iterator to feed you the File objects, (not the
     *                   File names), one by one. -s does subdirs as well for rest of dirs on line.
     *
     * @noinspection WeakerAccess
     */
    public CommandLine( String[] args,
                        FilenameFilter dirFilter,
                        FilenameFilter fileFilter )
        {
        this.args = args;
        this.dirFilter = dirFilter;
        this.fileFilter = fileFilter;
        collectFiles();
        }

    /**
     * Smarter version of File.getParent. It has yet to be tested with all combinations of absolute, relative, root, .,
     * .., ../ ../sub C: C:\ etc.
     *
     * @param file File or directory.
     *
     * @return parent directory that file lives in as File object, ready to use in FilenameFilter.accept.
     */
    private static File getParent( File file )
        {
        String parent = file.getParent();
        if ( parent == null || parent.length() == 0 )
            {
            // Sun's method failed, try another.
            String fullName = file.getAbsolutePath();
            String name = file.getName();
            if ( name == null )
                {
                name = "";
                }
            parent = fullName.substring( 0, fullName.length() - name.length() );
            if ( parent == null || parent.length() == 0 )
                {
                throw new IllegalArgumentException(
                        "CommandLine:getParent failed" );
                }
            }
        return new File( parent );
        }

    /**
     * does the actual work of collecting the files.
     */
    private void collectFiles()
        {
        saver = new Saver();
        cacheFile = saver.open();
        size = 0;
        // presume no recursion into subdirectories unless we hit a -s switch
        boolean recurse = false;
        for ( String filename : args )
            {
            // ignore null or blank parameters.
            if ( filename == null || filename.length() == 0 )
                {
                continue;
                }
            else if ( filename.indexOf( '*' ) > 10 || filename.indexOf( '?' ) >= 0 )
                {
                err.println( "Sorry, wildcards not yet supported. " + filename + " ignored." );
                }
            if ( filename.equalsIgnoreCase( "-s" ) )
                {
                recurse = true;
                if ( !quiet )
                    {
                    err.println( "-s including subdirectories" );
                    }
                // affects all subsequent parms.
                continue;
                }
            else if ( filename.equalsIgnoreCase( "-q" ) )
                {
                // no more progress messages
                quiet = true;
                verbose = false;
                continue;
                }
            else if ( filename.equalsIgnoreCase( "-v" ) )
                {
                verbose = true;
                quiet = false;
                err.println( "-v verbose" );
                continue;
                }
            else if ( filename.startsWith( "@" ) )
                {
                err.println( "Sorry, @indirect lists not yet implemented. " );
                continue;
                }
            else if ( filename.startsWith( "-" ) )
                {
                err.println( "Unrecognised command line switch " + filename + " ignored." );
                continue;
                }
            else if ( filename.equals( "*.*" ) )
                {
                // this will only happen if user put "*.*" on command line.
                // raw *.* will appear as a giant expanded list
                filename = ".";
                // fall thru  to process as an ordinary dir name.
                }
            final File file = new File( filename );
            final File parent = CommandLine.getParent( file );
            String name = file.getName();
            if ( file.isFile() )
                {
                // it is a simple file
                // works whether name is absolute or relative.
                // we don't check . with the directory filter.
                if ( fileFilter.accept( parent, name ) )
                    {
                    saver.save( file.getAbsolutePath() );
                    size++;
                    if ( !quiet )
                        {
                        // individual file, not one in dir full
                        err.println( EIO.getCanOrAbsPath( file ) + " selected" );
                        }
                    }
                }
            else
                {
                // it is directory
                // works whether name is absolute or relative.
                // We are not checking the parent, we just need to parent to
                // help check this dir.
                if ( dirFilter.accept( parent, name ) )
                    {
                    // guaranteed at this point the file represents a dir we
                    // do want to process.
                    processDirContents( file, recurse, 0
                            /* indentation */ );
                    }
                }
            } // end for
        if ( !quiet )
            {
            // keep out of data stream
            if ( lines != 1 )
                {
                err.println( size + " TOTAL FILES SELECTED for further processing" );
                }
            }
        saver.close();
        saver = null;
        }

    /**
     * process everything in this directory, and possibly in subdirs under it.
     *
     * @param dir     a directory to be examined for files to process. It must be a dir. We don't check.
     * @param recurse if want files in subdirs processed as well
     * @param indent  how much to indent the display, recursive so indent gets bigger.
     *
     * @noinspection ConstantConditions
     */
    private void processDirContents( File dir, boolean recurse, int indent )
        {
        int before = size;
        if ( dir == null )
            {
            return;
            }
        if ( DEBUGGING )
            {
            err.println( "searching " + EIO.getCanOrAbsPath( dir ) );
            }
        // process all files in this dir first
        String[] allFiles = dir.list( fileFilter );
        if ( allFiles != null )
            {
            for ( String filename : allFiles )
                {
                final String absFilename = new File( dir, filename ).getAbsolutePath();
                saver.save( absFilename );
                size++;
                }
            }
        // process all subdirs in this directory
        if ( recurse )
            {
            String[] allDirs = dir.list( dirFilter );
            if ( allDirs != null )
                {
                for ( String dirname : allDirs )
                    {
                    final File subdir = new File( dir, dirname );
                    processDirContents( subdir, recurse, indent + 4 );
                    }
                }
            }
        // all done, put out the stats for that dir
        if ( !quiet )
            {
            int count = size - before;
            if ( count > 0 && !quiet )
                {
                final FastCat sb = new FastCat( 6 );
                sb.append( ST.spaces( indent ) );
                sb.append( count );
                sb.append( " file" );
                if ( count != 1 )
                    {
                    sb.append( 's' );
                    }
                sb.append( " selected from " );
                sb.append( EIO.getCanOrAbsPath( dir ) );
                err.println( sb.toString() );
                lines++;
                }
            }
        } // end processDirContents

    /**
     * TEST harness.
     *
     * @param args not used.
     *
     * @noinspection ConstantConditions
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            // gather all the files mentioned on the command line.
            // either directories, files, *.*, with -s and subdirs option.
            err.println( "Gathering files to process..." );
            CommandLine wantedFiles = new CommandLine( args,
                    new AllButSVNDirectoriesFilter(),
                    new ExtensionListFilter( "java" ) );
            for ( File file : wantedFiles )
                {
                err.println( EIO.getCanOrAbsPath( file ) );
                } // end for
            }
        } // end main

    /**
     * Return true if should suppress some output.
     *
     * @return true if -q (quiet) option detected on command line.
     */
    public boolean isQuiet()
        {
        return quiet;
        }

    /**
     * Return true if should provide verbose output.
     *
     * @return true if -v  (verbose) option detected on command line.
     */
    public boolean isVerbose()
        {
        return verbose;
        }

    /**
     * iterator over all files mentioned on the command line, expanded
     *
     * @return simple iterator over all files, producing File objects of all the filenames mentioned on the command
     * line, directly or indirectly. In same order as mentioned on command line.
     * <p/>
     * single filenames, directory names, dot, dot dot. If you use wildcards such as *.* a*.* abc?.html they
     * expanded BY THE JAVA RUNTIME, to a list of directories and files. This is rarely what you want. *.* is
     * effectively a one deep recursion into all directories, even without -s!! "*.*" however, will be treated
     * like . The problem is, CommandLine can't tell apart a list of directories and files you want processed,
     * from one Windows expanded from *.* or h*.*. Using an extension, on your wildcard e.g. *.html will tend to
     * keep directories out. It then returns the files in the form of an Iterator to feed you the File objects,
     * (not the File name Strings), one by one. -s does subdirs as well for rest of dirs on line.
     */
    public Iterator<File> iterator()
        {
        return new CacheIterator( cacheFile, size );
        }

    /**
     * get count of how many files have been cached.
     *
     * @return count
     */
    public int size()
        {
        return size;
        }
    }
