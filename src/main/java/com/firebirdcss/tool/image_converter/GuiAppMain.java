package com.firebirdcss.tool.image_converter;

import javax.swing.SwingUtilities;

import com.firebirdcss.tool.image_converter.view.MainWindow;

/**
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
