package com.firebirdcss.tool.image_converter.data.pojo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

import com.firebirdcss.tool.image_converter.utils.ImageUtils;

public class ImageAsset {
    private File imageFile;
    private String id;
    private BufferedImage originalImage;
    private BufferedImage scaledImage;
    private int x;
    private int y;
    
    public ImageAsset(File imageFile) throws IOException {
        this(imageFile, 0, 0);
    }
    
    public ImageAsset(File imageFile, int x, int y) throws IOException {
        if (imageFile.exists() && imageFile.canRead()) { // File exists and is readable...
            this.imageFile = imageFile;
            this.id = this.imageFile.getName();
            IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
            BufferedImage bImage = ImageIO.read(imageFile);
            this.originalImage = ImageUtils.removeAlphaFromImage(bImage);
            this.scaledImage = null;
        } else {
            throw new IOException("Specified File doesn't exist or is not readable!");
        }
        
        this.x = x;
        this.y = y;
    }
    
    public BufferedImage getOriginalImage() {
        
        return this.originalImage;
    }
    
    public BufferedImage getMaxScaledImage(int maxWidth, int maxHeight) {
        if (this.scaledImage != null) { // There is a ScaledImage to see if works...
            if (scaledImage.getWidth() == maxWidth || scaledImage.getHeight() == maxHeight) { // At lest one dimension is equal to Max...
                if (scaledImage.getWidth() <= maxWidth && scaledImage.getHeight() <= maxHeight) { // ...and all dimensions are less than or equal to the Max...
                    
                    return scaledImage;
                }
            }
        }
        
        // Need a scaled image...
        if (originalImage.getWidth() > originalImage.getHeight()) { // Wide image...
            scaledImage = ImageUtils.scaleImage(originalImage, maxWidth, -1);
        } else if (originalImage.getHeight() > originalImage.getWidth()) { // Tall image...
            scaledImage = ImageUtils.scaleImage(originalImage, -1, maxHeight);
        } else { // Image square...
            if (maxWidth < maxHeight) {
                this.scaledImage = ImageUtils.scaleImage(originalImage, maxWidth, -1);
            } else {
                this.scaledImage = ImageUtils.scaleImage(originalImage, -1, maxHeight);
            }
        }
        
        return this.scaledImage;
    }
    
    public BufferedImage getFitsDisplayScaledImage(int maxWidth, int maxHeight) {
        if (this.scaledImage != null) {
            if (this.scaledImage.getWidth() <= maxWidth && this.scaledImage.getHeight() <= maxHeight) { // Cached is acceptable...
                
                return scaledImage;
            }
        }
        
        return getMaxScaledImage(maxWidth, maxHeight);
    }
    
    public BufferedImage getScaledImage() {
        
        return scaledImage;
    }
    
    public BufferedImage getScaledImage(int width, int height) {
        if (scaledImage != null && scaledImage.getWidth() == width && scaledImage.getHeight() == height) {
            
            return scaledImage;
        }
        
        // Scale and cache image...
        scaledImage = ImageUtils.scaleImage(originalImage, width, height);
        
        return scaledImage;
    }
    
    public int getX() {
        
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public String getId() {
        
        return this.id;
    }
    
    public String getPath() throws IOException {
        
        return this.imageFile.getCanonicalPath();
    }
}
