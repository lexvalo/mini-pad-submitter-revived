/*
 * [FileListFilter.java]
 *
 * Summary: Selects files that match one of a list of filenames.
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
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Selects files that match one of a list of filenames.
 * <p/>
 * You provide an array of specific filenames that you want. Does not work with
 * Directories. See DirListFilter.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2003-06-06
 */
public final class FileListFilter implements FilenameFilter
    {
    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * Collection of filenames describing files you do want, in lower case
     */
    private final HashSet<String> want;

    /**
     * do we return all directories but the ones in the list.
     */
    private final boolean invert;

    /**
     * constructor
     *
     * @param want Array of filenames without directory names you want. No wildcards. when old Javas die we will convert
     *             this to String...
     */
    public FileListFilter( final String... want )
        {
        this( false, want );
        }

    /**
     * constructor
     *
     * @param invert if true, selects files not in the list instead.
     * @param want   Array of filenames without directory names you want. No wildcards. when old Javas die we will
     *               convert
     *               this to String...
     */
    public FileListFilter( final boolean invert, final String... want )
        {
        // we need them in lower case. We don't want to disturb the caller's
        // array.
        // we don't lower case the caller's array.
        this.invert = invert;
        this.want =
                new HashSet<>( Math.max( ( int ) ( want.length / .75f ) + 1,
                        16 ) );
        for ( String aWant : want )
            {
            this.want.add( aWant.toLowerCase() );
            }
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
            // find all just the files listed, case-insensitive.
            FilenameFilter f = new FileListFilter( false, "filter.use", "Just.bat", "notthere.txt" );
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
        return f.isFile() && ( invert ^ want.contains( name.toLowerCase() ) );
        }
    } // end FileListFilter
