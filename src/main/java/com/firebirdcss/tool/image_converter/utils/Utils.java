package com.firebirdcss.tool.image_converter.utils;

/**
 * This is a general utility class with static methods.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 11/10/2023
 *
 */
public class Utils {
    private Utils() {}
    
    public static String toSafeCVarName(String name) {
        String result = name;
        if (result != null && !result.isEmpty()) {
            int dotIndex = name.indexOf('.');
            if (dotIndex > 0) { // Assume extension; Use everything prior to dot...
                result = result.substring(0, dotIndex);
            }
            // Remove undesirable characters and capitalize char after...
            StringBuilder sb = new StringBuilder();
            boolean nextCapital = false;
            for (int i = 0; i < result.length(); i++) {
                char c = result.charAt(i);
                if (!Character.isLetter(c) && !Character.isDigit(c)) {
                    nextCapital = true;
                } else {
                    if (nextCapital && !Character.isDigit(c)) {
                        sb.append(Character.toUpperCase(c));
                        nextCapital = false;
                    } else {
                        sb.append(c);
                    }
                }
            }
            result = sb.toString();
            // Remove leading numbers...
            while (!result.isEmpty() && Character.isDigit(result.charAt(0))) {
                result = result.substring(1);
            }
            // Start with lowerCase...
            result = String.valueOf(Character.toLowerCase(result.charAt(0))) + result.substring(1);
            
            return result;
        }
        
        return name;
    }
}
