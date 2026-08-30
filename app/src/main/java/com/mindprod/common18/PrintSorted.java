/*
 * [PrintSorted.java]
 *
 * Summary: Accumulates a list of lines, sorts and prints them.
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
 *  1.0 2014-12-14 initial version
 */
package com.mindprod.common18;

// import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.System.*;

/**
 * Accumulates a list of lines, sorts and prints them.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2014-12-14 initial version
 * @since 2014-12-14
 */
public class PrintSorted
    {
    /**
     * Accumulates a list of lines, sorts and prints them.
     * <p/>
     * simple use:
     * <p/>
     * PrintSort ps = new PrintSorted( 2 , System.out ;
     * ps.add ( "08", "apple");
     * ps.add ( "03", "pear");
     * ps.print();
     * prints:
     * pear
     * apple
     */
    private final ArrayList<SortablePair> pairsToSort;

    /**
     * where to print the output
     */
    private PrintStream stream;

    /**
     * constructor
     *
     * @param estimatedSize est lines to print
     *                      can change output with print (sink)
     */
    public PrintSorted( int estimatedSize )
        {
        pairsToSort = new ArrayList<>( estimatedSize );
        }

    /**
     * empty the list of lines to print, preparing the PrintSorted object for reuse.
     */
    void clear()
        {
        pairsToSort.clear();
        }

    /**
     * add a line to print later
     *
     * @param key    separate key allows you to sort on a non-displayed field.  Often it is a substring of the line.
     * @param toSort line to be sorted then printed.
     */
    public void add( String key, String toSort )
        {
        pairsToSort.add( new SortablePair( key, toSort ) );
        }

    /**
     * sort and print the list on default java.lang.system.out
     */
    public void print()
        {
        print( out );
        }

    /**
     * sort and print the list.
     *
     * @param sink e.g. out, err where to print the lines
     */
    public void print( PrintStream sink )
        {
        if ( sink == null )
            {
            sink = out;
            }
        Collections.sort( pairsToSort ); // default Compare
        for ( SortablePair pair : pairsToSort )
            {
            sink.println( pair.getValue() );
            }
        }

    /**
     * how many lines are pending to be printed
     */
    public int size()
        {
        return pairsToSort.size();
        }

    /**
     * inner class key/value pairs we sort
     */
    static class SortablePair implements Comparable<SortablePair>
        {
        /**
         * string we sort by
         */
        private final String key;

        /**
         * payload for sort
         */
        private final String value;

        /**
         * constructor
         */
        SortablePair( final String key, final String value )
            {
            this.key = key;
            this.value = value;
            }

        /**
         * value getter
         */
        String getValue()
            {
            return value;
            }

        /**
         * Sort pairs by key.
         * Defines default the sort order for PairToSort Objects.
         * Compare this PairToSort with another PairToSort
         * Compares value case sensitively.
         * Informally, returns (this-other) or +ve if this sorts after other.
         * <p/>
         * Summary: Sort pairs by key.
         * <p/>
         * Requires: JDK 1.8+.
         * <p/>
         * Java code generated with: Canadian Mind Products ComparatorCutter.
         * <p/>
         * Version History:
         * 1.0 2014-12-14 - initial release
         *
         * @param other other PairToSort to compare with this one
         *
         * @return +ve if this&gt;other, 0 if this==other, -ve if this&lt;other
         */
        public final int compareTo( /** @NotNull **/ SortablePair other )
            {
            int result = this.key.compareTo( other.key );
            if ( result != 0 )
                {
                return result;
                }
            else
                {
                return this.value.compareTo( other.value );
                }
            }
        }
    }
