package com.firebirdcss.tool.image_converter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

/**
 * This class contains the Main method and intended primary entry point for
 * this application. Within the Main method of this class is where all execution
 * is to begin for this application.
 * <p>
 * NOTE: This version of the application is a quick dump of functional (NOT PRETTY)
 * code that I needed to get working before I could finish something else I was 
 * working on that was dependant on the functionality of this code. At some point I
 * plan on coming back to this code and making it better and even adding functionality
 * to it but for right now it is a fast coded mess, that is not typical my coding 
 * style if you have seen any of my work prior you would realize this. :-)
 * <p>
 * This code drew inspiration from the following sources:<br>
 * <ul>
 *   <li>https://www.dcode.fr/binary-image</li>
 *   <li>https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage</li>
 *   <li>https://support.ptc.com/help/mathcad/r9.0/en/index.html#page/PTC_Mathcad_Help/example_grayscale_and_color_in_images.html</li>
 * </ul>
 * <p>
 * @author Scott Griffis
 * <p>
 * Date: 11/01/2023
 * Version: 0.0.1
 *
 */
public class ApplicationMain {
    
    /**
     * MAIN MEHTOD: The main method and entry point of this application. Here is were
     * all of the execution begins.
     * 
     * @param args - NOT USED
     */
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in);) {
            System.out.println("==== Image to Binary Converter ====\n");
            System.out.print("Enter path to image (or 'exit'): ");
            String path = sc.nextLine();
            if (!"exit".equalsIgnoreCase(path)) {
                System.out.print("\nEnter new width in pixels (or 'exit'): ");
                String wpix = sc.nextLine();
                if (!"exit".equalsIgnoreCase(wpix)) {
                    try {
                        int iwpix = Integer.parseInt(wpix);
                        System.out.print("Enter BW Threshold percent [50]: ");
                        String thresh = sc.nextLine();
                        if (!"exit".equalsIgnoreCase(thresh)) {
                            try {
                                int ithresh = thresh.isBlank() ? 50 : -1;
                                if (ithresh == -1) {
                                    ithresh = Integer.parseInt(thresh);
                                }
                                System.out.print("Enter binary digit for background (1 or 0) [0]: ");
                                String backbin = sc.nextLine();
                                if (!"exit".equalsIgnoreCase(backbin)) {
                                    try {
                                        int ibackbin = backbin.isBlank() ? 0 : -1;
                                        if (ibackbin == -1) {
                                            ibackbin = Integer.parseInt(backbin);
                                        }
                                        if (ibackbin > -1 && ibackbin <= 1) {
                                            File file = new File(path);
                                            if (file.exists()) {
                                                System.out.println("FILE: File exists.");
                                            }
                                            if (file.isFile()) {
                                                System.out.println("FILE: File is a file.");
                                            }
                                            if (file.canRead()) {
                                                System.out.println("FILE: File is readable.");
                                            }
                                            System.out.print("\nReading image... ");
                                            IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
                                            BufferedImage image = ImageIO.read(file);
                                            System.out.println("Complete.");
                                            System.out.print("Scaling image... ");
                                            Image sImage = image.getScaledInstance(iwpix, -1, 1);
                                            BufferedImage bsImage = new BufferedImage(sImage.getWidth(null), sImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
                                            Graphics2D g = bsImage.createGraphics();
                                            g.drawImage(sImage, 0, 0, null);
                                            g.dispose();
                                            System.out.println("Complete.");
                                            System.out.print("Converting image... ");
                                            byte[][] binary = new byte[bsImage.getHeight()][bsImage.getWidth()];
                                            for (int y = 0; y < bsImage.getHeight(); y++) {
                                                for (int x = 0; x < bsImage.getWidth(); x++) {
                                                    int color = bsImage.getRGB(x, y);
                                                    int red = color >> 16;
                                                    int green = (color & 0x00FFFF) >> 8;
                                                    int blue = (color & 0x0000FF);
                                                    float grey = (float) (0.299 * (float)red + 0.587 * (float)green + 0.114 * (float)blue);
                                                    if (grey >= ithresh) {
                                                        binary[y][x] = (byte)(ibackbin == 0 ? 0 : 1);
                                                    } else {
                                                        binary[y][x] = (byte)(ibackbin == 0 ? 1 : 0);
                                                    }
                                                    
                                                }
                                            }
                                            System.out.println("Complete.");
                                            System.out.println("\n\nSample Binary:");
                                            for (int y = 0; y < binary.length; y++) {
                                                for (int x = 0; x < binary[y].length; x++) {
                                                    System.out.print(binary[y][x]);
                                                }
                                                System.out.println("");
                                            }
                                            System.out.println("\n\nChoose how to handle result...");
                                            System.out.println("1) Display as 'C' Array.");
                                            System.out.println("2) Save as 'C' Array.");
                                            System.out.print("\nEnter choice (or 'exit'): ");
                                            String choice = sc.nextLine();
                                            if (!"exit".equalsIgnoreCase(choice)) {
                                                try {
                                                    int ichoice = Integer.parseInt(choice);
                                                    switch (ichoice) {
                                                        case 1:
                                                            System.out.println("\nC-Type Array:\n");
                                                            System.out.println(toCTypeArray(binary, String.valueOf(ibackbin)));
                                                            System.out.println();
                                                            break;
                                                        case 2:
                                                            System.out.println("\n\nOOPS!!! Feature not yet created!\n\n");
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                } catch(NumberFormatException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("\n\n~ Application Ended ~\n");
    }
    
    private static String toCTypeArray(byte[][] binImg, String bgBinDigit) {
        StringBuilder sb = new StringBuilder("unsigned char theImage[] = {\n");
        for (int y = 0; y < binImg.length; y++) {
            sb.append("  ");
            int paddingNeeded = binImg[y].length % 8;
            paddingNeeded = paddingNeeded != 0 ? 8 - paddingNeeded : 0;
            int initPad = paddingNeeded / 2;
            int backPad = paddingNeeded - initPad;
            int byteIndex = 0;
            
            for (int x = 0; x < binImg[y].length; x++) { // Iterate the row (x) pixels...
                if (x == 0 || (initPad + x) % 8 == 0) { // Prior to byte...
                    sb.append("0b");
                    if (x == 0 && initPad > 0) { // In position for initial padding...
                        for (int i = 0; i < initPad; i++) {
                            sb.append(bgBinDigit);
                        }
                    }
                }
                
                sb.append(String.valueOf((int) binImg[y][x]));
                
                if (x == binImg[y].length - 1 && backPad > 0) { // In backPad position...
                    for (int i = 0; i < backPad; i++) { // Generate back padding...
                        sb.append(bgBinDigit);
                    }
                }
                
                if (x < binImg[y].length - 1 && (initPad + x - byteIndex) % 7 == 0 && (initPad + x) % 8 != 0) { // In last of byte position...
                    sb.append(", ");
                    byteIndex++;
                }
            }
            
            if (y < binImg.length - 1) {
                sb.append(",\n");
            }
        }
        sb.append("\n};");
        
        return sb.toString();
    }
}
