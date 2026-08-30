/*
 * [AvoidJunkFilter.java]
 *
 * Summary: Filter to permit only non-junk files.
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
 *  2.6 2009-05-09 add StartAndEndWithFilter and NoFootFilter
 *  2.7 2010-11-22 add optional invert parameter on a number of the filters.
 */
package com.mindprod.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter to permit only non-junk files.
 * <p/>
 * accept only non-junk files.
 * Can define junk files by name, extension, starts with or endsWith lists, the same as you would JunkFileFilter.
 * Returns false for all directories.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @see JunkFileFilter
 * @since 2009-05-15
 */
public final class AvoidJunkFilter extends JunkFileFilter implements FilenameFilter
    {
    /**
     * Defines which filenames and extensions are considered junk, to be deleted.
     */
    public AvoidJunkFilter()
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
        return !( dir == null || name == null || new File( dir, name ).isDirectory() ) && !super.accept( dir, name );
        }
    }
