/*
 * [TestDate.java]
 *
 * Summary: Validate BigDate to ensure it is bug-free. Also demonstrate and exercise the various methods.
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
 *  5.0 2007-10-15 add test for nextXXXDay.
 *  5.1 2009-05-05 add tests for isAnniversary
 */
package com.mindprod.common18;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import static java.lang.System.*;

/**
 * Validate BigDate to ensure it is bug-free. Also demonstrate and exercise the various methods.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 5.1 2009-05-05 add tests for isAnniversary
 * @since 1997
 */
public final class TestDate
    {
    /**
     * 86,400,000 the number of milliseconds in 24 hour day. Easily fits into an int.
     * Cannot use TimeUnit in Java 1.1
     */
    private static final int MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    /**
     * days of the week 3 letter abbreviations in English. Index Sunday = 0.
     */
    private static final String[] shortDayName =
            { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };

    private static int count;

    /**
     * show off some of the things BigDate can do See also com.mindprod.holidays.Holiday to compute various holidays and
     * com.mindprod.holidays.IsHoliday to tell if a given day is a holiday.
     *
     * @noinspection PointlessArithmeticExpression
     */
    private static void demoDate()
        {
        {
        // test constructor and toString for a variety of dates:
        out.println( "Test String constructor and toString" );
        final String[] tests = {
                "1948-02-04",
                "1948-02-04BC",
                "1948_02_04AD",
                "1948.02.04",
                "1948/02/04 ad",
                "10-02-04",
                "20202-2-4AD" };
        for ( int i = 0; i < tests.length; i++ )
            {
            String test = tests[ i ];
            out.println( test + " -> " + new BigDate( test ).toString() );
            }
        }
        sep();
        {
        // test for bithdays, deathdays and anniversaries.
        /* daylight saving starts  2:00 A.M. on the second Sunday in March */
        out.println( "Today " + ( BigDate.isAnniversary( new BigDate( "1948-02-04" ), BigDate.localToday() )
                                  ? "is" : "is not" ) + " Roedy's birthday" );
        out.println( "Today " + ( BigDate.isAnniversary( new BigDate( 1750, 7, 28 ), BigDate.localToday() )
                                  ? "is" : "is not" ) + " the aniversary of Bach's death" );
        out.println( "If someone were born on 2000-02-29, do we celebrate the birthday on the 28th this year?" );
        out.println( BigDate.isAnniversary( new BigDate( 2000, 2, 29 ),
                new BigDate( BigDate.localToday().getYYYY(), 2, 28 ) )
                     ? "yes, on the 28th" : "no, on the 29th" );
        out.println( "What about next year?" );
        out.println( BigDate.isAnniversary( new BigDate( 2000, 2, 29 ),
                new BigDate( BigDate.localToday().getYYYY() + 1, 2, 28 ) )
                     ? "yes, on the 28th" : "no, on the 29th" );
        out.println( "And in " + ( BigDate.localToday().getYYYY() + 2 ) + "?" );
        out.println( BigDate.isAnniversary( new BigDate( 2000, 2, 29 ),
                new BigDate( BigDate.localToday().getYYYY() + 2, 2, 28 ) )
                     ? "yes, on the 28th" : "no, on the 29th" );
        out.println( "Well perhaps " + ( BigDate.localToday().getYYYY() + 3 ) + "?" );
        out.println( BigDate.isAnniversary( new BigDate( 2000, 2, 29 ),
                new BigDate( BigDate.localToday().getYYYY() + 3, 2, 28 ) )
                     ? "yes, on the 28th" : "no, on the 29th" );
        }
        sep();
        {
        // Daylight saving (2:00 A.M. on the second Sunday in March, and
        // 2:00 A.M. on the first Sunday in November. Canada follows the USA.
        for ( int year = 2008; year <= 2020; year++ )
            {
            /* daylight saving starts  2:00 A.M. on the second Sunday in March */
            out.println( "In " + year + " daylight saving starts 2AM on March " + BigDate.nthXXXDay( 2
                    /* second */,
                    BigDate.SUNDAY,
                    year,
                    BigDate.MARCH ) );
            /* daylight saving ends  2:00 A.M. on the first Sunday in November. */
            out.println( "In " + year + " daylight saving ends 2AM on November " + BigDate.nthXXXDay( 1
                    /* first */,
                    BigDate.SUNDAY,
                    year,
                    BigDate.NOVEMBER ) );
            }
        }
        sep();
        {
        // Using traditional tools
        Date date = new Date();
        out.println( "Now, according to Java Date is : " + date );
        out.println( "That is "
                     + date.getTime()
                     + " milliseconds since 1970-01-01." );
        out.println(
                "In contrast, BigDate works with pure dates, without times." );
        out.println( "Today, according to BigDate is : "
                     + BigDate.localToday()
                     + "." );
        }
        sep();
        {
        // Test Normalise
        out.println( "How an invalid date can be normalised:" );
        testNormalize( 2007, 2, 34 );
        }
        sep();
        {
        // what is today's date in American format
        out.println( "Today American style is " + BigDate.localToday()
                .toDowMMDDYY() );
        }
        sep();
        {
        // what is today's date in iso format
        out.println( "Today ISO style is " + BigDate.localToday()
                .toString() );
        }
        sep();
        {
        // What date is it in Greenwich?
        BigDate bigDate = BigDate.UTCToday();
        out.println( "Today in Greenwich England = "
                     + bigDate.toString()
                     + "." );
        /* 0=Sunday to 6=Saturday */
        int dayOfWeek = bigDate.getDayOfWeek();
        String[] daysOfTheWeek = {
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday" };
        String dayName = daysOfTheWeek[ dayOfWeek ];
        out.println( "In Greenwich it is " + dayName + "." );
        }
        sep();
        {
        // What day of the week is today?
        // display ISO 8601:1988 international standard format: yyyy-mm-dd.
        BigDate bigDate = BigDate.localToday();
        out.println( "Today = " + bigDate.toString() + "." );
        /* 0=Sunday to 6=Saturday */
        int dayOfWeek = bigDate.getDayOfWeek();
        String[] daysOfTheWeek = {
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday" };
        String dayName = daysOfTheWeek[ dayOfWeek ];
        out.println( "Today is " + dayName + "." );
        out.println( "Today is dayOfWeek number "
                     + bigDate.getDayOfWeek()
                     + " if Sunday=0." );
        out.println( "Today is dayOfWeek number "
                     + bigDate.getCalendarDayOfWeek()
                     + " if Sunday=1." );
        }
        pause();
        {
        // 40 days hence
        BigDate d = BigDate.localToday();
        d.addDays( 40 );
        out.println( "40 days hence: " + d.toString() );
        }
        sep();
        {
        // If a news story refers to last Wednesday, what date did they mean?
        final int eventDOW = BigDate.WEDNESDAY;
        final BigDate announceDate = new BigDate( 2010, 1, 27 );
        final int announceDOW = announceDate.getDayOfWeek();
        // 0=Sunday 1=Monday 2=Tuesday 3=Wednesday 4=Thursday 5=Friday 6=Saturday
        // WARNING: not compatible with 1=Calendar.SUNDAY.
        // e.g if announcement was Friday, daysAgo will be 2 , if Sunday it will be 4,
        // if announcement was one week ago Wednesday, it will be 0
        // if announcement was the same day it will be 0.
        int daysAgo = ( ( announceDOW - eventDOW + 7 ) % 7 );
        // resolve ambiguity of same day vs one week ago
        if ( daysAgo == 0 )
            {
            daysAgo = 7;
            }
        final BigDate eventDate = announceDate;
        eventDate.addDays( -daysAgo );
        out.println( "Stephen Shankland announced on 2010-01-27 that Oracle had bought Sun \"last Wednesday\"." +
                     " " +
                     "That must have been " + eventDate );
        }
        pause();
        {
        // what season is it?
        String[] seasons = new String[] { "spring", "summer", "fall", "winter" };
        BigDate today = BigDate.localToday();
        out.println( "It is " + seasons[ today.getSeason() ] );
        out.println( "Season for months are: " );
        for ( int i = 1; i <= 12; i++ )
            {
            BigDate seasonStart = new BigDate( today.getYYYY(), i, 1 );
            out.println( i + " " + seasons[ seasonStart.getSeason() ] );
            }
        }
        pause();
        {
        // What Date is Cobol yyddd bigDate 99360?
        BigDate bigDate = new BigDate( 99 + 1900, 1, 360, BigDate.NORMALISE );
        out.println( "COBOL-style yyddd date 99360 = "
                     + bigDate.toString()
                     + "." );
        }
        sep();
        {
        // How do you convert Java timestamps into Windows timestamps.
        // Java timestamps use 64-bit milliseconds since 1970 GMT.
        // Windows timestamps use 64-bit value representing the number
        // of 100-nanosecond intervals since January 1, 1601.
        int windowsBase = BigDate.toOrdinal( 1601, 1, 1 );
        int javaBase = BigDate.toOrdinal( 1970, 1, 1 );
        int daysDifference = javaBase - windowsBase;
        // but Microsoft forgot that
        // 86,400,000 = 1000 * 60 * 60 * 24 = milliseconds per day
        long millisDifference = daysDifference * 86400000L;
        out.println( "To convert Java Timestamps to Windows Timestamps:" );
        out.println( "windows = ( java + "
                     + millisDifference
                     + "L ) * 10000" );
        }
        sep();
        {
        // Display a BigDate with SimpleDateFormat using local time
        BigDate bigDate = new BigDate( 1999, 12, 31 );
        Date date = bigDate.getLocalDate();
        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "EEEE yyyy-MM-dd G hh:mm:ss aa zz : zzzzzz" );
        sdf.setTimeZone( TimeZone.getDefault() );// local time
        String dateString = sdf.format( date );
        out.println( "Local SimpleDateFormat: " + dateString );
        }
        sep();
        {
        // Display a BigDate with SimpleDateFormat using UTC (Greenwich GMT)
        // time
        BigDate bigDate = new BigDate( 1999, 12, 31 );
        Date date = bigDate.getUTCDate();
        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "EEEE yyyy-MM-dd G hh:mm:ss aa zz : zzzzzz" );
        sdf.setTimeZone( TimeZone.getTimeZone( "GMT" ) );// GMT time
        String dateString = sdf.format( date );
        out.println( "UTC/GMT SimpleDateFormat: " + dateString );
        }
        sep();
        {
        // Display a BigDate with default-locale DateFormat using UTC
        // (Greenwich GMT) time
        BigDate bigDate = new BigDate( 1999, 12, 31 );
        Date date = bigDate.getUTCDate();
        DateFormat df = DateFormat.getDateInstance();
        df.setTimeZone( TimeZone.getTimeZone( "GMT" ) );// GMT time
        String dateString = df.format( date );
        out.println( "UTC/GMT locale DateFormat: " + dateString );
        }
        sep();
        {
        // What are the earliest and latest dates BigDate can handle
        BigDate bigDate = new BigDate( BigDate.MIN_ORDINAL );
        out.println( "Earliest BigDate possible is "
                     + bigDate.toString() );
        bigDate.setOrdinal( BigDate.MAX_ORDINAL );
        out.println( "Latest BigDate possible is " + bigDate.toString() );
        }
        sep();
        {
        // What is the last day of February in 2000, preferred method
        BigDate bigDate = new BigDate( 2000, 3, 1 );/* 2000-03-01 */
        bigDate.addDays( -1 );/* last day of Feb */
        out.println( "Last day in  2000-02 = "
                     + bigDate.toString()
                     + "." );
        }
        sep();
        {
        // What is the last day of February in 2004, alternate less safe,
        // no-check method
        BigDate bigDate = new BigDate( 2004, 2 + 1, 1 - 1, BigDate.NORMALIZE );/*
         * 2004,
         * March
         * 0
         */
        out.println( "Last day in  200-02 = "
                     + bigDate.toString()
                     + "." );
        }
        sep();
        {
        // What is the last day of February in 2009, generic method.
        BigDate bigDate = new BigDate( 2009, 2, 1 );/* 2009-02-01 */
        bigDate.addDays( BigDate.daysInMonth( 2, 2009 ) - 1 );
        out.println( "Last day in  200-02 = "
                     + bigDate.toString()
                     + "." );
        }
        sep();
        {
        // What Date was it yesterday?
        BigDate bigDate = BigDate.localToday();
        bigDate.addDays( -1 );
        out.println( "Yesterday = " + bigDate.toString() + "." );
        }
        sep();
        {
        // What Date will it be tomorrow?
        BigDate bigDate = BigDate.localToday();
        bigDate.addDays( 1 );
        out.println( "Tomorrow = " + bigDate.toString() + "." );
        }
        sep();
        {
        // What Date will it be 1000 days from now?
        BigDate bigDate = BigDate.localToday();
        bigDate.addDays( 1000 );
        out.println( "Today+1000 days = " + bigDate.toString() + "." );
        }
        sep();
        {
        // What date will it be 50 months after 1998-01-03
        // You can think of this as how to turn a possibly invalid date into
        // the equivalent valid one.
        BigDate bigDate = new BigDate( 1998, 1 + 50, 3, BigDate.NORMALISE );
        out.println( "1998-01-03+50 months = "
                     + bigDate.toString()
                     + "." );
        }
        sep();
        {
        // What date will it be 60 months from today
        BigDate today = BigDate.localToday();
        BigDate bigDate =
                new BigDate( today.getYYYY(), today.getMM() + 60, today
                        .getDD(), BigDate.NORMALISE );
        out.println( "60 months from today will be "
                     + bigDate.toString()
                     + "." );
        }
        sep();
        {
        // Was 1830-02-29 a valid date?
        boolean ok = BigDate.isValid( 1830, 2, 29 );
        out.println( "True or false: 1830/02/29 was a valid date: "
                     + ok
                     + "." );
        }
        sep();
        {
        // how many seconds between 1900-01-01 0:00 and 1970-01-01 0:00
        // i.e. difference between SNTP and Java timestamp bases.
        // No leap seconds to worry about.
        long diffInDays =
                BigDate.toOrdinal( 1970, 1, 1 ) - BigDate.toOrdinal( 1900,
                        1,
                        1 );
        long diffInSecs = diffInDays * ( 24 * 60 * 60L );
        out.println( diffInDays
                     + " days or "
                     + diffInSecs
                     + " seconds between 1900-01 1 and 1970-01-01" );
        }
        sep();
        {
        // How Many days since 1970 is today ?
        BigDate bigDate = BigDate.localToday();
        out.println( "Today is "
                     + bigDate.getOrdinal()
                     + " days since 1970" );
        }
        sep();
        {
        // How Many days since 1970 is 2000/2/29?
        BigDate bigDate = new BigDate( 2000, 2, 29 );
        out.println( "1999-12-29 is "
                     + bigDate.getOrdinal()
                     + " days since 1970" );
        }
        sep();
        {
        // How Many milliseconds since 1970 is 1999/11/7
        BigDate bigDate = new BigDate( 1999, 11, 7 );
        out.println( "1999-11-07is "
                     + bigDate.getLocalTimeStamp()
                     + " milliseconds since 1970" );
        }
        sep();
        {
        // Is the year 2000 a leap year?
        out.println( "True or false: 2000 is a leap year: "
                     + BigDate.isLeap( 2000 )
                     + "." );
        }
        sep();
        {
        // What is the last day of this week, e.g. this Saturday?
        // display yyyy-MM-dd format.
        BigDate bigDate = BigDate.localToday();
        /* 0=Sunday to 6=Saturday */
        bigDate.addDays( 6 - bigDate.getDayOfWeek() );
        out.println( "This Saturday is " + bigDate.toString() + "." );
        }
        sep();
        {
        // In what week number did 1999-08-20 fall?
        BigDate bigDate = new BigDate( 1999, 8, 20 );
        out.println( "1999-08-20 fell in ISO week number "
                     + bigDate.getISOWeekNumber()
                     + " on ISO day of week number "
                     + bigDate.getISODayOfWeek()
                     + "." );
        out.println(
                " According to BigDate.getWeekNumber that is week number "
                + bigDate.getWeekNumber()
        );
        GregorianCalendar g = new GregorianCalendar( 1999, 8 - 1, 20 );
        out.println(
                " According to java.util.Calendar that is week number "
                + g.get( Calendar.WEEK_OF_YEAR )
                + "."
        );
        }
        sep();
        {
        // what is the next business day?
        BigDate bigDate = BigDate.localToday();
        int interval;
        switch ( bigDate.getDayOfWeek() )
            {
            case 5/* Friday -> Monday */:
                interval = 3;
                break;
            case 6/* Saturday -> Monday */:
                interval = 2;
                break;
            case 0/* Sunday -> Monday */:
            case 1/* Monday -> Tuesday */:
            case 2/* Tuesday -> Wednesday */:
            case 3/* Wednesday -> Thursday */:
            case 4/* Thursday -> Friday */:
            default:
                interval = 1;
                break;
            } // end switch
        bigDate.addDays( interval );
        out.println( "Next business day = " + bigDate.toString() + "." );
        }
        sep();
        {
        // What Day of the Month Is The Second Thursday of This Month?
        // This is when the Vancouver PC User Society (VPCUS) meets.
        // MacMillan planetarium.
        BigDate today = BigDate.localToday();
        int dayOfMonthOfSecondThursday = BigDate.nthXXXDay( 2
                /* second */,
                BigDate.THURSDAY,
                today.getYYYY(),
                today
                        .getMM()
        );
        out.println(
                "VPCUS meets the second Thursday of the month. This month's meeting would have been on on the "
                + dayOfMonthOfSecondThursday
                + "th."
        );
        }
        sep();
        {
        // What Day of the Month Is The third Thursday of This Month?
        // This is when the Vancouver Apple User Society meets.
        // Scottish Cultural Center, 8886 Hudson, near the Oak St Bridge
        BigDate today = BigDate.localToday();
        int dayOfMonthOfThirdThursday = BigDate.nthXXXDay( 3
                /* third */,
                BigDate.THURSDAY,
                today.getYYYY(),
                today.getMM() );
        out.println(
                "Apples BC meets the third Thursday of the month. This month's meeting is on the "
                + dayOfMonthOfThirdThursday
                + "th."
        );
        }
        sep();
        {
        // When is the next issue of Xtra West magazine due out
        // It came out 2000-07-27, and comes out every two weeks.
        BigDate today = BigDate.localToday();
        BigDate first = new BigDate( 2000, 7, 27 );
        int days = today.getOrdinal() - first.getOrdinal();
        int fortnights = ( days + 13 ) / 14;// round up
        BigDate next = new BigDate( first.getOrdinal() + fortnights * 14 );
        out.println( "Next issue of Xtra West magazine is due out "
                     + next.toString()
                     + "." );
        }
        sep();
        {
        // When is J. McRee (Mac) Elrod's next potluck?
        // http://www.islandnet.com/~jelrod/gpl.html
        // the fourth Saturday of the month, jan, apr, jul, oct, every 3
        // months.
        BigDate today = BigDate.localToday();
        int year = today.getYYYY();
        // 1>1 2>4 3>4 4>4 5>7 6>7 7>7 8>10 9>10 10>10 11>1 12>1
        int month = ( ( ( today.getMM() + ( 3 - 1 - 1 ) ) / 3 ) * 3 ) % 12 + 1;
        BigDate when = new BigDate( BigDate.ordinalOfnthXXXDay( 4/* fourth */,
                BigDate.SATURDAY,
                year,
                month ) );
        if ( when.getOrdinal() < today.getOrdinal() )
            {
            // we missed this month's potlock, get next one.
            month += 3;
            if ( month > 12 )
                {
                year++;
                month -= 12;
                when = new BigDate( BigDate.ordinalOfnthXXXDay( 4/* fourth */,
                        BigDate.SATURDAY,
                        year,
                        month ) );
                }
            }
        out.println(
                "Mac's next potluck in on the fourth Saturday of the month, every 3 months,\nNext is on "
                + when
        );
        }
        sep();
        {
        // What Day of the Month Is The Last Friday of This Month?
        BigDate today = BigDate.localToday();
        int dayOfMonthOfLastFriday = BigDate.nthXXXDay( 5
                /* last */,
                BigDate.FRIDAY,
                today.getYYYY(),
                today.getMM() );
        out.println( "The last Friday of the month is on the "
                     + dayOfMonthOfLastFriday
                     + "th." );
        }
        sep();
        {
        // Precisely how old is Roedy Green, in years, months and days
        // Roedy was born on 1948-02-04.
        BigDate birthDate = new BigDate( 1948, 2, 4 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( birthDate, today );
        out.println( "Today Roedy is "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old." );
        out.println( "or "
                     + ( today.getOrdinal() - birthDate.getOrdinal() )
                     + " days." );
        }
        sep();
        {
        // Is it Andrew Pirrie's birthday today?
        // he was was born on  was born on 1935-04-25
        final BigDate birthDate = new BigDate( 1935, 4, 25 );
        final BigDate today = BigDate.localToday();
        // people with 02-29 birthdays are counted on the 28th in non leap years.
        final boolean birthDayIsToday =
                birthDate.getMM() == today.getMM()
                && birthDate.getDD() == today.getDD()
                || today.getDD() == 28
                   && today.getMM() == 2
                   && birthDate.getDD() == 29
                   && birthDate.getMM() == 2
                   && !BigDate.isLeap( today.getYYYY() );
        out.println( "It is " + ( birthDayIsToday ? "" : "not " ) + "Andrew Pirrie's birthday today." );
        }
        sep();
        {
        // How many days old was C Green on her 53th birthday
        // She was born on  was born on 1955-05-31.
        BigDate birthDate = new BigDate( 1955, 5, 31 );
        BigDate fiftythird = new BigDate( 2008, 5, 31 );
        out.println( "C. Green was "
                     + ( fiftythird.getOrdinal() - birthDate.getOrdinal() )
                     + " days old on her 53rd birthday." );
        }
        sep();
        {
        // Precisely how old is Dick Cheney, in years, months and days
        // Dick Cheney was born on 1941-01-30
        BigDate birthDate = new BigDate( 1941, 1, 30 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( birthDate, today );
        out.println( "Dick Cheney was born on " + birthDate.toString() + "." );
        out.println( "Today Dick Cheney is "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old," );
        out.println( "or "
                     + ( today.getOrdinal() - birthDate.getOrdinal() )
                     + " days." );
        BigDate inaug1 = new BigDate( 2001, 1, 20 );
        age = BigDate.age( birthDate, inaug1 );
        out.println( "Dick Cheney was "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old when he first took office." );
        BigDate inaug2 = new BigDate( 2005, 1, 20 );
        age = BigDate.age( birthDate, inaug2 );
        out.println( "Dick Cheney was "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old when he started his second term." );
        BigDate leave = new BigDate( 2009, 1, 20 - 1 );
        age = BigDate.age( birthDate, leave );
        out.println( "Dick Cheney will be "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old when he leaves office." );
        }
        sep();
        {
        // Precisely how old is John McCain, in years, months and days
        // John McCain was born on 1936-08-29.
        BigDate birthDate = new BigDate( 1936, 8, 28 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( birthDate, today );
        out.println( "John McCain was born on " + birthDate.toString() + "." );
        out.println( "Today John McCain is "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old," );
        out.println( "or "
                     + ( today.getOrdinal() - birthDate.getOrdinal() )
                     + " days." );
        BigDate inaug1 = new BigDate( 2009, 1, 20 );
        age = BigDate.age( birthDate, inaug1 );
        out.println( "John McCain would be "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old when he first takes office." );
        BigDate inaug2 = new BigDate( 2013, 1, 21 );
        age = BigDate.age( birthDate, inaug2 );
        out.println( "John McCain would be "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old when he starts his second term." );
        BigDate leave = new BigDate( 2017, 1, 20 - 1 );
        age = BigDate.age( birthDate, leave );
        out.println( "John McCain would be "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old when he leaves office." );
        }
        sep();
        {
        // How old would John Lennon be, in years, months and days
        // Lennon was born on 1940-10-09.
        BigDate birthDate = new BigDate( 1940, 10, 9 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( birthDate, today );
        out.println( "Today John Lennon would be "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old." );
        }
        sep();
        {
        // How long since John Lennon died, in years, months and days
        // Lennon died on on 1980-12-08
        BigDate deathDate = new BigDate( 1980, 12, 8 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( deathDate, today );
        out.println( "It has been "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days since John Lennon was murdered." );
        }
        sep();
        {
        // How old will Little Richard be on his next birthday?
        // Little Richard was born on 1932-12-05
        final BigDate birthDate = new BigDate( 1932, 12, 5 );
        final BigDate today = BigDate.localToday();
        final int[] age = BigDate.age( birthDate, today );
        final int nextAge = age[ 0 ] + 1;
        BigDate nextBirthday = new BigDate( 1932 + nextAge, 12, 5 );
        out.println( "Little Richard will be " + nextAge + " on " + nextBirthday.toString() + "." );
        }
        sep();
        {
        // How old was Bush's Press secretary Scott McClellan when he married
        // Jill Martinez on 2003-11-22.
        // Scotty was born on 1968-02-14.
        BigDate birthDate = new BigDate( 1968, 2, 14 );
        BigDate wedding = new BigDate( 2003, 11, 22 );
        int[] age = BigDate.age( birthDate, wedding );
        out.println( "Scott McClelland was "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old when he married Jill Martinez." );
        }
        sep();
        {
        // Dr. Paul Norman was killed in plane crash
        // on the Sunday before Friday 2004-07-02. What date was that?
        BigDate deathDate = new BigDate( 2004, 7, 2 );
        deathDate
                .setOrdinal( deathDate.getOrdinal() - 5/* Friday */ + 0
                        /* Sunday */ );
        out.println( "Dr. Paul Norman was killed in a plane crash on "
                     + deathDate
                     + "." );
        }
        sep();
        {
        // How much time elapsed after Hitler died before George W. Bush was
        // born.
        BigDate hitler = new BigDate( 1945, 5, 1 );
        BigDate bush = new BigDate( 1946, 7, 6 );
        int[] age = BigDate.age( hitler, bush );
        out.println( "George W. Bush was born "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days after Hitler died on 1945-05-01" );
        out.println( "or "
                     + ( bush.getOrdinal() - hitler.getOrdinal() )
                     + " days." );
        }
        sep();
        {
        // Precisely how old was Hillary Clinton (born 1947-10-26)
        // when Sir Edmund Hillary climbed
        // mount everent in 1953-05-29 in years, months and days
        BigDate birthDate = new BigDate( 1947, 10, 26 );
        BigDate climb = new BigDate( 1953, 5, 29 );
        int[] age = BigDate.age( birthDate, climb );
        out.println( "Hillary Clinton was "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old when Sir Edmund Hillary climbed Mount Everest on 1953-05-29" );
        out.println( "or "
                     + ( climb.getOrdinal() - birthDate.getOrdinal() )
                     + " days." );
        }
        sep();
        {
        // What day of the week was Jesus born on?
        // assume Jesus was born on 0001-12-25
        // Scholars assure us he was NOT actually born then, and most likely never existed..
        int dayOfWeek = BigDate.dayOfWeek( BigDate.toOrdinal( 1, 12, 25 ) );
        String[] daysOfTheWeek = {
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday" };
        String dayName = daysOfTheWeek[ dayOfWeek ];
        out.println( "Jesus was born on a " + dayName + "." );
        }
        sep();
        {
        // How much time elapsed between December -0004-12-25 BC and 0028-04-30
        // AD
        BigDate fromDate = new BigDate( -4, 12, 25 );
        BigDate toDate = new BigDate( 28, 4, 30 );
        out.println( "How long did Jesus live?" );
        out.println( toDate.getOrdinal() - fromDate.getOrdinal()
                     + " days elapsed between 0004/12/25 BC and 0028/04/30 AD," );
        int[] age = BigDate.age( fromDate, toDate );
        out.println( "or put another way, "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days." );
        }
        sep();
        {
        // Precisely how old is George W. Bush, in years, months and days
        // George W. Bush was born on 1946-07-06.
        BigDate birthDate = new BigDate( 1946, 7, 6 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( birthDate, today );
        out.println( "Today, George W. Bush is "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old." );
        }
        sep();
        {
        // Precisely how old is Saddam Hussein, in years, months and days
        // Saddam Hussein was born on 1937-04-28
        BigDate birthDate = new BigDate( 1937, 4, 28 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( birthDate, today );
        out.println( "Today, Saddam Hussein would be "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old." );
        }
        sep();
        {
        // How long did WWII last?
        BigDate startDate = new BigDate( 1939, 9, 1 );
        BigDate endDate = new BigDate( 1945, 8, 15 );
        int[] age = BigDate.age( startDate, endDate );
        out.println( "World War II raged one for "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days." );
        }
        sep();
        {
        // How long has the Afghan war waged?
        BigDate startDate = new BigDate( 2001, 10, 7 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( startDate, today );
        out.println( "The Afghan war has raged one for "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days." );
        }
        sep();
        {// How long has the Iraq war waged?
        BigDate startDate = new BigDate( 2003, 3, 20 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( startDate, today );
        out.println( "The Iraq war has raged one for "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days." );
        }
        {
        // How long has the Iraq war waged since Saddam was captured
        BigDate startDate = new BigDate( 2003, 12, 13 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( startDate, today );
        out.println( "The Iraq war has raged one for "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days since Saddam was captured." );
        }
        sep();
        {
        // How Many days after bin Laden's last documented meeting with the
        // CIA
        // was the World Trade attack?
        BigDate fromDate = new BigDate( 2001, 7, 1 );// 2001-07-01
        BigDate toDate = new BigDate( 2001, 9, 11 );// 2001-09-11
        out.println( "The World Trade attack occurred "
                     + ( toDate.getOrdinal() - fromDate.getOrdinal() )
                     + " days after Osmama bin Laden's last meeting with the CIA." );
        }
        sep();
        {
        // How long ago was the World Trade Center destroyed?
        // The attack was 2001-09-11.
        BigDate hitDate = new BigDate( 2001, 9, 11 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( hitDate, today );
        out.println( "The World Trade center was destroyed "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days ago." );
        }
        sep();
        {
        // How long did it take Bush to give up trying to catch bin Laden.
        // The attack was 2001-09-11.
        // On 2002-03-13 Bush announced:
        // I don't know where he is. I have no idea and I really don't care.
        // It's not that important. It's not our priority.
        BigDate hitDate = new BigDate( 2001, 9, 11 );
        BigDate giveUpDate = new BigDate( 2002, 3, 13 );
        int[] patience = BigDate.age( hitDate, giveUpDate );
        out.println( "Bush officially gave up chasing bin Laden after "
                     + patience[ 0 ]
                     + " years,  "
                     + patience[ 1 ]
                     + " months and "
                     + patience[ 2 ]
                     + " days." );
        }
        sep();
        {
        // What was the date and day of week 72 days prior to Lincoln's
        // assassination?
        BigDate assassinDate = new BigDate( 1865, 4, 18 );// 1865-04-18
        BigDate priorDate = new BigDate( assassinDate.getOrdinal() - 72 );
        int dayOfWeek = priorDate.getDayOfWeek();
        final String[] daysOfTheWeek = {
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday" };
        out.println( "72 days prior to Lincoln's assassination was "
                     + priorDate.toString()
                     + ", a "
                     + daysOfTheWeek[ dayOfWeek ] );
        }
        sep();
        {
        // how long has it been since since John F. Kennedy was
        // assassinated?
        // John F. Kennedy was assassinated on November 22, 1963
        out.println( "John F. Kennedy was assassinated "
                     + ( BigDate.localToday().getOrdinal()
                         - BigDate.toOrdinal( 1963, 11, 22 ) )
                     + " days ago." );
        }
        sep();
        {
        // How long after Martin Luther King's assassination
        // was Bobby Kennedy assassinated?
        // MLK was assassinated 1968-04-04
        // RK was assassinated 1968-06-05 according to http://foia.fbi.gov/foiaindex/rfkasumm.htm
        out.println( "Robert Kennedy was assassinated "
                     + ( BigDate.toOrdinal( 1968, 6, 5 )
                         - BigDate.toOrdinal( 1968, 4, 4 ) )
                     + " days after Martin Luther King." );
        }
        sep();
        {
        // How old would Martin Luther King be if he were still alive today?
        // MLK was born 1929-01-15, celebrated 3rd monday in January
        BigDate mlk = new BigDate( 1929, 1, 15 );
        BigDate today = BigDate.localToday();
        int[] age = BigDate.age( mlk, today );
        out.println( "Martin Luther King would be "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days old today." );
        }
        sep();
        {
        BigDate al = new BigDate( 1865, 4, 18 );
        BigDate jfk = new BigDate( 1963, 11, 22 );
        BigDate mlk = new BigDate( 1968, 4, 4 );
        BigDate rfk = new BigDate( 1968, 6, 5 );// according to http://foia.fbi.gov/foiaindex/rfkasumm.htm
        BigDate als = new BigDate( 1981, 10, 6 );
        BigDate zar = new BigDate( 2006, 6, 8 );
        BigDate bb = new BigDate( 2007, 12, 27 );
        out.println( "Abraham Lincoln was assassinated on " + al.toString() + "." );
        int[] elapsed = BigDate.age( al, jfk );
        out.println( "John Fitzgerald Kennedy was assassinated on " + jfk.toString()
                     + ", "
                     + elapsed[ 0 ]
                     + " years and "
                     + elapsed[ 1 ]
                     + " months and "
                     + elapsed[ 2 ]
                     + " days later." );
        elapsed = BigDate.age( jfk, mlk );
        out.println( "Martin Luther King was assassinated on " + mlk.toString()
                     + ", "
                     + elapsed[ 0 ]
                     + " years and "
                     + elapsed[ 1 ]
                     + " months and "
                     + elapsed[ 2 ]
                     + " days later." );
        elapsed = BigDate.age( mlk, rfk );
        out.println( "Robert Francis (Bobby) Kennedy was assassinated on " + rfk.toString()
                     + ", "
                     + elapsed[ 1 ]
                     + " months and "
                     + elapsed[ 2 ]
                     + " days later." );
        elapsed = BigDate.age( rfk, als );
        out.println( "Anwar El Sadat was assassinated on " + bb.toString()
                     + ", "
                     + elapsed[ 0 ]
                     + " years and "
                     + elapsed[ 1 ]
                     + " months and "
                     + elapsed[ 2 ]
                     + " days later." );
        elapsed = BigDate.age( als, zar );
        out.println( "Abu Musab al-Zarqawi was assassinated on " + bb.toString()
                     + ", "
                     + elapsed[ 0 ]
                     + " years and "
                     + elapsed[ 1 ]
                     + " months and "
                     + elapsed[ 2 ]
                     + " days later." );
        elapsed = BigDate.age( zar, bb );
        out.println( "Benazir Bhutto was assassinated on " + bb.toString()
                     + ", "
                     + elapsed[ 0 ]
                     + " years and "
                     + elapsed[ 1 ]
                     + " months and "
                     + elapsed[ 2 ]
                     + " days later." );
        }
        sep();
        {
        // Mysterious missing days in the calendar.
        // Pope Gregory: 1582-10-04 Thursday, was followed immediately
        // by 1582-10-15 Friday dropping 10 days.
        //
        // British: 1752-09-02 Wednesday was followed immediately
        // by 1752-09-14 Thursday dropping 12 days.
        BigDate priorDate;
        BigDate afterDate;
        if ( BigDate.IS_BRITISH )
            {
            out.println(
                    "According to the British/American/Canadian calendar, 12 days were dropped." );
            priorDate = new BigDate( 1752, 9, 2 );
            afterDate = new BigDate( 1752, 9, 14 );
            }
        else
            {
            out.println(
                    "According to Pope Gregory's calendar, 10 days were dropped." );
            priorDate = new BigDate( 1582, 10, 4 );
            afterDate = new BigDate( 1582, 10, 15 );
            }
        final String[] daysOfTheWeek = {
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday" };
        int dayOfWeek = priorDate.getDayOfWeek();
        out.println( priorDate.toString()
                     + " was a "
                     + daysOfTheWeek[ dayOfWeek ]
                     + "." );
        dayOfWeek = afterDate.getDayOfWeek();
        out.println( afterDate.toString()
                     + " was a "
                     + daysOfTheWeek[ dayOfWeek ]
                     + "." );
        }
        pause();
        {
        // What is the Julian Day number of various dates 1970/1/1
        // Cross check these with the US Naval Observatory at:
        // http://aa.usno.navy.mil/AA/data/docs/JulianDate.html
        BigDate bigDate = new BigDate( 2000, 3, 20 );
        out.println(
                "Astronomical Julian Proleptic calendar day for 2000-03-20 is "
                + bigDate.getProlepticJulianDay()
        );
        bigDate = new BigDate( 1970, 1, 1 );
        out.println(
                "Astronomical Julian Proleptic calendar day for 1970-01-01 is "
                + bigDate.getProlepticJulianDay()
        );
        bigDate = new BigDate( 1600, 1, 1 );
        out.println(
                "Astronomical Julian Proleptic calendar day for 1600/01/01 is "
                + bigDate.getProlepticJulianDay()
        );
        bigDate = new BigDate( 1500, 1, 1 );
        out.println(
                "Astronomical Julian Proleptic calendar day for 1500/01/01 is "
                + bigDate.getProlepticJulianDay()
        );
        bigDate = new BigDate( 1, 1, 1 );
        out.println(
                "Astronomical Julian Proleptic calendar day for 0001/01/01 is "
                + bigDate.getProlepticJulianDay()
        );
        bigDate = new BigDate( -1, 12, 31 );
        out.println(
                "Astronomical Julian Proleptic calendar day for -0001/12/31 is "
                + bigDate.getProlepticJulianDay()
        );
        bigDate = new BigDate( -6, 1, 1 );
        out.println(
                "Astronomical Julian Proleptic calendar day for -0006/01/01 is "
                + bigDate.getProlepticJulianDay()
        );
        bigDate = new BigDate( -4713, 1, 1 );
        out.println(
                "Astronomical Julian Proleptic calendar day for -4713/01/01 is "
                + bigDate.getProlepticJulianDay()
        );
        }
        pause();
        {
        // How long till Christmas? ( Sun's way.)
        // this code will give a negative number just after Christmas.
        out.println( "Your child asks, how long is it until Christmas?" );
        out.println(
                "Here are the possible answers you might give to an American child" );
        GregorianCalendar now = new GregorianCalendar();
        int thisYear = now.get( Calendar.YEAR );
        // You may open presents at 7 AM December 25, local time
        out.println(
                "You may open your presents at 7 AM Christmas morning." );
        GregorianCalendar christmas =
                new GregorianCalendar( thisYear, 12 - 1, 25, 7, 0, 0 );
        // millis since 1970-01-01
        long christmasTimeStamp = christmas.getTime().getTime();
        long nowTimeStamp = now.getTime().getTime();
        double dayUnitsDiff =
                ( christmasTimeStamp - nowTimeStamp )
                / ( double ) MILLISECONDS_PER_DAY;
        out.println( "1. It is "
                     + dayUnitsDiff
                     + " day units of 24 hours until you may open your presents." );
        out.println( "2. It is "
                     + Math.ceil( dayUnitsDiff )
                     + " day units of 24 hours rounded up until you may open your presents." );
        out.println( "3. It is "
                     + Math.round( dayUnitsDiff )
                     + " day units of 24 hours rounded until you may open your presents." );
        out.println( "4. It is "
                     + Math.floor( dayUnitsDiff )
                     + " day units of 24 hours rounded down until you may open your presents." );
        int gmtChristmasOrdinal =
                ( int ) ( christmasTimeStamp / MILLISECONDS_PER_DAY );
        int gmtNowOrdinal = ( int ) ( nowTimeStamp / MILLISECONDS_PER_DAY );
        int gmtDiffInDays = gmtChristmasOrdinal - gmtNowOrdinal;
        out.println( "5. Children in Greenwich have "
                     + gmtDiffInDays
                     + " sleeps (midnight crossings) to go until\n"
                     + "you may open your presents.\n"
                     + "They may open theirs even sooner." );
        // For children living in the USA, the TimeZone offset will be
        // negative.
        // days since 1970-01-01
        // There is no GregorianCalender.getOffset or
        // get(ADJUSTED_ZONE_OFFSET);
        int christmasZoneOffset =
                christmas.get( Calendar.ZONE_OFFSET )
                + now.get( Calendar.DST_OFFSET );
        int localChristmasOrdinal =
                ( int ) ( ( christmasTimeStamp + christmasZoneOffset )
                          / MILLISECONDS_PER_DAY );
        int nowZoneOffset =
                now.get( Calendar.ZONE_OFFSET )
                + now.get( Calendar.DST_OFFSET );
        int localNowOrdinal =
                ( int ) ( ( nowTimeStamp + nowZoneOffset )
                          / MILLISECONDS_PER_DAY );
        int localDiffInDays = localChristmasOrdinal - localNowOrdinal;
        out.println( "6. You have "
                     + localDiffInDays
                     + " sleeps (midnight crossings) to go." );
        }
        {
        // how long till Christmas? ( with BigDate. )
        // this code will give a negative number just after Christmas.
        BigDate today = BigDate.localToday();
        BigDate christmas = new BigDate( today.getYYYY(), 12, 25 );
        out.println( "7. Uncle Roedy's Bigdate says it is "
                     + ( christmas.getOrdinal() - today.getOrdinal() )
                     + " sleeps until Christmas." );
        int[] age = BigDate.age( today, christmas );
        out.println( "8. Or put another way "
                     + age[ 0 ]
                     + " years and "
                     + age[ 1 ]
                     + " months and "
                     + age[ 2 ]
                     + " days to go." );
        }
        } // end demoDate

    /**
     * Display an error message and exit.
     *
     * @param why the error message explaining why we are terminating.
     */
    private static void fail( String why )
        {
        out.println( why );
        pause();
        System.exit( 99 );
        }

    /**
     * Pauses the display until the user hits enter.
     *
     * @noinspection ResultOfMethodCallIgnored
     */
    private static void pause()
        {
        count = 0;
        out.println();
        try
            {
            for ( int available = System.in.available(); available
                                                         > 0; available-- )
                {
                // discard pending chars
                System.in.read();
                }
            out.println( "Hit Enter to continue" );
            // might get cr, lf, cr lf. Ignored previous input from InputStream
            System.in.read();// wait for user to hit Enter, discard result
            }
        catch ( IOException e )
            {
            out.println( "keyboard failed" );
            }
        out.println();
        out.println();
        out.println();
        } // end pause

    /**
     * Separates items
     */
    private static void sep()
        {
        // pause after every three items
        count++;
        if ( count >= 3 )
            {
            // will reset to 0
            pause();
            }
        else
            {
            out.println();
            }
        }

    /**
     * Display a date and its ordinal on out.
     *
     * @param desc a description of the date to label it.
     * @param yyyy the year to display.
     * @param mm   the month to display.
     * @param dd   the day of the month to display.
     */
    private static void show( String desc, int yyyy, int mm, int dd )
        {
        out.println( desc + BigDate.toOrdinal( yyyy, mm, dd ) );
        }

    /**
     * Test that the age routine generates plausible results.
     *
     * @param fromDate    earliest date to test.
     * @param toDate      last date to test.
     * @param samples     how many random pairs to test.
     * @param showDetails true if want details of individual tests
     */
    private static void testAge( BigDate fromDate,
                                 BigDate toDate,
                                 int samples,
                                 boolean showDetails )
        {
        // test with a random sampling of date pairs.
        out.println( "Testing age from: "
                     + fromDate.toString()
                     + " to: "
                     + toDate.toString()
                     + " using "
                     + samples
                     + " random samples." );
        Random wheel = new Random();
        BigDate birth = new BigDate();
        BigDate asof = new BigDate();
        // date after which our approximations hold.
        int plausibleYear = BigDate.IS_BRITISH ? 1752 : 1582;
        // calculate transform to take result of Random.nextInt into our range.
        int base = fromDate.getOrdinal();
        int divisor = toDate.getOrdinal() - base + 1;
        if ( divisor < 2 )
            {
            divisor = 2;
            }
        // Any difference 4 or less is reasonable because of the way months have
        // unequal lengths. Only flag really bad cases. There should not be any.
        int worstApprox = 4;
        for ( int i = 0; i < samples; i++ )
            {
            // Generate a pair of random dates in range. Might not be in order.
            int ord1 = ( wheel.nextInt() & Integer.MAX_VALUE ) % divisor + base;
            int ord2 = ( wheel.nextInt() & Integer.MAX_VALUE ) % divisor + base;
            // put them in order
            if ( ord1 > ord2 )
                {
                int temp = ord1;
                ord1 = ord2;
                ord2 = temp;
                }
            birth.set( ord1 );
            asof.set( ord2 );
            int[] age = BigDate.age( birth, asof );
            if ( showDetails )
                {
                out.print( "birth: " + birth.toString() + " " );
                out.print( "as of: " + asof.toString() + " " );
                out.println( age[ 0 ]
                             + " years and "
                             + age[ 1 ]
                             + " months and "
                             + age[ 2 ]
                             + " days old." );
                }
            if ( age[ 0 ] < 0 )
                {
                fail( "Negative age in years" );
                }
            if ( age[ 1 ] < 0 )
                {
                fail( "Negative age in months" );
                }
            if ( age[ 2 ] < 0 )
                {
                fail( "Negative age in days" );
                }
            if ( age[ 1 ] > 11 )
                {
                fail( "Age > 11 months" );
                }
            if ( age[ 2 ] > 31 )
                {
                fail( "Age > 31 days" );
                }
            if ( age[ 0 ] > 0
                 && age[ 1 ] > 0
                 && age[ 2 ] > 0
                 && birth.getYYYY() > plausibleYear )
                {
                // plausibility test, approximation works after Gregorian
                // instituted.
                int roughAgeInDays =
                        ( int ) Math.round( age[ 0 ] * 365.2425d
                                            + age[ 1 ] * 30.436875d
                                            + age[ 2 ] );
                int exactAgeInDays = asof.getOrdinal() - birth.getOrdinal();
                int approx = Math.abs( roughAgeInDays - exactAgeInDays );
                if ( approx > worstApprox )
                    {
                    worstApprox = approx;
                    out.print( "birth: " + birth.toString() + " " );
                    out.print( "as of: " + asof.toString() + " " );
                    out.println( age[ 0 ]
                                 + " years and "
                                 + age[ 1 ]
                                 + " months and "
                                 + age[ 2 ]
                                 + " days old. Differs from approx by "
                                 + approx
                                 + "." );
                    } // end if got a new worst approximation
                } // end if plausibility test
            } // end for
        } // end testAge

    /**
     * Check for bugs in toOrdinal and toGregorian.
     *
     * @param fromYear the first year to test.
     * @param toYear   the last year to test.
     */
    private static void testDate( int fromYear, int toYear )
        {
        int gotOrdinal, expectedOrdinal;
        BigDate gotGregorian;
        int gotYear, expectedYear;
        int gotMonth, expectedMonth;
        int gotDay, expectedDay;
        out.println( "Testing toOrdinal and toGregorian "
                     + fromYear
                     + " .. "
                     + toYear );
        out.println( "This could take a while..." );
        try
            {
            expectedOrdinal = BigDate.toOrdinal( fromYear, 1, 1 );
            for ( expectedYear = fromYear; expectedYear
                                           <= toYear; expectedYear++ )
                {
                if ( expectedYear % 10000 == 0 )
                    {
                    out.println( "reached " + expectedYear );
                    }
                for ( expectedMonth = 1; expectedMonth <= 12; expectedMonth++ )
                    {
                    for ( expectedDay = 1; expectedDay <= 31; expectedDay++ )
                        {
                        if ( BigDate.isValid( expectedYear,
                                expectedMonth,
                                expectedDay ) )
                            {
                            // test toOrdinal
                            gotOrdinal =
                                    BigDate.toOrdinal( expectedYear,
                                            expectedMonth,
                                            expectedDay );
                            if ( gotOrdinal != expectedOrdinal )
                                {
                                fail( "toOrdinal oops "
                                      + " expected: "
                                      + " YYYY:"
                                      + expectedYear
                                      + " MM:"
                                      + expectedMonth
                                      + " DD:"
                                      + expectedDay
                                      + " JJJJ:"
                                      + expectedOrdinal
                                      + " got: "
                                      + " JJJJ:"
                                      + gotOrdinal );
                                } // end if
                            // test toGregorian
                            gotGregorian = new BigDate( expectedOrdinal );
                            gotYear = gotGregorian.getYYYY();
                            gotMonth = gotGregorian.getMM();
                            gotDay = gotGregorian.getDD();
                            if ( ( gotYear != expectedYear )
                                 || ( gotMonth != expectedMonth )
                                 || ( gotDay != expectedDay ) )
                                {
                                fail( "toGregorian failed"
                                      + " expected: "
                                      + " JJJJ:"
                                      + expectedOrdinal
                                      + " YYYY:"
                                      + expectedYear
                                      + " MM:"
                                      + expectedMonth
                                      + " DD:"
                                      + expectedDay
                                      + " got: "
                                      + " YYYY:"
                                      + gotYear
                                      + " MM:"
                                      + gotMonth
                                      + " DD:"
                                      + gotDay );
                                } // end if
                            // increment only for valid dates
                            expectedOrdinal = gotOrdinal + 1;
                            } // end if isValid
                        }
                    }
                } // all three for loops
            } // end try
        catch ( IllegalArgumentException e )
            {
            fail( "test failed " + e.getMessage() );
            }
        out.println(
                "BigDate toOrdinal and toGregorian test completed successfully" );
        } // end testDate

    /**
     * Test getISOWeekNumber and getISODayOfWeek.
     *
     * @param fromYear first year to test.
     * @param toYear   last year to test.
     *
     * @noinspection SameParameterValue
     */
    private static void testISOWeekNumber( int fromYear, int toYear )
        {
        BigDate start = new BigDate( fromYear, 1, 1 );
        BigDate stop = new BigDate( toYear, 12, 31 );
        out.println( "Testing getISOWeekNumber and getISODayOfWeek "
                     + start.toString()
                     + " .. "
                     + stop.toString() );
        BigDate candidate = new BigDate( start );
        int expectedWeekNumber = candidate.getISOWeekNumber();
        int expectedDayOfWeek = candidate.getISODayOfWeek();
        for ( int i = start.getOrdinal(); i < stop.getOrdinal(); i++ )
            {
            candidate.setOrdinal( i );
            int weekNumber = candidate.getISOWeekNumber();
            int dayOfWeek = candidate.getISODayOfWeek();
            if ( weekNumber != expectedWeekNumber )
                {
                if ( expectedWeekNumber >= 53 && weekNumber == 1 )
                    {
                    expectedWeekNumber = 1;
                    }
                else
                    {
                    fail( "getISOWeekNumber bug at " + candidate.toString() );
                    }
                }
            if ( dayOfWeek != expectedDayOfWeek )
                {
                fail( "getISoDayOfWeek bug at " + candidate.toString() );
                }
            expectedDayOfWeek = dayOfWeek + 1;
            if ( expectedDayOfWeek == 8 )
                {
                expectedDayOfWeek = 1;
                expectedWeekNumber++;
                }
            }
        } // end testIOSWeekNumber

    /**
     * make sure testNearestXXX Day is working correctly
     */
    private static void testNearestXXXDay()
        {
        out.println( "Testing nearestXXXDay" );
        BigDate start = new BigDate( 2007, 10, 14 );
        BigDate stop = new BigDate( 2007, 10, 21 );
        for ( int i = start.getOrdinal(); i < stop.getOrdinal(); i++ )
            {
            BigDate aDay = new BigDate( i );
            for ( int day = 0; day < 7; day++ )
                {
                BigDate nearest = aDay.nearestXXXDay( day );
                if ( nearest.getDayOfWeek() != day )
                    {
                    err.println( "wrong day of week for nearstXXXDay base:"
                                 + aDay
                                 + " nearest:"
                                 + nearest
                                 + " day:"
                                 + shortDayName[ day ] );
                    }
                if ( Math.abs( nearest.getOrdinal() - i ) >= 4 )
                    {
                    err.println( "not nearest for nearestXXXDay base:"
                                 + aDay
                                 + " nearest:"
                                 + nearest
                                 + " day:"
                                 + shortDayName[ day ] );
                    }
                }
            }
        out.println( "nearstXXXDay tests done." );
        }

    /**
     * Test Normalize by displaying how an invalid date is converted.
     *
     * @param yyyy the year to test, may be negative or bigger than 4 digits.
     * @param mm   the month to test.
     * @param dd   the day of the month to test.
     *
     * @noinspection SameParameterValue
     */
    private static void testNormalize( int yyyy, int mm, int dd )
        {
        BigDate g = new BigDate( yyyy, mm, dd, BigDate.NORMALIZE );
        out.println( " input: "
                     + " YYYY:"
                     + yyyy
                     + " MM:"
                     + mm
                     + " DD:"
                     + dd );
        out.println( "output: "
                     + " YYYY:"
                     + g.getYYYY()
                     + " MM:"
                     + g.getMM()
                     + " DD:"
                     + g.getDD()
                     + " ORD:"
                     + g.getOrdinal()
                     + " DOW:"
                     + g.getDayOfWeek()
                     + " DDD:"
                     + g.getDDD()
                     + " TIMESTAMP:"
                     + g.getUTCTimeStamp() );
        } // end testNormalize

    /**
     * Run a series of tests on BigDate to ensure it is bug-free.
     *
     * @param args command line arguments, not used.
     */
    public static void main( String[] args )
        {
        demoDate();
        pause();
        show( "NULL date = ", 0, 0, 0 );
        show( "Jan 01, 999999BC = ", -999999, 1, 1 );
        show( "Jan 01, 04BC = ", -4, 1, 1 );
        show( "Dec 31, 01BC = ", -1, 12, 31 );
        show( "Jan 01, 01AD = ", 1, 1, 1 );
        show( "Oct 04, 1582 = ", 1582, 10, 4 );
        show( "Oct 15, 1582 = ", 1582, 10, 15 );
        show( "Jan 01, 1860 = ", 1860, 1, 1 );
        show( "Dec 31, 1969 = ", 1969, 12, 31 );
        show( "Jan 01, 1970 = ", 1970, 1, 1 );
        show( "Jan 02, 1970 = ", 1970, 1, 2 );
        show( "Jun 18, 1998 = ", 1998, 6, 18 );
        show( "Dec 31, 999999AD = ", 999999, 12, 31 );
        pause();
        testNearestXXXDay();
        pause();
        testAge( new BigDate( 1990, 1, 1 ),
                new BigDate( 2006, 12, 31 ),
                10,
                true );
        testAge( new BigDate( -999999, 1, 1 ),
                new BigDate( 999999, 12, 31 ),
                1000000,
                false );
        testAge( new BigDate( 1900, 1, 1 ),
                new BigDate( 2050, 12, 31 ),
                1000000,
                false );
        // after all danger of goofy missing days
        testISOWeekNumber( 1800, 9999 );
        pause();
        testDate( -2100, +2100 );
        testDate( -999999, -999000 );
        testDate( +999000, +999999 );
        testDate( -99999, +99999 );
        pause();
        } // end main
    }
