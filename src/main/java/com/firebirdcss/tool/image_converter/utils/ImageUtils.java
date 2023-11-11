package com.firebirdcss.tool.image_converter.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/03/2023
 *
 */
public class ImageUtils {
    private ImageUtils() {} // Prevents instantiation externally.
    
    /**
     * Scales the given image as indicated by the given width and height dimensions
     * and then returns the resulting image.
     * 
     * @param image - Image to scale as {@link BufferedImage}
     * @param widthAndHeight - Width and Height information as {@link String}
     * @return Returns the result as {@link BufferedImage}
     */
    public static BufferedImage scaleImage(BufferedImage image, String widthAndHeight) {
        String[] wh = widthAndHeight.split(",");
        
        return scaleImage(image, Integer.parseInt(wh[0]), Integer.parseInt(wh[1]));
    }
    
    /**
     * 
     * @param image
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage scaleImage(BufferedImage image, int width, int height) {
        // Derive the scaled image...
        Image sImage = image.getScaledInstance(
            (width == 0 ? image.getWidth() : width), 
            (height == 0 ? image.getHeight() : height), 
            Image.SCALE_DEFAULT
        );
        
        // Convert the image back to a BufferedImage...
        BufferedImage bufSImage = new BufferedImage(
            sImage.getWidth(null), 
            sImage.getHeight(null),
            image.getType()
        );
        Graphics2D g = bufSImage.createGraphics();
        g.drawImage(sImage, 0, 0, null);
        g.dispose();
        
        return bufSImage;
    }
    
    /**
     * Converts the given image to a JPEG image.
     * 
     * @param image - The given image as a {@link BufferedImage}
     * @return Returns the converted image as {@link BufferedImage}
     * @throws IOException 
     */
    public static BufferedImage convertImageToJpeg(BufferedImage image) throws IOException {
        ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", bOutStream);

        ByteArrayInputStream bais = new ByteArrayInputStream(bOutStream.toByteArray());
        BufferedImage jpegImage = ImageIO.read(bais);
        
        return jpegImage;
    }
    
    /**
     * This removes the Alpha from an image if it exists as that can
     * interfere with some processes.
     * 
     * @param image - The image from which to remove the alpha as {@link BufferedImage}
     * @return Returns the image without the alpha as {@link BufferedImage}
     */
    public static BufferedImage removeAlphaFromImage(BufferedImage image) {
        BufferedImage target = image;
        if (image.getColorModel().hasAlpha()) { // Remove the Alpha if it exists...
            target = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = target.createGraphics();
            // g.setColor(new Color(color, false));
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.drawImage(image, 0, 0, null);
            g.dispose();
        }
        
        return target;
    }
    
    /**
     * This method does the work of converting a given image to a binary image.
     * The result gets returned as a two dimensional byte array where the first
     * dimension is the 'y' dimension and the second is the 'x' dimension.
     * 
     * @param image - The image to convert as {@link BufferedImage}
     * @param threshold - The threshold as int.
     * @param blkBinValue - The binary value of black as int.
     * @param fixBytes - This indicates if image data should be padded so rows fit into bytes properly, as boolean
     * @param binPadValue - The binary value to use for padding or -1 to auto determine based on nearest pixel as int.
     * 
     * @return Returns the binary image.
     */
    public static byte[][] convertImageToBinary(BufferedImage image, int threshold, int blkBinValue, boolean fixBytes, int binPadValue) {
        int padNeeded = fixBytes ? (8 - (image.getWidth() % 8)) : 0;
        int adjWidth = image.getWidth() + padNeeded;
        byte[][] binImage = new byte[image.getHeight()][adjWidth];
        for (int y = 0; y < image.getHeight(); y++) { // Iterate the y axis...
            int padAdjust = 0;
            for (int x = 0; x < image.getWidth(); x++) { // Iterate the x axis row...
                int color = image.getRGB(x, y);
                int red = color >> 16;
                int green = (color & 0x00FFFF) >> 8;
                int blue = (color & 0x0000FF);
                float grey = (float) (0.299 * (float)red + 0.587 * (float)green + 0.114 * (float)blue);
                boolean meetsThresh = grey >= threshold;
                
                // Handle front padding...
                if (x == 0 && padNeeded != 0) { // Pad the front...
                    int pad = padNeeded / 2;
                    if (pad > 0) {
                        for (int i = 0; i < pad; i++) {
                            if (binPadValue == -1) {
                                binImage[y][x + padAdjust] = meetsThresh ? (byte)(blkBinValue == 0 ? 1 : 0) : (byte)blkBinValue;
                            } else {
                                binImage[y][x + padAdjust] = (byte)binPadValue;
                            }
                            padAdjust ++;
                        }
                    }
                }
                
                // Store the converted pixel...
                if (meetsThresh) { // Value meets or exceeds threshold...
                    binImage[y][x + padAdjust] = (byte)(blkBinValue == 0 ? 1 : 0);
                } else { // Value doesn't meet the threshold...
                    binImage[y][x + padAdjust] = (byte)blkBinValue;
                }
                
                // Handle back padding...
                if (x == image.getWidth() - 1 && padNeeded != 0) { // Pad the back...
                    int pad = padNeeded - (padNeeded / 2);
                    if (pad > 0) { // Need some back padding...
                        padAdjust ++;
                        for (int i = 0; i < pad; i++) {
                            if (binPadValue == -1) {
                                binImage[y][x + padAdjust] = meetsThresh ? (byte)(blkBinValue == 0 ? 1 : 0) : (byte)blkBinValue;
                            } else {
                                binImage[y][x + padAdjust] = (byte)binPadValue;
                            }
                            padAdjust++;
                        }
                    }
                }
            }
        }
        
        return binImage;
    }
    
    /**
     * Converts a binary image to a C-Type Array.
     * 
     * @param binImg - The binary image as a 2 dimensional byte array.
     * @param imageId - The image ID which will be used for naming the Array as {@link String}
     * @return Returns the result as {@link String}
     */
    public static String toCTypeArray(byte[][] binImg, String imageId) {
        StringBuilder sb = new StringBuilder("unsigned char " + Utils.toSafeCVarName(imageId) + "[] = {\n");
        for (int y = 0; y < binImg.length; y++) {
            sb.append("  ");
            int byteIndex = 0;
            for (int x = 0; x < binImg[y].length; x++) { // Iterate the row (x) pixels...
                // Append 0b to byte data...
                if (x == 0 || x % 8 == 0) { // Prior to byte...
                    sb.append("0b");
                }
                
                // Append the current image pixel value...
                sb.append(String.valueOf((int) binImg[y][x]));
                
                // Append comma at end of byte...
                if (x < binImg[y].length - 1 && (x - byteIndex) % 7 == 0 && x % 8 != 0) { // In last of byte position...
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
