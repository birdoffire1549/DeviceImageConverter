package com.firebirdcss.tool.device_image_converter.view.components;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.firebirdcss.tool.device_image_converter.data.pojo.ImageAsset;

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
            g.drawImage(scaled, itm.getX(), itm.getY(), this);
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
