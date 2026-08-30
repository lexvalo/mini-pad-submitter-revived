/*
 * [FileLengthFilter.java]
 *
 * Summary: Filters accepting only short or long files (not filenames).
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
 *  2.7 2010-11-22 add optional invert parameter on a number of the filters.
 */
package com.mindprod.filter;

import java.io.File;
import java.io.FilenameFilter;

import static java.lang.System.*;

/**
 * Filters accepting only short or long files (not filenames).
 * <p/>
 * Use it like this to get a list of files shorter than 12
 * characters.
 * <p/>
 * FilenameFilter f = new FileLengthFilter( 12, false ); String[] filenames = new File ( &quot;MyDir&quot; ).list( f );
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2003
 */
public final class FileLengthFilter implements FilenameFilter
    {
    /**
     * convenience constant to remember that true means you want long files.
     */
    public static final boolean LONG_FILES = true;

    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * convenience constant to help you remember that false means accept short files.
     */
    public static boolean SHORT_FILES = false;

    /**
     * true if want long filenames, false if want short filenames.
     */
    private final boolean wantLongFiles;

    /**
     * The minimum/maximum length of file we will let through our filter.
     */
    private final long length;

    /**
     * constructor
     *
     * @param length        The length of file you want to accept.
     * @param wantLongFiles true LONG_FILES if you want file longer or equal to the specified length. false SHORT_FILES
     *                      if you want filenames shorter or equal to the specified length.
     */
    public FileLengthFilter( final long length, final boolean wantLongFiles )
        {
        this.length = length;
        this.wantLongFiles = wantLongFiles;
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
            // get files 200 bytes or longer
            FilenameFilter f = new FileLengthFilter( 200, LONG_FILES );
            String[] filenames = new File( "." ).list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only files with appropriate length.
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        final File file = new File( dir, name );
        if ( file.isDirectory() )
            {
            return false;
            }
        long length = file.length();
        return length == this.length || ( length >= this.length ) == wantLongFiles;
        }
    }
