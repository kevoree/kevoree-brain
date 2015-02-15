package org.kevoree.brain.imageprocessnew;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class ImageTest {


    public static void main(String[] arg) {
        long starttime;
        long endtime;
        double timeres;
       // final File dir = new File("I:\\to delete\\LEBANESE STARS\\karim\\processed\\wow\\javatest");
        final File dir = new File("I:\\to delete\\LEBANESE STARS\\karim\\processed\\wow\\javatest\\test");
        final String workdir="D:\\result\\";
        BufferedImage[] frames = new BufferedImage[31];
        final ImageFilter imageFilter = new ImageFilter();
        int count=0;
        for (final File imgFile : dir.listFiles()) {
            if (imageFilter.accept(imgFile)) {
               // System.out.println(imgFile.getName());
                try {
                    frames[count]= ImageIO.read(imgFile);
                    ImageUtil.save(frames[count], workdir, imgFile.getName());
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        double ratio=((double)frames[0].getHeight())/frames[0].getWidth();

        int threshold=127;
        System.out.println(ImageUtil.compareFrames(frames[0], frames[1], threshold)+" "+ImageUtil.compareFrames(frames[0], frames[1], 0));
        System.out.println(ImageUtil.compareFrames(frames[1], frames[2], threshold)+" "+ImageUtil.compareFrames(frames[1], frames[2], 0));
        System.out.println(ImageUtil.compareFrames(frames[0], frames[2], threshold)+" "+ImageUtil.compareFrames(frames[0], frames[2], 0));


        for(int size= 80; size<200; size+=20) {
            for(double k=1;k<4;k=k*1.4) {
                int nb = Math.max(frames[0].getWidth() * frames[0].getHeight() / ((int) (size * size * ratio * k)), 30);
                //System.out.println(nb + "," + (int) (nb * ratio) + "," + size + "," + (int) (size * ratio));
                ImageLearner il = new ImageLearner(nb, size, (int) (size * ratio), frames[1], TrackingDistributionStrategy.UNIFORM);
                // ImageLearner il = new ImageLearner(nb, (int) (nb * ratio), size, (int) (size * ratio), frames[1]);
                //System.out.println("Training started");
                starttime = System.nanoTime();
                il.train(frames[0], frames[1]);
                endtime = System.nanoTime();
                timeres = ((double) (endtime - starttime)) / (1000000000);
                BufferedImage nextfr = il.createNextFrame(frames[1]);
                ImageUtil.save(nextfr, workdir, "result " + size + "- "+k+".jpg");
                System.out.println("size: " + size + "k: "+k+" trained in: " + timeres + " s! error: " + ImageUtil.compareFrames(frames[2], nextfr, threshold) + " " + ImageUtil.compareFrames(frames[2], nextfr, 0));
                //il.print();
            }
        }
    }
}
