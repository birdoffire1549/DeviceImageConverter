package com.firebirdcss.tool.device_image_converter.utils;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import com.firebirdcss.tool.device_image_converter.data.Settings;
import com.firebirdcss.tool.device_image_converter.data.pojo.ImageExportInfo;

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
            int dotIndex = name.lastIndexOf('.');
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
                    } else if (nextCapital) { // Is number but should be capital...
                        sb.append("_").append(c);
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
    
    public static void persistDataToClipboard(BufferedImage image) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        ImageTransferable it = new ImageTransferable(image);
        cb.setContents(it, null);
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
    
    public static File getNoOverwriteFile(File possibleFile) {
        if (!possibleFile.exists()) {
            
            return possibleFile;
        }
        
        String filePath = possibleFile.getAbsolutePath();
        int dotIndex = filePath.lastIndexOf('.');
        String extension = dotIndex == -1 ? "" : filePath.substring(dotIndex);
        String filePathNoExt = dotIndex == -1 ? filePath : filePath.substring(0, dotIndex);
        int count = 0;
        File tempFile = null;
        do {
            count ++;
            tempFile = new File(filePathNoExt + "_" + count + extension);
        } while (tempFile.exists());
        
        return tempFile;
    }
    
    public static boolean persistDataToFiles(List<ImageExportInfo> data, Component dialogParent) throws IOException {
        JFileChooser fileChooser;
        if (Settings.lastExportDirectory.isEmpty()) { // No previous directory used...
            fileChooser = new JFileChooser();
        } else { // A previous directory was used...
            fileChooser = new JFileChooser(new File(Settings.lastExportDirectory));
            fileChooser.setSelectedFile(fileChooser.getCurrentDirectory());
        }
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Specify directory to create file(s)");
        int retVal = fileChooser.showSaveDialog(dialogParent);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getCurrentDirectory();
            Settings.lastExportDirectory = file.getAbsolutePath();
            List<Exception> exceptions = new ArrayList<>();
            for (ImageExportInfo info : data) {
                try {
                    ImageIO.write(info.getImage(), info.getOutputType().toString(), getNoOverwriteFile(info.getFile(Settings.lastExportDirectory)));
                } catch (IOException e) {
                    exceptions.add(e);
                }
            }
            
            if (exceptions.isEmpty()) {
                
                return true;
            } else {
                StringBuilder message = new StringBuilder("");
                String lastMsg = "";
                for (int i = 0; i < exceptions.size(); i ++) {
                    if (!lastMsg.equals(exceptions.get(i).getMessage())) {
                        if (i > 0) {
                            message.append("\n");
                        }
                        message.append(exceptions.get(i));
                    }
                }
                
                throw new IOException("The following errors occurred while trying to save files:\n" + message.toString());
            }
        }
        
        return false;
    }
    
    public static boolean persistDataToFiles(Map<String/*FileName*/, String/*Data*/> data, Component dialogParent) throws IOException {
        if (data.size() == 1) {
            
            return persistDataToFile((String) data.values().toArray()[0], dialogParent);
        } 
        
        JFileChooser fileChooser;
        if (Settings.lastExportDirectory.isEmpty()) { // No previous directory used...
            fileChooser = new JFileChooser();
        } else { // A previous directory was used...
            fileChooser = new JFileChooser(new File(Settings.lastImportDirectory));
            fileChooser.setSelectedFile(fileChooser.getCurrentDirectory());
        }
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Specify directory to create file(s)");
        int retVal = fileChooser.showSaveDialog(dialogParent);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getCurrentDirectory();
            Settings.lastExportDirectory = file.getAbsolutePath();
            List<Exception> exceptions = new ArrayList<>();
            for (Entry<String,String> e : data.entrySet()) {
                try (FileWriter fw = new FileWriter(getNoOverwriteFile(new File(Settings.lastExportDirectory + File.separator + e.getKey())));) {
                    fw.write(e.getValue());
                    fw.flush();
                } catch (IOException e1) {
                    exceptions.add(e1);
                }
            }
            
            if (exceptions.isEmpty()) {
                
                return true;
            } else {
                StringBuilder message = new StringBuilder("");
                String lastMsg = "";
                for (int i = 0; i < exceptions.size(); i ++) {
                    if (!lastMsg.equals(exceptions.get(i).getMessage())) {
                        if (i > 0) {
                            message.append("\n");
                        }
                        message.append(exceptions.get(i));
                    }
                }
                
                throw new IOException("The following errors occurred while trying to save files:\n" + message.toString());
            }
        }
        
        return false;
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
            Settings.lastExportDirectory = file.getParent();
            try (FileWriter fw = new FileWriter(file);) {
                fw.write(data);
                fw.flush();
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 
     * Code inspired by:<br>
     * https://stackoverflow.com/questions/7834768/setting-images-to-clipboard-java
     * 
     * @author Scott Griffis
     * <p>
     * Date: 11/13/2023
     *
     */
    private static class ImageTransferable implements Transferable {
        private BufferedImage image;
        
        public ImageTransferable(BufferedImage image) {
            this.image = image;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            
            return new DataFlavor[] {DataFlavor.imageFlavor};
        }
        
        /*
         * (non-Javadoc)
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
         */
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            
            return flavor == DataFlavor.imageFlavor;
        }
        
        /*
         * (non-Javadoc)
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (isDataFlavorSupported(flavor)) {
                
                return image;
            } 
            
            throw new UnsupportedFlavorException(flavor);
        }
        
    }
}
