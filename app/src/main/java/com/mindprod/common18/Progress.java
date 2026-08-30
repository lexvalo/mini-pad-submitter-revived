/*
 * [Progress.java]
 *
 * Summary: Simple text-based progress, just counts up on the same line.
 *
 * Copyright: (c) 2012-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2012-12-12 initial version
 *  1.1 2013-01-11 add optional modulus parm. Make thread safe.
 */
package com.mindprod.common18;

import java.io.PrintStream;

/**
 * Simple text-based progress, just counts up on the same line.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2013-01-11 add optional modulus parm. Make thread safe.
 * @see Twirler
 * @since 2012-12-12
 */
public final class Progress
    {
    /**
     * final value of counter
     */
    private final int end;

    /**
     * modulus, just display even muliples of this changes
     */
    private final int modulus;

    /**
     * counter to display
     */
    private int counter = 0;

    /**
     * constructor when end value is not known. Just displays counter.
     */
    public Progress()
        {
        this.end = -1;
        this.modulus = -1;
        }

    /**
     * constructor, displays counter and final value.
     *
     * @param end last value (if wrong, keeps going)
     */
    public Progress( int end )
        {
        this.end = end;
        this.modulus = -1;
        }

    /**
     * constructor, displays counter and final value, changing the display only every modulus counts.
     *
     * @param end last value (if wrong, keeps going)
     */
    public Progress( int end, int modulus )
        {
        this.end = end;
        this.modulus = modulus;
        }

    /**
     * increment and display the counter.  No longer overlap. Sometimes lost text.
     *
     * @param o usually System.out or System.err, where to display counter.
     */
    public synchronized void progress( PrintStream o )
        {
        counter++;
        if ( modulus < 2 | counter % modulus == 0 )
            {
            if ( end >= 0 )
                {
                // o.print( counter + " of " + end + "\r" );
                o.println( counter + " of " + end );
                }
            else
                {
                //   o.print( counter + "\r" );
                o.println( counter );
                }
            o.flush();
            }
        }
    }
