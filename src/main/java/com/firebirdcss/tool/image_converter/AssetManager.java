package com.firebirdcss.tool.image_converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AssetManager {
    private static final String[] HEADERS = {"Asset ID", "Width", "Height", "XPos", "YPos"};
    final private Map<String, ImageAsset> assets = new HashMap<>();
    
    public AssetManager() {
        
    }
    
    public void add(ImageAsset asset) {
        assets.put(asset.getId(), asset);
    }
    
    public ImageAsset get(String assetId) {
        
        return assets.get(assetId);
    }
    
    public void remove(ImageAsset asset) {
        assets.remove(asset.getId());
    }
    
    public void remove(String imageId) {
        assets.remove(imageId);
    }
    
    public int size() {
        return assets.size();
    }
    
    public List<ImageAsset> getAssets() {
        List<ImageAsset> results = new ArrayList<>();
        results.addAll(assets.values());
        
        return results;
    }
    
    public String[] getHeaders() {
        
        return HEADERS;
    }
    
    public String[][] getData() {
        String[][] result = new String[assets.size()][HEADERS.length];
        int row = 0;
        for (Entry<String, ImageAsset> e : assets.entrySet()) {
            ImageAsset a = e.getValue();
            result[row][0] = a.getId();
            result[row][1] = a.getScaledImage() != null ? String.valueOf(a.getScaledImage().getWidth()) : String.valueOf(a.getOriginalImage().getWidth());
            result[row][2] = a.getScaledImage() != null ? String.valueOf(a.getScaledImage().getHeight()) : String.valueOf(a.getOriginalImage().getHeight());
            result[row][3] = String.valueOf(a.getX());
            result[row][4] = String.valueOf(a.getY());
            row ++;
        }
        
        return result;
    }
}
