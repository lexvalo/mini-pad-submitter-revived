/*
 * [Limiter.java]
 *
 * Summary: Simple methods to corral values into bounds.
 *
 * Copyright: (c) 1997-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.6 2005-07-14
 */
package com.mindprod.common18;

/**
 * Simple methods to corral values into bounds.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.6 2005-07-14
 * @since 1997
 */
public final class Limiter
    {
    /**
     * true if you want extra debugging output and test code
     */
    static final boolean DEBUGGING = false;

    /**
     * Limiter contains only static methods.
     */
    private Limiter()
        {
        }

    /**
     * Caps the max value, ensuring it does not go too high. alias for min.
     *
     * @param v    the value
     * @param high the high bound above which v cannot go.
     *
     * @return the lesser of v and high.
     * @see Math#min(int, int)
     */
    public static int cap( int v, int high )
        {
        if ( v > high )
            {
            return high;
            }
        else
            {
            return v;
            }
        } // end cap

    /**
     * Corrals a value back into safe bounds.
     *
     * @param v    the value
     * @param low  the low bound below which v cannot go.
     * @param high the high bound above which v cannot go.
     *
     * @return low if v < low, high if v > high, but normally just v.
     */
    public static int corral( int v, int low, int high )
        {
        if ( v < low )
            {
            return low;
            }
        else if ( v > high )
            {
            return high;
            }
        else
            {
            return v;
            }
        } // end corral

    /**
     * Ensures a value does not go too low. alias for max
     *
     * @param v   the value
     * @param low the low bound below which v cannot go.
     *
     * @return the greater of v and low.
     * @see Math#max(int, int)
     */
    public static int hem( int v, int low )
        {
        if ( v < low )
            {
            return low;
            }
        else
            {
            return v;
            }
        } // end hem
    }
