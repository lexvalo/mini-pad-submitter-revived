/*
 * [RecentFilter.java]
 *
 * Summary: filter out all but recent files (or old files)., Usually cascaded with some other filter.
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
 * filter out all but recent files (or old files)., Usually cascaded with some other filter.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2003-06-05
 */
public final class RecentFilter implements FilenameFilter
    {
    /**
     * convenience constant so you don't have to remember that false means ask for files before the date.
     */
    public static final boolean AFTER = false;

    /**
     * convenience constant so you don't have to remember that true means ask for files before the date.
     */
    public static final boolean BEFORE = true;

    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * true if want old files, with timestamps prior to the given timestamp.
     */
    private final boolean before;

    /**
     * filename to pass must start with this string.
     */
    private final long timestamp;

    /**
     * constructor
     *
     * @param timestamp e.g. for one hour ago : System.currentTimeMillis() - 1000L * 60 * 60 * 1 Timestamp to separate
     *                  files by lastModified date.
     * @param before    true BEFORE if you want old files before the timestamp date/elapsedTime. false AFTER if you want
     *                  recent files after the timestamp date/elapsedTime.
     */
    private RecentFilter( long timestamp, boolean before )
        {
        this.timestamp = timestamp;
        this.before = before;
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
            // get files that have not been modified in the last hour.
            FilenameFilter f =
                    new RecentFilter( System.currentTimeMillis()
                                      - 1000L * 60 * 60 * 1, BEFORE );
            String[] filenames = new File( "." ).list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only files with the appropriate timestamps
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        final File f = new File( dir, name );
        return f.isFile() && ( f.lastModified() <= timestamp ) == before;
        }
    } // end RecentFilter
