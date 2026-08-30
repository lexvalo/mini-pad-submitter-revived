/*
 * [Shuffle.java]
 *
 * Summary: Shuffles an int[], much like Collections.shuffle.
 *
 * Copyright: (c) 2007-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2007-10-03 Created with IntelliJ IDEA.
 */
package com.mindprod.common18;

import java.util.Random;

import static java.lang.System.*;

/**
 * Shuffles an int[], much like Collections.shuffle.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2007-10-03 Created with IntelliJ IDEA.
 * @since 2007-10-03
 */
public class Shuffle
    {
    /**
     * true if you want the debugging main harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * default source of randomness for shuffling.
     */
    private static Random wheel;

    /**
     * test driver demonstrate use of shuffle.
     *
     * @param args not used.
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            /**
             * how many cards in a deck that we shuffle.
             */
            final int DECKSIZE = 52;
            int[] deck = new int[ DECKSIZE ];
            for ( int i = 0; i < DECKSIZE; i++ )
                {
                // assign each card in deck a number 0..51
                deck[ i ] = i;
                }
            out.println( "\nbefore shuffle" );
            for ( int i = 0; i < DECKSIZE; i++ )
                {
                out.print( " " );
                out.print( deck[ i ] );
                }
            shuffle( deck );
            out.println( "\nafter shuffle" );
            for ( int i = 0; i < DECKSIZE; i++ )
                {
                out.print( " " );
                out.print( deck[ i ] );
                }
            }
        }

    /**
     * Shuffle an array when Collections.shuffle is not available or where you need high quality randomness. Works much
     * like Collections.shuffle. Randomly permutes the specified list using a default source of randomness. All
     * permutations occur with approximately equal likelihood.<p>
     * <p/>
     * The hedge "approximately" is used in the foregoing description because default source of randomness is only
     * approximately an unbiased source of independently chosen bits. If it were a perfect source of randomly chosen
     * bits, then the algorithm would choose permutations with perfect uniformity.<p>
     * <p/>
     * This implementation traverses the list backwards, from the last element up to the second, repeatedly swapping a
     * randomly selected element into the "current position".  Elements are randomly selected from the portion of the
     * list that runs from the first element to the current position, inclusive.<p>
     *
     * @param toShuffle the array to be shuffled.
     */
    public static void shuffle( Object[] toShuffle )
        {
        // wheel is initialised only once, and only if needed.
        if ( wheel == null )
            {
            wheel = new Random();
            }
        shuffle( toShuffle, wheel );
        }

    /**
     * This method uses the same technique as Collections.shuffle, but is somewhat simpler with direct array access.
     * Randomly permute the specified list using the specified source of randomness.  All permutations occur with equal
     * likelihood assuming that the source of randomness is fair.<p>
     * <p/>
     * This implementation traverses the array backwards, from the last element up to the second, repeatedly swapping a
     * randomly selected element into the "current position".  Elements are randomly selected from the portion of the
     * list that runs from the first element to the current position, inclusive.<p>
     *
     * @param toShuffle the array to be shuffled.
     * @param wheel     the source of randomness to use to shuffle the list.
     */
    public static void shuffle( int[] toShuffle, Random wheel )
        {
        for ( int i = toShuffle.length; i > 1; i-- )
            {
            // swap elt i-1 and a random elt 0..i-1, last with any previous, working down
            final int temp = toShuffle[ i - 1 ];
            final int otherSlot = wheel.nextInt( i );
            toShuffle[ i - 1 ] = toShuffle[ otherSlot ];
            toShuffle[ otherSlot ] = temp;
            }
        }

    /**
     * This method uses the same technique as Collections.shuffle, but is somewhat simpler with direct array access.
     * Randomly permute the specified list using the specified source of randomness.  All permutations occur with equal
     * likelihood assuming that the source of randomness is fair.<p>
     * <p/>
     * This implementation traverses the array backwards, from the last element up to the second, repeatedly swapping a
     * randomly selected element into the "current position".  Elements are randomly selected from the portion of the
     * list that runs from the first element to the current position, inclusive.<p>
     *
     * @param toShuffle the array to be shuffled.
     * @param wheel     the source of randomness to use to shuffle the list.
     */
    public static void shuffle( String[] toShuffle, Random wheel )
        {
        for ( int i = toShuffle.length; i > 1; i-- )
            {
            // swap elt i-1 and a random elt 0..i-1, last with any previous, working down
            final String temp = toShuffle[ i - 1 ];
            final int otherSlot = wheel.nextInt( i );
            toShuffle[ i - 1 ] = toShuffle[ otherSlot ];
            toShuffle[ otherSlot ] = temp;
            }
        }

    /**
     * Shuffle an array when Collections.shuffle is not available or where you need high quality randomness. Works much
     * like Collections.shuffle. Randomly permutes the specified list using a default source of randomness. All
     * permutations occur with approximately equal likelihood.<p>
     * <p/>
     * The hedge "approximately" is used in the foregoing description because default source of randomness is only
     * approximately an unbiased source of independently chosen bits. If it were a perfect source of randomly chosen
     * bits, then the algorithm would choose permutations with perfect uniformity.<p>
     * <p/>
     * This implementation traverses the list backwards, from the last element up to the second, repeatedly swapping a
     * randomly selected element into the "current position".  Elements are randomly selected from the portion of the
     * list that runs from the first element to the current position, inclusive.<p>
     *
     * @param toShuffle the array to be shuffled.
     */
    public static void shuffle( String[] toShuffle )
        {
        // wheel is initialised only once, and only if needed.
        if ( wheel == null )
            {
            wheel = new Random();
            }
        shuffle( toShuffle, wheel );
        }

    /**
     * This method uses the same technique as Collections.shuffle, but is somewhat simpler with direct array access.
     * Randomly permute the specified list using the specified source of randomness.  All permutations occur with equal
     * likelihood assuming that the source of randomness is fair.<p>
     * <p/>
     * This implementation traverses the array backwards, from the last element up to the second, repeatedly swapping a
     * randomly selected element into the "current position".  Elements are randomly selected from the portion of the
     * list that runs from the first element to the current position, inclusive.<p>
     *
     * @param toShuffle the array to be shuffled.
     * @param wheel     the source of randomness to use to shuffle the list.
     */
    public static void shuffle( Object[] toShuffle, Random wheel )
        {
        for ( int i = toShuffle.length; i > 1; i-- )
            {
            // swap elt i-1 and a random elt 0..i-1, last with any previous, working down
            final Object temp = toShuffle[ i - 1 ];
            final int otherSlot = wheel.nextInt( i );
            toShuffle[ i - 1 ] = toShuffle[ otherSlot ];
            toShuffle[ otherSlot ] = temp;
            }
        }

    /**
     * Shuffle an array when Collections.shuffle is not available or where you need high quality randomness. Works much
     * like Collections.shuffle. Randomly permutes the specified list using a default source of randomness. All
     * permutations occur with approximately equal likelihood.<p>
     * <p/>
     * The hedge "approximately" is used in the foregoing description because default source of randomness is only
     * approximately an unbiased source of independently chosen bits. If it were a perfect source of randomly chosen
     * bits, then the algorithm would choose permutations with perfect uniformity.<p>
     * <p/>
     * This implementation traverses the list backwards, from the last element up to the second, repeatedly swapping a
     * randomly selected element into the "current position".  Elements are randomly selected from the portion of the
     * list that runs from the first element to the current position, inclusive.<p>
     *
     * @param toShuffle the array to be shuffled.
     */
    public static void shuffle( int[] toShuffle )
        {
        // wheel is initialised only once, and only if needed.
        if ( wheel == null )
            {
            wheel = new Random();
            }
        shuffle( toShuffle, wheel );
        }
    }
