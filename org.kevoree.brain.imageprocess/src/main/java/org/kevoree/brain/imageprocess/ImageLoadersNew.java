package org.kevoree.brain.imageprocess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class ImageLoadersNew {

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

    public static double compare(BufferedImage frame1,BufferedImage frame2){
        int threshold=120;
        int counter=0;
        int[] blob1=frame1.getRGB(0,0,frame1.getWidth(),frame1.getHeight(),null,0,frame1.getWidth());
        int[] blob2=frame2.getRGB(0,0,frame2.getWidth(),frame2.getHeight(),null,0,frame2.getWidth());
        double res=0;
        for(int i=0;i<blob1.length;i++){
            Color c1= new Color(blob1[i]);
            Color c2= new Color(blob2[i]);
            if(c1.getRed()>threshold&&c1.getGreen()>threshold&&c1.getBlue()>threshold) {
                res += (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue()) + (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed()) + (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen());
                counter++;

            }}
        res=res/(3*counter);
        return Math.sqrt(res);
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

        double ratio=((double)frames[0].getHeight())/frames[0].getWidth();

      /*  System.out.println(compare(frames[0],frames[1]));
        System.out.println(compare(frames[1],frames[2]));
        System.out.println(compare(frames[0],frames[2]));*/
        for(int size= 10; size<550; size=(int)(size*1.2)) {
            int nb = Math.max(frames[0].getWidth() / ((int) (size * 2)),30);
            System.out.println(nb + "," + (int) (nb * ratio) + "," + size + "," + (int) (size * ratio));
            ImageLearnerArray il = new ImageLearnerArray(nb, (int) (nb * ratio), size, (int) (size * ratio), frames[1]);
            //System.out.println("Training started");
            starttime = System.nanoTime();
            il.train(frames[0], frames[1]);
            endtime = System.nanoTime();
            timeres = ((double) (endtime - starttime)) / (1000000000);
            // il.createImage(frames[1]);
            //il.createImage2(frames[1]);
            //il.createImage3(frames[1]);
            BufferedImage nextfr= il.createImage4(frames[1]);
            save(nextfr,"result "+size+".jpg");
            System.out.println("size: "+size+" trained in: " + timeres + " s! error: "+compare(frames[2],nextfr));
            //il.print();
        }
    }
}
