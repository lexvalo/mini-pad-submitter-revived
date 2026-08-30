/*
 * [FilenameLengthFilter.java]
 *
 * Summary: Filter accepting only short or long filenames (not files).
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
 * Filter accepting only short or long filenames (not files).
 * <p/>
 * See FileLengthFilter for short or long files. filename used to check the length is
 * File.getName() without path stuff e.g. &quot;thing.html&quot;
 * <p/>
 * Use it like this to get a list of file names shorter than 4
 * characters.
 * <p/>
 * FilenameFilter f = new FilenameLengthFilter( 4, false ); String[] filenames = new File( &quot;MyDir&quot; ).list(
 * f );
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2003
 */
public final class FilenameLengthFilter implements FilenameFilter
    {
    /**
     * convenience constant to remember that true means you want long filenames.
     */
    public static final boolean LONG_FILENAMES = true;

    /**
     * convenience constant to help you remember that false means accept short names.
     */
    public static final boolean SHORT_FILENAMES = false;

    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * true if want long filenames, false if want short filenames.
     */
    private final boolean wantLongNames;

    /**
     * The miniumum/maximum length of filename we will let through our filter.
     */
    private final int desiredLength;

    /**
     * constructor
     *
     * @param desiredLength The length of filename you want to accept.
     * @param wantLongNames true LONG_FILENAMES if you want filenames longer or equal to the specified length. false
     *                      SHORT_FILENAMES if you want filenames shorter or equal to the specified length.
     */
    public FilenameLengthFilter( int desiredLength, boolean wantLongNames )
        {
        this.desiredLength = desiredLength;
        this.wantLongNames = wantLongNames;
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
            // get filenames 12 chars or less long.
            FilenameFilter f = new FilenameLengthFilter( 12, SHORT_FILENAMES );
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
        if ( new File( dir, name ).isDirectory() )
            {
            return false;
            }
        int length = name.length();
        return length == this.desiredLength || ( length >= this.desiredLength ) == wantLongNames;
        }
    }
