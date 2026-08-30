/*
 * [Chunkify.java]
 *
 * Summary: Iterator to find all delimited chunks in a big String.
 *
 * Copyright: (c) 2014-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2014-08-08 initial version
 */
package com.mindprod.common18;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator to find all delimited chunks in a big String.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2014-08-08 initial version
 * @since 2014-08-08
 */
public class Chunkify implements Iterable<String>
    {
    private static final boolean DEBUGGING = true;

    private final String big;

    private final String leadDelimiter;

    private final String trailDelimiter;

    /**
     * constructor
     *
     * @param big            big string to look for delimited strings in .
     * @param leadDelimiter  lead delimiter, e.g. <blockquote
     * @param trailDelimiter trail delimiter, e.g. </blockquote>
     */
    public Chunkify( final String big, final String leadDelimiter, final String trailDelimiter )
        {
        this.big = big;
        this.leadDelimiter = leadDelimiter;
        this.trailDelimiter = trailDelimiter;
        }

    /**
     * ChunkIterator factory
     */
    public Iterator<String> iterator()
        {
        return new ChunkIterator();
        }

    /**
     * inner class to implement Interator
     */
    private class ChunkIterator implements Iterator<String>
        {
        /**
         * tracks how far in big we have scanned.
         */
        int start;

        /**
         * constructor
         */
        private ChunkIterator()
            {
            start = 0;
            }

        /**
         * are there any more items?
         */
        public boolean hasNext()
            {
            start = big.indexOf( leadDelimiter );
            return start >= 0;
            }

        /**
         * Get the next delimited string
         *
         * @return next delimited string, including delimiters.
         * @throws NoSuchElementException
         */
        public String next() throws NoSuchElementException
            {
            assert big.substring( start, leadDelimiter.length() ).equals( leadDelimiter ) :
                    "next called without hasNext";
            int end = big.indexOf( trailDelimiter, start );
            if ( end < 0 )
                {
                throw new NoSuchElementException( "missing trail delimiter " + trailDelimiter );
                }
            end += trailDelimiter.length();
            // include both delimiters
            final String chunk = big.substring( start, end );
            start = end;
            return chunk;
            }

        /**
         * Remove the current object
         * Not implemented.
         *
         * @throws UnsupportedOperationException
         * @throws IllegalStateException
         */
        public void remove() throws UnsupportedOperationException, IllegalStateException
            {
            throw new UnsupportedOperationException( "remove" );
            }
        }
    }
