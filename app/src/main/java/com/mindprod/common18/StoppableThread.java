/*
 * [StoppableThread.java]
 *
 * Summary: A thread you can stop gently and safely.
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
 *  1.0 2002-08-01 initial version
 *  1.1 2002-08-02 Make pleaseStop volatile and synchronize access to it.
 *  1.2 2006-03-04 reformat with IntelliJ and provide Javadoc.
 */
package com.mindprod.common18;

/**
 * A thread you can stop gently and safely.
 * <p/>
 * Remember to nullify references to the thread after you are finished. Threads are big.
 * You can't restart it! Required for multi-cpu cache synchronization.
 *
 * @author Roedy Green, Canadian Mind Products
 * @version 1.2 2006-03-04
 * @since 2002-08-01
 */
public final class StoppableThread extends Thread
    {
    /**
     * true when we want the current run method to stop as soon as it can. volatile so threads will take always take a
     * fresh look at what other threads have set.
     */
    private volatile boolean pleaseStop = false;

    /**
     * Constructor.
     *
     * @param r class that has a run method
     */
    public StoppableThread( Runnable r )
        {
        super( r );
        }

    /**
     * Stop this thread gracefully. If the thread is already stopped, does nothing. For this to work, this.run must exit
     * by checking stopping() at convenient intervals and returning if it is true.
     *
     * @param interrupt true if this thread should be interrupted from sleep or from doing an I/O (which might close the
     *                  channel), before stopping it.
     * @param timeout   How long in milliseconds to wait for the thread to die before giving up. 0 means wait forever.
     *                  -1 means don't wait at all.
     */
    public void gentleStop( boolean interrupt, long timeout )
        {
        if ( !this.isAlive() )
            {
            return;
            }
        synchronized ( this )
            {
            // ask thread to stop.
            this.pleaseStop = true;
            }
        // wake this thread up if it is sleeping,
        // and get it to notice stopping() flag.
        // Will also interrupt i/o.
        if ( interrupt )
            {
            this.interrupt();
            }
        if ( timeout >= 0 )
            {
            try
                {
                // wait for the thread to die.
                this.join( timeout );
                }
            catch ( InterruptedException e )
                {
                }
            }
        } // end stop

    /**
     * Start this thread executing its run method on a separate thread. You may only call start once. After the thread
     * dies it cannot be restarted.
     *
     * @throws IllegalThreadStateException if this thread is already started.
     */
    public void start()
        {
        super.start();
        }

    /**
     * this.run should call stopping() at convenient invervals to see if it has been requested to stop. If true, it
     * should finish up quickly and return. Will have to be called via while ( ! ((StoppableThead)Thread
     * .currentThread()).stopping()
     * )
     *
     * @return true if run should exit soon.
     */
    public synchronized final boolean stopping()
        {
        return pleaseStop;
        }
    } // end StoppableThread
