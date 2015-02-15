package org.kevoree.brain.imageprocessnew;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by assaa_000 on 15/02/2015.
 */
public class ImageUtil {

    public static void generateShadesGrey(BufferedImage img, String path, String filename){
        int step=10;
        for(int gray=step; gray<255; gray+=step) {
            BufferedImage nextframe = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color c=new Color(img.getRGB(i, j));
                    if(c.getBlue()>gray&&c.getGreen()>gray&&c.getRed()>gray) {
                        nextframe.setRGB(i, j, img.getRGB(i, j));
                    }
                }
            }
            try {
                File outputfile = new File(path+filename+gray+".jpg");
                ImageIO.write(nextframe, "jpg", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save(BufferedImage frame,String path, String name){
        try {
            File outputfile = new File(path+name);
            ImageIO.write( frame, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static double compare(int[] blob1, int[] blob2, int threshold){
        double res=0;
        int counter=0;
        for(int i=0;i<blob1.length;i++){
            Color c1= new Color(blob1[i]);
            Color c2= new Color(blob2[i]);
            if(c1.getRed()>threshold&&c1.getGreen()>threshold&&c1.getBlue()>threshold) {
                res += (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue()) + (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed()) + (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen());
                counter++;
            }
        }
        if(counter!=0){
            res=res/(3*counter);
        }
        else{
            res=-1;
        }
        return res;
    }

    public static double compareFrames(BufferedImage frame1,BufferedImage frame2, int threshold){
        int[] blob1=frame1.getRGB(0,0,frame1.getWidth(),frame1.getHeight(),null,0,frame1.getWidth());
        int[] blob2=frame2.getRGB(0,0,frame2.getWidth(),frame2.getHeight(),null,0,frame2.getWidth());
        return compare(blob1,blob2,threshold);
    }

}
