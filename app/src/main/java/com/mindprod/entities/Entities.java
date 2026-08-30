/*
 * [Entities.java]
 *
 * Summary: Generates various text files describing entities.
 *
 * Copyright: (c) 2004-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.0 2004-01-01 initial version
 *  1.1 2004-01-01 optimise using
 *                 text.indexOf('&amp;') and sb.append(string) rather
 *                 than processing character by character.
 *  1.2 2004-07-21 add stripHTMLTags -
 *                 stripFile also strips tags - add stripNbsp
 *  1.3 2005-06-20 fix bug in possEntityToChar
 *                 exposed possEntityToChar as public
 *  1.4 2005-07-02 check for null input
 *  1.5 2005-07-29 no longer needs entitiestochar.ser
 *                 file. Converted to JDK 1.5 back to 1,2
 *  1.6 2005-09-05 faster code for stripHTMLTags that
 *                 returns original string if nothing changed.
 *  1.7 2005-09-05
 *  1.8 2007-02-26 fix bug. hex entity it &#xffff; not &x#ffff;
 *  1.9 2007-03-26
 *  2.0 2007-04-26
 *  2.1 2007-05-10
 *  2.2 2007-05-14 StripHTMLTags now strips applet, style, script pairs.
 *                 generate hex entities as comments in entitycase.javafrag
 *  2.3 2008-07-29 refactor code for Entities, add notes to chars, add a few new missing Entities.
 *  2.4 2008-08-05 add translateNbspTo parameter to several methods and deprecate the versions without it.
 *                 This allows you to directly control how &nbsp; is translated, usually ' ' or (char)160.
 *                 Renamed methods to make it clearer just what sort of input is expect.
 *  2.5 2008-08-06 add ability to insert XML entities. Convert to JDK 1.5+, with generics, and for:each, StringBuilder
 *  2.6 2009-04-05 StripEntities now leaves a space behind when it removes a <br><p><td> etc tag.
 *  2.7 2009-11-14 generate a table for the HTML cheat sheet of quote-like entities.
 *  2.8 2009-12-22 export table on HTML 5 entities. Now import csv file rather than embed entity facts.
 *  2.9 2010-01-29 export XHTML entities (currently same as HTML-4 entities).
 *  3.0 2011-02-10 rename classes and methods. Add file utilities for Entify, DeEntify, Flatten.
 *                 StripEntities -> DeEntifyStrings,
 *                 InsertEntities -> EntifyStrings,
 *                 stripHTMLEntities -> deEntifyHTML,
 *                 insertHTMLEntities -> entifyHTML.
 *  3.1 2011-09-02 correct error in tables reversing Y dierisis and y dieresis. Correct upper/lower categories in table.
 *  3.2 2014-08-02 add code b, and the space category.
 *  3.3 2014-08-24 add musical symbols and support for chars without alpha entities.
 */
package com.mindprod.entities;

import com.mindprod.common18.BigDate;
import com.mindprod.common18.EIO;
import com.mindprod.common18.ST;
import com.mindprod.csv.CSVReader;
import com.mindprod.fastcat.FastCat;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.System.*;

/**
 * Generates various text files describing entities.
 * <p/>
 * see main for details.
 * <p/>
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 3.3 2014-08-24 add musical symbols and support for chars without alpha entities.
 * @noinspection WeakerAccess
 * @see DeEntify
 * @see DeEntifyStrings
 * @see Entify
 * @see EntifyStrings
 * @see Flatten
 * @since 2004-01-01
 */
public final class Entities
    {
    /**
     * Longest an HTML 4 entity can be, at least in our tables, including the lead & and trail ;.
     */
    static final int LONGEST_HTML4_ENTITIY = "&thetasysm;".length();

    /**
     * Longest an HTML 5 entity can be, at least in our tables, including the lead & and trail ;.
     */
    static final int LONGEST_HTML5_ENTITY = "&CounterClockwiseContourIntegral;".length();

    private static final int FIRST_COPYRIGHT_YEAR = 2004;

    /**
     * undisplayed copyright notice
     *
     * @noinspection UnusedDeclaration
     */
    private static final String EMBEDDED_COPYRIGHT =
            "Copyright: (c) 2004-2017 Roedy Green, Canadian Mind Products, http://mindprod.com";

    /**
     * @noinspection UnusedDeclaration
     */
    private static final String RELEASE_DATE = "2014-08-24";

    /**
     * title of this package.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String TITLE_STRING = "Entities";

    /**
     * version of this code.
     *
     * @noinspection UnusedDeclaration
     */
    private static final String VERSION_STRING = "3.3";

    /**
     * used to print 8 items per line.
     */
    private static int counter = 0;

    /**
     * entities for the HTML Cheat sheet.
     */
    private static PrintWriter entityArrow;

    /**
     * entities for the HTML Cheat sheet.
     */
    private static PrintWriter entityCurrency;

    /**
     * entities as case statements to convert number to entity.
     */
    private static PrintWriter entityCase;

    /**
     * entities as case statements to use in StripQuotes to get rid of accented chars
     */
    private static PrintWriter entityCaseHex;

    /**
     * Russian numeric entities
     */
    private static PrintWriter entityCyrillic;

    /**
     * entities for the Cyrillic-5 entity list
     */
    private static PrintWriter entityCyrillic5;

    /**
     * entities for the Diacritical/accents entity list
     */
    private static PrintWriter entityDiacritic;

    /**
     * entities for the HTML Cheat sheet.
     */
    private static PrintWriter entityGreek;

    /**
     * entities for the HTML-5 entity list
     */
    private static PrintWriter entityHTML5;

    /**
     * pure hex entities.
     */
    private static PrintWriter entityHex;

    /**
     * entity names for musical symbols.
     */
    private static PrintWriter entityMusic;

    /**
     * entity names without &;.
     */
    private static PrintWriter entityJustKeys;

    /**
     * character codes.
     */
    private static PrintWriter entityJustValues;

    /**
     * entities for the HTML Cheat sheet.
     */
    private static PrintWriter entityLatin;

    /**
     * Polish numeric entities
     */
    private static PrintWriter entityPolish;

    /**
     * entities for the  proposed HTML-5 entity list
     */
    private static PrintWriter entityProposed;

    /**
     * entities for the HTML Cheat sheet.
     */
    private static PrintWriter entityQuoter;

    /**
     * entities for the HTML Cheat sheet.
     */
    private static PrintWriter entitySpecial;

    /**
     * entities for the HTML Cheat sheet.
     */
    private static PrintWriter entitySpace;

    /**
     * entities for the HTML Cheat sheet.
     */
    private static PrintWriter entitySymbol;

    /**
     * For Vslick editor vslick\built-ins\html.tagdoc.
     */
    private static PrintWriter entityVslickHtmlTagdoc;

    /**
     * For Vslick editor user.vlx.
     */
    private static PrintWriter entityVslickVlx;

    /**
     * entities for the XHTML entity list
     */
    private static PrintWriter entityXHTML;

    private static String AsJavaCharLit( final int theCharNumber )
        {
        final FastCat sb = new FastCat( 5 );
        if ( theCharNumber <= 0xffff )
            {
            // simple 1-char encoding
            sb.append( "\'\\u" );
            sb.append( ST.toLZHexString( theCharNumber, 4 ) );
            sb.append( '\'' );
            }
        else
            {
            // we need to encode it as a surrogate pair.
            final int extract = theCharNumber - 0x10000;
            final int high = ( extract >>> 10 ) + 0xd800;
            final int low = ( extract & 0x3ff ) + 0xdc00;
            sb.append( "&quot;\\u" );
            sb.append( ST.toLZHexString( high, 4 ) );
            sb.append( "\\u" );
            sb.append( ST.toLZHexString( low, 4 ) );
            sb.append( "&quot;" );
            }
        return sb.toString();
        }

    // Polish, Russian, obscure arrows were done manually.
    private static void addDoNotEditLines() throws IOException
        {
        final String doNotEdit = "<!-- The following table was generated on " + BigDate.localToday().toString() + " by Entities. D o   n o t   e d i t . -->\n";
        entityArrow.print( doNotEdit );
        entityCurrency.print( doNotEdit );
        entityCyrillic.print( doNotEdit );
        entityGreek.print( doNotEdit );
        entityPolish.print( doNotEdit );
        entityHex.print( doNotEdit );
        entityHTML5.print( doNotEdit );
        entityProposed.print( doNotEdit );
        entityCyrillic5.print( doNotEdit );
        entityLatin.print( doNotEdit );
        entitySpecial.print( doNotEdit );
        entitySymbol.print( doNotEdit );
        entityQuoter.print( doNotEdit );
        entityXHTML.print( doNotEdit );
        entityMusic.print( doNotEdit );
        entityDiacritic.print( doNotEdit );
        entitySpace.print( doNotEdit );
        }

    /**
     * Used to build the entityToChar conversion table. This is not a constructor. It builds nothing and leaves nothing
     * behind.  It just emits various bits of source code for use elsewhere.
     *
     * @param categories    one or more category letters:  l=lower case Latin L=upper case Latin (alpha accents)
     *                      g=lower case Greek G=upper case Greek
     *                      a=arrow s=symbol 4=html4 5=html5 p=proposed n=not an entity,
     *                      X=XHTML c=lower Cyrillic C=upper Accents
     * @param theCharNumber Character equivalent
     * @param overstrike    char number for second overstrike char. 0 if none.
     * @param entity        entity with &..;
     * @param description   of this character,  no html allowed. No entities allowed.
     * @param notes         html giving extra info about this entity. May contain HTML and entities.
     */
    private static void associate(
            String categories,
            int theCharNumber,
            final int overstrike,
            String entity,
            String description,
            String notes
    )
        {
        // Print out in number order, one line in each file each
        // time associate is called.
        // include 4 and 5 arrows
        if ( categories.indexOf( 'a' ) >= 0 )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entityArrow );
            }
        if ( categories.indexOf( '$' ) >= 0 )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entityCurrency );
            }
        if ( categories.indexOf( '4' ) >= 0 )
            {
            associateEntityCase( entity, theCharNumber, description );
            associateEntityCaseHex( entity, theCharNumber, description );
            associateEntityJustKeys( entity, theCharNumber, description );
            associateEntityJustValues( entity, theCharNumber, description );
            associateEntityVslickVlx( entity );
            associateEntityVslickHtmlTagdoc( entity, theCharNumber, description );
            }
        // new HTML 5 entities.
        if ( ST.containsAnyOf( "5o", categories ) )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entityHTML5 );
            }
        // proposed HTML 5 entities.
        if ( ST.containsAnyOf( "p", categories ) )
            {
            singleEntityLine( entity, categories.indexOf( 'p' ) >= 0, theCharNumber, overstrike, description, notes,
                    entityProposed );
            }
        // new Cyrillic HTML 5 entities.
        // Cyrillic handed by makeCyrillicEntityPairs() for HTML4
        if ( ST.containsAnyOf( "Cc", categories ) )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entityCyrillic5 );
            }
        if ( categories.indexOf( 'q' ) >= 0 && categories.indexOf( '4' ) >= 0 )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entityQuoter );
            }
        // latin, greek handled specially in pairs, not using this code.
        if ( ST.containsAnyOf( "agGs", categories ) && categories.indexOf( '4' ) >= 0 )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entitySpecial );
            }
        if ( categories.indexOf( 's' ) >= 0 && categories.indexOf( '4' ) >= 0 )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entitySymbol );
            }
        // XHTML entities. (currently same as HTML-4)
        if ( ST.containsAnyOf( "Xo", categories ) )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entityXHTML );
            }
        // musical entities
        if ( ST.containsAnyOf( "m", categories ) )
            {
            singleHexEntityLine( theCharNumber, overstrike, description, notes, entityMusic );
            }
        // Diacritical entities.
        if ( ST.containsAnyOf( "d", categories ) )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entityDiacritic );
            }
        // Space entities.
        if ( ST.containsAnyOf( "b", categories ) )
            {
            singleEntityLine( entity, categories.indexOf( '5' ) >= 0, theCharNumber, overstrike, description, notes,
                    entitySpace );
            }
        }

    /**
     * Display Java source code for the association of number to entity
     * For EntifyStrings  as a case clause.
     *
     * @param entity        entity with &..;
     * @param theCharNumber unicode number of the character
     * @param description   short description of the char
     */
    private static void associateEntityCase( String entity, int theCharNumber, String description )
        {
        final FastCat sb = new FastCat( 10 );
        // should later be sorted in numerical order.
        sb.append( "case " );
        sb.append( ST.leftPad( Integer.toString( theCharNumber ),
                4,
                false ) );
        sb.append( " : return " );
        sb.append( ST.rightPad( "\"" + entity + "\"",
                LONGEST_HTML4_ENTITIY + 2,
                false ) );
        sb.append( " /* " );
        sb.append( "&#x" );
        sb.append( toHex( theCharNumber ) );
        sb.append( "; " );
        sb.append( description );// no not entify, it may contain entities already which must be acted on, not displayed
        sb.append( " */;\n" );
        // Java case   34 : return "&quot;"     /* &#x0022; quotation mark */;
        entityCase.print( sb.toString() );
        }

    private static void associateEntityCaseHex( String entity, int theCharNumber, final String description )
        {
        final FastCat sb = new FastCat( 12 );
        sb.append( "case " );
        sb.append( ST.rightPad( "/* " + entity + " */",
                14,
                false ) );
        sb.append( " \'\\u" );
        sb.append( ST.toLZHexString( theCharNumber, 4 ) );
        sb.append( "\' : return " );
        sb.append( ST.rightPad( "\"" + entity + "\"",
                LONGEST_HTML4_ENTITIY + 2,
                false ) );
        sb.append( " /* " );
        sb.append( "&#" );
        sb.append( theCharNumber ); // in decimal
        sb.append( "; " );
        sb.append( description );// no not entify
        sb.append( " */;" );
        // Java case /* AElig */    '\u00c6' :  return "&AElig;" /* &#188 AE ligature */
        entityCaseHex.println( sb.toString() );
        }

    private static void associateEntityJustKeys( String entity, int theCharNumber, String description )
        {
        // list of entities for array init with C-style comment what they are
        // for DeEntifyStrings
        final FastCat sb = new FastCat( 9 );
        final String choppedEntity = ST.chopTrailingString( ST.chopLeadingString( entity, "&" ), ";" );
        sb.append( ST.rightPad(
                "\"" + choppedEntity + "\"",
                LONGEST_HTML4_ENTITIY,
                false ) );
        sb.append( " /* " );
        sb.append( ST.leftPad( Integer.toString( theCharNumber ),
                4,
                false ) );
        sb.append( " : " );
        sb.append( "&#x" );
        sb.append( toHex( theCharNumber ) );
        sb.append( "; " );
        sb.append( description ); // do not entify
        sb.append( " */," );
        // java: "quot"     /*   34 : &#x0022; quotation mark */,
        entityJustKeys.println( sb.toString() );
        }

    /**
     * output one row if information about an entity creating a list of character codes with comments.
     *
     * @param entity        char entity with lead & and trail ;
     * @param theCharNumber Unicode char ordinal
     * @param description   description of what symbol looks like.
     */
    private static void associateEntityJustValues( String entity, int theCharNumber, String description )
        {
        final FastCat sb = new FastCat( 9 );
        // list of entity numbers for array init with C-style comment what they are
        // for DeEntifyStrings
        sb.append( ST.leftPad(
                Integer.toString( theCharNumber ),
                4,
                false ) );
        sb.append( " /* " );
        sb.append( ST.rightPad( entity,
                LONGEST_HTML4_ENTITIY, false ) );
        sb.append( " : " );
        sb.append( "&#x" );
        sb.append( toHex( theCharNumber ) );
        sb.append( "; " );
        sb.append( description );  // do not entify
        sb.append( " */," );
        // java 34 /* &quot;     : &#x0022; quotation mark */
        entityJustValues.println( sb.toString() );
        }

    private static void associateEntityVslickHtmlTagdoc( String entity, int theCharNumber, String description )
        {
        final FastCat sb = new FastCat( 9 );
        // For SlickEdit list of entities.
        sb.append( "const " );
        final String choppedEntity = ST.chopTrailingString( ST.chopLeadingString( entity, "&" ), ";" );
        sb.append( ST.rightPad( choppedEntity + ";",
                LONGEST_HTML4_ENTITIY - 1, false ) );
        sb.append( " // " );
        sb.append( ST.leftPad( Integer.toString( theCharNumber ),
                4,
                false ) );
        sb.append( " : " );
        sb.append( "&#x" );
        sb.append( toHex( theCharNumber ) );
        sb.append( "; " );
        sb.append( description ); // do not entify
        // const quot;     //   34 : &#x0022; quotation mark
        entityVslickHtmlTagdoc.println( sb.toString() );
        }

    private static void associateEntityVslickVlx( String entity )
        {
        final FastCat sb = new FastCat( 3 );
        //  8 entries per line
        if ( counter++ % 8 == 0 )
            {
            sb.append( "\ncskeywords=" );
            }
        // cskeywords= &AElig; &Aacute; &Acirc; &Agrave; &Aring; &Atilde; &Auml;
        sb.append( ' ' );
        sb.append( entity );
        entityVslickVlx.print( sb.toString() );
        }

    private static void closeFiles()
        {
        // C L O S E
        entityArrow.close();
        entityCurrency.close();
        entityCase.close();
        entityCaseHex.close();
        entityCyrillic.close();
        entityGreek.close();
        entityPolish.close();
        entityJustKeys.close();
        entityJustValues.close();
        entityHex.close();
        entityHTML5.close();
        entityProposed.close();
        entityCyrillic5.close();
        entityLatin.close();
        entitySpecial.close();
        entitySymbol.close();
        entityQuoter.close();
        entityVslickHtmlTagdoc.close();
        entityVslickVlx.close();
        entityXHTML.close();
        entityMusic.close();
        entityDiacritic.close();
        entitySpace.close();
        }

    /**
     * Emit row of HTML to describe one entity upper/lower case pain
     *
     * @param p                   Where to emit the html output
     * @param upperCaseCharNumber Unicode ordinal for upper case version
     * @param lowerCaseCharNumber Unicode ordinal for lower case version
     * @param description         What the entity looks like
     */
    private static void entityPair( PrintWriter p, final int upperCaseCharNumber, final int lowerCaseCharNumber,
                                    final String description )
        {
        final FastCat sb = new FastCat( 45 );
        // for HTML cheat sheet, after massaging.
        final String upperEntity = EntifyStrings.entifyHTML( String.valueOf( ( char ) upperCaseCharNumber ) );
        final String lowerEntity = EntifyStrings.entifyHTML( String.valueOf( ( char ) lowerCaseCharNumber ) );
        sb.append( "<tr>" );
        sb.append( "<td class=\"behold\">" );
        sb.append( upperEntity );
        sb.append( "</td>" );
        sb.append( "<td class=\"behold\">" );
        sb.append( lowerEntity );
        sb.append( "</td>" );
        sb.append( "<td class=\"entity\">" );
        sb.append( "&amp;" );
        sb.append( upperEntity.substring( 1, upperEntity.length() - 1 ) );
        sb.append( ";" );
        sb.append( "</td>" );
        sb.append( "<td class=\"entity\">" );
        sb.append( "&amp;" );
        sb.append( lowerEntity.substring( 1, lowerEntity.length() - 1 ) );
        sb.append( ";" );
        sb.append( "</td>" );
        sb.append( "<td class=\"hexentity\">&amp;#x" );
        sb.append( toHex( upperCaseCharNumber ) );
        sb.append( ";</td>" );
        sb.append( "<td class=\"hexentity\">&amp;#x" );
        sb.append( toHex( lowerCaseCharNumber ) );
        sb.append( ";</td>" );
        sb.append( "<td class=\"decimalentity\">&amp;#" );
        sb.append( Integer.toString( upperCaseCharNumber ) );
        sb.append( ";</td>" );
        sb.append( "<td class=\"decimalentity\">&amp;#" );
        sb.append( Integer.toString( lowerCaseCharNumber ) );
        sb.append( ";</td>" );
        sb.append( "<td  class=\"charliteral\">" );
        sb.append( "\'\\u" );
        sb.append( ST.toLZHexString( upperCaseCharNumber, 4 ) );
        sb.append( '\'' );
        sb.append( "</td>" );
        sb.append( "<td  class=\"charliteral\">" );
        sb.append( "\'\\u" );
        sb.append( ST.toLZHexString( lowerCaseCharNumber, 4 ) );
        sb.append( '\'' );
        sb.append( "</td>" );
        sb.append( "<td>" );
        sb.append( description );  // no not entifdy
        sb.append( "</td></tr>\n" );
        // html rendered, char entity, hex entity, java literal, desc
        // html: char itself, entity , hex entity, decimal entity, Java, notes  in upper/lower pairs.
        p.write( sb.toString() );
        }

    /**
     * emit horizontal lines to demark sections of hex entity table.
     */
    private static void hexLineSeparator()
        {
        entityHex.print( "<tr><td colspan=\"3\"><hr></td></tr>\n" );
        }

    /**
     * is this char an unprintable control char?
     *
     * @param theCharNumber
     *
     * @return true if is a control char
     */
    private static boolean isControlChar( int theCharNumber )
        {
        return ( 0 <= theCharNumber && theCharNumber <= 31 )
               || ( 127 <= theCharNumber && theCharNumber <= 159 );
        }

    /**
     * read in facts about each entity and let associations method generate various files.
     *
     * @throws java.io.IOException if trouble reading entityfacts.csv file.
     */
    private static void makeAssociations() throws IOException
        {
        final HashMap<String, String> html4descs = new HashMap<>( 3000 );
        final HashMap<String, String> html4notes = new HashMap<>( 3000 );
        final HashSet<String> entities = new HashSet<>( 3000 );
        // presume current dir is E:\com\mindprod\entities
        // presume sorted by entity, hex, desc, categories
        final CSVReader r = new CSVReader( new BufferedReader( new FileReader( "entityfacts.csv" ) ) );
        try
            {
            while ( true )
                {
                // chop off lead 0x
                final String[] fields = r.getAllFieldsInLine();
                if ( fields.length < 5 )
                    {
                    for ( String field : fields )
                        {
                        err.println( "[" + field + "]" );
                        }
                    throw new IllegalArgumentException( "Should be at least 5 fields on the line above on line " + r.lineCount() );
                    }
                final String categories = fields[ 0 ];
                final String hexString = fields[ 1 ];
                // parse the hex string.
                final int theCharNumber = Integer.parseInt( hexString.substring( 2 ), 16 );
                final String hexOverstrike = fields[ 2 ];
                final int overstrike = hexOverstrike.length() == 0 ? 0 : Integer.parseInt( hexOverstrike.substring( 2
                ), 16 );
                final String entity = fields[ 3 ];
                String description = fields[ 4 ];
                String notes = fields.length > 5 ? fields[ 5 ] : "";
                // one or more category letters:  l=lower case Latin L=upper case Latin (alpha accents) g=lower case
                // Greek G=upper case Greek
                // a=arrow b=blank s=symbol 4=html4 5=html5 n=not an entity, d=diacritical/accent X=XHTML
                if ( categories.indexOf( 'n' ) < 0
                     && categories.indexOf( 'o' ) < 0 && !( entity.startsWith( "&" ) && entity.endsWith( ";" ) ) )
                    {
                    err.println( "non entity [" + entity + "] on line " + r.lineCount() );
                    }
                if ( !ST.isEmpty( entity ) )
                    {
                    if ( !entities.add( entity ) )
                        {
                        // &lang; and &rang; are defined in HTML 5 so are dups
                        err.println( "duplicate entity [" + entity + "] on line " + r.lineCount() );
                        }
                    }
                if ( categories.indexOf( '5' ) >= 0 )
                    {
                    // see if this duplicates html4
                    final String html4desc = html4descs.get( hexString );
                    // don't overwrite existing description.
                    if ( html4desc != null && description.length() == 0 )
                        {
                        // was a duplicate
                        description = html4desc;
                        }
                    final String html4note = html4notes.get( hexString );
                    // don't overwrite existing note.
                    if ( html4note != null && notes.length() == 0 )
                        {
                        // was a duplicate
                        notes = html4note;
                        }
                    }
                else
                    {
                    // was html4
                    html4descs.put( hexString, description );
                    html4notes.put( hexString, notes );
                    }
                /// emit in same order as entityfacts.csv (alpha by entity)
                associate( categories, theCharNumber, overstrike, entity, description, notes );
                }
            }
        catch ( EOFException e )
            {
            r.close();
            }
        }

    private static void makeCyrillicEntityPairs()
        {
        /* file, upper,lower, desc */
        // emit in numeric order
        entityPair( entityCyrillic, 0x0401, 0x0451, "IO" );
        entityPair( entityCyrillic, 0x0402, 0x0452, "DJ" );
        entityPair( entityCyrillic, 0x0403, 0x0453, "GZ" );
        entityPair( entityCyrillic, 0x0404, 0x0454, "JUK" );
        entityPair( entityCyrillic, 0x0405, 0x0455, "DS" );
        entityPair( entityCyrillic, 0x0406, 0x0456, "Byelorussian I" );
        entityPair( entityCyrillic, 0x0407, 0x0457, "YI" );
        entityPair( entityCyrillic, 0x0408, 0x0458, "JSER" );
        entityPair( entityCyrillic, 0x040a, 0x045a, "NJ" );
        entityPair( entityCyrillic, 0x040c, 0x045c, "KJ" );
        entityPair( entityCyrillic, 0x040e, 0x045e, "U breve" );
        entityPair( entityCyrillic, 0x040f, 0x045f, "DZ" );
        entityPair( entityCyrillic, 0x0410, 0x0430, "A" );
        entityPair( entityCyrillic, 0x0411, 0x0431, "BE" );
        entityPair( entityCyrillic, 0x0412, 0x0432, "VE" );
        entityPair( entityCyrillic, 0x0413, 0x0433, "GHE" );
        entityPair( entityCyrillic, 0x0414, 0x0434, "DE" );
        entityPair( entityCyrillic, 0x0415, 0x0435, "IE" );
        entityPair( entityCyrillic, 0x0416, 0x0436, "ZHE" );
        entityPair( entityCyrillic, 0x0417, 0x0437, "ZE" );
        entityPair( entityCyrillic, 0x0418, 0x0438, "I" );
        entityPair( entityCyrillic, 0x0419, 0x0439, "short I" );
        entityPair( entityCyrillic, 0x041a, 0x043a, "KA" );
        entityPair( entityCyrillic, 0x041b, 0x043b, "EL" );
        entityPair( entityCyrillic, 0x041c, 0x043c, "EM" );
        entityPair( entityCyrillic, 0x041d, 0x043d, "EN" );
        entityPair( entityCyrillic, 0x041e, 0x043e, "O" );
        entityPair( entityCyrillic, 0x041f, 0x043f, "PE" );
        entityPair( entityCyrillic, 0x0420, 0x0440, "ER" );
        entityPair( entityCyrillic, 0x0421, 0x0441, "ES" );
        entityPair( entityCyrillic, 0x0422, 0x0442, "TE" );
        entityPair( entityCyrillic, 0x0423, 0x0443, "U" );
        entityPair( entityCyrillic, 0x0424, 0x0444, "EF" );
        entityPair( entityCyrillic, 0x0425, 0x0445, "HA" );
        entityPair( entityCyrillic, 0x0426, 0x0446, "TSE" );
        entityPair( entityCyrillic, 0x0427, 0x0447, "CHE" );
        entityPair( entityCyrillic, 0x0428, 0x0448, "SHA" );
        entityPair( entityCyrillic, 0x0429, 0x0449, "SHCHA" );
        entityPair( entityCyrillic, 0x042a, 0x044a, "hard sign" );
        entityPair( entityCyrillic, 0x042b, 0x044b, "YERU" );
        entityPair( entityCyrillic, 0x042c, 0x044c, "soft sign" );
        entityPair( entityCyrillic, 0x042d, 0x044d, "E" );
        entityPair( entityCyrillic, 0x042e, 0x044e, "YU" );
        entityPair( entityCyrillic, 0x042f, 0x044f, "YA" );
        }

    private static void makeGreekEntityPairs()
        {
        /* file, upper,lower, desc */
        // emit in alpha order
        entityPair( entityGreek, 0x0391/*  913 */, 0x03b1/*  945 */, "alpha" );
        entityPair( entityGreek, 0x0392/*  914 */, 0x03b2/*  946 */, "beta" );
        entityPair( entityGreek, 0x03a7/*  935 */, 0x03c7/*  967 */, "chi" );
        entityPair( entityGreek, 0x0394/*  916 */, 0x03b4/*  948 */, "delta" );
        entityPair( entityGreek, 0x0395/*  917 */, 0x03b5/*  949 */, "epsilon" );
        entityPair( entityGreek, 0x0397/*  919 */, 0x03b7/*  951 */, "eta" );
        entityPair( entityGreek, 0x0393/*  915 */, 0x03b3/*  947 */, "gamma" );
        entityPair( entityGreek, 0x0399/*  921 */, 0x03b9/*  953 */, "iota" );
        entityPair( entityGreek, 0x039a/*  922 */, 0x03ba/*  954 */, "kappa" );
        entityPair( entityGreek, 0x039b/*  923 */, 0x03bb/*  955 */, "lambda" );
        entityPair( entityGreek, 0x039c/*  924 */, 0x03bc/*  956 */, "mu like &amp;micro; &micro;" );
        entityPair( entityGreek, 0x039d/*  925 */, 0x03bd/*  957 */, "nu" );
        entityPair( entityGreek, 0x03a9/*  937 */, 0x03c9/*  969 */, "omega" );
        entityPair( entityGreek, 0x039f/*  927 */, 0x03bf/*  959 */, "omicron" );
        entityPair( entityGreek, 0x03a6/*  934 */, 0x03c6/*  966 */, "phi" );
        entityPair( entityGreek, 0x03a0/*  928 */, 0x03c0/*  960 */, "pi" );
        entityPair( entityGreek,
                0x03d6
                /*  982 */,
                0x03d6
                /*  982 */,
                "Greek pi symbol, not ordinary pi, looks like omega bar," ); // manual fix
        entityPair( entityGreek, 0x03a8/*  936 */, 0x03c8/*  968 */, "psi" );
        entityPair( entityGreek, 0x03a1/*  929 */, 0x03c1/*  961 */, "rho" );
        entityPair( entityGreek, 0x03a3/*  931 */, 0x03c3/*  963 */, "sigma" );
        entityPair( entityGreek, 0x03c2/*  962 */, 0x03c2/*  962 */, "final sigma" );  // manual fix
        entityPair( entityGreek, 0x03a4/*  932 */, 0x03c4/*  964 */, "tau" );
        entityPair( entityGreek, 0x0398/*  920 */, 0x03b8/*  952 */, "theta" );
        entityPair( entityGreek, 0x03d1/*  977 */, 0x03d2/*  978 */, "Greek upsilon with hook symbol" );
        entityPair( entityGreek, 0x03a5/*  933 */, 0x03c5/*  965 */, "upsilon" );
        entityPair( entityGreek, 0x039e/*  926 */, 0x03be/*  958 */, "xi" );
        entityPair( entityGreek, 0x0396/*  918 */, 0x03b6/*  950 */, "zeta" );
        }

    /**
     * pure hex entities, including ones not in the CSV file
     */
    private static void makeHex()
        {
        // one long tall column
        for ( int i = 32; i <= 0x2b54; i++ )
            {
            if ( !isControlChar( i ) )
                {
                oneHexLine( i );
                }
            }
        hexLineSeparator();
        for ( int i = 0x2c60; i <= 0x2dde; i++ )
            {
            if ( !isControlChar( i ) )
                {
                oneHexLine( i );
                }
            }
        }

    private static void makeLatinEntityPairs()
        {
        /* file, upper,lower, desc */
        // emit in alpha order
        entityPair( entityLatin, 0x00c1, 0x00e1, "a acute" );
        entityPair( entityLatin, 0x00c2, 0x00e2, "a circumflex" );
        entityPair( entityLatin, 0x00c6, 0x00e6, "ligature ae" );
        entityPair( entityLatin, 0x00c0, 0x00e0, "a grave" );
        entityPair( entityLatin, 0x00c5, 0x00e5, "a ring above" );
        entityPair( entityLatin, 0x00c3, 0x00e3, "a tilde" );
        entityPair( entityLatin, 0x00c4, 0x00e4, "a diaeresis" );
        entityPair( entityLatin, 0x00c7, 0x00e7, "c cedilla" );
        entityPair( entityLatin, 0x00c9, 0x00e9, "e acute" );
        entityPair( entityLatin, 0x00ca, 0x00ea, "e circumflex" );
        entityPair( entityLatin, 0x00c8, 0x00e8, "e grave" );
        entityPair( entityLatin, 0x00d0, 0x00f0, "eth" );
        entityPair( entityLatin, 0x00cb, 0x00eb, "e diaeresis" );
        entityPair( entityLatin, 0x0192, 0x0192, "f hook" ); // needs manual fixup. LC only.
        entityPair( entityLatin, 0x00cd, 0x00ed, "i acute" );
        entityPair( entityLatin, 0x00ce, 0x00ee, "i circumflex" );
        entityPair( entityLatin, 0x00cc, 0x00ec, "i grave" );
        entityPair( entityLatin, 0x00cf, 0x00ef, "i diaeresis" );
        entityPair( entityLatin, 0x00d1, 0x00f1, "n tilde" );
        entityPair( entityLatin, 0x00d3, 0x00f3, "o acute" );
        entityPair( entityLatin, 0x00d4, 0x00f4, "o circumflex" );
        entityPair( entityLatin, 0x0152, 0x0153, "ligature oe" );
        entityPair( entityLatin, 0x00d2, 0x00f2, "o grave" );
        entityPair( entityLatin, 0x00d8, 0x00f8, "o stroke" );
        entityPair( entityLatin, 0x00d5, 0x00f5, "o tilde" );
        entityPair( entityLatin, 0x00d6, 0x00f6, "o diaeresis" );
        entityPair( entityLatin, 0x0160, 0x0161, "s caron" );
        entityPair( entityLatin, 0x00df, 0x00df, "sharp s, German SS" ); // needs manual fixup UC only
        entityPair( entityLatin, 0x00de, 0x00fe, "thorn" );
        entityPair( entityLatin, 0x00da, 0x00fa, "u acute" );
        entityPair( entityLatin, 0x00db, 0x00fb, "u circumflex" );
        entityPair( entityLatin, 0x00d9, 0x00f9, "u grave" );
        entityPair( entityLatin, 0x00fc, 0x00dc, "u diaeresis" );
        entityPair( entityLatin, 0x00dd, 0x00fd, "y acute" );
        entityPair( entityLatin, 0x0178, 0x00ff, "y diaeresis" );
        }

    private static void makePolishEntityPairs()
        {
        /* file, upper,lower, desc */
        // emit in alpha order
        entityPair( entityPolish, 0x0104, 0x0105, "A ogonek" );
        entityPair( entityPolish, 0x0118, 0x0119, "E ogonek" );
        entityPair( entityPolish, 0x00d3, 0x00f3, "O acute" );
        entityPair( entityPolish, 0x0106, 0x0107, "C acute" );
        entityPair( entityPolish, 0x0141, 0x0142, "L stroke" );
        entityPair( entityPolish, 0x0143, 0x0144, "N acute" );
        entityPair( entityPolish, 0x015a, 0x015b, "S acute" );
        entityPair( entityPolish, 0x0179, 0x017a, "Z acute" );
        entityPair( entityPolish, 0x017b, 0x017c, "Z dot above" );
        }

    /**
     * emit html for one unanamed hex entity showing both hex and decimal entities.
     *
     * @param i codepoint
     */
    private static void oneHexLine( final int i )
        {
        String entity = "&#x" + toHex( i ) + ';';
        entityHex.print( "<tr><td class=\"behold\">"
                         + entity
                         + "</td><td class=\"hexentity\">&amp;"
                         + entity.substring( 1 )
                         + "</td><td class=\"decimalentity\">&amp;#"
                         + i
                         + ";</td></tr>\n" );
        }

    // Polish, Russian, obscure arrows were done manually.
    private static void openFiles() throws IOException
        {
        // O P E N
        entityArrow = EIO.getPrintWriter( new File( "entityarrow.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityCurrency = EIO.getPrintWriter( new File( "entitycurrency.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityCase = EIO.getPrintWriter( new File( "entitycase.javafrag" ), 4 * 1024, EIO.UTF8 );
        entityCaseHex = EIO.getPrintWriter( new File( "entitycasehex.javafrag" ), 4 * 1024, EIO.UTF8 );
        entityCyrillic = EIO.getPrintWriter( new File( "entitycyrillic.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityGreek = EIO.getPrintWriter( new File( "entitygreek.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityPolish = EIO.getPrintWriter( new File( "entitypolish.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityJustKeys = EIO.getPrintWriter( new File( "entityjustkeys.javafrag" ), 4 * 1024, EIO.UTF8 );
        entityJustValues = EIO.getPrintWriter( new File( "entityjustvalues.javafrag" ), 4 * 1024, EIO.UTF8 );
        entityHex = EIO.getPrintWriter( new File( "entityhex.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityHTML5 = EIO.getPrintWriter( new File( "entityhtml5.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityProposed = EIO.getPrintWriter( new File( "entityproposed.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityCyrillic5 = EIO.getPrintWriter( new File( "entitycyrillic5.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityLatin = EIO.getPrintWriter( new File( "entitylatin.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entitySpecial = EIO.getPrintWriter( new File( "entityspecial.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entitySymbol = EIO.getPrintWriter( new File( "entitysymbol.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityQuoter = EIO.getPrintWriter( new File( "entityquoter.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityVslickHtmlTagdoc = EIO.getPrintWriter( new File( "entityvslickhtml.tagdoc" ), 4 * 1024, EIO.UTF8 );
        entityVslickVlx = EIO.getPrintWriter( new File( "entityvslick.vlx" ), 4 * 1024, EIO.UTF8 );
        entityXHTML = EIO.getPrintWriter( new File( "entityxhtml.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityMusic = EIO.getPrintWriter( new File( "entitymusic.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entityDiacritic = EIO.getPrintWriter( new File( "entitydiacritic.htmlfrag" ), 4 * 1024, EIO.UTF8 );
        entitySpace = EIO.getPrintWriter( new File( "entityspace.htmlfrag" ), 1 * 1024, EIO.UTF8 );
        }

    /**
     * Generate HTML for a table row to display one entity, behold twice, as char entity and hex entity.
     *
     * @param entity        String for entity with lead & and trail ;
     * @param isHtml5       true if this is an HTML5 entity. Displayed with differet CSS class.
     * @param theCharNumber unicode character ordinal
     * @param overstrike    char number of second overstrike character if any.
     * @param description   description of what char looks like
     * @param notes         notes about char
     * @param p             where to emit HTML
     */
    private static void singleEntityLine( String entity,
                                          final boolean isHtml5,
                                          final int theCharNumber,
                                          final int overstrike,
                                          final String description,
                                          final String notes,
                                          final PrintWriter p )
        {
        final FastCat sb = new FastCat( 45 );
        // for HTML cheat sheet, after massaging.
        if ( ST.isEmpty( entity ) )
            {
            entity = "&#x" + toHex( theCharNumber ) + ";";
            }
        sb.append( "<tr>" );
        // show char
        sb.append( "<td class=\"behold\">" );
        if ( description.contains( "combining" ) )
            {
            // give combining accent a blank to combine with to become visible
            sb.append( "&#xa0;" );
            }
        if ( !isControlChar( theCharNumber ) )
            {
            // don't try to display control chars
            sb.append( entity );
            }
        sb.append( "</td>" );
        // show alpha entity
        if ( isHtml5 )
            {
            sb.append( "<td class=\"html5entity\">" );
            }
        else
            {
            sb.append( "<td class=\"entity\">" );
            }
        if ( entity.startsWith( "&" ) )
            {
            final String choppedEntity = entity.substring( 1, entity.length() - 1 );
            sb.append( "&amp;" );
            sb.append( choppedEntity );
            sb.append( ";" );
            }
        else
            {
            sb.append( entity );// handle n non-entity
            }
        sb.append( "</td>" );
        // show rendering with hex
        sb.append( "<td class=\"behold\">" );
        if ( description.contains( "combining" ) )
            {
            // give combining accent a blank to combine with to become visible
            sb.append( "&#xa0;" );
            }
        sb.append( "&#x" );
        sb.append( toHex( theCharNumber ) );
        sb.append( ";" );
        if ( overstrike != 0 )
            {
            sb.append( "&#x" );
            sb.append( toHex( overstrike ) );
            sb.append( ";" );
            }
        sb.append( "</td>" );
        // show hex entity
        sb.append( "<td class=\"hexentity\">&amp;#x", toHex( theCharNumber ), ";" );
        if ( overstrike != 0 )
            {
            sb.append( "&amp;#x", toHex( overstrike ), ";" );
            }
        sb.append( "</td>" );
        // show decimal entity
        sb.append( "<td class=\"decimalentity\">&amp;#", Integer.toString( theCharNumber ), ";" );
        if ( overstrike != 0 )
            {
            sb.append( "&amp;#", Integer.toString( overstrike ), ";" );
            }
        sb.append( "</td>" );
        // show java lit
        sb.append( "<td class=\"charliteral\">" );
        sb.append( AsJavaCharLit( theCharNumber ) );
        if ( overstrike != 0 )
            {
            sb.append( AsJavaCharLit( overstrike ) );
            }
        sb.append( "</td>" );
        // desc
        sb.append( "<td>", description, "</td>" );
        sb.append( "<td>", notes, "</td></tr>\n" );
        p.write( sb.toString() );
        }

    /**
     * Generate HTML for a table row to display one entity, behold twice, as char entity and hex entity.
     *
     * @param theCharNumber unicode character ordinal
     * @param overstrike    char number of second overstrike character if any.
     * @param description   description of what char looks like
     * @param notes         notes about char
     * @param p             where to emit HTML
     */
    private static void singleHexEntityLine(
            final int theCharNumber,
            final int overstrike,
            final String description,
            final String notes,
            final PrintWriter p )
        {
        // presum sorted by entity(blank), hex, desc, categories
        final FastCat sb = new FastCat( 25 );
        // for HTML cheat sheet, after massaging.
        sb.append( "<tr><td class=\"behold\">" );
        if ( description.contains( "combining" ) )
            {
            // give combining accent a blank to combine with to become visible
            sb.append( "&#xa0;" );
            }
        if ( !isControlChar( theCharNumber ) )
            {
            // don't try to display control chars
            sb.append( "&#x", toHex( theCharNumber ), ";" );
            if ( overstrike != 0 )
                {
                sb.append( "&#x", toHex( overstrike ), ";" );
                }
            }
        sb.append( "</td>" );
        // hex entity
        sb.append( "<td class=\"hexentity\">&amp;#x", toHex( theCharNumber ), ";" );
        if ( overstrike != 0 )
            {
            sb.append( "&amp;#x", toHex( overstrike ), ";" );
            }
        sb.append( "</td>" );
        // decimal entity
        sb.append( "<td class=\"decimalentity\">&amp;#", Integer.toString( theCharNumber ), ";" );
        if ( overstrike != 0 )
            {
            sb.append( "&amp;#", Integer.toString( overstrike ), ";" );
            }
        sb.append( "</td>" );
        sb.append( "<td class=\"charliteral\">", AsJavaCharLit( theCharNumber ) );
        if ( overstrike != 0 )
            {
            sb.append( AsJavaCharLit( overstrike ) );
            }
        sb.append( "</td>" );
        sb.append( "<td>", description, "</td>" );    // already entified
        sb.append( "<td>", notes, "</td></tr>\n" );   // already entified
        p.write( sb.toString() );
        }

    /**
     * display as hex nnnn or nnnnnn
     *
     * @param theCharNumber 16-bit char number.
     *
     * @return as  hex string with no lead zeros.
     */
    private static String toHex( final int theCharNumber )
        {
        return Integer.toString( theCharNumber, 16 );
        }

    /**
     * Run once to generate [various text files that are inserted to handle entity coding.
     * generates the following files in current directory.
     * <p/>
     * entitycase.javafrag : case statements. Insert in EntifyStrings.java, then recompile.
     * entitycasehex.javafrag : alternate case statements to Insert in EntifyStrings.java, then recompile. Not used.
     * entityfacts.javafrag : edit then rerun prop.bat
     * entityjustkeys.javafrag  :  legal entity keys. Insert in DeEntifyStrings.java, then recompile.
     * entityjustvalues.javafrag  : legal entity values. Insert in DeEntifyStrings.java, then recompile.
     * entityvslickhtml.tagdoc : insert into F:\program files\vslick\built-ins\html.tagdoc, just ones not there yet.
     * entityvslick.vlx  : Insert into F:\program files\vslick\vslick.vlx,
     * patch keywords= &amp; &lt; &gt; &quot;  instead of cskeywords.
     * <p/>
     *
     * @param args not used.
     *
     * @throws java.io.IOException to get maximal info about the problem.
     */
    public static void main( String[] args ) throws IOException
        {
        openFiles();
        addDoNotEditLines();
        // W R I T E , associate does the actual writing
        makeAssociations();
        makeLatinEntityPairs();
        makeCyrillicEntityPairs();
        makeGreekEntityPairs();
        makePolishEntityPairs();
        makeHex();
        closeFiles();
        } // end main
    }
// end Entities
