package org.kevoree.brain.imageprocess;

import java.io.File;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class ImageFilter{
    String GIF = "gif";
    String PNG = "png";
    String JPG = "jpg";
    String BMP = "bmp";
    String JPEG = "jpeg";
    public boolean accept(File file) {
        if(file != null) {
            if(file.isDirectory())
                return false;
            String extension = getExtension(file);
            if(extension != null && isSupported(extension))
                return true;
        }
        return false;
    }
    private String getExtension(File file) {
        if(file != null) {
            String filename = file.getName();
            int dot = filename.lastIndexOf('.');
            if(dot > 0 && dot < filename.length()-1)
                return filename.substring(dot+1).toLowerCase();
        }
        return null;
    }
    private boolean isSupported(String ext) {
        return ext.equalsIgnoreCase(GIF) || ext.equalsIgnoreCase(PNG) ||
                ext.equalsIgnoreCase(JPG) || ext.equalsIgnoreCase(BMP) ||
                ext.equalsIgnoreCase(JPEG);
    }
}
