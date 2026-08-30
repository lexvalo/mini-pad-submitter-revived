/*
 * [Twirler.java]
 *
 * Summary: Displays twirling activity indicator on the console.
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
 *  1.0 2011-06-09 initial version.
 */
package com.mindprod.common18;

import java.io.PrintStream;

/**
 * Displays twirling activity indicator on the console.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2011-06-09 initial version.
 * @see Progress
 * @since 2011-06-09
 */
public final class Twirler
    {
    /**
     * chars to create twirling animation. Can't use &mdash;
     * because it in not supported in IBMOEM encoding for horizontal bar in DOS box.
     * Display in reverse order, giving illusion of bar spinning counter clockwise.
     */
    private static final String[] TWIRLS = { "/\r", "-\r", "\\\r", "|\r" };

    /**
     * rotation state of the twirling animation
     */
    private int twirlState = 0;

    /**
     * null constructor
     */
    public Twirler()
        {
        }

    /**
     * make the twirling display disappear.
     *
     * @param o usually System.out or System.err, where to display twirler.
     */
    public synchronized void clear( PrintStream o )
        {
        o.print( " \r" );
        o.flush();
        twirlState = 0;
        }

    /**
     * make the twirling display kick over 1/8 rev.
     *
     * @param o usually System.out or System.err, where to display twirler.
     */
    public synchronized void twirl( PrintStream o )
        {
        o.print( TWIRLS[ twirlState-- ] );
        o.flush();
        if ( twirlState < 0 )
            {
            twirlState = 3;
            }
        }
    }
