/*
 * [InOrder.java]
 *
 * Summary: Are arrays/collections already in order?.
 *
 * Copyright: (c) 2011-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2011-11-07 initial version
 *  1.1 2011-12-03 improve efficiency of List version for Linkedlist or other List that does not index quickly.
 *                 The problem was pointed out by Patricia Shanahan.
 */
package com.mindprod.common18;

import java.util.Comparator;
import java.util.List;

/**
 * Are arrays/collections already in order?.
 * <p/>
 * Generics for these methods were cannibalised from Arrays.sort and Collections.sort.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2011-12-03 improve efficiency of List version for Linkedlist or other List that does not index quickly.
 * The problem was pointed out by Patricia Shanahan.
 * @since 2011-11-07
 */
public final class InOrder
    {
    /**
     * Is this array already in order according to its Comparable interface?
     * Generics and arrays don't get along well.  We get unchecked warnings here.
     * I don't know how to fix them. It may not be possible.
     *
     * @param array array of Comparable objects.
     *
     * @return true if array already in order.
     */
    @SuppressWarnings( { "unchecked" } )
    public static boolean inOrder( Comparable[] array )
        {
        for ( int i = 1; i < array.length; i++ )
            {
            if ( array[ i ].compareTo( array[ i - 1 ] ) < 0 )
                {
                return false;
                }
            }
        return true;
        }

    /**
     * Is this List already in order according to its Comparable interface?
     *
     * @param list List of Comparable objects, e.g. ArrayList or LinkedList
     *
     * @return true if array already in order.
     */
    public static <T extends Comparable<? super T>> boolean inOrder( List<T> list )
        {
        T prev = null;
        for ( T item : list )
            {
            if ( prev != null && item.compareTo( prev ) < 0 )
                {
                return false;
                }
            else
                {
                prev = item;
                }
            }
        return true;
        }

    /**
     * Is this Array already in order according to a given Comparator?
     *
     * @param array      Array of Objects
     * @param comparator Comparator for objects in the array.
     *
     * @return true if Array already in order.
     */
    public static <T> boolean inOrder( T[] array, Comparator<? super T> comparator )
        {
        for ( int i = 1; i < array.length; i++ )
            {
            if ( comparator.compare( array[ i ], array[ i - 1 ] ) < 0 )
                {
                return false;
                }
            }
        return true;
        }

    /**
     * Is this List already in order according to a given Comparator?
     *
     * @param list       List of objects. , e.g. ArrayList, LinkedList
     * @param comparator Comparator for objects in the list.
     *
     * @return true if array already in order.
     */
    public static <T> boolean inOrder( List<T> list, Comparator<? super T> comparator )
        {
        T prev = null;
        for ( T item : list )
            {
            if ( prev != null && comparator.compare( item, prev ) < 0 )
                {
                return false;
                }
            else
                {
                prev = item;
                }
            }
        return true;
        }
    }
