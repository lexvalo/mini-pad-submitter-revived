/*
 * [ImageInfo.java]
 *
 * Summary: Rapidly determine the gif or jpg or png image width and height without loading the image.
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
 *  1.1 2006-03-04
 */
package com.mindprod.common18;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import static java.lang.System.*;

/**
 * Rapidly determine the gif or jpg or png image width and height without loading the image.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2006-03-04
 * @since 2003-05-15
 */
public final class ImageInfo
    {
    /**
     * Read a 24-bit unsigned int in little -endian binary format.
     *
     * @param dis stream to read from.
     *
     * @return binary value.
     */
    private static int read24LittleEndian( DataInputStream dis ) throws IOException
        {
        final int b1 = dis.readByte() & 0xff;
        final int b2 = dis.readByte() & 0xff;
        final int b3 = dis.readByte() & 0xff;
        return ( b3 << 16 ) | ( b2 << 8 ) | b1;
        }

    /**
     * Read a 16-bit signed short, in little-endian binary format.
     *
     * @param dis stream to read from.
     *
     * @return binary value.
     */
    private static char readCharLittleEndian( DataInputStream dis ) throws IOException
        {
        return Character.reverseBytes( dis.readChar() );
        }

    /**
     * Read a 32-bit signed int, in little-endian binary format.
     *
     * @param dis stream to read from.
     *
     * @return binary value.
     */
    private static int readIntLittleEndian( DataInputStream dis ) throws IOException
        {
        return Integer.reverseBytes( dis.readInt() );
        }

    /**
     * Read a 16-bit signed short, in little-endian binary format.
     *
     * @param dis stream to read from.
     *
     * @return binary value.
     */
    private static short readShortLittleEndian( DataInputStream dis ) throws IOException
        {
        return Short.reverseBytes( dis.readShort() );
        }

    /**
     * Summary: Rapidly calculate image size without loading the image.
     * <p/>
     * get the height and width of a gif or jpg image without having to read the entire Image into RAM. This works only
     * with local images, not ones out on the web accessible by URL.
     * Works with fully qualified name, not necessarily anything to do with the htmlmacros package.
     *
     * @param imageFilename filename. Must end in .jpg, .gif, .png or .webp
     *
     * @return length-2 array of two numbers, width and height of the image, or 0,0 if it could not be found. We don't
     * return a Dimension object because it provides doubles, not ints.
     * @noinspection WeakerAccess
     */
    public static int[] getImageDimensions( String imageFilename )
        {
        int width = 0;
        int height = 0;
        DataInputStream inle = null; // could have used LeDataStream but that drags it in for a trivial task.
        DataInputStream inbe = null;
        final int place = imageFilename.lastIndexOf( '.' );
        if ( place < 0 )
            {
            return new int[] { 0, 0 };
            }
        final String ext = imageFilename.substring( place + 1 ).toLowerCase();
        try
            {
            try
                {
                if ( ext.equals( "png" ) )
                    {
                    // see http://mindprod.com/jgloss/png.html
                    // The PNG file header looks like this:
                    // signature \u0085PNG\r\n\u001a\n 8-bytes
                    // ie. in hex 89504e470d0a1a0a
                    // chunksize 4 bytes 0x0000000D
                    // chunkid 4 bytes "IHDR" 0x49484452
                    // width 4 bytes big-endian binary at offset 0x10
                    // height 4 bytes big-endian binary at offset 0x14
                    inbe = EIO.getDataInputStream( new File( imageFilename ), 1024 );
                    long signature = inbe.readLong();
                    if ( signature != 0x89504e470d0a1a0aL )
                        {
                        throw new IOException( "not a valid png file" );
                        }
                    inbe.skipBytes( 4 + 4 );
                    width = inbe.readInt();
                    height = inbe.readInt();
                    inbe.close();
                    }
                else if ( ext.equals( "gif" ) )
                    {
                    // signature GIF89a i.e. 0x474946383961
                    // or GIF87a
                    // just check first 4 chars
                    // width at offset 0x06 and height at 0x08 16-bit little
                    // endian
                    inle = EIO.getDataInputStream( new File( imageFilename ), 1024 );
                    int signature4 = readIntLittleEndian( inle );
                    if ( signature4 != 0x38464947/* reversed */ )
                        {
                        throw new IOException( "not a valid gif" );
                        }
                    inle.skipBytes( 2 );
                    width = readShortLittleEndian( inle );
                    height = readShortLittleEndian( inle );
                    inle.close();
                    }
                else if ( ext.equals( "jpg" ) || ext.equals( "jpeg" ) )
                    {
                    // ffd8
                    // in variable location: height, then width, big endian.
                    inbe = EIO.getDataInputStream( new File( imageFilename ), 1024 );
                    if ( inbe.readUnsignedByte() != 0xff )
                        {
                        throw new IOException( "not a valid jpg" );
                        }
                    if ( inbe.readUnsignedByte() != 0xd8 )
                        {
                        throw new IOException( "not a valid jpg" );
                        }
                    while ( true )
                        {
                        int p1 = inbe.readUnsignedByte();
                        int p2 = inbe.readUnsignedByte();
                        if ( p1 == 0xff && 0xc0 <= p2 && p2 <= 0xc3 )
                            {
                            inbe.skipBytes( 3 );
                            height = inbe.readShort();  // usually offset 1a, little endian
                            width = inbe.readShort();   // usually offset 1c, little endian
                            break;
                            }
                        else
                            {
                            // bypass this marker
                            int length = inbe.readShort();
                            inbe.skipBytes( length - 2 );
                            }
                        } // end while
                    inbe.close();
                    } // end else
                else if ( ext.equals( "webp" ) )
                    {
                    // signature web is RIFF 0x00 5249_4646    WEBP  0x08 5745_4250
                    // https://developers.google.com/speed/webp/docs/riff_container?hl=de
                    // width at offset 0x18 and height at 0x1b 24-bit little
                    // endian
                    // An earlier format used 16 bit width and height
                    inle = EIO.getDataInputStream( new File( imageFilename ), 1024 );
                    final int signature4 = inle.readInt();
                    if ( signature4 != 0x5249_4646/* RIFF */ )
                        {
                        throw new IOException( "not a valid webp file" );
                        }
                    inle.skipBytes( 4 );
                    final int webp4 = inle.readInt();
                    if ( webp4 != 0x5745_4250/* WEBP  */ )
                        {
                        throw new IOException( "not a valid webp file" );
                        }
                    final int vp4 = inle.readInt();
                    if ( vp4 == 0x5650_3858 ) /* VP8X */
                        {
                        // lossless format
                        inle.skipBytes( 8 );
                        width = read24LittleEndian( inle ) + 1;
                        height = read24LittleEndian( inle ) + 1;
                        }
                    else if ( vp4 == 0x5650_3820 ) /* VP8_ */
                        {
                        // lossy format
                        inle.skipBytes( 10 );
                        width = readCharLittleEndian( inle );
                        height = readCharLittleEndian( inle );
                        inle.close();
                        }
                    else
                        {
                        throw new IllegalArgumentException( "webp file missing internal signatures" );
                        }
                    }
                // other file types will default to 0,0
                } // end try
            catch ( IOException e )
                {
                if ( inle != null )
                    {
                    inle.close();
                    }
                if ( inbe != null )
                    {
                    inbe.close();
                    }
                width = 0;
                height = 0;
                }
            }
        catch ( Exception e )
            {
            width = 0;
            height = 0;
            }
        return new int[] { width, height };
        }

    /**
     * DEBUGGING Test driver to find size of an image mentioned on the command line.
     *
     * @param args name of a *.gif or *.jpg or *.png image file to test. Should print out its width and height.
     */
    public static void main( String[] args )
        {
        if ( args.length != 1 )
            {
            out.println( "Need exactly one image filename on the command line." );
            }
        String imageFilename = args[ 0 ];
        int[] d = getImageDimensions( imageFilename );
        out.println( imageFilename
                     + " width:"
                     + d[ 0 ]
                     + " height:"
                     + d[ 1 ] );
        }
    }
