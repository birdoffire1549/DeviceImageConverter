package com.firebirdcss.tool.image_converter.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

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
public class SelectOnFocusListener implements FocusListener {
    private static volatile Component lastSelectedComponent = null;
    
    private Component thisComponent;
    private JFormattedTextField textField;
    
    public static void addListenerTo(Component component) {
        addListenerTo(component, true);
    }
    
    public static void addListenerTo(Component component, boolean autoSelectThis) {
        if (component instanceof JSpinner) {
            JFormattedTextField jft = ((JSpinner.DefaultEditor) ((JSpinner) component).getEditor()).getTextField();
            jft.addFocusListener(new SelectOnFocusListener(component, autoSelectThis));
        } else {
            component.addFocusListener(new SelectOnFocusListener(component, autoSelectThis));
        }
    }
    
    public SelectOnFocusListener(Component component) {
        this(component, true);
    }
    
    public SelectOnFocusListener(Component component, boolean autoSelectThis) {
        thisComponent = component;
        if (autoSelectThis) {
            if (component instanceof JSpinner) {
                textField = ((JSpinner.DefaultEditor) ((JSpinner) component).getEditor()).getTextField();
            } else {
                textField = null;
            }
        } else {
            textField = null;
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    @Override
    public void focusGained(FocusEvent e) {
        if (textField != null) {
             if (lastSelectedComponent == null || lastSelectedComponent != thisComponent) {
                 Timer timer = new Timer(200, new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent e) {
                         textField.selectAll();
                     }
                     
                 });
                 timer.setRepeats(false);
                 timer.start();
             }
        }
        lastSelectedComponent = thisComponent;
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    @Override
    public void focusLost(FocusEvent e) {
        
    }
    
}
