/*
 * [StartAndEndsWithFilter.java]
 *
 * Summary: Filter based on startsWith/EndsWith Regex includes/excludes.
 *
 * Copyright: (c) 2005-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
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

import com.mindprod.common18.Build;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * Filter based on startsWith/EndsWith Regex includes/excludes.
 * <p/>
 * To be selected a file must start with one of the include start with strings or end with one of the include end
 * with strings,
 * and it must not start with any of the exclude start with strings or end with any of the exclude exclude end with
 * strings.
 * It need not have both an include start and end string to be included.
 * It need not have both an include start and end string to be excluded.
 * <p/>
 * Rejects all directories.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @see ClamFilter
 * @since 2009-05-09
 */
public class StartAndEndsWithFilter implements FilenameFilter
        // note Sun's spelling, not FileNameFilter
    {
    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * do we return all directories but the ones in the list.
     */
    private final boolean invert;

    /**
     * regex Patterns on filenames to exclude
     */
    private Pattern[] excludeRegexMatch = new Pattern[ 0 ];

    /**
     * regex Patterns on filenames to include
     */
    private Pattern[] includeRegexMatch = new Pattern[ 0 ];

    /**
     * ends with strings on filenames to exclude
     */
    private String[] excludeEndsWith = new String[ 0 ];

    /**
     * starts with strings on filenames to exclude
     */
    private String[] excludeStartsWith = new String[ 0 ];

    /**
     * ends with strings on filenames to include
     */
    private String[] includeEndsWith = new String[ 0 ];

    /**
     * starts with strings on filenames to include
     */
    private String[] includeStartsWith = new String[ 0 ];

    /**
     * constructor.  Fill in criteria later with setters
     */
    public StartAndEndsWithFilter()
        {
        this( false );
        }

    /**
     * constructor.  Fill in criteria later with setters
     *
     * @param invert if true, selects files not in the list instead
     */
    public StartAndEndsWithFilter( final boolean invert )
        {
        this.invert = invert;
        }

    /**
     * constructor. All include/exclude strings must be in lower case. Can use null for unused parms.
     *
     * @param includeStartsWith filename start strings, to include, case-insensitive, e.g. new String[ "prod"];
     * @param includeEndsWith   filename end strings, to include, case-insensitive, e.g. new String[".html", ".txt",
     *                          .bat" ];
     * @param includeRegexMatch filename regex matches to include
     * @param excludeStartsWith filename start  strings, to exclude, case-insensitive, e.g. new String["_" ];
     * @param excludeEndsWith   filename end strings, to exclude, case-insensitive, e.g. new String[".foot.html" ];
     * @param excludeRegexMatch filename regex matches to exclude
     */
    public StartAndEndsWithFilter( String[] includeStartsWith,
                                   String[] includeEndsWith,
                                   Pattern[] includeRegexMatch,
                                   String[] excludeStartsWith,
                                   String[] excludeEndsWith,
                                   Pattern[] excludeRegexMatch )
        {
        this( false,
                includeStartsWith,
                includeEndsWith,
                includeRegexMatch,
                excludeStartsWith,
                excludeEndsWith,
                excludeRegexMatch );
        }
    /**
     * TEST harness
     * @param args not used
     */
    /**
     * constructor. All include/exclude strings must be in lower case. Can use null for unused parms.
     * * @param invert if true, selects files not in the list instead
     *
     * @param includeStartsWith filename start strings, to include, case-insensitive, e.g. new String[ "prod"];
     * @param includeEndsWith   filename end strings, to include, case-insensitive, e.g. new String[".html", ".txt",
     *                          .bat" ];
     * @param includeRegexMatch filename regex matches to include
     * @param excludeStartsWith filename start  strings, to exclude, case-insensitive, e.g. new String["_" ];
     * @param excludeEndsWith   filename end strings, to exclude, case-insensitive, e.g. new String[".foot.html" ];
     * @param excludeRegexMatch filename regex matches to exclude
     */
    public StartAndEndsWithFilter( final boolean invert,
                                   final String[] includeStartsWith,
                                   final String[] includeEndsWith,
                                   final Pattern[] includeRegexMatch,
                                   final String[] excludeStartsWith,
                                   final String[] excludeEndsWith,
                                   final Pattern[] excludeRegexMatch )
        {
        this.invert = invert;
        setIncludeStartsWith( includeStartsWith );
        setIncludeEndsWith( includeEndsWith );
        setIncludeRegexMatch( includeRegexMatch );
        setExcludeStartsWith( excludeStartsWith );
        setExcludeEndsWith( excludeEndsWith );
        setExcludeRegexMatch( excludeRegexMatch );
        }

    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            // find all just the files listed, case-insensitive.
            StartAndEndsWithFilter f = new StartAndEndsWithFilter( new String[] { "x" },
                    new String[] { "t.html" },
                    null, new String[] { "to" },
                    new String[] { ".foot.html" }, null );
            f.setIncludeRegexMatch( Pattern.compile( "[09].+\\.html" ) );
            String[] filenames = new File( Build.MINDPROD_WEBROOT + "/jgloss" ).list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only Files that pass the 4 include/exclude criteria
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        final File f = new File( dir, name );
        if ( f.isDirectory() )
            {
            return false;
            }
        // must pass gauntlet of 3 include and 3 exclude conditions
        boolean include = false;
        final String lc = name.toLowerCase();
        for ( String start : includeEndsWith )
            {
            if ( lc.endsWith( start ) )
                {
                include = true;
                break;
                }
            }
        if ( !include )
            {
            for ( String start : includeStartsWith )
                {
                if ( lc.endsWith( start ) )
                    {
                    include = true;
                    break;
                    }
                }
            }
        if ( !include )
            {
            for ( Pattern pattern : includeRegexMatch )
                {
                if ( pattern.matcher( lc ).matches() )
                    {
                    include = true;
                    break;
                    }
                }
            }
        // must match at least one of the includes .
        if ( !include )
            {
            return invert;
            }
        for ( String end : excludeEndsWith )
            {
            if ( lc.endsWith( end ) )
                {
                // even one match means exclusion.
                return invert;
                }
            for ( String start : excludeStartsWith )
                {
                if ( lc.startsWith( start ) )
                    {
                    // even one match means exclusion.
                    return invert;
                    }
                }
            for ( Pattern pattern : excludeRegexMatch )
                {
                if ( pattern.matcher( lc ).matches() )
                    {
                    return invert;
                    }
                }
            }
        // passed all six gauntlets, keep the file.
        return !invert;
        }

    /**
     * set strings to exclude when filename ends with these strings.  Specify in lower case.
     *
     * @param excludeEndsWith end with strings
     */
    public void setExcludeEndsWith( String... excludeEndsWith )
        {
        if ( excludeEndsWith == null )
            {
            excludeEndsWith = new String[ 0 ];
            }
        for ( String s : excludeEndsWith )
            {
            if ( !s.toLowerCase().equals( s ) )
                {
                throw new IllegalArgumentException( "excludeEndsWith must be lower case" );
                }
            }
        this.excludeEndsWith = excludeEndsWith;
        }

    /**
     * set regex patterns to exclude when filename matches.
     *
     * @param excludeRegexMatch end with strings
     */
    public void setExcludeRegexMatch( Pattern... excludeRegexMatch )
        {
        if ( excludeRegexMatch == null )
            {
            excludeRegexMatch = new Pattern[ 0 ];
            }
        this.excludeRegexMatch = excludeRegexMatch;
        }

    /**
     * set strings exclude when filename starts with them. Specify in lower case.
     *
     * @param excludeStartsWith end with strings
     */
    public void setExcludeStartsWith( String... excludeStartsWith )
        {
        if ( excludeStartsWith == null )
            {
            excludeStartsWith = new String[ 0 ];
            }
        for ( String s : excludeStartsWith )
            {
            if ( !s.toLowerCase().equals( s ) )
                {
                throw new IllegalArgumentException( "excludeStartsWith must be lower case" );
                }
            }
        this.excludeStartsWith = excludeStartsWith;
        }

    /**
     * set strings include when filename ends with them.  Specify in lower case.
     *
     * @param includeEndsWith end with strings
     */
    public void setIncludeEndsWith( String... includeEndsWith )
        {
        if ( includeEndsWith == null )
            {
            includeEndsWith = new String[ 0 ];
            }
        for ( String s : includeEndsWith )
            {
            if ( !s.toLowerCase().equals( s ) )
                {
                throw new IllegalArgumentException( "includeEndsWith must be lower case" );
                }
            }
        this.includeEndsWith = includeEndsWith;
        }

    /**
     * set regex patterns to include when filename matches.
     *
     * @param includeRegexMatch end with strings
     */
    public void setIncludeRegexMatch( Pattern... includeRegexMatch )
        {
        if ( includeRegexMatch == null )
            {
            includeRegexMatch = new Pattern[ 0 ];
            }
        this.includeRegexMatch = includeRegexMatch;
        }

    /**
     * set strings include when filename starts with them. Specify in lower case.
     *
     * @param includeStartsWith end with strings
     */
    public void setIncludeStartsWith( String... includeStartsWith )
        {
        if ( includeStartsWith == null )
            {
            includeStartsWith = new String[ 0 ];
            }
        for ( String s : includeStartsWith )
            {
            if ( !s.toLowerCase().equals( s ) )
                {
                throw new IllegalArgumentException( "includeStartsWith must be lower case" );
                }
            }
        this.includeStartsWith = includeStartsWith;
        }
    }
