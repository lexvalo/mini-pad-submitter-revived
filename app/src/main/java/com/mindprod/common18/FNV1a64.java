/*
 * [FNV1a64.java]
 *
 * Summary: Quick 64-bit hashing function.
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
 *  1.0 2014-04-29 initial version.
 */
package com.mindprod.common18;

import java.nio.charset.Charset;

/**
 * Quick 64-bit hashing function.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2014-04-29 initial version.
 * @see com.mindprod.common18.FNV1a64Digester
 * @since 2014-04-29
 */
public class FNV1a64
    {
    /**
     * encoding for UTF-8
     */
    private static final Charset UTF8 = Charset.forName( "UTF-8" );

    /**
     * magic 64 bit initial value for the hash = 14,695,981,039,346,656,037, needs to be unsigned to fit in long.
     * faked unsigned.
     */
    @SuppressWarnings( "NumericOverflow" )
    private static final long MAGIC_OFFSET = 14_695_981_039_346_656_03L * 10 + 7;

    /**
     * prime specified to use in 64 bit hashes
     * magic 64 bit FNV_prime = 2^40 + 2^8 + 0xb3 = 1,099,511,628,211
     */
    private static final long MAGIC_PRIME = 1_099_511_628_211L;

    /**
     * calculate 64 bit hash on array of strings
     */
    public static long computeHash( String[] strings )
        {
        long hash = MAGIC_OFFSET;
        for ( String s : strings )
            {
            final byte[] theTextToDigestAsBytes = s.getBytes( UTF8 );
            for ( byte b : theTextToDigestAsBytes )
                {
                hash ^= b & 0xff;  // account for strange signedness of byte
                hash *= MAGIC_PRIME;
                }
            }
        return hash;
        } // /method

    /**
     * calculate 64 bit hash on string
     */
    public static long computeHash( String s )
        {
        long hash = MAGIC_OFFSET;
        final byte[] theTextToDigestAsBytes = s.getBytes( UTF8 );
        for ( byte b : theTextToDigestAsBytes )
            {
            hash ^= b & 0xff;  // account for strange signedness of byte
            hash *= MAGIC_PRIME;
            }
        return hash;
        } // /method

    /**
     * calculate 64 bit hash on byte array
     */
    public static long computeHash( byte[] ba )
        {
        long hash = MAGIC_OFFSET;
        for ( byte b : ba )
            {
            hash ^= b & 0xff;  // account for strange signedness of byte
            hash *= MAGIC_PRIME;
            }
        return hash;
        } // /method
    }
