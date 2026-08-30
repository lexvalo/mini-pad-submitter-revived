/*
 * [CSVCharCategory.java]
 *
 * Summary: enumeration of the classes of characters processed by the CSV finite state automaton.
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
 *  1.0 2009-03-24 initial version
 */
package com.mindprod.csv;

/**
 * enumeration of the classes of characters processed by the CSV finite state automaton.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2009-03-24 initial version
 * @since 2009-03-24
 */
public enum CSVCharCategory
    {
        COMMENT_START,
        ORDINARY,
        QUOTE,
        SEPARATOR,
        WHITESPACE
    }
