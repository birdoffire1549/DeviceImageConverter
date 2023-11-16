package com.firebirdcss.tool.device_image_converter;

import javax.swing.SwingUtilities;

import com.firebirdcss.tool.device_image_converter.view.MainWindow;

/**
 * Main Entry point for the Application.
 * Creates the MainWindow and then hands over execution to it.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/07/2023
 *
 */
public class GuiAppMain {
    
    /**
     * MAIN: The main entry point of the application and where all
     * execution begins.
     * 
     * @param args - NOT USED!
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow window = new MainWindow();
                
                window.setVisible(true);
            }
        });
    }
}
