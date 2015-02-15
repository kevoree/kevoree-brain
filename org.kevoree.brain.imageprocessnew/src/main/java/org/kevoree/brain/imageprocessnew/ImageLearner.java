package org.kevoree.brain.imageprocessnew;

import org.kevoree.brain.imageprocessnew.structure.KeyDuplicateException;
import org.kevoree.brain.imageprocessnew.structure.KeySizeException;
import weka.core.neighboursearch.KDTree;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class ImageLearner {

    int w;
    int h;
    public static int imgwid;
    public static int imghei;

    public static int xsearch=13;


    int sq = 5;

   org.kevoree.brain.imageprocessnew.structure.KDTree trackersArray=new org.kevoree.brain.imageprocessnew.structure.KDTree(2);

    public ImageLearner(int trackers, int w, int h, BufferedImage img, TrackingDistributionStrategy tc){
        this.w=w;
        this.h=h;
        this.imgwid=img.getWidth();
        this.imghei=img.getHeight();

        if(tc==TrackingDistributionStrategy.UNIFORM){
            int y=Math.max(1, (int) (Math.sqrt((((double) trackers * imghei)) / (imgwid))));
            int x=Math.max(1,trackers/y);
            System.out.println("trackers: "+x+","+y);
            int varx=Math.max(xsearch,w);
            int stepx=(imgwid-2*varx)/(x);
            int vary=Math.max(xsearch,h);
            int stepy=(imghei-2*vary)/(y);
            double[] positions=new double[2];
            for(int j=0;j<y;j++) {
                positions[1]=vary+j*stepy+h/2;
                for (int i = 0; i < x; i++) {
                    positions[0]=varx +i*stepx+w/2;
                    Tracker t = new Tracker(varx +i*stepx,vary+j*stepy,w,h,imgwid,imghei);
                    try {
                        trackersArray.insertToTree(t,positions);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }



    }

    public void train(BufferedImage prev, BufferedImage cur){
        Tracker.prev=prev;
        Tracker.cur=cur;
        int cores = Runtime.getRuntime().availableProcessors()-1;
        ExecutorService threadPool = Executors.newFixedThreadPool(cores);
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

        LinkedList<Tracker> lt= trackersArray.getLinkedList();
        for(int i=0;i<lt.size();i++){
            threadPool.execute(lt.get(i));
            tasks.add(Executors.callable(lt.get(i)));
        }

        try {
            threadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private void setPixel(int i, int j, int dispx, int dispy, BufferedImage prev, BufferedImage next) {
  for (int k = -sq; k <= sq; k++) {
            for (int l = -sq; l <= sq; l++) {
                if((i+dispx+k)<0||(i+dispx+k)>=next.getWidth()||(j+dispy+l)<0||(j+dispy+l)>=next.getHeight()||(i+k)<0||(i+k)>=next.getWidth()||(j+l)<0||(j+l)>=next.getHeight()){
                    continue;
                }
                else {
                    try {
                        next.setRGB(i + k, j + l, prev.getRGB(i + dispx + k, j + dispy + l));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    private void calcDisp(ArrayList<Tracker> learners, int i, int j, BufferedImage prev, BufferedImage next){

        int dispx=0;
        int dispy=0;
        if(learners.size()==0){
            //System.out.println("0 size " + i + "," + j);
            return;
        }
        else if(learners.size()==1){
            dispx=learners.get(0).getState().getNx();
            dispy=learners.get(0).getState().getNy();
        }
        else{
            if(learners.size()>4){
                System.out.println(learners.size());
            }
            double[] dist=new double[learners.size()];
            double[] weights=new double[learners.size()];
            double total=0;
            for(int l=0;l<dist.length;l++){
                dist[l]=(i-learners.get(l).x)*(i-learners.get(l).x)+(j-learners.get(l).y)*(j-learners.get(l).y);
                for(int k=0;k<dist.length;k++){
                    if(k==l){
                        continue;
                    }
                    else {
                        weights[k] += dist[l];
                        total += dist[l];
                    }
                }
            }
            for(int l=0;l<dist.length;l++){
                dispx+=learners.get(l).getState().getNx()*weights[l]/total;
                dispy+=learners.get(l).getState().getNy()*weights[l]/total;
            }
        }
        setPixel(i,j,(int)dispx,(int)dispy,prev,next);
    }


    public BufferedImage createNextFrame(BufferedImage prev){

        BufferedImage nextframe = new BufferedImage(imgwid,imghei,BufferedImage.TYPE_INT_RGB);
        LinkedList<Tracker> lt= trackersArray.getLinkedList();

        org.kevoree.brain.imageprocessnew.structure.KDTree newtracking = new org.kevoree.brain.imageprocessnew.structure.KDTree(2);
        System.out.println("before: " + lt.size());
        double[] positions=new double[2];
        for(int i=0;i<lt.size();i++){
            Tracker obj= lt.get(i).getTranslatedTracker();
            positions[0]=obj.x+w/2;
            positions[1]=obj.y+h/2;
            try {
                newtracking.insertToTree(obj,positions);
            } catch (Exception e) {
               // e.printStackTrace();
            }
        }
        System.out.println("after: "+newtracking.getLinkedList().size());

       /* double[] lowk=new double[2];
        double[] upk= new double[2];
         lowk[0]=i-w-xsearch;
        lowk[0]=i+w+xsearch;
        lowk[1]=j-h-xsearch;
        upk[1]=j+h+xsearch;*/
        double[] keys= new double[2];

        for(int i=0;i<imgwid;i+=2*sq){

            keys[0]=i;

            for(int j=0;j<imghei;j+=2*sq){

                keys[1]=j;
                ArrayList<Tracker> learners=new ArrayList<Tracker>();
                try {
                    Object[] result =newtracking.nearest(keys,2);
                    //Object[] result =trackersArray.nearest(keys,2);
                    for(Object o: result){
                        learners.add((Tracker)o);
                    }
                } catch (KeySizeException e) {
                    e.printStackTrace();
                }
                calcDisp(learners, i, j, prev,nextframe);
            }
        }




        return nextframe;
    }



}
