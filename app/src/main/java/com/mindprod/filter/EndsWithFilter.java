/*
 * [EndsWithFilter.java]
 *
 * Summary: Select files that end with a particular string.
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
 * Select files that end with a particular string.
 * <p/>
 * Filters files whose file names end with a given string, case-insensitive. Does not accept directories. See
 * DirListFilter. see also FileListFilter and ClamFilter.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters..
 * @since 2003
 */
public final class EndsWithFilter implements FilenameFilter// note Sun's
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
    private final String endsWith;

    /**
     * do we return all directories but the ones in the list.
     */
    private final boolean invert;

    /**
     * constructor
     *
     * @param endsWith string file must end with. Case Insensitive.
     */
    @SuppressWarnings( { "WeakerAccess", "SameParameterValue" } )
    public EndsWithFilter( final String endsWith )
        {
        this( false, endsWith );
        }

    /**
     * constructor
     *
     * @param invert   if true, selects files not in the list instead.
     * @param endsWith string file must end with. Case Insensitive.
     */
    @SuppressWarnings( { "WeakerAccess", "SameParameterValue" } )
    public EndsWithFilter( final boolean invert, final String endsWith )
        {
        this.invert = invert;
        this.endsWith = endsWith.toLowerCase();
        }

    /**
     * TEST harness.
     *
     * @param args not used.
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            // find all just the files listed, case-insensitive.
            FilenameFilter f = new EndsWithFilter( "filter.class" );
            String[] filenames = new File( "C:\\com\\mindprod\\filter" )
                    .list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only Files ending with our string.
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file.
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    @SuppressWarnings( { "SimplifiableIfStatement" } )
    public boolean accept( File dir, String name )
        {
        final File f = new File( dir, name );
        if ( f.isDirectory() )
            {
            return false;
            }
        return invert ^ name.toLowerCase().endsWith( endsWith );
        }
    } // end EndsWithFilter
