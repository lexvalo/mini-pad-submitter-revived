/*
 * [PrintWriterPortable.java]
 *
 * Summary: Extension of PrintWriter/PrinterWriterPlus to deal properly with embedded \n character.
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
import java.io.Writer;

import static java.lang.System.*;

/**
 * Extension of PrintWriter/PrinterWriterPlus to deal properly with embedded \n character.
 * <p/>
 * Prints lines with a  configurable line separator character,
 * even when it is embedded in the Strings to be printed,
 * even those printed with print or println.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.3 2009-05-03 create PrintWriterPlus vs PrintWriterPortable
 * @see com.mindprod.common18.Localise#localise(String)
 * @see com.mindprod.hunkio.PrintWriterPlus
 * @since 2003-06-01
 */
public final class PrintWriterPortable extends PrintWriterPlus
    {
    /**
     * if true, turns on code in debugging test harness.
     */
    private static final boolean DEBUGGING = false;

    /**
     * Constructor just like PrintWriter. Create a new PrintWriterPlus, without automatic line flushing, from an
     * existing OutputStream. This convenience constructor creates the necessary intermediate OutputStreamWriter, which
     * will convert characters into bytes using the default character encoding.
     *
     * @param out An output stream
     *
     * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     */
    public PrintWriterPortable( OutputStream out )
        {
        super( out );
        }

    /**
     * Constructor just like PrintWriter. Create a new PrintWriterPlus, without automatic line flushing.
     *
     * @param out A character-output stream
     */
    public PrintWriterPortable( Writer out )
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
    public PrintWriterPortable( OutputStream out, boolean autoFlush )
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
    public PrintWriterPortable( Writer out, boolean autoFlush )
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
                PrintWriterPortable pp =
                        new PrintWriterPortable( new FileWriter( "C:/temp/temp.txt" ),
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
     * like PrintWriter.print, but emits platform dependent line separator if c is a \n Print a character.
     *
     * @param c The <code>char</code> to be printed
     *
     * @noinspection WeakerAccess
     */
    public void print( char c )
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
    public void println( char c )
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
    public void println( String s )
        {
        emit( s );
        write( lineSeparator );
        }
    }
