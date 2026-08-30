/*
 * [DirListFilter.java]
 *
 * Summary: Filters a simple list of dir legs you want. Does not work with Files, see FileListFilter.
 *
 * Copyright: (c) 2004-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  2.6 2009-05-09 add StartAndEndWithFilter and NoFootFilter
 *  2.7 2010-11-22 add optional invert parameter on a number of the filters.
 */
package com.mindprod.filter;

import com.mindprod.common18.ST;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Filters a simple list of dir legs you want. Does not work with Files, see FileListFilter.
 * <p/>
 * Names filtered are not fully qualified directories, but legs without :/\.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters..
 * @since 2004-08-03
 */
public final class DirListFilter implements FilenameFilter
    {
    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * Collection of Directory names describing directories you do want, in lower case
     */
    private final HashSet<String> want;

    /**
     * do we return all directories but the ones in the list.
     */
    private final boolean invert;

    /**
     * constructor
     *
     * @param want Array of simple directory names without qualification. case-insensitive.
     */
    public DirListFilter( final String... want )
        {
        this( false, want );
        }

    /**
     * constructor
     *
     * @param invert if true, gets you all directories except the ones in the list.
     * @param want   Array of simple directory names without qualification. case-insensitive.
     */
    public DirListFilter( final boolean invert, final String... want )
        {
        this.invert = invert;
        // we need them in lower case. We don't want to disturb the caller's
        // array.
        // we don't lower case the caller's array.
        // If no parms will have String[0], not null.
        this.want = new HashSet<>( Math.max( ( int ) ( want.length / .75f ) + 1, 16 ) );
        for ( String aWant : want )
            {
            if ( ST.isLegal( aWant, ":/\\" ) )
                {
                throw new IllegalArgumentException( "DirListFilter directory legs must not contain : / or \\. " + aWant );
                }
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
            FilenameFilter f = new DirListFilter( false, "filter", "fileio" );
            String[] filenames = new File( "C:\\com\\mindprod" ).list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only Dirs with that pass muster
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        final File f = new File( dir, name );
        return ( !f.isFile() ) && ( invert ^ want.contains( name.toLowerCase() ) );
        }
    } // end DirListFilter
