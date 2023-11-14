package com.firebirdcss.tool.image_converter.data;

/**
 * This is a place to keep settings related variables and constants
 * that are to be available for access anywhere in the applicaiton.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/11/2023
 *
 */
public class Settings {
    private Settings() {}
    
    public static String lastImportDirectory = "";
    public static String lastExportDirectory = "";
    
    public static int lastSelectedExportAs = -1;
    public static int lastSelectedBinaryColorRep = -1;
    public static int lastSelectedExportWhere = -1;
    public static int lastSelectedRowTerm = -1;
    
    public static int lastBWConversionThresh = -1;
    public static int lastBinPadVal = -1;
    
}
