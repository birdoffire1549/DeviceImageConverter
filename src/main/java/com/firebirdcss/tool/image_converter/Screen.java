package com.firebirdcss.tool.image_converter;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/07/2023
 *
 */
public class Screen extends Canvas {
    private static final long serialVersionUID = 1L;
    
    private int width;
    private int height;
    private Map<String, Item> screenItems = new HashMap<>();
    
    /**
     * CONSTRUCTOR: 
     *
     * @param initWidth
     * @param initHeight
     */
    public Screen(int initWidth, int initHeight) {
        this.width = initWidth;
        this.height = initHeight;
        this.setSize(initWidth + 1, initHeight + 1);
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.Canvas#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        for (Item itm :screenItems.values()) {
            g.drawImage(itm.getImage(), itm.getX(), itm.getY(), this);
        }
        g.drawRect(0, 0, width, height);
    }
    
    public void registerItem(String id, Image image, int x, int y) {
        if (screenItems.containsKey(id)) { // Just update existing...
            Item itm = screenItems.get(id);
            itm.setImage(image);
            itm.setX(x);
            itm.setY(y);
        } else { // Add the new item...
            screenItems.put(id, new Item(image, x, y));
        }
    }
    
    public void updateImage(String id, Image image) {
        if (screenItems.containsKey(id)) {
            screenItems.get(id).setImage(image);
        }
    }
    
    public void moveItem(String id, int newX, int newY) {
        if (screenItems.containsKey(id)) {
            Item itm = screenItems.get(id);
            itm.setX(newX);
            itm.setY(newY);
        }
    }
    
    @Override
    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
        super.setSize(w + 1, h + 1);
    }
    
    /**
     * 
     * @author Scott Griffis
     * <p>
     * Date: 11/07/2023
     *
     */
    private class Item {
        private Image image;
        private int x;
        private int y;
        
        public Item(Image image, int x, int y) {
            this.image = image;
            this.x = x;
            this.y = y;
        }
        
        public Image getImage() {
            
            return this.image;
        }
        
        public void setImage(Image image) {
            this.image = image;
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
    }
    
}
