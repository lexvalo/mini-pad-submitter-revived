/*
 * [NoFootFilter.java]
 *
 * Summary: Select a file for processing with a .html extension but not .foot.html.
 *
 * Copyright: (c) 2005-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.7+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  2.5 2009-05-09 Initial version
 *  2.6 2009-05-09 add StartAndEndWithFilter and NoFootFilter
 *  2.7 2010-11-22 add optional invert parameter on a number of the filters.
 */
package com.mindprod.filter;
/**
 * Select a file for processing with a .html extension but not .foot.html.
 * <p/>
 * Does not work with directories, see DirListFilter. see also
 * FileListFilter and ClamFilter. Rejects all directories.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2009-05-09
 */
public final class NoFootFilter extends StartAndEndsWithFilter
        // note Sun's spelling, note FileNameFilter
    {
    public NoFootFilter()
        {
        super( null, new String[] { ".html" }, null, null, new String[] { ".foot.html" }, null );
        }
    }
