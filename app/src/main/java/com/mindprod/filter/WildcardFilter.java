/*
 * [WildcardFilter.java]
 *
 * Summary: filter out all but files that match a given wildcard Pattern.
 *
 * Copyright: (c) 2009-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
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
 */
package com.mindprod.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import static java.lang.System.*;

/**
 * filter out all but files that match a given wildcard Pattern.
 * <p/>
 * Note the name is WildcardFilter not WildCardFilter.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.5 2009-02-28 CommandLine split off in its own package.
 * @since 2009-04-08
 */
public final class WildcardFilter implements FilenameFilter
    {
    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * wildcard as a regex
     */
    private final Pattern pattern;

    /**
     * constructor
     *
     * @param wildcard pattern of files you want e.g. some*.txt debug?.c*
     */
    @SuppressWarnings( { "WeakerAccess" } )
    public WildcardFilter( String wildcard )
        {
        pattern = Pattern.compile( wildcardAsRegex( wildcard ), Pattern.CASE_INSENSITIVE );
        if ( DEBUGGING )
            {
            out.println( pattern.toString() );
            }
        }

    /**
     * convert a wild card containing * and ? to the equivalent regex
     * Code duplicated in com.mindprod.example.TestWildcard
     *
     * @param wildcard wildcard string describing a file.
     *
     * @return regex string that could be fed to Pattern.comile
     */
    private static String wildcardAsRegex( String wildcard )
        {
        StringBuilder sb = new StringBuilder( wildcard.length() * 110 / 100 );
        for ( int i = 0; i < wildcard.length(); i++ )
            {
            final char c = wildcard.charAt( i );
            switch ( c )
                {
                case '*':
                    sb.append( ".*?" );
                    break;
                case '?':
                    sb.append( "." );
                    break;
                // chars that have magic regex meaning. They need quoting to be taken literally
                case '$':
                case '(':
                case ')':
                case '+':
                case '-':
                case '.':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '{':
                case '|':
                case '}':
                    sb.append( '\\' );
                    sb.append( c );
                    break;
                default:
                    sb.append( c );
                    break;
                }
            }
        return sb.toString();
        }

    /**
     * Test Harness
     *
     * @param args wildcar, best enclosed in quotes to prevent other levels tampering with it..
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            if ( args.length != 1 )
                {
                err.println( "Must supply one wildcard on the command line in quotes." );
                err.println( "Command line was effectively:" );
                for ( String arg : args )
                    {
                    err.println( "[" + arg + "]" );
                    }
                System.exit( 1 );
                }
            String wildcard = args[ 0 ];
            out.println( "seeking: \""
                         + wildcard
                         + "\"" );
            final File dir = new File( "." );
            String[] files = dir.list( new WildcardFilter( wildcard ) );
            for ( String file : files )
                {
                out.println( file );
                }
            }
        }

    /**
     * Select only files that match the wildcard/regex Pattern.
     *
     * @param dir  the directory in which the file was found.
     * @param name the simple name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        final File f = new File( dir, name );
        return f.isFile() && pattern.matcher( name ).matches();
        }
    } //
