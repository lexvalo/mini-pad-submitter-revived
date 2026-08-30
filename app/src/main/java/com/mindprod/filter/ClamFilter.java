/*
 * [ClamFilter.java]
 *
 * Summary: Select files that begin/end with a given string.
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
 * Select files that begin/end with a given string.
 * <p/>
 * filter out all but files/directories that begin and end with given string, a primitive sort of wildcard feature. Case
 * insensitive. If you just want extensions, the newer ExtensionListFilter is better. see FileListFilter if you just
 * have a list of specific files.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @see StartAndEndsWithFilter
 * @since 2003
 */
public final class ClamFilter implements FilenameFilter
    {
    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * Filename to pass must start with this string.
     */
    private final String mustEndWith;

    /**
     * filename to pass must start with this string.
     */
    private final String mustStartWith;

    /**
     * do we return all directories but the ones in the list.
     */
    private final boolean invert;

    /**
     * constructor
     *
     * @param mustStartWith string the filename must start with
     * @param mustEndWith   string the filename must end with, usually of form ".html" including the dot.
     */
    public ClamFilter( final String mustStartWith, final String mustEndWith )
        {
        this( false, mustStartWith, mustEndWith );
        }

    /**
     * constructor
     *
     * @param invert        if true, selects files not in the range instead.
     * @param mustStartWith string the filename must start with
     * @param mustEndWith   string the filename must end with, usually of form ".html" including the dot.
     */
    private ClamFilter( final boolean invert, final String mustStartWith, final String mustEndWith )
        {
        this.invert = invert;
        this.mustStartWith = mustStartWith.toLowerCase();
        this.mustEndWith = mustEndWith.toLowerCase();
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
            // find all cl*.java files, case-insensitive.
            FilenameFilter f = new ClamFilter( "cl", ".java" );
            String[] filenames = new File( "." ).list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only files with with appropriate beginning and end.
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        if ( !( new File( dir, name ).isFile() ) )
            {
            return false;
            }
        name = name.toLowerCase();
        return invert ^ ( name.startsWith( mustStartWith ) && name.endsWith( mustEndWith ) );
        }
    } // end ClamFilter
