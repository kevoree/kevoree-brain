package org.kevoree.brain.imageprocess;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class ImageLearner {
    public int x;
    public int y;
    public int w;
    public int h;
    public static int xres=1;
    public static int yres=1;
    public static int xsearch=13;
    public static int ysearch=13;

    private int ixmin;
    private int ixmax;
    private int iymin;
    private int iymax;

    //Grey threashold filter
    private int threshold=60;

    private ImageLearnerState state;

    public ImageLearner(int x, int y, int w, int h, int imgW, int imgH){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        ixmin=Math.max(0, x - xsearch);
        ixmax= Math.min(imgW - w, x + xsearch);
        iymin=Math.max(0, y - ysearch);
        iymax=Math.min(imgH - h, y + ysearch);
        state = new ImageLearnerState();
       // System.out.println("x search: "+ixmin+" , "+ixmax);
       // System.out.println("y search: "+iymin+" , "+iymax);
    }


    public double compare(int[] blob1, int[] blob2){
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


    public void train(BufferedImage prev, BufferedImage cur){
        int[] currentBlob = cur.getRGB(x,y,w,h,null, 0, w);

        int bestix=0;
        int bestiy=0;
        double min=2e100;
        double res=0;
        for(int ix=ixmin;ix<ixmax;ix+=xres){
            for(int iy=iymin;iy<iymax;iy+=yres){
                int[] newBlob=prev.getRGB(ix,iy,w,h,null,0,w);
                res=compare(currentBlob,newBlob);
                if(res==-1){
                    state.AddResult(0,0,-1);
                    return;
                }
                if(res<min){
                    min=res;
                    bestix=ix;
                    bestiy=iy;
                }
            }
        }
        state.AddResult(bestix-x,bestiy-y,Math.sqrt(res));
    }

    public ImageLearnerState getState() {
        return state;
    }

    public void setState(ImageLearnerState state) {
        this.state = state;
    }
}
