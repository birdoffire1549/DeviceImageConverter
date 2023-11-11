package com.firebirdcss.tool.image_converter.utils;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.firebirdcss.tool.image_converter.data.Settings;

/**
 * This is a general utility class with static methods.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/10/2023
 *
 */
public class Utils {
    private Utils() {}
    
    public static String toSafeCVarName(String name) {
        String result = name;
        if (result != null && !result.isEmpty()) {
            int dotIndex = name.indexOf('.');
            if (dotIndex > 0) { // Assume extension; Use everything prior to dot...
                result = result.substring(0, dotIndex);
            }
            // Remove undesirable characters and capitalize char after...
            StringBuilder sb = new StringBuilder();
            boolean nextCapital = false;
            for (int i = 0; i < result.length(); i++) {
                char c = result.charAt(i);
                if (!Character.isLetter(c) && !Character.isDigit(c)) {
                    nextCapital = true;
                } else {
                    if (nextCapital && !Character.isDigit(c)) {
                        sb.append(Character.toUpperCase(c));
                        nextCapital = false;
                    } else {
                        sb.append(c);
                    }
                }
            }
            result = sb.toString();
            // Remove leading numbers...
            while (!result.isEmpty() && Character.isDigit(result.charAt(0))) {
                result = result.substring(1);
            }
            // Start with lowerCase...
            result = String.valueOf(Character.toLowerCase(result.charAt(0))) + result.substring(1);
            
            return result;
        }
        
        return name;
    }
    
    public static void persistDataToClipboard(String data) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection strSel = new StringSelection(data.toString());
        cb.setContents(strSel, null);
    }
    
    /**
     * Asks the user to choose a file from the file-system using
     * a {@link JFileChooser} dialog.
     * 
     * @param dialogParent - The {@link Component} which is to be the parent of the 
     * JFileChooser dialog.
     * @return Returns a file if one is chosen or null as {@link File}
     */
    public static File getChosenFile(Component dialogParent) {
        JFileChooser fileChooser;
        if (Settings.lastImportDirectory.isEmpty()) { // No previous directory used...
            fileChooser = new JFileChooser();
        } else { // A previous directory was used...
            fileChooser = new JFileChooser(new File(Settings.lastImportDirectory));
        }
        int retVal = fileChooser.showOpenDialog(dialogParent);
        if (retVal == JFileChooser.APPROVE_OPTION) { // File was chosen...
            File file = fileChooser.getSelectedFile();
            Settings.lastImportDirectory = file.getParent();
            
            return file;
        }
        
        return null;
    }
    
    public static boolean persistDataToFile(String data, Component dialogParent) throws IOException {
        JFileChooser fileChooser;
        if (Settings.lastExportDirectory.isEmpty()) { // No previous directory used...
            fileChooser = new JFileChooser();
        } else { // A previous directory was used...
            fileChooser = new JFileChooser(new File(Settings.lastImportDirectory));
        }
        fileChooser.setDialogTitle("Specify a file to save");
        int retVal = fileChooser.showSaveDialog(dialogParent);
        if (retVal == JFileChooser.APPROVE_OPTION) { // File was chosen...
            File file = fileChooser.getSelectedFile();
            Settings.lastImportDirectory = file.getParent();
            try (FileWriter fw = new FileWriter(file);) {
                fw.write(data);
                fw.flush();
            }
            
            return true;
        }
        
        return false;
    }
}
