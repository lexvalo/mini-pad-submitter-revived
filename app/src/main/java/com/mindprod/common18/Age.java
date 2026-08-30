/*
 * [Age.java]
 *
 * Summary: Calculates the age of something in reasonable units.
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
 *  1.0 2012-11-20 initial version
 */
package com.mindprod.common18;

import java.util.concurrent.TimeUnit;

/**
 * Calculates the age of something in reasonable units.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2012-11-20 initial version
 * @see com.mindprod.htmlmacros.macro.Age
 * @since 2012-11-20
 */
public class Age
    {
    /**
     * calulate age in reasonable units e.g.  "5 minutes" or "3 hours"
     *
     * @param timestamp timestamp in in past.
     *
     * @return How much time has elapsed since the timestamp?
     */
    public static String ageIn( long timestamp )
        {
        final long ageInMillis = System.currentTimeMillis() - timestamp;
        if ( ageInMillis < 1000 )
            {
            return Long.toString( ageInMillis ) + " millisecond" + ( ageInMillis != 1 ? "s" : "" );
            }
        final long ageInSeconds = TimeUnit.MILLISECONDS.toSeconds( ageInMillis );
        if ( ageInSeconds < 60 )
            {
            return Long.toString( ageInSeconds ) + " second" + ( ageInSeconds != 1 ? "s" : "" );
            }
        final long ageInMinutes = TimeUnit.MILLISECONDS.toMinutes( ageInMillis );
        if ( ageInMinutes < 60 )
            {
            return Long.toString( ageInMinutes ) + " minute" + ( ageInMinutes != 1 ? "s" : "" );
            }
        final long ageInHours = TimeUnit.MILLISECONDS.toHours( ageInMillis );
        if ( ageInHours < 24 )
            {
            return Long.toString( ageInHours ) + " hour" + ( ageInHours != 1 ? "s" : "" );
            }
        final long ageInDays = TimeUnit.MILLISECONDS.toDays( ageInMillis );
        return Long.toString( ageInDays ) + " day" + ( ageInDays != 1 ? "s" : "" );
        // could with some work do weeks, months and years.
        }
    }
