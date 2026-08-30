/*
 * [StartsWithFilter.java]
 *
 * Summary: Filters files whose names start with a given string, case-insensitive.
 *
 * Copyright: (c) 2007-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
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
 * Filters files whose names start with a given string, case-insensitive.
 * <p/>
 * Does not accept directories. See
 * DirListFilter. see also FileListFilter and ClamFilter.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @noinspection WeakerAccess
 * @since 2007-03-20
 */
public final class StartsWithFilter implements FilenameFilter// note Sun's
        // spelling, not
        // FileNameFilter
    {
    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * we filter to accept only files starting with this string.
     */
    private final String startsWith;

    /**
     * constructor.
     *
     * @param startsWith string file must start with. Case Insensitive.
     */
    @SuppressWarnings( { "WeakerAccess", "SameParameterValue" } )
    public StartsWithFilter( String startsWith )
        {
        this.startsWith = startsWith.toLowerCase();
        }

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
            // find all just the files listed, case-insensitive.
            FilenameFilter f = new StartsWithFilter( "fi" );
            String[] filenames = new File( "C:\\com\\mindprod\\filter" )
                    .list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only Files starting with our String.
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file.
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    @SuppressWarnings( { "SimplifiableIfStatement" } )
    public final boolean accept( File dir, String name )
        {
        final File f = new File( dir, name );
        if ( f.isDirectory() )
            {
            return false;
            }
        return name.toLowerCase().startsWith( startsWith );
        }
    } // end StartsWithFilter
