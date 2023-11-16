package com.firebirdcss.tool.device_image_converter.data.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    
    private String value;
    
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
    
    /**
     * Used to determine of the ImageType is supported by
     * the System.
     * 
     * @return Returns true if supported, otherwise false as <code>boolean</code>
     */
    public boolean isSupported() {
        String writers[] = ImageIO.getWriterFormatNames();
        
        return Arrays.stream(writers).anyMatch(w -> w.equals(this.toString()));
    }
    
    /**
     * STATIC METHOD: Used to fetch a String List of System supported
     * ImageTypes.
     * 
     * @return Returns a {@link List} of {@link String}s which is the ImagesTypes
     * supported by the System.
     */
    public static String[] getSystemSupportedTypeStrings() {
        List<String> result = new ArrayList<>();
        for (ImageType t : ImageType.values()) {
            if (t.isSupported()) {
                result.add(t.toString());
            }
        }
        
        Collections.sort(result);
        
        return result.toArray(new String[] {});
    }
    
    /**
     * When supplied an Enum value this method can retrieve the Enum
     * which belongs to that value.
     * 
     * @param value - The value of the enum to fetch as {@link String}
     * @return Returns the enum as {@link ImageType}
     */
    public static ImageType getEnumByValue(String value) {
        for (ImageType t : ImageType.values()) {
            if (t.value.equals(value)) {
                
                return t;
            }
        }
        
        return null;
    }
}
