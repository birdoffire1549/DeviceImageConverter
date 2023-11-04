package com.firebirdcss.tool.image_converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

import com.firebirdcss.tool.image_converter.utils.ImageUtils;

/**
 * This class contains the Main method and intended primary entry point for
 * this application. Within the Main method of this class is where all execution
 * is to begin for this application.
 * <p>
 * This code drew inspiration from the following sources:<br>
 * <ul>
 *   <li>https://www.dcode.fr/binary-image</li>
 *   <li>https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage</li>
 *   <li>https://support.ptc.com/help/mathcad/r9.0/en/index.html#page/PTC_Mathcad_Help/example_grayscale_and_color_in_images.html</li>
 *   <li>https://stackoverflow.com/questions/60380175/how-to-resolve-javax-imageio-iioexception-bogus-input-colorspace</li>
 * </ul>
 * <p>
 * @author Scott Griffis
 * <p>
 * Date: 11/01/2023
 * Version: 0.3.0
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
            String intro = "" +
                "===================================\n" +
                "==== Image to Binary Converter ====\n" +
                "===================================\n\n" +
                "This tool will guide you through it's usage. You may enter the \n" +
                "word 'quit' at any prompt in the application to immidiately \n" +
                "terminate the application. Enjoy!!!\n"
            ;
            System.out.println(intro);
            
            // Obtain the image file...
            File imageFile = getImageFile(sc);
            
            // Read in the image...
            System.out.print("\nReading image... ");
            IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
            BufferedImage bImage = ImageIO.read(imageFile);
            System.out.println("Complete.");
            
            // Obtain resizing information...
            String widthAndHeight = getWidthAndHeight(sc);
            
            // Stripping the Alpha from the image...
            System.out.print("Stripping Alpha from image... ");
            BufferedImage bStrippedImage = ImageUtils.removeAlphaFromImage(bImage); 
            System.out.println("Complete.");
            
            // Apply the resizing information...
            System.out.print("Scaling image... ");
            BufferedImage bScaledImage = ImageUtils.scaleImage(bStrippedImage, widthAndHeight); 
            System.out.println("Complete.");
            
            // Obtain Threshold and desired black binary value...
            int threshold = getThreshold(sc);
            int blkBinValue = getBlackBinaryValue(sc);
          
            System.out.print("Converting image to binary... ");
            byte[][] binImage = ImageUtils.convertImageToBinary(bScaledImage, threshold, blkBinValue);
            System.out.println("Complete.");
            
            // Show binary image data...
            dumpBinaryImageDataToScreen(binImage);
            
            // Do something with the binary image...
            doSomethingWithBinaryImage(sc, binImage, blkBinValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        quitApplication();
    }
    
    /**
     * Dumps the binary image data to the screen.
     * 
     * @param binImage - The binary image data in a two dimensional byte array
     * where the first dimension is the 'y' dimension and the second is the 'x'
     * dimension.
     */
    private static void dumpBinaryImageDataToScreen(byte[][] binImage) {
        System.out.println("\n\nSample Binary:");
        for (int y = 0; y < binImage.length; y++) { // Iterate the y axis...
            for (int x = 0; x < binImage[y].length; x++) { // Iterate the x axis row...
                System.out.print(binImage[y][x]);
            }
            System.out.println("");
        }
    }
    
    /**
     * This method us used to give the user choices for what to do with the newly created binary image.
     * Then this method delegates the work of doing those things to other methods.
     * 
     * @param sc - A {@link Scanner} instance for communicating with user.
     * @param binImage - The binary image in two dimensional array form.
     * @param blkBinValue - The binary value for black.
     */
    private static void doSomethingWithBinaryImage(Scanner sc, byte[][] binImage, int blkBinValue) {
        System.out.println("\n\nChoose how to handle result...");
        System.out.println("1) Display as 'C' Array.");
        System.out.println("2) Save as 'C' Array.");
        System.out.print("\nEnter choice: ");
        String choice = sc.nextLine();
        if (!"quit".equalsIgnoreCase(choice)) { // User wants to quit...
            try {
                int ichoice = Integer.parseInt(choice);
                switch (ichoice) {
                    case 1:
                        System.out.println("\nC-Type Array:\n");
                        System.out.println(toCTypeArray(binImage, String.valueOf(blkBinValue)));
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
    
    /**
     * Gets the binary value for black from the user, forcing user to 
     * retry until they pick a valid response.
     * 
     * @param sc - A {@link Scanner} instance to communicate with user.
     * @return Returns a valid binary digit for the background as int.
     */
    private static int getBlackBinaryValue(Scanner sc) {
        while(true) {
            System.out.print("\nEnter binary digit for black (1 or 0) [0]: ");
            String in = sc.nextLine();
            if ("quit".equalsIgnoreCase(in)) { // User wants to quit...
                quitApplication();
            }
            if (in.isBlank()) { // User wants to use a zero/default value...
                
                return 0;
            }
            
            try {
                int bin = Integer.parseInt(in);
                if (bin == 1 || bin == 0) { // value is valid...
                    
                    return bin;
                }
                System.out.println("Input can only be a 1 or 0; Try Again!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input; Input should be a 1 or 0; Try Again!");
            }
        }
    }
    
    
    /**
     * This is used to obtain a threshold percent from the user.
     * A Value of 50 is assumed if user enters nothing. Otherwise the value
     * must be from 0 to 100. User will be forced to retry until they get it
     * right.
     * 
     * @param sc - A {@link Scanner} instance to communicate with user.
     * @return Returns the chosen threshold as int.
     */
    private static int getThreshold(Scanner sc) {
        while(true) {
            System.out.print("\nEnter Black&White Threshold percent [50]: "); 
            String in = sc.nextLine();
            if (in.isBlank()) {
                
                return 50;
            }
            if ("quit".equalsIgnoreCase(in)) {
                quitApplication();
            }
            try {
                int value = Integer.parseInt(in);
                if (value >= 0 && value <= 100) {
                    
                    return value;
                }
                
                System.out.println("Entered value must be from 0 to 100; Try Again!");
            } catch (NumberFormatException  e) {
                System.out.println("Only numeric values from 0 to 100 are valid; Try Again!");
            }
        }
    }
    
    /**
     * Used to get the new width and height for scaling of the image from
     * the user. Result is comma separated values and dimensions the user
     * doesn't want scaled will be 0 values, the ones they do will have a 
     * numeric value and if they want one to be auto adjusted for aspect-ratio 
     * by the other dimension then one will have a numeric positive value and
     * the auto size dimension will be -1.
     * <p>
     * The values fetched are verified and user is made to retry if values
     * are not acceptable.
     * 
     * @param sc - A {@link Scanner} instance.
     * @return Returns a comma separated width and height as {@link String}
     */
    private static String getWidthAndHeight(Scanner sc) {
        while(true) { // Keep trying until user gets it right... 
            String result = "";
            
            String descr = "" +
                "\nRESIZE: To resize the image you will have the chance to enter new dimensions\n" +
                "for the image. If you want a dimension to remain unchanged simply don't enter\n" +
                "any value for that dimension. However, if you would like to retain aspect-ratio\n" +
                "enter a specific size for the dimension you want to specify and a -1 for the\n" +
                "one you wish to be auto set to maintain aspect-ratio.\n"
            ;
            System.out.println(descr);
            System.out.print("Enter the new width in pixels: ");
            String width = sc.nextLine();
            if ("quit".equalsIgnoreCase(width)) { // User wants to quit...
                quitApplication();
            }
            try {
                try {
                    if (width.isBlank()) { // Blank for no change to dimension...
                        result = "0,";
                    } else { // Should be a numeric value...
                        int val = Integer.valueOf(width);
                        if (val < -1 || val == 0) {
                            System.out.println("Value of dimension is outside valid range; Must be positive or -1; Try Again!");
                            
                            continue;
                        }
                        result = "" + val + ",";
                    }
                    System.out.print("Enter the new height in pixels: ");
                    String height = sc.nextLine();
                    if ("quit".equalsIgnoreCase(height)) { // User wants to quit...
                        quitApplication();
                    }
                    if (height.isBlank()) { // Blank for no change to dimension...
                        
                        return (result += 0);
                    } else { // Should be a numeric value...
                        int val = Integer.valueOf(height);
                        if (val < -1 || val == 0) {
                            System.out.println("Value of dimension is outside valid range; Must be positive or -1; Try Again!");
                            
                            continue;
                        }
                        return result += "" + val;
                    }
                } catch (NumberFormatException e) { // Non-numeric value that should have been numeric...
                    System.out.println("Entered dimension was non-numeric; Try Again!");
                    
                    continue;
                }
            } catch (Exception e) { // Unexpected error...
                System.out.print("Encounterd the following Error:\n\t");
                System.out.println("\"" + e.getMessage() + "\"\nTry again!");
                
                continue;
            }
        }
    }
    
    /**
     * Used to get the file for the image we will be working with
     * from the user. This method handles error checking and retries.
     * 
     * @param sc - The scanner as {@link Scanner}
     * @return Returns an existing and readable {@link File}
     */
    private static File getImageFile(Scanner sc) {
        while(true) { // Keep trying until user gets it right...
            System.out.print("\nEnter the path to the desired Image: ");
            String path = sc.nextLine().trim();
            if ("quit".equalsIgnoreCase(path)) { // User wants to quit...
                quitApplication();
            }
            
            try {
                File file = new File(path);
                if (!file.exists()) { // File doesn't exist...
                    System.out.println("It appears the file doesn't exist; Try Again!");
                    
                    continue;
                } else if (!file.canRead()) { // File exists but cannot read it...
                    System.out.println("Cannot access the specified file; Try Again!");
                    
                    continue;
                }
                
                return file;
            } catch (Exception e) { // Something unexpected happened...
                System.out.print("Encounterd the following Error:\n\t");
                System.out.println("\"" + e.getMessage() + "\"\nTry again!");
                
                continue;
            }
        }
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
    
    /**
     * Handles terminating the application, and can be called
     * anywhere in the application where it is needed.
     */
    private static void quitApplication() {
        System.out.println("\n\n~ Application Ended ~\n");
        System.exit(0);
    }
}
