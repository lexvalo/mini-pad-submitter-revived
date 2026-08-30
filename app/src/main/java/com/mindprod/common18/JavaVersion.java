/*
 * [JavaVersion.java]
 *
 * Summary: enumeration to describe what features various versions of Java support.
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
 *  1.0 2012-05-26 initial version.
 *  1.1 2014-08-01 add JDK 1.8
 */
package com.mindprod.common18;

/**
 * enumeration to describe what features various versions of Java support.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2014-08-01 add JDK 1.8
 * @since 2012-05-26
 */
public enum JavaVersion
    {
        J11( "1.1", false, false, false, false, false, false ),
        J12( "1.2", false, true, false, false, false, false ),
        J13( "1.3", true, true, false, false, false, false ),
        J14( "1.4", true, true, true, false, false, false ),
        J15( "1.5", true, true, true, true, false, false ),
        J16( "1.6", true, true, true, true, true, false ),
        J17( "1.7", true, true, true, true, true, true ),
        J18( "1.8", true, true, true, true, true, true );

    /**
     * e.g. 1.1 Java Version
     */
    private final String versionString;

    /**
     * e.g. true if version supports annotations
     */
    private final boolean supportsAnnotations;

    /**
     * e.g. true if version supports Collections
     */
    private final boolean supportsCollections;

    /**
     * e.g. true if version supports enums
     */
    private final boolean supportsEnums;

    /**
     * e.g. true if version supports generics
     */
    private final boolean supportsGenerics;

    /**
     * e.g. true if version supports Integer.compare
     */
    private final boolean supportsIntegerCompare;

    /**
     * e.g. true if version supports swing
     */
    private final boolean supportsSwing;

    /**
     * constructor
     *
     * @param versionString          e.g. "1.1"
     * @param supportsCollections    true if this version supports Collections
     * @param supportsSwing          true if this version supports swing
     * @param supportsEnums          true if this version supports enums
     * @param supportsGenerics       true if this version supports generics
     * @param supportsAnnotations    true if this version supports annotations
     * @param supportsIntegerCompare true if this version supports Integer.compare
     */
    JavaVersion( final String versionString, final boolean supportsCollections, final boolean supportsSwing,
                 final boolean supportsEnums, final boolean supportsGenerics, final boolean supportsAnnotations,
                 final boolean supportsIntegerCompare )
        {
        this.versionString = versionString;
        this.supportsAnnotations = supportsAnnotations;
        this.supportsCollections = supportsCollections;
        this.supportsEnums = supportsEnums;
        this.supportsGenerics = supportsGenerics;
        this.supportsIntegerCompare = supportsIntegerCompare;
        this.supportsSwing = supportsSwing;
        }

    /**
     * does this version support annotations
     *
     * @return true if it does
     */
    public boolean supportsAnnotations()
        {
        return supportsAnnotations;
        }

    /**
     * does this version support Collections.
     *
     * @return true if it does
     */
    public boolean supportsCollections()
        {
        return supportsCollections;
        }

    /**
     * does this version support enums.
     *
     * @return true if it does
     */
    public boolean supportsEnums()
        {
        return supportsEnums;
        }

    /**
     * does this version support generics
     *
     * @return true if it does
     */
    public boolean supportsGenerics()
        {
        return supportsGenerics;
        }

    /**
     * does this version support Integer.compare
     *
     * @return true if it does
     */
    public boolean supportsIntegerCompare()
        {
        return supportsIntegerCompare;
        }

    /**
     * does this version support Swing
     *
     * @return true if it does
     */
    public boolean supportsSwing()
        {
        return supportsSwing;
        }

    public String toString()
        {
        return versionString;
        }
    }
