/*
 * [StringSearch.java]
 *
 * Summary: Methods for searching strings for multiple targets. Especially useful for screenscraping.
 *
 * Copyright: (c) 2005-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2005-01-01 initial version
 */
package com.mindprod.common18;

/**
 * Methods for searching strings for multiple targets. Especially useful for screenscraping.
 * <p/>
 * to seach for &quot;apple&quot;, &quot;pear&quot; or &quot;cherry&quot; all in one go.
 * Methods use ... notation only  available in JDK 1.5+.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2005-01-01 initial version
 * @since 2005-01-01
 */
public class StringSearch
    {
    /**
     * find first of a number of possible targets
     *
     * @param s       String to search in
     * @param base    offset where to start looking
     * @param targets multiple targets to search for
     *                *@param ofEnd true if want index of one past end of match, false if want index of start.
     *
     * @return index of the char one past the end of the matching target, or index of first char of match,
     * -1 if none of the targets match.
     */
    private static int indexOf( String s, int base, boolean ofEnd, String... targets )
        {
        int bestOffset = -1;
        String bestTarget = null;
        for ( String target : targets )
            {
            final int place = s.indexOf( target, base );
            if ( 0 <= place && ( place < bestOffset || bestTarget == null ) )
                {
                bestOffset = place;
                bestTarget = target;
                }
            }
        if ( !ofEnd || bestTarget == null )
            {
            return bestOffset;
            }
        else
            {
            return bestOffset + bestTarget.length();
            }
        }

    /**
     * find last of a number of possible targets, one closest to the end
     *
     * @param s       String to search in
     * @param base    offset where to start looking
     * @param targets multiple targets to search for
     * @param ofEnd   true if want index of one past end of match, false if want index of start.
     *
     * @return index of the char one past the end of the matching target that starts closest to the end,
     * or index of first char of match, -1 if none of the targets match.
     */
    private static int lastIndexOf( String s, int base, boolean ofEnd, String... targets )
        {
        int bestOffset = -1;
        String bestTarget = null;
        for ( String target : targets )
            {
            final int place = s.lastIndexOf( target, base );
            if ( 0 <= place && ( bestOffset < place || bestTarget == null ) )
                {
                bestOffset = place;
                bestTarget = target;
                }
            }
        if ( !ofEnd || bestTarget == null )
            {
            return bestOffset;
            }
        else
            {
            return bestOffset + bestTarget.length();
            }
        }

    /**
     * find first of a number of possible targets.
     *
     * @param s       String to search in
     * @param targets multiple targets to search for
     *
     * @return index of the first matching target, -1 if none of the targets match.
     */
    public static int indexOf( String s, String... targets )
        {
        return indexOf( s, 0, false, targets );
        }

    /**
     * find first of a number of possible targets.
     *
     * @param s       String to search in
     * @param base    offset where to start looking
     * @param targets multiple targets to search for
     *
     * @return index of the first matching target, -1 if none of the targets match.
     */
    public static int indexOf( String s, int base, String... targets )
        {
        return indexOf( s, base, false, targets );
        }

    /**
     * find first of a number of possible targets
     *
     * @param s       String to search in
     * @param targets multiple targets to search for
     *
     * @return index of char one past the end of the first matching target, -1 if none of the targets match.
     */
    public static int indexOfEnd( String s, String... targets )
        {
        return indexOf( s, 0, true, targets );
        }

    /**
     * find first of a number of possible targets
     *
     * @param s       String to search in
     * @param base    offset where to start looking
     * @param targets multiple targets to search for
     *
     * @return index of char one past the end of the first matching target, -1 if none of the targets match.
     */
    public static int indexOfEnd( String s, int base, String... targets )
        {
        return indexOf( s, base, true, targets );
        }

    /**
     * find last of a number of possible targets, one closest to the end
     *
     * @param s       String to search in
     * @param targets multiple targets to search for
     *
     * @return index of the first matching target, -1 if none of the targets match.
     */
    public static int lastIndexOf( String s, String... targets )
        {
        return lastIndexOf( s, s.length(), false, targets );
        }

    /**
     * find last of a number of possible targets, one closest to the end
     *
     * @param s       String to search in
     * @param base    offset where to start looking
     * @param targets multiple targets to search for
     *
     * @return index of the first matching target, -1 if none of the targets match.
     */
    public static int lastIndexOf( String s, int base, String... targets )
        {
        return lastIndexOf( s, base, false, targets );
        }

    /**
     * find last of a number of possible targets, one closest to the end
     *
     * @param s       String to search in
     * @param targets multiple targets to search for
     *
     * @return index of the char one past the matching target that starts closest to the end,
     * -1 if none of the targets match.
     */
    public static int lastIndexOfEnd( String s, String... targets )
        {
        return lastIndexOf( s, s.length(), true, targets );
        }

    /**
     * find last of a number of possible targets, one closest to the end
     *
     * @param s       String to search in
     * @param base    offset where to start looking
     * @param targets multiple targets to search for
     *
     * @return index of the char one past the matching target that starts closest to the end,
     * -1 if none of the targets match.
     */
    public static int lastIndexOfEnd( String s, int base, String... targets )
        {
        return lastIndexOf( s, base, true, targets );
        }
    }
