/*
 * [AllButSVNDirectoriesFilter.java]
 *
 * Summary: FileFilter that accepts all directories, except .svn (Subversion), but not files.
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
 *  1.0 2011-01-17 initial version.
 */
package com.mindprod.filter;

import java.io.File;
import java.io.FilenameFilter;

import static java.lang.System.*;

/**
 * FileFilter that accepts all directories, except .svn (Subversion), but not files.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2011-01-17 initial version
 * @since 2011-01-17
 */
public final class AllButSVNDirectoriesFilter implements FilenameFilter
    {
    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * constructor
     */
    public AllButSVNDirectoriesFilter()
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
            FilenameFilter f = new AllButSVNDirectoriesFilter();
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
        final File f = new File( dir, name ); // the dir we are testing is dir.name not dir.
        return f.isDirectory() && !name.equalsIgnoreCase( ".svn" ) && !name.equalsIgnoreCase( "_svn" );
        }
    }
