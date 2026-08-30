/*
 * [ImageViewer.java]
 *
 * Summary: Component to display an Image.
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;

/**
 * Component to display an Image.
 * <p/>
 * Simplified version of Symantec ImageViewer. Does not resize the image, though
 * may crop it. Thus always maintains original magnification and aspect ratio.
 * Used by com.mindprod.bio.Bio
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.1 2006-03-04
 * @see ResizingImageViewer
 * @since 2003
 */
public final class ImageViewer extends Component
    {
    /**
     * Image that this viewer is currently displaying.
     */
    private Image image;

    /**
     * default Constructor
     */
    public ImageViewer()
        {
        image = null;
        }

    /**
     * Constructor with Image.
     *
     * @param image the Image to be displayed . See the Java glossary under Image for ways to create an Image from a
     *              file.
     */
    public ImageViewer( Image image )
        {
        this();
        setImage( image );
        }

    /**
     * Get the Image currently being displayed.
     *
     * @return the Image currently displayed or null if no Image
     */
    public Image getImage()
        {
        return image;
        }

    /**
     * Set or change the current Image to display. setImage does a MediaTracker to ensure the Image is loaded. You don't
     * have to. If you don't plan to use the old image again you should do a getImage().flush();
     *
     * @param image the new Image to be displayed. If the image jpg may have recently changed, don't use getImage to
     *              create it, use URL.openConnection() URLConnection.setUseCaches( false ) Connection.getContent
     *              Component.createImage
     */
    public void setImage( Image image )
        {
        // even if Image object is same, we use it since it may have changed
        // state.
        this.image = image;
        if ( image != null )
            {
            MediaTracker tracker;
            try
                {
                // wait until image is fully loaded.
                // and so that paint will be instantaneous, rather than gradual
                // as
                // the image arrives.
                // MediaTracker notifies of progress via our
                // Component.ImageObsever interface
                tracker = new MediaTracker( this );
                tracker.addImage( image, 0 );
                tracker.waitForID( 0 );
                }
            catch ( InterruptedException e )
                {
                // nothing to do
                }
            }
        // image is now ready, let's paint it
        repaint();
        }

    /**
     * Minimum layout size.
     *
     * @return he minimum dimensions to properly display the Image
     */
    public Dimension getMinimumSize()
        {
        return getPreferredSize();
        }

    /**
     * Preferred Layout size.
     *
     * @return the recommended dimensions to display the Image.
     */
    public Dimension getPreferredSize()
        {
        if ( image != null )
            {
            /* should just fit the Image */
            return ( new Dimension( image.getWidth( this ), image
                    .getHeight( this ) ) );
            }
        else
            {/* empty square as a placeholder */
            return new Dimension( 10, 10 );
            }
        }

    /**
     * Paints this component using the given graphics context.
     *
     * @param g Graphics context where to paint, e.g. to screen, printer, RAM.
     */
    public void paint( Graphics g )
        {
        super.paint( g );
        // get size of box we have to draw in
        Dimension dim = getSize();
        if ( image != null )
            {
            /*
             * center Image in box, normally should exactly fill the box. If we
             * overflow, no problem, drawImage will clip.
             */
            int imageWidth = image.getWidth( this );
            int imageHeight = image.getHeight( this );
            // this does not complete the job, just starts it.
            // We are notified of progress through our Component ImageObserver
            // interface.
            g.drawImage( image, /* Image to draw */
                    ( dim.width - imageWidth ) / 2, /* x */
                    ( dim.height - imageHeight ) / 2, /* y */
                    imageWidth, /* width */
                    imageHeight, /* height */
                    this );/* this ImageViewer component */
            }
        else
            {
            /* we have no Image, clear the box */
            g.setColor( getBackground() );
            g.clearRect( 0, 0, dim.width, dim.height );
            }
        }
    }
