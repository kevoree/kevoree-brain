package org.kevoree.brain.imageprocess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class ImageLoaders {

    public static void generateShadesGrey(BufferedImage img){
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
                File outputfile = new File("D:\\saved"+gray+".jpg");
                ImageIO.write(nextframe, "jpg", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save(BufferedImage frame, String name){
        try {
            File outputfile = new File("D:\\result\\"+name);
            ImageIO.write( frame, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] arg) {
        long starttime;
        long endtime;
        double timeres;
       // final File dir = new File("I:\\to delete\\LEBANESE STARS\\karim\\processed\\wow\\javatest");
        final File dir = new File("I:\\to delete\\LEBANESE STARS\\karim\\processed\\wow\\javatest\\test");
        BufferedImage[] frames = new BufferedImage[31];
        final ImageFilter imageFilter = new ImageFilter();
        int count=0;
        for (final File imgFile : dir.listFiles()) {
            if (imageFilter.accept(imgFile)) {
               // System.out.println(imgFile.getName());
                try {
                    frames[count]= ImageIO.read(imgFile);
                    save(frames[count],imgFile.getName());
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //generateShadesGrey(frames[1]);;

     /*   BufferedImage nextframe = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                nextframe.setRGB(i,j,frames[1].getRGB(i,j));
            }
        }
        try {
            File outputfile = new File("D:\\saved.jpg");
            ImageIO.write(nextframe, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }*/



        // ImageLearner il = new ImageLearner(1859, 951, 50, 50,width,height);


       /* */


        int size=40;
        int nb=frames[0].getWidth()/((int)(size*1.2));
        double ratio=((double)frames[0].getHeight())/frames[0].getWidth();

        System.out.println(nb+","+(int)(nb*ratio)+","+size+","+(int)(size*ratio));
        ImageLearnerArray il = new ImageLearnerArray(nb,(int)(nb*ratio),size,(int)(size*ratio),frames[1]);
        System.out.println("Training started");
        starttime = System.nanoTime();
        il.train(frames[0],frames[1]);
        endtime = System.nanoTime();
        timeres=((double)(endtime-starttime))/(1000000000);
        System.out.println("trained in: " + timeres + " s!");
       // il.createImage(frames[1]);
        //il.createImage2(frames[1]);
        //il.createImage3(frames[1]);
        il.createImage4(frames[1]);
        il.print();
    }
}
