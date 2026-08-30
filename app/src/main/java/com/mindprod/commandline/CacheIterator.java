/*
 * [CacheIterator.java]
 *
 * Summary: Iterator to retrieve files as File Objects from the cache.
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
 *  2.5 2009-02-28 CommandLine now uses considerably less RAM by caching the list of files on disk.
 *                 ou no longer need specify estimatedFiles.
 *                 pit off into its own package.
 */
package com.mindprod.commandline;

import java.io.File;
import java.util.Iterator;

/**
 * Iterator to retrieve files as File Objects from the cache.
 * <p/>
 * You no longer need specify estimatedFiles.
 * Spit off into its own package.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.5 2009-02-28 CommandLine now uses considerably less RAM by caching the list of files on disk.
 * ou no longer need specify estimatedFiles.
 * pit off into its own package.
 * @since 2009-02-28
 */
class CacheIterator implements Iterator<File>
    {
    private final Retriever retriever;

    private final int size;

    private int where;

    /**
     * constructor for Iterator over the cache
     *
     * @param cacheFile File of the cache from Saver.
     * @param size      how many files are in the cache.
     */
    CacheIterator( File cacheFile, int size )
        {
        this.size = size;
        retriever = new Retriever( cacheFile );
        retriever.open();
        }

    /**
     * standard Iterator hasNext()
     *
     * @return true if  cache has at least one more unprocessed file
     */
    public boolean hasNext()
        {
        if ( where < size )
            {
            return true;
            }
        else
            {
            retriever.close();
            return false;
            }
        }

    /**
     * standard Iterator next()
     *
     * @return net File as a File object, not a String.
     */
    public File next()
        {
        where++;
        return new File( retriever.fetch() );
        }

    /**
     * standard Iterator remove(), not implemented
     */
    public void remove()
        {
        throw new UnsupportedOperationException();
        }
    }
