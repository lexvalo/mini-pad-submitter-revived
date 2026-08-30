/*
 * [PrintWriterPlus.java]
 *
 * Summary: Extension of PrintWriter to deal proper[y with embedded \n character.
 *
 * Copyright: (c) 2003-2017 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.8+
 *
 * Created with: JetBrains IntelliJ IDEA IDE http://www.jetbrains.com/idea/
 *
 * Version History:
 *  1.2 2006-01-01
 *  1.3 2009-05-03 create PrintWriterPlus vs PrintWriterPortable
 */
package com.mindprod.hunkio;

import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import static java.lang.System.*;

/**
 * Extension of PrintWriter to deal proper[y with embedded \n character.
 * <p/>
 * Prints lines with a configurable line separator character,
 * even when it is embedded in the Strings to be printed,
 * so long as you print with emit and emitln, rather than print and println.
 * Use PrintWriter Portable if you want it to work even with text
 * printed with print or println.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.3 2009-05-03 create PrintWriterPlus vs PrintWriterPortable
 * @see com.mindprod.common18.Localise#.localise(String)
 * @see com.mindprod.hunkio.PrintWriterPortable
 * @since 2003-06-01
 */
public class PrintWriterPlus extends PrintWriter
    {
    /*
     * add configurable lineSeparator
     */

    /**
     * if true, turns on code in debugging test harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * lineSeparator using now.
     */
    String lineSeparator = System.getProperty( "line.separator" );

    /**
     * Constructor just like PrintWriter. Create a new PrintWriterPlus, without automatic line flushing, from an
     * existing OutputStream. This convenience constructor creates the necessary intermediate OutputStreamWriter, which
     * will convert characters into bytes using the default character encoding.
     *
     * @param out An output stream
     *
     * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     */
    public PrintWriterPlus( OutputStream out )
        {
        super( out );
        }

    /**
     * Constructor just like PrintWriter. Create a new PrintWriterPlus, without automatic line flushing.
     *
     * @param out A character-output stream
     */
    public PrintWriterPlus( Writer out )
        {
        super( out );
        }

    /**
     * Constructor just like PrintWriter. Create a new PrintWriterPlus from an existing OutputStream. This convenience
     * constructor creates the necessary intermediate OutputStreamWriter, which will convert characters into bytes using
     * the default character encoding.
     *
     * @param out       An output stream
     * @param autoFlush A boolean; if true, the println() methods will flush the output buffer
     *
     * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     */
    public PrintWriterPlus( OutputStream out, boolean autoFlush )
        {
        super( out, autoFlush );
        }

    /**
     * Constructor just like PrintWriter. Create a new PrintWriterPlus.
     *
     * @param out       A character-output stream
     * @param autoFlush A boolean; if true, the println() methods will flush the output buffer
     *
     * @noinspection WeakerAccess, SameParameterValue
     */
    public PrintWriterPlus( Writer out, boolean autoFlush )
        {
        super( out, autoFlush );
        }

    /**
     * Debugging test harness for PrintWriterPlus
     *
     * @param args not used
     */
    public static void main( String[] args )
        {
        if ( DEBUGGING )
            {
            try
                {
                PrintWriterPlus pp =
                        new PrintWriterPlus( new FileWriter( "C:/temp/temp.txt" ),
                                false
                                /* auto flush on println */ );
                pp.print( "print embedded linefeed \n in the middle " );
                pp.println( "println embedded linefeed \n in the middle " );
                pp.emit( "emit embedded linefeed \n in the middle " );
                pp.emit( "emitln embedded linefeed \n in the middle " );
                pp.emit( '\n' );
                pp.emit( 'E' );
                pp.close();
                }
            catch ( Exception e )
                {
                err.println();
                err.println();
                e.printStackTrace( System.err );
                err.println();
                }
            } // end if
        } // end main

    /**
     * like PrintWriter.print, but converts embedded \n to platform-specific line terminators Print a string. Don't use
     * emit on Strings that already contain crlf line endings. You will then get crcrlf as the result. Arrgh.
     *
     * @param s The <code>String</code> to be printed
     *
     * @noinspection WeakerAccess
     */
    public void emit( String s )
        {
        int start = 0;
        while ( true )
            {
            int place = s.indexOf( '\n', start );
            if ( place < 0 )
                {
                // no more line terminators
                // write the tail
                int length = s.length() - start;
                if ( length > 0 )
                    {
                    write( s, start, length );
                    }
                return;
                }
            else
                {
                // found line terminator
                // write part before it
                int length = place - start;
                if ( length > 0 )
                    {
                    write( s, start, length );
                    }
                // write platform specific line separator
                write( lineSeparator );
                // process the rest of the String
                start = place + 1;
                }
            }
        }

    /**
     * like PrintWriter.print, but emits platform dependent line separator if c is a \n Print a character.
     *
     * @param c The <code>char</code> to be printed
     *
     * @noinspection WeakerAccess
     */
    public void emit( char c )
        {
        if ( c == '\n' )
            {
            write( lineSeparator );
            }
        else
            {
            write( c );
            }
        }

    /**
     * Like PrintWriter.println, but emits two line separators if c is a \n. Print a character and then terminate the
     * line.
     *
     * @param c the <code>char</code> value to be printed
     */
    public void emitln( char c )
        {
        if ( c == '\n' )
            {
            write( lineSeparator );
            write( lineSeparator );
            }
        else
            {
            write( c );
            write( lineSeparator );
            }
        }

    /**
     * like PrintWriter.println, but converts embedded \n to platform-specific line terminators Print a String and then
     * terminate the line.
     *
     * @param s the <code>String</code> value to be printed
     */
    public void emitln( String s )
        {
        emit( s );
        write( lineSeparator );
        }

    /**
     * Use a line separator other than the system default.
     *
     * @param lineSeparator Sequence to separate lines. usually Windows: "\r\n" Unix: "\n" or Mac: "\r". But could be
     *                      anything.
     */
    public void setLineSeparator( String lineSeparator )
        {
        this.lineSeparator = lineSeparator;
        }
    }
