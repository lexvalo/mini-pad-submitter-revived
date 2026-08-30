/*
 * [Saver.java]
 *
 * Summary: used to save names of selected files to a temporary cache for CommandLine.
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
 *  2.6 2011-03-05 add size tracking
 */
package com.mindprod.commandline;

import com.mindprod.hunkio.HunkIO;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * used to save names of selected files to a temporary cache for CommandLine.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 2.5 2009-02-28 CommandLine now uses considerably less RAM by caching the list of files on disk.
 * ou no longer need specify estimatedFiles.
 * pit off into its own package.
 * @since 2009-02-28
 */
class Saver
    {
    /**
     * write binary UTF-8 Strings
     */
    private DataOutputStream cache;

    /**
     * close the cache
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
     * create and open the cache to save selected file names to
     *
     * @return File where names will be saved
     */
    File open()
        {
        try
            {
            final File cacheFile = HunkIO.createTempFile( "selectedfiles", ".temp", null );
            // Arrange to have file eventually deleted.
            // We can't delete it on Retriever.close, since we might iterate the files again.
            cacheFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream( cacheFile, false );
            BufferedOutputStream bos = new BufferedOutputStream( fos, 4 * 1024 /* buffsize in bytes */ );
            cache = new DataOutputStream( bos );
            return cacheFile;
            }
        catch ( IOException e )
            {
            throw new IllegalArgumentException( "Problem creating temporary cache of selected files." );
            }
        }

    /**
     * save one line in the cache
     *
     * @param line the name of the file to save
     */
    void save( String line )
        {
        try
            {
            cache.writeUTF( line );
            }
        catch ( IOException e )
            {
            throw new IllegalArgumentException( "Problem writing temporary cache of selected files." );
            }
        }
    }
