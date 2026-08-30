/*
 * [ExtensionListFilter.java]
 *
 * Summary: Select a file based on it matches one of list of extensions, or a single extension.
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Select a file based on it matches one of list of extensions, or a single extension.
 * <p/>
 * Does not work with directories, see DirListFilter. see also
 * FileListFilter and ClamFilter. Rejects all directories.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.7 2010-11-22 add optional invert parameter on a number of the filters.
 * @since 2005-06-30
 */
public final class ExtensionListFilter implements FilenameFilter// note Sun's
        // spelling, not
        // FileNameFilter
    {
    public static final String[] COMMON_BAT_EXTENSIONS = { "bat", "batfrag", "btm", "btmfrag" };

    public static final String[] COMMON_JAVA_SOURCE_EXTENSIONS = { "java", "javafrag" };

    public static final String[] COMMON_HTML_EXTENSIONS = { "html", "htmlfrag", "htm", "htmfrag" };

    public static final String[] COMMON_AMPER_EXTENSIONS = { "html", "htmlfrag", "htm", "htmfrag", "csv", "csvfrag" };

    public static final String[] BASIC_IMAGE_EXTENSIONS = { "png", "jpg", "jpeg", "gif" };

    /**
     * list of ext file extensions safet to do  text processing on.
     * duplicated in Blout.C
     */
    public static final String[] COMMON_TEXT_EXTENSIONS = {
            "ans",
            "asm",
            "bat",
            "batfrag",
            "btm",
            "btmfrag",
            "c",
            "cfrag",
            "cmd",
            "cpp",
            "cppfrag",
            "css",
            "cssfrag",
            "csv",
            "csvfrag",
            "ctl",
            "doc",
            "dtd",
            "dtdfrag",
            "e",
            "h",
            "hfrag",
            "hpp",
            "hppfrag",
            "htm",
            "htmfrag",
            "html",
            "htmlfrag",
            "http",
            "httpfrag",
            "idx",
            "ih",
            "ini",
            "java",
            "javafrag",
            "jnlp",
            "jnlpfrag",
            "jsp",
            "jspfrag",
            "list",
            "log",
            "look",
            "lst",
            "mac",
            "mf",
            "mffrag",
            "mft",
            "mftfrag",
            "pas",
            "pml",
            "policy",
            "pom",
            "pomflag",
            "prn",
            "properties",
            "ps",
            "raw",
            "rh",
            "sf",
            "sffrag",
            "sh",
            "site",
            "sql",
            "sqlfrag",
            "tab",
            "text",
            "txt",
            "use",
            "wiki",
            "xml",
            "xmlfrag",
            "xsd",
            "xsdfrag"
    };

    /**
     * list of binary files. Do no to text processing on them.
     * duplicated in Blout.C
     */
    public static final String[] COMMON_BINARY_EXTENSIONS = {
            "7z",
            "blk",
            "bmp",
            "bod",
            "bz",
            "bz2",
            "bzip2",
            "class",
            "com",
            "dat",
            "dll",
            "doc",
            "exe",
            "gif",
            "gz",
            "ico",
            "jar",
            "jpeg",
            "jpg",
            "mbx",
            "obj",
            "p7b",
            "png",
            "rar",
            "seq",
            "ser",
            "so",
            "sym",
            "tbz",
            "tbz2",
            "toc",
            "usg",
            "zip",
            "zipx" };

    /**
     * true if debugging. Enables debugging harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * Collection of extension names describing directories you do want, in lower case, without the leading dot.
     */
    private final HashSet<String> want;

    /**
     * do we return all directories but the ones in the list.
     */
    private final boolean invert;

    /**
     * constructor
     *
     * @param want Array of simple file extension names, case-insensitive, e.g. new String["html", "txt",
     *             "bat" ] without  lead "*."
     */
    public ExtensionListFilter( final String... want )
        {
        this( false, want );
        }

    /**
     * constructor
     *
     * @param invert if true, selects files not in the list instead.
     * @param want   Array of simple file extension names, case-insensitive, e.g. new String["html", "txt",
     *               "bat" ] without  lead "*."
     */
    public ExtensionListFilter( final boolean invert, final String... want )
        {
        // we need them in lower case. We don't want to disturb the caller's
        // array.
        // we don't lower case the caller's array.
        this.invert = invert;
        this.want = new HashSet<>( Math.max( ( int ) ( want.length / .75f ) + 1, 16 ) );
        for ( String aWant : want )
            {
            if ( aWant.startsWith( "." ) )
                {
                throw new IllegalArgumentException(
                        "ExtensionListFilter extensions should not include the leading dot"
                        + aWant
                );
                }
            this.want.add( aWant.toLowerCase() );
            }
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
            // find all just the files listed, case-insensitive.
            FilenameFilter f = new ExtensionListFilter( "java", "class" );
            String[] filenames = new File( "C:\\com\\mindprod\\filter" )
                    .list( f );
            for ( String filename : filenames )
                {
                out.println( filename );
                }
            }
        }

    /**
     * Select only Files with an extension in our want list
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file
     *
     * @return true if and only if the name should be included in the file list; false otherwise.
     */
    public boolean accept( File dir, String name )
        {
        if ( name == null )
            {
            return false;
            }
        final File f = new File( dir, name );
        if ( f.isDirectory() )
            {
            return false;
            }
        // strip out the extension.
        int place = name.lastIndexOf( '.' );
        if ( place < 0 )
            {
            return invert;
            }
        // get just stuff past the dot to the end
        String ext = name.substring( place + 1 );
        return invert ^ want.contains( ext.toLowerCase() );
        }
    } // end ExtensionListFilter
