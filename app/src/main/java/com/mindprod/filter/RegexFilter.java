/*
 * [RegexFilter.java]
 *
 * Summary: filter out all but files (or directories) that match a given Regex Pattern.
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
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * filter out all but files (or directories) that match a given Regex Pattern.
 * <p/>
 * If you are clever, can do multiple positive and negative wildcards.
 * <p/>
 * See http://mindprod/quoter.html for help in creating these Strings.
 * <p/>
 * The \\ means treat . Literally, not as the &quot;any&quot; character.  You need
 * two, because \ has special meaning in Java string literals.  On the
 * command line you need only one \.
 * <p/>
 * e.g.  jgloss.* is &quot;jgloss\\.[.]*&quot; on the command line &quot;jgloss\.[.]*&quot;
 * <p/>
 * e.g.  ?gloss.html is &quot;.gloss\\.html&quot; on the command line is &quot;.gloss\.html&quot;
 * <p/>
 * e.g.  to find a list of files &quot;cat\\.txt|dog\\.txt|rabbit\\.txt&quot; On the
 * command line you would type &quot;cat\.txt|dog\.txt|rabbit\.txt&quot;
 * <p/>
 * see http://mindprod.com/jgloss/regex.html
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2003-05-24
 */
public final class RegexFilter implements FilenameFilter
    {
    /**
     * code for requesting both files and directories.
     */
    public static final int BOTH = 3;

    /**
     * code for requesting directories only
     */
    public static final int DIRS = 2;

    /**
     * Code for requesting files only.
     */
    public static final int FILES = 1;

    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * Regex pattern to select filenames
     */
    private Pattern negativePattern;

    /**
     * Regex pattern to select filenames
     */
    private Pattern positivePattern;

    /**
     * Do you want files, directories, or both?
     */
    private int want;

    /**
     * constructor
     *
     * @param positivePattern Regex pattern applied to simple filename describing files you want. Null for all.
     * @param negativePattern Regex pattern applied to simple filename\ describing files you don't want. Null for none.
     * @param want            What do you want returned FILES, DIRS or BOTH?
     */
    public RegexFilter( String positivePattern,
                        String negativePattern,
                        int want )
        {
        if ( positivePattern != null )
            {
            this.positivePattern = Pattern.compile( positivePattern );
            }
        if ( negativePattern != null )
            {
            this.negativePattern = Pattern.compile( negativePattern );
            }
        if ( FILES <= want && want <= BOTH )
            {
            this.want = want;
            }
        else
            {
            throw new IllegalArgumentException( "bad value for RegexFilter.want" );
            }
        }

    /**
     * Test Harness
     *
     * @param args parm is a TEST regex pattern positive and negative. It will display all files in the current
     *             directory
     *             that match. Note that on the command line \\ should not be doubled in regex patterns. On windows,
     *             enclose parms with blanks in "s. This won't work on all platforms.
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            if ( args.length != 2 )
                {
                out.println(
                        "Must supply 2 command line parms, +ve and -ve regex. " );
                return;
                }
            String positivePattern = args[ 0 ];
            String negativePattern = args[ 1 ];
            out.println( "seeking: \""
                         + positivePattern
                         + "\" but not \""
                         + negativePattern
                         + "\"" );
            final File dir = new File( "." );
            String[] files =
                    dir.list( new RegexFilter( positivePattern,
                            negativePattern,
                            FILES ) );
            for ( String file : files )
                {
                out.println( file );
                }
            }
        }

    /**
     * Select only files that match the Regex Pattern.
     *
     * @param dir  the directory in which the file was found.
     * @param name the simple name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        final File f = new File( dir, name );
        switch ( want )
            {
            case FILES:
                // no more to do if not a file
                if ( !f.isFile() )
                    {
                    return false;
                    }
                break;
            case DIRS:
                // no more to do if not a directory
                if ( !f.isDirectory() )
                    {
                    return false;
                    }
                break;
            case BOTH:
                // no need to TEST
                break;
            }
        return !( positivePattern != null && !( positivePattern.matcher( name ).matches() ) ) && !( negativePattern
                                                                                                    != null &&
                                                                                                    negativePattern
                                                                                                            .matcher(
                                                                                                                    name ).matches() );
        }
    } // end RegexFilter
