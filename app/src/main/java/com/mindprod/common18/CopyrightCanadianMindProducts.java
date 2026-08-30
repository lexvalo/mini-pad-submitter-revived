/*
 * [CopyrightCanadianMindProducts.java]
 *
 * Summary: embedded copyright annotation.
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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * embedded copyright annotation.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.0 2005-01-01 initial version
 * @since 2005-01-01
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@interface CopyrightCanadianMindProducts
    {
    static final int FIRST_COPYRIGHT_YEAR = 1998;
    /**
     * copyright to embed via annotations so it is embedded in all classes that reference it
     */
    String defaultCopyright =
            "Copyright: (c) 1998-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * get value of this annotation
     *
     * @return the copyright string
     */
    String value() default defaultCopyright;
    }
