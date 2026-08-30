/*
 * [Build.java]
 *
 * Summary: Track the build number used for all applications.
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
 *  1.0 2007-09-19 added comment created with IntelliJ IDEA.
 *  1.1 2016-01-20 build 9603 new JDK 1 8 0 72
 *  1.2 2016-02-06 build 9605 new JDK 1 8 0 74
 *  1.3 2016-03-11 JET MP2
 *  1.4 2016-03-23 build 9610 new JDK 1 8 0 77
 *  1.5 2016-04-19 build 9612 new JDK 1 8 0 92
 *  1.6 2016-04-29 build 9613 new Jet MP3
 *  1.7 2016-05-11 jsse.enableSNIExtension=false
 *  1.8 2016-05-14 System.setProperty("jdk.tls.ephemeralDHKeySize", "2048");
 *  1.9 2016-05-21 move deleteAndRename.
 *  2.0 2016-05-22 propagate new CSVWriter that quotes properly
 *  2.1 2016-05-23 flush out uses of <td .../>
 *  2.2 2016-06-03 add ConfigurationForMindprodCa and ConfigurationForMindprodCom
 *  2.3 2016-06-03 split website in two.
 *  2.4 2016-06-12 invent JPrepConfiguration mini Configuration.
 *  2.5 2016-06-13 make JPrepConfiguration an interface. add text/javascript to Google ads.
 *  2.6 2016-06-17 correct requirement Java 1.7+ to 1.8+ universally.
 *  2.7 2016-06-22 incorporate fresh pads from ASP.
 *  2.8 2016-06-27 propagate FastCat, SortCode
 *  2.9 2016-07-03 propagate new Config with consistent naming conventions.
 *  3.0 2016-07-08 propagate improved ISBN error massages and SPLIT_ON_COMMA.
 *  3.1 2016-07-19 new JDK 1 8 0 102
 *  3.2 2016-08-21 new JET 11.3
 *  3.3 2016-08-24 tidy source
 *  3.4 2016-08-31 new IntelliJ version, encoding on all generated btm files.
 *  3.5 2016-09-14 propagate FTPDownload.Download
 *  3.6 2016-10-18 new JDK 1 8 0 112
 *  3.7 2016-10-21 revert to Jet 11.0
 *  3.8 2016-11-02 new Jet 11.3
 *  3.9 2017-01-01 year end
 *  4.0 2017-01-17 update to JDK 1 8 0 121
 *  4.1 2017-03-08 update to Jet profile 1 8 0 121
 *  4.2 2017-04-18 update to JDK 1 8 0 131
 *  4.3 2017-05-14 new signing timestamp service
 *  4.4 2017-07-10 new JET version 12.0
 *  4.5 2017-07-18 new Intellij version 2017.2
 *  4.6 2017-12-12 new IntelliJ version 2017.3
 */
package com.mindprod.common18;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

/**
 * Track the build number used for all applications.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 4.6 2017-12-12 new IntelliJ version 2017.3
 * @see com.mindprod.htmlmacros.support.JDKandJREVersions;
 * @since 2007-09-19
 */
public class Build
    {
    // declarations
    public static final String INTELLIJ_DIR = "IntelliJ IDEA 2017.3";

    public static final String INTELLIJ_FULL_VERSION = "2017.3.1";

    /**
     * incremented for each microrelease even when version numbers are not changed. Global to all apps.
     */
    public static final int BUILD_NUMBER = 9639;

    /**
     * current year, used for copyright, might differ from Misc.thisYear() if flip copyright a few days early or late
     */
    public static final int THIS_COPYRIGHT_YEAR = 2017;

    /**
     * version of JDK we used to build. repeated in JDKandJREVersion, must fix also.
     */
    public static final String JDK_FULL_VERSION = "1.8.0_131";

    /**
     * version of JRE we used to build. repeated in JDKandJREVersion, must fix also.
     */
    public static final String JRE_FULL_VERSION = "1.8.0_131";

    /**
     * version of Jet profile , repeated in JDKandJREVersion
     */
    public static final String JET_PROFILE_VERSION = "1.8.0_131";  // NOT necessarily most recent JDK 1.8 !!

    /**
     * version of Jet itself , repeated in JDKandJREVersion, Also com.which.FileType.JET_CURRENT_MP
     * Version we actually use
     */
    public static final String JET_VERSION = "jet12.0-pro-x86";

    /**
     * 64-bit version of Jet itself
     */
    public static final String JET_VERSION64 = "jet12.0-pro-amd64";

    public static final String JET_FULL_VERSION = "jet12.0-pro-x86";

    /**
     * name of the code signing cert without .cer
     */
    public static final String MINDPRODCERT = "mindprodcert2017rsa";

    /**
     * where the local files for the mindprod website are:
     */
    public static final String MINDPROD_SOURCE = "E:/com/mindprod";

    /**
     * JDKVersion
     * where the local files for the mindprod website are:
     */
    public static final String MINDPROD_WEBROOT = "E:/mindprod";

    /**
     * old version of JDK. repeated in JDKandJREVersion
     */
    public static final String OLD_JDK_FULL_VERSION = "1.7.0_79";

    /**
     * the user agent we pretend do be, Firefox
     */
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0";

    /**
     * Accept-Charset for header, no charset in response for binary files.
     */
    public static final String ACCEPT_CHARSET = "utf-8,iso-8859-1,utf-16;q=0.7,*;q=0.3";

    /**
     * default connectTimeout for http
     */
    public static final int CONNECT_TIMEOUT = ( int ) TimeUnit.SECONDS.toMillis( 50 );

    /**
     * default readTimeout for http
     */
    public static final int READ_TIMEOUT = ( int ) TimeUnit.SECONDS.toMillis( 40 );

    /**
     * Accept property for HTTP header
     */
    public static final String ACCEPT_MIMES = "application/octet-stream," +
                                              "application/x-java-jnlp-file," +
                                              "application/x-java-serialized-object," +
                                              "application/xhtml+xml," +
                                              "application/xml," +
                                              "application/zip," +
                                              "image/gif," +
                                              "image/jpeg," +
                                              "image/png," +
                                              "text/css," +
                                              "text/html," +
                                              "text/plain," +
                                              "text/x-java-source," +
                                              "text/xml," +
                                              "*;q=.2,*/*;q=.2";

    /**
     * background colour of most mindprod pages
     */
    public static final Color BACKGROUND_FOR_BLENDING = new Color( 0xf3fff6 );
    // /declarations
    }
