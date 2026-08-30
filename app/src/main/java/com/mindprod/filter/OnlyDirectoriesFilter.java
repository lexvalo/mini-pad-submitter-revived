/*
 * [OnlyDirectoriesFilter.java]
 *
 * Summary: FileFilter that accepts all directories, but not files.
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

import static java.lang.System.*;

/**
 * FileFilter that accepts all directories, but not files.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2003-06-06
 */
public final class OnlyDirectoriesFilter implements FilenameFilter
    {
    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * constructor
     */
    public OnlyDirectoriesFilter()
        {
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
            // just subdirectories of current directory
            FilenameFilter f = new OnlyDirectoriesFilter();
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
        return f.isDirectory();
        }
    } // end  OnlyDirectoriesFilter
