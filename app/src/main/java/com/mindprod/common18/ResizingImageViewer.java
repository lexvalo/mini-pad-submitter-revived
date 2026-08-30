/*
 * [ResizingImageViewer.java]
 *
 * Summary: Component to display an Image, magnifying or shrinking it.
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
 *  1.2 2007-04-20 allow AffineTransform.
 */
package com.mindprod.common18;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.geom.AffineTransform;

/**
 * Component to display an Image, magnifying or shrinking it.
 * <p/>
 * Simplified version of Symantec ImageViewer.  Always resizes the image to fit the size
 * of the component, even if it distorts the aspect ratio.
 * Used by com.mindprod.networkcam.NetworkCam
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.2 2007-04-20 allow AffineTransform.
 * @see ImageViewer
 * @since 2003
 */
public final class ResizingImageViewer extends Component
    {
    /**
     * transform to flip, mirror, rotate etc. usually null.
     */
    private AffineTransform transform;

    /**
     * Image that this viewer is currently displaying.
     */
    private Image image;

    /**
     * default Constructor
     */
    public ResizingImageViewer()
        {
        image = null;
        }

    /**
     * Constructor with transform.
     *
     * @param transform to flip, mirror, rotate the image.  Usually null.
     */
    public ResizingImageViewer( AffineTransform transform )
        {
        this.transform = transform;
        }

    /**
     * Constructor with Image.
     *
     * @param image the Image to be displayed, See the Java glossary under Image for ways to create an Image.
     */
    public ResizingImageViewer( Image image )
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
        if ( image == null )
            {
            this.image = null;
            repaint();
            }
        else
            {
            this.image = image;
            // wait until image is fully loaded so that sizes will be correct,
            // and so that paint will be instantaneous, rather than gradual as
            // the image arrives.
            // If repaint freakishly happens during this reload, we will see a
            // gradual paint.
            // MediaTracker arranges repaint via ImageObsever interface
            MediaTracker tracker;
            try
                {
                tracker = new MediaTracker( this );
                tracker.addImage( image, 0 );
                tracker.waitForID( 0 );
                }
            catch ( InterruptedException e )
                {
                // nothing to do
                }
            repaint();
            }
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
        // get size of box we have to draw in, we will totally fill it.
        Dimension dim = this.getSize();
        if ( image != null )
            {
            AffineTransform saveTransform = null;
            final Graphics2D g2 = ( Graphics2D ) g;
            if ( transform != null )
                {
                // save existing transform.
                saveTransform = g2.getTransform();
                g2.setTransform( transform );
                }
            /*
             * center Image in box, normally should exactly fill the box. If we
             * overflow, no problem, drawImage will clip.
             */
            g.drawImage( image, /* Image to draw */
                    0, /* x */
                    0, /* y */
                    dim.width, /* width */
                    dim.height, /* height */
                    this );/* this ResizingImageViewer component */
            if ( transform != null )
                {
                // restore usual transform
                g2.setTransform( saveTransform );
                }
            }
        else
            {
            /* we have no Image, clear the box */
            g.setColor( getBackground() );
            g.clearRect( 0, 0, dim.width, dim.height );
            }
        }

    /**
     * bypass usual clear for speed since will fill entire frame
     *
     * @param g graphics region to paint
     */
    public void update( Graphics g )
        {
        paint( g );
        }
    }
