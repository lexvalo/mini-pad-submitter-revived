/*
 * [MultiFilter.java]
 *
 * Summary: Used to combine filters. Automatically filters out all directories, and/or files.
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
 *  2.5 2009-02-28 CommandLine split off in its own package.
 *  2.6 2009-05-09 add StartAndEndWithFilter and NoFootFilter
 *  2.7 2010-11-22 add optional invert parameter on a number of the filters.
 */
package com.mindprod.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import static java.lang.System.*;

/**
 * Used to combine filters. Automatically filters out all directories, and/or files.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2003-06-06
 */
public final class MultiFilter implements FilenameFilter
    {
    /**
     * code for requesting both files and directories.
     */
    public static final int BOTH = 3;

    /**
     * code for requesting directories only
     */
    public static final int DIRS = 2;

    /**
     * Code for requesting files only.
     */
    public static final int FILES = 1;

    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * ArrayList of FilenameFilters describing files you do want. Only one of the filters has to return true for the
     * file to be considered for further processing.
     */
    private ArrayList<FilenameFilter> consider;

    /**
     * ArrayList of FilenameFilters describing files you want. Considered files must pass all must filters. to to be
     * considered for further processing.
     */
    private ArrayList<FilenameFilter> must;

    /**
     * ArrayList of FilenameFilters describing files you don't want. Only one of the filters needs to return true for
     * the file to be rejected.
     */
    private ArrayList<FilenameFilter> never;

    /**
     * Do you want files, directories, or both?
     */
    private int want;

    /**
     * constructor
     *
     * @param want What do you want returned FILES, DIRS or BOTH?
     */
    public MultiFilter( int want )
        {
        if ( FILES <= want && want <= BOTH )
            {
            this.want = want;
            }
        else
            {
            throw new IllegalArgumentException( "bad value for RegexFilter.want" );
            }
        this.consider = new ArrayList<>( 11 );
        this.must = new ArrayList<>( 11 );
        this.never = new ArrayList<>( 11 );
        }

    /**
     * TEST harness
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            // find all *.java and *.final class files with names longer than 12
            // characters,
            // and longer than 2000 bytes,
            // but don't count MultiFilter.java, case-insensitive.
            MultiFilter f = new MultiFilter( FILES );
            f.addConsider( new ClamFilter( "", ".java" ) );
            f.addConsider( new ClamFilter( "", ".class" ) );
            f.addConsider( new ExtensionListFilter( "doc", "bat", "exe" ) );
            f.addMust( new FilenameLengthFilter( 12,
                    FilenameLengthFilter.LONG_FILENAMES ) );
            f
                    .addMust( new FileLengthFilter( 2000,
                            FileLengthFilter.LONG_FILES ) );
            f.addNever( new FileListFilter( "MultiFilter.java" ) );
            String[] filenames = new File( "." ).list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only files with that pass muster
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        final File f = new File( dir, name );
        // we have to past a gauntlet of tests before the file is acceptable.
        switch ( want )
            {
            case FILES:
                // no more to do if not a file
                if ( !f.isFile() )
                    {
                    return false;
                    }
                break;
            case DIRS:
                // no more to do if not a directory
                if ( !f.isDirectory() )
                    {
                    return false;
                    }
                break;
            case BOTH:
                // no need to TEST
                break;
            // guaranteed no other possibilities.
            }
        // if there are no consider filters, assume all files are ok unless
        // don't wants reject.
        // if ANY of consider filters say YES, we continue processing.
        int size = consider.size();
        if ( size != 0 )
            {
            // only one of consider filters has to say yes to continue with
            // further processing.
            boolean take = false;
            for ( FilenameFilter aConsider : consider )
                {
                if ( aConsider.accept( dir,
                        name ) )
                    {
                    take = true;
                    break;
                    }
                }
            // if fell out the bottom without any matches, take will be false.
            if ( !take )
                {
                return false;
                }
            }
        // If there are no must filters, assume any files that got this
        // far are ok.
        size = must.size();
        if ( size != 0 )
            {
            // even one of the must filters failing is enough to immediately
            // reject.
            for ( int i = 0; i < size; i++ )
                {
                if ( !( must.get( i ) ).accept( dir, name ) )
                    {
                    return false;
                    }
                }
            }
        // If there are no never filters, assume any files that got this
        // far are ok.
        size = never.size();
        if ( size != 0 )
            {
            // even one of the never filters true is enough to immediately
            // reject.
            for ( int i = 0; i < size; i++ )
                {
                if ( ( never.get( i ) ).accept( dir, name ) )
                    {
                    return false;
                    }
                }
            }
        // no rejects, so we are golden.
        return true;
        }

    /**
     * Add a FilenameFilter describing files you want to consider. Only one of the consider filters has to return true
     * for the file to be considered for further processing. If you have no consider-type filters, then all files in the
     * directory are considered. The results of all the consider filters are ORed together. Add the consider filters
     * first that will catch the most files. If there is only one consider filter, it acts like a must filter.
     * ExtensionListFilters, ClamFilters or RegexFilters are useful to list all the wild card extensions to consider.
     *
     * @param consider FilenameFilter, one of mine or anyone else's to consider in the combination.
     */
    public void addConsider( FilenameFilter consider )
        {
        if ( consider != null )
            {
            this.consider.add( consider );
            }
        }

    /**
     * Add a FilenameFilter describing the conditions that all files must pass. Only one of the must filters has to
     * return false for the file to be rejected. If you have no must-type filters, then all considered files are used
     * for further processing. The results of all the must filters are ANDed together. Add the must filters first that
     * will reject the most files.
     *
     * @param must FileFilter to add.
     */
    public void addMust( FilenameFilter must )
        {
        if ( must != null )
            {
            this.must.add( must );
            }
        }

    /**
     * Add a FilenameFilter describing the files you want to reject. These filters return true for files NOT wanted. All
     * it takes is one of these never filters to return true, and the file will be rejected. If you have no never-type
     * filters, then all considered and must-passing files are accepted. The results of all the never filters are NORed
     * together. Add the never filters first that will return true to reject the most files. ListFilters are useful to
     * provide a list of files to be excluded. ClamFilters can exclude a wildcard.
     *
     * @param never FilenameFilter to add
     */
    public void addNever( FilenameFilter never )
        {
        if ( never != null )
            {
            this.never.add( never );
            }
        }
    } // end MultiFilter
