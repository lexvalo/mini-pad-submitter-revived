/*
 * [CSVReadState.java]
 *
 * Summary: enumeration of the finite state automaton used to read CSV files.
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
 *  3.0 2009-06-15 lookup table to speed CSVReader
 *  3.1 2009-12-03 add CSVSort
 *  3.2 2010-02-23 add hex sort 9x+ option to CSVSort
 *  3.3 2010-11-14 change default to no comments in input file for CSVTab2Comma.
 *  3.4 2010-12-03 add CSV2SRS
 */
package com.mindprod.csv;

/**
 * enumeration of the finite state automaton used to read CSV files.
 * <p/>
 * We don't put more logic here since enums are static, and we need separate state for separate CSVReaders.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.4 2010-12-03 add CSVToSRS.
 * @since 2009-03-25
 */
public enum CSVReadState
    {
        /**
         * parser: We have just hit a quote, might be doubled or might be last one.
         */
        AFTER_END_QUOTE,
        /**
         * parser: We are in the middle of an ordinary field, possible full of blanks.
         */
        IN_PLAIN_FIELD,
        /**
         * parser: e are in middle of field surrounded in quotes.
         */
        IN_QUOTED_FIELD,
        /**
         * parser : we don't yet know if there is another non-comment field on the line.
         * In blanks prior to first field.
         */
        SEEKING_FIELD,
        /**
         * parser: We are in blanks before a field that we know is there, possibly empty, because we have seen
         * the comma after the previous field.
         */
        SEEKING_START,
        /**
         * parser: We are in blanks after a quoted field looking for the separator
         */
        SKIPPING_TAIL_AFTER_QUOTE
    }
