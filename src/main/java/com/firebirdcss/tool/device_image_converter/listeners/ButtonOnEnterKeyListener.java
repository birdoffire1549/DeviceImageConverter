package com.firebirdcss.tool.device_image_converter.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.Timer;

/**
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/15/2023
 *
 */
public class ButtonOnEnterKeyListener implements KeyListener {
    private JButton button;
    
    public static void addListenerTo(Component component, JButton button) {
        if (component instanceof JSpinner) {
            JFormattedTextField jft = ((JSpinner.DefaultEditor) ((JSpinner) component).getEditor()).getTextField();
            jft.addKeyListener(new ButtonOnEnterKeyListener(button));
        } else {
            component.addKeyListener(new ButtonOnEnterKeyListener(button));
        }
    }
    
    ButtonOnEnterKeyListener(JButton button) {
        this.button = button;
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Do nothing...
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // Do nothing...
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    button.doClick();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}
