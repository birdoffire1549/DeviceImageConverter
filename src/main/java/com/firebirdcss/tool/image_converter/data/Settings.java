package com.firebirdcss.tool.image_converter.data;

import javax.swing.ButtonModel;

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
    
    public static ButtonModel lastSelectedExportAs = null;
    public static int lastBWConversionThresh = -1;
    public static int lastBinPadVal = -1;
    public static ButtonModel lastSelectedBinaryColorRep = null;
    public static ButtonModel lastSelectedExportWhere = null;
    
    public static ButtonModel lastSelectedRowTerm = null;
}
