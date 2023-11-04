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
        
        // Derive the scaled image...
        Image sImage = image.getScaledInstance(
            (wh[0].equals("0") ? image.getWidth() : Integer.parseInt(wh[0])), 
            (wh[1].equals("0") ? image.getHeight() : Integer.parseInt(wh[1])), 
            Image.SCALE_FAST
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
     * @return Returns the binary image.
     */
    public static byte[][] convertImageToBinary(BufferedImage image, int threshold, int blkBinValue) {
        byte[][] binImage = new byte[image.getHeight()][image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) { // Iterate the y axis...
            for (int x = 0; x < image.getWidth(); x++) { // Iterate the x axis row...
                int color = image.getRGB(x, y);
                int red = color >> 16;
                int green = (color & 0x00FFFF) >> 8;
                int blue = (color & 0x0000FF);
                float grey = (float) (0.299 * (float)red + 0.587 * (float)green + 0.114 * (float)blue);
                if (grey >= threshold) { // Value meets or exceeds threshold...
                    binImage[y][x] = (byte)(blkBinValue == 0 ? 1 : 0);
                } else { // Value doesn't meet the threshold...
                    binImage[y][x] = (byte)blkBinValue;
                }
            }
        }
        
        return binImage;
    }
}
