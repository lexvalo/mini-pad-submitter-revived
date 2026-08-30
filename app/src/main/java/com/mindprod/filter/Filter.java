/*
 * [Filter.java]
 *
 * Summary: Collection of FilenameFilters.
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
 *  1.4 2005-06-18 cleaner bat files
 *  1.5 2005-07-07 displays stats, with
 *                 recursive indenting. converted to Java 1.5 syntax
 *  1.6 2006-01-01
 *  1.7 2006-01-01
 *  1.8 2006-03-13 rewrite with for:each JDK 1.5
 *  1.9 2007-01-01
 *  2.0 2007-01-01
 *  2.1 2007-01-01
 *  2.2 2007-06-29 Commandline is Iterable, isQuiet, now iterate CommandLine directly.
 *  2.3 2007-08-27 add JunkFilter
 *  2.4 2009-02-27 send output to err instead of out so will not contaminate batch data to out.
 *  2.5 2009-02-28 CommandLine split off in its own package
 *  2.6 2009-05-10 add StartAndEndWithFilter and NoFootFilter
 *  2.7 2010-11-22 add optional invert parameter on a number of the filters.
 */
package com.mindprod.filter;

import static java.lang.System.*;

/**
 * Collection of FilenameFilters.
 * <p/>
 * Dummy Main. Potentially could be used for experiments. Or demonstration of method use.
 * e.g.
 * AllButFootDirectoriesFilter
 * AllButSVNDirectoriesFilter
 * AllDirectoriesFilter
 * AllFilesFilter
 * AvoidJunkFilter
 * ClamFilter
 * DirListFilter
 * EndsWithFilter
 * EverythingFilter
 * ExtensionListFilter
 * FileLengthFilter
 * FileListFilter
 * FilenameLengthFilter
 * JunkFileFilter
 * MultiFilter
 * NoFootFilter
 * RecentFilter
 * RegexFilter
 * StartsAndEndsWithFilter  (regex)
 * StartsWithFilter
 * WildcardFilter
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2003
 */
public final class Filter
    {
    private static final int FIRST_COPYRIGHT_YEAR = 2003;

    /**
     * undisplayed copyright notice
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2003-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * when package was released.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2010-11-22";

    /**
     * name of package.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String TITLE_STRING = "Filters";

    /**
     * version of package.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String VERSION_STRING = "2.7";

    /**
     * dummy constructor to stop instantiation
     */
    private Filter()
        {
        }

    /**
     * TEST harness
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        out.println( "Filter should not be run standalone." );
        }
    }
