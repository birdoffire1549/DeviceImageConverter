package com.firebirdcss.tool.device_image_converter.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

public abstract class ThreadedActionListener implements ActionListener {
    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            /*
             * (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                actionOccurred(e);
            }
        });
    }
    
    /**
     * Invoked when an action occurs.
     * 
     * @param e -The event to be processed as {@link ActionEvent}
     */
    public abstract void actionOccurred(ActionEvent e);
}
