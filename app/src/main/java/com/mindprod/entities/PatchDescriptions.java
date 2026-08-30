/*
 * [PatchDescriptions.java]
 *
 * Summary: One shot to patch missing Entity descriptions from Unicode descriptions.
 *
 * Copyright: (c) 2004-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2010-02-24 initial version
 */
package com.mindprod.entities;

import com.mindprod.common18.EIO;
import com.mindprod.csv.CSVReader;
import com.mindprod.csv.CSVWriter;
import com.mindprod.hunkio.HunkIO;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * One shot to patch missing Entity descriptions from Unicode descriptions.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2010-02-24 initial version
 * @since 2010-02-24
 */
public final class PatchDescriptions
    {
    /**
     * patch entitityfacts.csv with descriptions from allunicode.csv
     * presume current dir is E:\com\mindprod\entities
     *
     * @param args not used.
     *
     * @throws java.io.IOException if problems acessing files.
     */
    public static void main( String[] args ) throws IOException
        {
        final HashMap<Integer, String> descs = new HashMap<>( 60000 );
        // get  list of all Unicode descs indexed by code
        final CSVReader u = new CSVReader( new BufferedReader( new FileReader( "allunicode.csv" ) ) );
        try
            {
            while ( true )
                {
                final String unicodeHex = u.get();
                final String desc = u.get();
                descs.put( Integer.parseInt( unicodeHex, 16 ), desc );
                u.skipToNextLine();
                }
            }
        catch ( EOFException e )
            {
            u.close();
            }
        final File fileBeingProcessed = new File( "entityfacts.csv" );
        final CSVReader r = new CSVReader( new BufferedReader( new FileReader( fileBeingProcessed ) ) );
        final File tempFile = HunkIO.createTempFile( "temp_", ".tmp", fileBeingProcessed );
        // writer, quoteLevel, separatorChar, quoteChar, commentChar, trim
        final CSVWriter w = new CSVWriter( EIO.getPrintWriter( tempFile, 64 * 1024, EIO.UTF8 ),
                0
                /* minimal  */,
                ',',
                '\"',
                '#',
                true );
        try
            {
            while ( true )
                {
                final String categories = r.get();
                final String hexString = r.get();
                // chop off lead 0x
                final int theCharNumber = Integer.parseInt( hexString.substring( 2 ), 16 );
                final String entity = r.get();
                String description = r.get();
                final String notes = r.get();
                if ( description.length() == 0 )
                    {
                    description = descs.get( theCharNumber );
                    }
                r.skipToNextLine();
                w.put( categories );
                w.put( hexString );
                w.put( entity );
                w.put( description );
                w.put( notes );
                w.nl();
                }
            }
        catch ( EOFException e )
            {
            r.close();
            w.close();
            HunkIO.deleteAndRename( tempFile, fileBeingProcessed );
            }
        }
    }
