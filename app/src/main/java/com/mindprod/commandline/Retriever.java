/*
 * [Retriever.java]
 *
 * Summary: used to retrieve names of selected files from a temporary cache for Commandline.
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

import com.mindprod.common18.EIO;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

/**
 * used to retrieve names of selected files from a temporary cache for Commandline.
 * <p/>
 * You no longer need specify estimatedFiles.
 * Spit off into its own package.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.5 2009-02-28 CommandLine now uses considerably less RAM by caching the list of files on disk.
 * ou no longer need specify estimatedFiles.
 * pit off into its own package.
 * @noinspection WeakerAccess
 * @since 2003
 */
class Retriever
    {
    /**
     * defined by constructor
     */
    private final File cacheFile;

    /**
     * cache containing binary UTF-8 Strings
     */
    private DataInputStream cache;

    /**
     * constructor
     *
     * @param cacheFile cache file used by Saver
     */
    Retriever( File cacheFile )
        {
        this.cacheFile = cacheFile;
        }

    /**
     * close the cache file
     */
    void close()
        {
        try
            {
            cache.close();
            }
        catch ( IOException e )
            {
            throw new IllegalArgumentException( "Problem closing temporary cache of selected files." );
            }
        }

    /**
     * fetch the next line from the cache
     *
     * @return the next string from the cache.
     */
    String fetch()
        {
        try
            {
            return cache.readUTF();
            }
        catch ( IOException e )
            {
            throw new IllegalArgumentException( "Problem reading temporary cache of selected files." );
            }
        }

    /**
     * open the cache file for read
     */
    void open()
        {
        try
            {
            cache = EIO.getDataInputStream( cacheFile, 4 * 1024 );
            }
        catch ( IOException e )
            {
            throw new IllegalArgumentException( "Problem opening temporary cache of selected files." );
            }
        }
    }
