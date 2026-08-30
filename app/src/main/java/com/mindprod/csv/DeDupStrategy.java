/*
 * [DeDupStrategy.java]
 *
 * Summary: Strategies for what to do with duplicate when DeDuping csv files.
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
 *  1.0 2012-01-19 initial version
 */
package com.mindprod.csv;

/**
 * Strategies for what to do with duplicate when DeDuping csv files.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2012-01-19 initial version
 * @since 2012
 */
public enum DeDupStrategy
    {
        KEEP_FIRST /* keep the first of a set of duplicates */,
        KEEP_LAST /* keep the last of a set of duplicates */,
        DELETE /* delete all duplicates */
    }
