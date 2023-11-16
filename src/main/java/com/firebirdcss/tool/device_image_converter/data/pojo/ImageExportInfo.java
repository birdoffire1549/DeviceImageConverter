package com.firebirdcss.tool.device_image_converter.data.pojo;

import java.awt.image.BufferedImage;
import java.io.File;

import com.firebirdcss.tool.device_image_converter.data.enums.ImageType;
import com.firebirdcss.tool.device_image_converter.utils.Utils;

/**
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/13/2023
 *
 */
public class ImageExportInfo {
    BufferedImage image;
    String imageId;
    ImageType outputType;
    
    public ImageExportInfo(ImageAsset imageAsset, ImageType outputType) {
        this.image = imageAsset.getScaledImage();
        this.imageId = imageAsset.getId();
        this.outputType = outputType;
    }
    
    public ImageExportInfo(BufferedImage image, String id, ImageType outputType) {
        this.image = image;
        this.imageId = id;
        this.outputType = outputType;
    }
    
    public BufferedImage getImage() {
        
        return image;
    }
    
    public String getImageId() {
        
        return imageId;
    }
    
    public ImageType getOutputType() {
        
        return outputType;
    }
    
    public String getFileName() {
        
        return Utils.toSafeCVarName(imageId) + "." + outputType.toString();
    }
    
    public File getFile(String directoryPath) {
        
        return new File(directoryPath + File.separator + getFileName());
    }
}
