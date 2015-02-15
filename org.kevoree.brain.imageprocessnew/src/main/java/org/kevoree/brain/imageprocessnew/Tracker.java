package org.kevoree.brain.imageprocessnew;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class Tracker implements Runnable {
    public int x;
    public int y;
    public int w;
    public int h;
    public int imgW;
    public int imgH;

    public static BufferedImage prev, cur;
    public static int xres=1;
    public static int yres=1;
    public static int xsearch=13;
    public static int ysearch=13;
    //Grey threashold filter
    private static int threshold=70;


    private int ixmin;
    private int ixmax;
    private int iymin;
    private int iymax;

    public void setState(TrackerState state) {
        this.state = state;
    }

    private TrackerState state;


    public Point getCenter(){
        Point p=new Point(x+w/2,y+w/2);
        return p;
    }

    public Point getDirection(){

        Point p=new Point(state.getNx(),state.getNy());
        return p;
    }
    public Tracker getTranslatedTracker(){
        Tracker res=new Tracker(x-state.getNx(),y-state.getNy(),w,h,imgW,imgH);
        res.setState(state); //todo to clone later
        return res;
    }

    public Tracker(int x, int y, int w, int h, int imgW, int imgH){
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        this.imgH=imgH;//toremove
        this.imgW=imgW;//toremove
        ixmin=Math.max(0, x - xsearch);
        ixmax= Math.min(imgW - w, x + xsearch);
        iymin=Math.max(0, y - ysearch);
        iymax=Math.min(imgH - h, y + ysearch);
        state = new TrackerState();
    }

    /*public void train(BufferedImage prev, BufferedImage cur){
        int[] currentBlob = cur.getRGB(x,y,w,h,null, 0, w);

        int bestix=0;
        int bestiy=0;
        double min=2e100;
        double res=0;
        for(int ix=ixmin;ix<ixmax;ix+=xres){
            for(int iy=iymin;iy<iymax;iy+=yres){
                int[] newBlob=prev.getRGB(ix,iy,w,h,null,0,w);
                res=ImageUtil.compare(currentBlob, newBlob, threshold);
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
    }*/

    @Override
    public void run() {
        int[] currentBlob = cur.getRGB(x,y,w,h,null, 0, w);

        int bestix=0;
        int bestiy=0;
        double min=2e100;
        double res=0;
        for(int ix=ixmin;ix<ixmax;ix+=xres){
            for(int iy=iymin;iy<iymax;iy+=yres){
                int[] newBlob=prev.getRGB(ix,iy,w,h,null,0,w);
                res=ImageUtil.compare(currentBlob, newBlob, threshold);
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


    public TrackerState getState() {
        return state;
    }
}
