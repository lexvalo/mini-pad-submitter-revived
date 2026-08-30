/*
 * [Keyword.java]
 *
 * Summary: enum of PAD site keywords.
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
 *  1.0 2011-03-14 initial version
 *  1.1 2012-11-09 use EnumSets instead of arrays of Strings for contains logic.
 */
package com.mindprod.submitter;

import com.mindprod.common18.ST;
import com.mindprod.entities.DeEntifyStrings;
import com.mindprod.fastcat.FastCat;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.regex.Pattern;

/**
 * enum of PAD site keywords.
 * <p/>
 * Use to prepare mindprod.com HTML versions of hassle, no-hassle or candidate pad submission sites.
 * This factors out common code that was previously duplicated with theme and variations in the utilities.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2012-11-09 use EnumSets instead of arrays of Strings for contains logic.
 * @since 2011-03-14
 */
enum Keyword
    {
        // comments will be sorted into this order, so put most important first.
        ACTIVE( "Active", "Actively responding.", false ),
        APPVISOR( "AppVisor", "Submit via <a class=\"com\" href=\"http://publisher.appvisor.com/\">AppVisor</a>.", false ),
        CANDIDATE( "Candidate",
                "Candidate site to be possibly included in the hassle-free sites supported by the mini PAD submitter" +
                ".", false
        ),
        ADD( "Add",
                "Candidate site to be <strong>definitely</strong> included in the hassle-free sites supported by the " +
                "mini PAD submitter.", false
        ),
        DELETE( "Delete",
                "Site to be deleted from the list of hassle-free sites supported by the mini PAD submitter.", false ),
        TOP10( "TOP10", "<span class=\"top10\">Ranked in the top 10.</span>", false ),
        TOP20( "TOP20", "<span class=\"top20\">Ranked in the top 20.</span>", false ),
        TOP30( "TOP30", "<span class=\"top30\">Ranked in the top 30.</span>", false ),
        TOP100( "TOP100", "<span class=\"top100\">Ranked in the top 100.</span>", false ),
        // more serious problems are at the top
        RECOMMENDED( "Recommended", "<span class=\"rec\">Recommended.</span>", false ),
        ONION( "Onion", "<span class=\"onion\">Not recommended.</span>", false ),
        MALFORMEDURL( "MalformedURL", "Malformed URL.", true ),
        DUPLICATE( "Duplicate", "Duplicate.", true ),
        NOSUBMISSIONPAGE( "NoSubmissionPage", "Cannot find PAD submission page.", true ),
        NOTACCEPTING( "NotAccepting", "Not currently accepting PAD submissions.", true ),
        NOTRESPONDING( "NotResponding", "Not Responding and domain abandoned.", true ),
        ATTACK( "Attack", "Firefox accuses this site of covertly installing malware.", false ),
        MALWARE( "Malware", "Chrome or Firefox accuses this site of listing malware.", false ),
        PARKED( "Parked", "Site parked and domain abandoned.", true ),
        STALLS( "Stalls", "Stalls ignoring timeout when Java probes the site.", true ),
        DOWN( "Down", "Site temporarily down.", false ),
        CRAPWARE( "Crapware", "<span class=\"crapware\">When you download programs from this site, they use a "
                              + "custom downloader/installer that installs a number of "
                              + "unwanted, unrelated extra crapware programs.</span>", false ),
        BROKEN( "Broken", "Site has bugs that prevent PAD submission.", true ),
        EXPIRED( "Expired", "Expired SSL Certificate", false ),
        RESURRECTED( "Resurrected", "Site was off the air for a while, but it is back.", false ),
        OLDSPEC( "OldSpec", "They support only an obsolete (pre 3.0) versionof the PAD spec.", false ),
        // use Pay  CHARGE( "Charge", "They charge to list your PAD.", false ),
        DEAD( "Dead", "Site dead &mdash; suspended, closed, disappeared or repurposed.", true ),
        BLIND( "Blind", "The onerous way they make you type the security code, you might as well be blind.", false ),
        CAPTCHA( "Captcha",
                "Must prove your worthiness by keying an almost-impossible-to-decipher <span " +
                "class=\"captcha\">Captcha</span>. "
                + "They have contempt for programmers. "
                + "They think that programmers have nothing better to do with their time than as tediously as " +
                "possible manually submit programs. "
                + "They forget that the programmers are doing <strong>them</strong> a favour.", false
        ),
        VALIDATION( "Validation",
                "You must key a legible validation code to prove you are not using automated software submission.",
                false ),
        HIDDENVALIDATION( "HiddenValidation", "Hidden validation code to deter automated submission.", false ),
        OBFUSCATED( "Obfuscated", "Obfuscated to deter automated submission.", false ),
        SKILL( "Skill", "You must answer a skill-testing question.", false ),
        LOGIN( "Login", "You must set up an account and login with a password.", false ),
        FORM( "Form", "Does not use PADs. You must fill in a form.", false ),
        // Restricted types of submission
        NOOPERA( "NoOpera", "No Opera.", false ),
        PAY( "Pay", "You must pay to list.", false ),
        GAMES( "Games", "Games only.", false ),
        SCREENSAVER( "Screensaver", "Screensavers only.", false ),
        SITELINKS( "SiteLinks", "Site links only.", true ),
        HANDHELD( "Handheld", "Software for handhelds only.", false ),
        ARTICLELINKS( "ArticleLinks", "Article links only.", false ),
        SIXTYFOURBIT( "64-bit", "64-bit software only.", false ),
        MAC( "Mac", "Macintosh only.", false ),
        PALM( "Palm", "Palm only.", false ),
        UNIX( "Unix", "Unix only.", false ),
        REGNOW( "RegNow", "Only software sold via RegNow/Bluesnap (n&eacute;e Plimus)/eSellerate etc.", false ),
        SHAREWARE( "Shareware", "Shareware only. No freeware.", false ),
        EDUCATIONAL( "Educational", "Educational software only.", false ),
        FINANCIAL( "Financial", "Financial software only.", false ),
        AUDIO( "Audio", "Audio software only.", false ),
        MOBILE( "Mobile", "Mobile software only.", false ),
        MULTIMEDIA( "Multimedia", "Multimedia software only.", false ),
        WINDOWS( "Windows", "Windows only.", false ),
        FREEWARE( "Freeware", "Freeware only.", false ),
        UPLOAD( "Upload", "You don&rsquo;t submit an URL, you upload the PAD file off your local hard disk.", false ),
        VIEWSTATE( "ViewState", "Complicated state information, difficult to automate.", false ),
        NOAUTO( "NoAuto", "They explicitly ask you not to use any automation in PAD submission.", false ),
        // proprietary category
        PROPRIETARY( "Proprietary", "You must select a proprietary category for the PAD.", false ),
        // minor hassles
        BACKLINKHOME( "BackLinkHome",
                "They have the cheek to demand you put a back-link ad for them on your home page (or possibly a page " +
                "linked directly from your home page). Screw em! You must have the back link set up and uploaded " +
                "before you submit your PAD.", false
        ),
        BACKLINKMANDATORY( "BackLinkMandatory",
                "Must provide a backlink from your website to theirs and inform them where it is. You must have the " +
                "back link set up and uploaded to your website before you submit your PAD.", false
        ),
        BACKLINKTIP( "BackLinkTip",
                "To ensure prompt processing, they request you provide a backlink from your website to theirs and " +
                "inform them where it is. You must have the back link set up and uploaded before you submit your PAD" +
                ".", false
        ),
        BACKLINKOPTIONAL( "BackLinkOptional",
                "They request you provide an optional backlink from your website to theirs and inform them where it " +
                "is. You must have the back link set up and uploaded before you submit your PAD.", false
        ),
        REKEY( "Rekey", "You must manually rekey some of the fields in the PAD.", false ),
        HEAD( "Head",
                "They probe your website for the PAD, screenshot, zip etc. using an abbreviated HTTP HEAD rather than" +
                " a full HTTP GET. This may lead them to complain about missing files that are indeed present or fail" +
                " to detect PAD changes.", false
        ),
        PREFILL( "Prefill",
                "You press a PREFILL button to display the contents of the PAD and then you edit the fields before " +
                "final submission.",
                false
        ),
        CONFIRM( "Confirm", "You must confirm your PAD submission by visiting a link sent via email.", false ),
        NOPROBE( "NoProbe",
                "You must resubmit after every version change because they fail to periodically probe your PAD for " +
                "changes.", false
        ),
        SLOW( "Slow", "Site is unusually slow to respond. Please be patient.", false ),
        VIAEMAIL( "ViaEmail", "Submit your PAD via email.", false ),
        STRICT( "Strict", "They have their own stricter-than-ASP standards for validating PADs.", false ),
        // foreign languages , we allow more that one per site.
        ARABIC( "Arabic" ),
        BULGARIAN( "Bulgarian" ),
        CHINESE( "Chinese" ),
        CZECH( "Czech" ),
        DANISH( "Danish" ),
        DUTCH( "Dutch" ),
        ENGLISH( "English" ),
        ESPERANTO( "Esperanto" ),
        FRENCH( "French" ),
        GERMAN( "German" ),
        GREEK( "Greek" ),
        HUNGARIAN( "Hungarian" ),
        INDIAN( "Indian" ),
        INDONESIAN( "Indonesian" ),
        ITALIAN( "Italian" ),
        JAPANESE( "Japanese" ),
        POLISH( "Polish" ),
        PORTUGUESE( "Portuguese" ),
        ROMANIAN( "Romanian" ),
        RUSSIAN( "Russian" ),
        SPANISH( "Spanish" ),
        SWEDISH( "Swedish" ),
        THAI( "Thai" ),
        TURKISH( "Turkish" ),
        UKRANIAN( "Ukranian" ),
        JAVASCRIPT( "JavaScript", "The site is obfuscated with JavaScript.", false ),
        // pluses
        FORMER( "Former", "Formerly hassle-free.", false ),
        ALMOST( "Almost", "Almost hassle-free.", false ),
        NOLOGO( "NoLogo", "They have not customised their site with a logo.", false ),
        ALTUPLOAD( "AltUpload", "You may alternately upload the PAD file off your local hard disk.", false ),
        SCAVENGER( "Scavenger",
                "You may not even have to submit your PAD to them. They go out scouring the web for PADs.", false ),
        MULTIPLE( "Multiple", "You may submit multiple PADs at once.", false ),
        PROPAGATE( "Propagate",
                "They pass the PADs you submit to other PADSites.", false ),
        PADRING( "PADRING",
                "They support the <span class=\"term\">PADRING</span> protocol so that submitting one PAD submits " +
                "your whole catalog.", false
        ),
        PADMAPS( "PADmaps",
                "<span class=\"padmaps\">They support the <span class=\"term\">PADmaps</span> protocol so that " +
                "submitting one PAD submits your whole catalog.</span>", false
        ),
        WARN( "Warn", "They warn you of subtle problems with the PAD.", false ),
        NOTIFY( "Notify",
                "They notify you by email when they notice your PAD or the files it references change or become " +
                "inaccessible.", false
        ),
        BLACKLIST( "Blacklist", "<em>They <strong>blacklisted</strong> me without explanation and without possibility" +
                                " of appeal. " +
                                "It could be because I wrote the mini PAD Submitter, I am gay, atheist, " +
                                "anti-war&hellip;, " +
                                "because I hold many unpopular opinions, because I " +
                                "submitted such a large catalog of software, because I frequently release new builds," +
                                " " +
                                "because they saw my name on a blacklist somewhere or they just made an error.</em>",
                false
        );

    /**
     * used to split keywords list  into words
     */
    private static final Pattern SPACE_SPLITTER = Pattern.compile( "\\s+" );

    /**
     * enumset of keyword that imply padsite is dead
     */
    private static final EnumSet<Keyword> DEAD_KEYWORDS = buildDeadKeywords();

    /**
     * official name of for the keyword, mixed case.
     */
    private final String canonical;

    /**
     * the meaning of the keyword.
     */
    private final String meaning;

    /**
     * true if the presence of this keyword implies the padsite is dead.
     */
    private final boolean impliesDead;

    /**
     * constructor for a language Keyword.  Name of language is sufficient without further explanation.
     *
     * @param canonical official name of for the keyword, mixed case.
     */
    Keyword( final String canonical )
        {
        this.canonical = canonical;
        this.meaning = null;
        this.impliesDead = false;
        }

    /**
     * constructor
     *
     * @param canonical   official name of for the keyword, mixed case.
     * @param meaning     the meaning of the keyword.
     * @param impliesDead true if the presence of this keyword implies the padsite is dead.
     */
    Keyword( final String canonical, final String meaning, final boolean impliesDead )
        {
        this.canonical = canonical;
        this.meaning = meaning;
        this.impliesDead = impliesDead;
        }

    /**
     * Get Enumset  of Keywords corresponding to the list of space separated keywords.
     * Implicitly dedups and puts in canonical order.
     *
     * @param keywords space-separated string of Keyword names
     *
     * @return EnumSet of Keywords, possibly empty. Implied enum order.
     */
    static EnumSet<Keyword> asSet( String keywords ) throws IllegalArgumentException
        {
        if ( ST.isEmpty( keywords ) )
            {
            return EnumSet.noneOf( Keyword.class );
            }
        final String[] individualKeywordStrings = SPACE_SPLITTER.split( keywords.trim() );
        final EnumSet<Keyword> build = EnumSet.noneOf( Keyword.class );
        for ( String individualKeywordString : individualKeywordStrings )
            {
            build.add( Keyword.valueOfAlias( individualKeywordString ) );
            }
        return build;
        }

    /**
     * used to initialised DEAD_KEYWORDS
     *
     * @return enumset of keywords that imply site is dead
     */
    private static EnumSet<Keyword> buildDeadKeywords()
        {
        EnumSet<Keyword> build = EnumSet.noneOf( Keyword.class );
        assert Keyword.values().length > 0 : "Oops no Keyword.values not yet initialised.";
        for ( Keyword keyword : Keyword.values() )
            {
            if ( keyword.impliesDead() )
                {
                build.add( keyword );
                }
            }
        return build;
        }

    /**
     * Does this set of Keywords contain any of a set of relevant keywords
     *
     * @param have     set of keywords we have
     * @param relevant set of keywords that are relevant.
     *
     * @return true if have contains any of the relevant keywords
     */
    @SuppressWarnings( "WeakerAccess" )
    static boolean containsAnyOf( EnumSet<Keyword> have, EnumSet<Keyword> relevant )
        {
        for ( Keyword keyword : have )
            {
            if ( relevant.contains( keyword ) )
                {
                return true;
                }
            }
        return false;
        }

    /**
     * Get space-separated list of meanings corresponding to the list of space separated keywords.
     *
     * @param keywordEnums EnumSet of keywords separated by space.
     *
     * @return meanings of keywords, sentences separated by spaces.
     * @see #asSet(String)
     */
    static String getCorrespondingMeanings( EnumSet<Keyword> keywordEnums )
        {
        if ( keywordEnums.isEmpty() )
            {
            return "";
            }
        int languages = 0;
        // size gives you the current number elements in the set, not Keyword.values().length
        final FastCat sb = new FastCat( keywordEnums.size() * 2 + 1 );
        for ( Keyword keyword : keywordEnums )
            {
            if ( keyword.meaning == null )
                {
                languages++;   // count languages used on first pass, but don't display them.
                }
            else
                {
                sb.append( keyword.meaning );  // leave embedded HTML decoration in place.
                sb.append( " " );
                }
            }
        // generate list of languages supported.
        if ( languages > 0 )
            {
            if ( languages == 1 )
                {
                sb.append( "In " );
                }
            else
                {
                sb.append( "Languages supported include " );
                }
            for ( Keyword keyword : keywordEnums )
                {
                if ( keyword.meaning == null )
                    {
                    sb.append( keyword.canonical );
                    languages--;
                    switch ( languages )
                        {
                        case 0:
                            sb.append( "." );
                            break;
                        case 1:
                            sb.append( " and " );
                            break;
                        default:
                            sb.append( ", " );
                        }
                    }
                }
            }
        return sb.toString().trim();
        }

    /**
     * Does this set of Keywords contain any of a set of keywords that implies this site is dead
     *
     * @param have set of keywords we have
     *
     * @return true if have contains any of the relevant keywords
     * @see #impliesDead()
     */
    static boolean impliesDead( EnumSet<Keyword> have )
        {
        for ( Keyword keyword : have )
            {
            if ( DEAD_KEYWORDS.contains( keyword ) )
                {
                return true;
                }
            }
        return false;
        }

    /**
     * Does this Padsite need an image.  It usually does if it supports PADMaps.
     *
     * @param keywordEnums which keywords this padsite has
     *
     * @return true if absence/presence of image is consistent with keywords.
     */
    static boolean isConsistentWithImage( EnumSet<Keyword> keywordEnums, String image )
        {
        if ( ST.isEmpty( image ) )
            {
            return Keyword.impliesDead( keywordEnums ) ||
                   !( keywordEnums.contains( Keyword.PADMAPS )
                      && !Keyword.containsAnyOf( keywordEnums,
                           EnumSet.of( Keyword.CANDIDATE, Keyword.NOLOGO ) ) );
            }
        else
            {
            return !keywordEnums.contains( Keyword.NOLOGO );
            }
        }

    /**
     * Delete a keyword form a string of keywords
     *
     * @param keywords        keywords separated by space.
     * @param keywordToDelete keyword to delete.
     *
     * @return keywords sorted an de-duped space separated.
     */
    static String removeKeyword( String keywords, Keyword keywordToDelete )
        {
        // will implicitly dedup and put in canonical order.
        final EnumSet<Keyword> keywordEnums = asSet( keywords );
        final FastCat sb = new FastCat( keywordEnums.size() );
        for ( Keyword keyword : keywordEnums )
            {
            if ( keyword != keywordToDelete )
                {
                sb.append( keyword.canonical );
                }
            }
        return sb.toSpaceList();
        }

    /**
     * Sort and dedup the list of keywords, give canonical spellings.
     *
     * @param keywords keywords separated by space.
     *
     * @return keywords sorted an de-duped space separated.
     */
    static String tidyKeywords( String keywords ) throws IllegalArgumentException
        {
        // will implicitly dedup and put in canonical order.
        final EnumSet<Keyword> keywordEnums = asSet( keywords );
        final FastCat sb = new FastCat( keywordEnums.size() * 2 );
        for ( Keyword keyword : keywordEnums )
            {
            sb.append( keyword.canonical );
            sb.append( " " );
            }
        return sb.toString().trim();
        }

    /**
     * true if the presence of this keyword implies the padsite is dead.
     *
     * @see #impliesDead(java.util.EnumSet)
     */
    boolean impliesDead()
        {
        return impliesDead;
        }

    /**
     * convert alias string to equivalent canonical enum constant, like valueOf but accepts aliases matching the
     * alias name too, and does not care about case.
     *
     * @param s alias as string.
     *
     * @return equivalent BreedA enum constant.
     * @noinspection WeakerAccess
     */
    public static Keyword valueOfAlias( String s )
        {
        try
            {
            return valueOf( s.toUpperCase() );
            }
        catch ( IllegalArgumentException e )
            {
            // usual method failed, try looking up alias
            // This seems long winded, why no HashSet?
            // Because Java won't let me access a static common
            // lookup in the enum constructors. There are problems with initialisation
            // enum constants at static init time.
            // See notes at http://mindprod.com/jgloss/enum.html on Piotr Kobza's
            // kludge to get one.
            for ( Keyword candidateEnum : Keyword.values() )
                {
                if ( candidateEnum.canonical.equalsIgnoreCase( s ) )
                    {
                    return candidateEnum;
                    }
                }
            // fell out the bottom of search over all enums and aliases
            // give up.
            throw new IllegalArgumentException( "unknown Keyword: [" + s + "]" );
            }
        }

    /**
     * get the canonical mixed case name for this enum constant
     *
     * @return mixed cane enum name
     */
    public String getCanonical()
        {
        return canonical;
        }

    /**
     * get the meaning of this enum constant
     *
     * @return phrase describing the enum constant
     */
    public String getMeaning()
        {
        return ( meaning == null ) ? canonical : meaning;
        }

    /**
     * get the meaning of this enum constant, without any HTML tags in it
     *
     * @return phrase describing the enum constant
     */
    public String getUndecoratedMeaning()
        {
        return ( meaning == null ) ? canonical : DeEntifyStrings.flattenHTML( meaning, ' ' );
        }
    }

/**
 * Sort Keywords alphabetically.
 * <p/>
 * Defines an alternate sort order for Keyword. Used in TidyKeywords to generate comment headers for *.csv files.
 *
 * @author ...
 * @version 1.0 2011-09-16 - initial release
 * @see TidyKeywords#main(String[])
 * @since 2011-09-16
 */
class KeywordsAlphabetically implements Comparator<Keyword>
    {
    /**
     * Sort Keywords alphabetically.
     * Defines an alternate sort order for Keyword with JDK 1.5+ generics.
     * Compare two Keyword Objects.
     * Compares canonical case insensitively.
     * Informally, returns (a-b), or +ve if a sorts after b.
     * The Java source code for this Comparator was generated by the
     * Canadian Mind Products ComparatorCutter Applet at http://mindprod.com/applet/comparatorcutter.html
     *
     * @param a first Keyword to compare
     * @param b second Keyword to compare
     *
     * @return +ve if a&gt;b, 0 if a==b, -ve if a&lt;b
     */
    public final int compare( Keyword a, Keyword b )
        {
        return a.getCanonical().compareToIgnoreCase( b.getCanonical() );
        }
    }
