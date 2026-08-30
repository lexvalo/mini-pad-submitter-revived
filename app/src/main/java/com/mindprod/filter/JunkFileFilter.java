/*
 * [JunkFileFilter.java]
 *
 * Summary: Filter to detect junk files.
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
import java.util.Arrays;
import java.util.HashSet;

/**
 * Filter to detect junk files.
 * <p/>
 * accept only junk files. Can define junk files by name, extension, starts with or endsWith lists.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @see AvoidJunkFilter
 * @since 2007-08-28
 */
public class JunkFileFilter implements FilenameFilter
    {
    /**
     * Extension to be deleted, stored in lower case. If none, this usually be null. It does not matter what is to the
     * left of the dot.
     */
    private HashSet<String> junkExtensions;

    /**
     * filenames to be deleted, in lower case. If none, this usually be null. It does not matter what is to the right of
     * the dot.
     */
    private HashSet<String> junkFilenames;

    /**
     * if a filename ends in these chars, it will be deleted. Stored in lower case. If none, this usually be null.  Does
     * not matter what it starts with.
     */
    private String[] junkEndsWith;

    /**
     * if a filename starts in these chars, it will be deleted. Stored in lower case. If none, this usually be null.
     * Does not matter what it ends with.
     */
    private String[] junkStartsWith;

    /**
     * Defines which filenames and extensions are considered junk, to be deleted.
     */
    public JunkFileFilter()
        {
        }

    /**
     * Accept only junk files to be deleted.
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file.
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        if ( dir == null || name == null || new File( dir, name ).isDirectory() )
            {
            return false;
            }
        // do easy startsWith and endsWith first
        if ( junkStartsWith != null )
            {
            for ( String sw : junkStartsWith )
                {
                if ( name.startsWith( sw ) )
                    {
                    return true;
                    }
                }
            }
        if ( junkEndsWith != null )
            {
            for ( String ew : junkEndsWith )
                {
                if ( name.endsWith( ew ) )
                    {
                    return true;
                    }
                }
            }
        // split file into name and extension
        String filename = name;
        String extension = "";
        int whereDot = name.lastIndexOf( '.' );
        if ( 0 < whereDot && whereDot <= name.length() - 2 )
            {
            extension = name.substring( whereDot + 1 ).toLowerCase();
            }
        if ( junkExtensions != null && junkExtensions.contains( extension ) )
            {
            return true;
            }
        if ( 1 < whereDot )
            {
            filename = name.substring( 0, whereDot ).toLowerCase();
            }
        return junkFilenames != null && junkFilenames
                .contains( filename );
        }

    /**
     * Set the list of junk ends-with criteria. Replaces previous list, does not add to it.
     *
     * @param junkEndsWith Array of strings. If file starts this way, it considered junk.
     */
    public void setEndsWith( String... junkEndsWith )
        {
        this.junkEndsWith = junkEndsWith;
        // store in lower case for fast compare.
        if ( this.junkEndsWith != null )
            {
            // cannot use for:each
            for ( int i = 0; i < this.junkEndsWith.length; i++ )
                {
                this.junkEndsWith[ i ] = this.junkEndsWith[ i ].toLowerCase();
                }
            }
        }

    /**
     * Set the list of just extensions. Replaces previous list, does not add to it.
     *
     * @param junkExtensions Array of strings defining unqualified, dotless filename extensions considered junk.
     */
    public void setExtensions( String... junkExtensions )
        {
        if ( junkFilenames != null )
            {
            this.junkExtensions =
                    new HashSet<>( Arrays.asList( junkExtensions ) );
            }
        else
            {
            this.junkExtensions = null;
            }
        }

    /**
     * Set the list of junk filenames. Replaces previous list, does not add to it.
     *
     * @param junkFilenames Array of strings defining the unqualified, extensionless filenames considered junk.
     */
    public void setFilenames( String... junkFilenames )
        {
        if ( junkFilenames != null )
            {
            this.junkFilenames =
                    new HashSet<>( Arrays.asList( junkFilenames ) );
            }
        else
            {
            this.junkFilenames = null;
            }
        }

    /**
     * Set the list of junk start
     * -with criteria. Replaces previous list, does not add to it.
     *
     * @param junkStartsWith Array of strings. If file starts this way, it considered junk.
     */
    public void setStartsWith( String... junkStartsWith )
        {
        this.junkStartsWith = junkStartsWith;
        // store in lower case for fast compare.
        if ( this.junkStartsWith != null )
            {
            // cannot use for:each
            for ( int i = 0; i < this.junkStartsWith.length; i++ )
                {
                this.junkStartsWith[ i ] =
                        this.junkStartsWith[ i ].toLowerCase();
                }
            }
        }
    }
