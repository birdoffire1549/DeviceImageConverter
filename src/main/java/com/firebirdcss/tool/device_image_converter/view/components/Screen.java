package com.firebirdcss.tool.device_image_converter.view.components;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.firebirdcss.tool.device_image_converter.data.pojo.ImageAsset;
import com.firebirdcss.tool.device_image_converter.utils.ImageUtils;

/**
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/07/2023
 *
 */
public class Screen extends Canvas {
    private static final long serialVersionUID = 1L;
    
    private int scrWidth;
    private int scrHeight;
    private Map<String, ImageAsset> screenItems = new HashMap<>();
    
    private boolean showBinary = false;
    private boolean invertImages = false;
    private Color bgColor = Color.WHITE;
    private Color fgColor = Color.BLACK;
    
    /**
     * CONSTRUCTOR: 
     *
     * @param initWidth
     * @param initHeight
     */
    public Screen(int initWidth, int initHeight) {
        this.scrWidth = initWidth;
        this.scrHeight = initHeight;
        this.setSize(scrWidth, scrHeight);
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.Canvas#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        for (ImageAsset itm :screenItems.values()) {
            BufferedImage scaled = itm.getScaledImage() != null ? itm.getScaledImage() : itm.getMaxScaledImage(scrWidth, scrHeight);
            BufferedImage finalImage;
            if (showBinary) {
                BufferedImage binary = ImageUtils.convertImageToBinaryColor(scaled, this.bgColor, this.fgColor);
                if (invertImages) {
                    finalImage = ImageUtils.invertBinaryImage(binary);
                } else {
                    finalImage = binary;
                }
            } else {
                finalImage = scaled;
            }
            g.drawImage(finalImage, itm.getX(), itm.getY(), this);
        }
        g.drawRect(0, 0, scrWidth, scrHeight);
    }
    
    public void registerItem(ImageAsset imageAsset) {
        if (imageAsset.getScaledImage() == null) {
            imageAsset.getMaxScaledImage(scrWidth, scrHeight);
        }
        screenItems.put(imageAsset.getId(), imageAsset);
    }
    
    public void unregisterItem(String imageId) {
        screenItems.remove(imageId);
    }
    
    public void moveItem(String id, int newX, int newY) {
        if (screenItems.containsKey(id)) {
            ImageAsset itm = screenItems.get(id);
            itm.setX(newX);
            itm.setY(newY);
        }
    }
    
    public void showBinary(boolean enabled) {
        this.showBinary = enabled;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }
    
    public void invertImages(boolean enabled) {
        this.invertImages = enabled;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }
    
    /* (non-Javadoc)
     * @see java.awt.Component#getForeground()
     */
    @Override
    public Color getForeground() {
        
        return this.fgColor;
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setForeground(java.awt.Color)
     */
    @Override
    public void setForeground(Color c) {
        this.fgColor = c;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    /* (non-Javadoc)
     * @see java.awt.Component#getBackground()
     */
    @Override
    public Color getBackground() {
        
        return this.bgColor;
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setBackground(java.awt.Color)
     */
    @Override
    public void setBackground(Color c) {
        this.bgColor = c;
        super.setBackground(c);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.Component#setSize(int, int)
     */
    @Override
    public void setSize(int w, int h) {
        this.scrWidth = w;
        this.scrHeight = h;
        super.setSize(w + 1, h + 1);
    }
}
