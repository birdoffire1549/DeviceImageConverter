package com.firebirdcss.tool.image_converter.data.enums;

import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * ENUM CLASS: This class enforces the string names for Image Types.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/13/2023
 *
 */
public enum ImageType {
    JPG("jpg"),
    TIFF("tiff"),
    BMP("bmp"),
    GIF("gif"),
    PNG("png"),
    JPEG("jpeg"),
    TIF("tif"),
    WBMP("wbmp");
    
    String value;
    
    private ImageType(String value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        
        return this.value;
    }
    
    public boolean isSupported() {
        String writers[] = ImageIO.getWriterFormatNames();
        
        return Arrays.stream(writers).anyMatch(w -> w.equals(this.toString()));
    }
}
