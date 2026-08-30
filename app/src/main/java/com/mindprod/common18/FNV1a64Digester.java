/*
 * [FNV1a64Digester.java]
 *
 * Summary: Quick 64-bit hashing function with a digester.
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
 *  1.1 2014-08-01 make conform to the Checksum interface.
 */
package com.mindprod.common18;

import java.nio.charset.Charset;
import java.util.zip.Checksum;

/**
 * Quick 64-bit hashing function with a digester.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2014-04-07 initial version.
 * @see com.mindprod.common18.FNV1a64
 * @since 2014-04-07
 */
public class FNV1a64Digester implements Checksum
    {
    // declarations

    /**
     * magic 64 bit initial value for the hash = 14,695,981,039,346,656,037, needs to be unsigned to fit in long.
     * faked unsigned.
     */
    @SuppressWarnings( "NumericOverflow" )
    private static final long MAGIC_OFFSET = 14_695_981_039_346_656_03L * 10 + 7;

    /**
     * prime specified to use in 64 bit hashes
     * magic 64 bit FNV_prime = 240 + 28 + 0xb3 = 1,099,511,628,211
     */
    private static final long MAGIC_PRIME = 1_099_511_628_211L;

    /**
     * encoding for UTF-8
     */
    private static final Charset UTF8 = Charset.forName( "UTF-8" );

    /**
     * where we accumulate the hash
     */
    private long hash;
    // /declarations

    /**
     * constructor
     */
    public FNV1a64Digester()
        {
        hash = MAGIC_OFFSET;
        }
    // methods

    /**
     * Returns the current checksum value.
     *
     * @return the current checksum value
     */
    public long getValue()
        {
        return hash;
        }// /method

    /**
     * clear, ready to start another has computation
     */
    public void reset()
        {
        hash = MAGIC_OFFSET;
        }// /method

    /**
     * Updates the current checksum with the specified byte.
     *
     * @param bchar the byte to update the checksum with. Parm is int, only low 8 bits should be used.
     */
    public void update( int bchar )
        {
        hash ^= bchar;
        hash *= MAGIC_PRIME;
        }// /method

    /**
     * calculate 64 bit hash on byte array
     */
    public void update( byte[] ba )
        {
        for ( byte b : ba )
            {
            hash ^= b;
            hash *= MAGIC_PRIME;
            }
        }// /method

    /**
     * Updates the current checksum with the specified array of bytes.
     *
     * @param ba  the byte array to update the checksum with
     * @param off the start offset of the data
     * @param len the number of bytes to use for the update
     */
    public void update( byte[] ba, int off, int len )
        {
        final int to = off + len;
        for ( int i = off; i <= to; i++ )
            {
            hash ^= ba[ i ];
            hash *= MAGIC_PRIME;
            }
        }// /method

    /**
     * add int to info to be hashed
     */
    public void update( long n )
        {
        for ( int shift = 56; shift >= 0; shift -= 8 )
            {
            hash ^= ( int ) ( n >>> shift ) & 0xff;
            hash *= MAGIC_PRIME;
            }
        }// /method

    /**
     * calculate 64 bit hash on string
     */
    public void update( String s )
        {
        final byte[] theTextToDigestAsBytes = s.getBytes( UTF8 );
        for ( byte b : theTextToDigestAsBytes )
            {
            hash ^= b;
            hash *= MAGIC_PRIME;
            }
        }// /method

    /**
     * calculate 64 bit hash on array of strings
     */
    public void update( String[] strings )
        {
        for ( String s : strings )
            {
            final byte[] theTextToDigestAsBytes = s.getBytes( UTF8 );
            for ( byte b : theTextToDigestAsBytes )
                {
                hash ^= b;
                hash *= MAGIC_PRIME;
                }
            }
        }// /method

    /**
     * add int to info to be hashed
     */
    public void updateInt( int i )
        {
        for ( int shift = 24; shift >= 0; shift -= 8 )
            {
            hash ^= ( i >>> shift ) & 0xff;
            hash *= MAGIC_PRIME;
            }
        }// /method
    // /methods
    }
