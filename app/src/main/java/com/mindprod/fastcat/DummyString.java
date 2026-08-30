/*
 * [DummyString.java]
 *
 * Summary: Demonstrate how a new efficient concatenating String constructor could work.
 *
 * Copyright: (c) 2009-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2009-09-29 initial release
 */
package com.mindprod.fastcat;

/**
 * Demonstrate how a new efficient concatenating String constructor could work.
 * <p/>
 * Constructor permits more efficient concatenation than StringBuffer or StringBuilder.
 * It uses no intermediate buffers and no intermediate character copying.
 * It might be used to more efficiently implement the + concatenation operator and StringWriter.
 * Why bother?  Webservers do almost nothing but concatenate Strings and garbage collection.
 * I have reported this as an RFE to Sun with ID 1620012.
 * <p/>
 * 1.0 2009-09-30 - initial release
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2009-09-29 initial release
 * @since 2009-09-30
 */
public class DummyString
    {
    // ------------------------------ FIELDS ------------------------------

    /**
     * The value is used for character storage.
     */
    private final char value[];

    /**
     * The count is the number of characters in the String.
     */
    private final int count;

    /**
     * The offset is the first index of the storage that is used.
     */
    private final int offset;
    // -------------------------- PUBLIC INSTANCE  METHODS --------------------------

    /**
     * Initializes a newly created {@code String} object so that it represents
     * an empty character sequence.  Note that use of this constructor is
     * unnecessary since Strings are immutable.
     */
    public DummyString()
        {
        this.offset = 0;
        this.count = 0;
        this.value = new char[ 0 ];
        }

    /**
     * concatenating String constructor
     *
     * @param pieces vararg of Strings to be concatenated to create this new String.
     */
    public DummyString( String... pieces )
        {
        offset = 0;
        int joinedLength = 0;
        /** find out how big a buffer we need to contain all the joined Strings */
        for ( String piece : pieces )
            {
            joinedLength += piece.length();
            }
        // allocate buffer for joined pieces. This becomes the new String.
        count = joinedLength;
        value = new char[ joinedLength ];
        // copy over all the pieces into the slot in the joined buffer
        int position = 0;
        for ( String piece : pieces )
            {
            final int length = piece.length();
            // copy piece into buffer with System.arraycopy
            piece.getChars( 0, length, value, position );
            position += length;
            }
        }
    }
