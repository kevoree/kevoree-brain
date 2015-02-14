package org.kevoree.brain.imageprocess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class ImageLearnerArray {
    int nx;
    int ny;
    int w;
    int h;

    public static int imgwid;
    public static int imghei;

    public  int stepx;
    public  int stepy;
    int initx=13;
    int inity=13;
    int sq = 10;

    ImageLearner[][] array;

    public ImageLearnerArray(int nx, int ny, int w, int h, BufferedImage img){
        this.nx=nx;
        this.ny=ny;
        this.w=w;
        this.h=h;
        this.imgwid=img.getWidth();
        this.imghei=img.getHeight();

        stepx=(imgwid-initx)/nx;
        stepy=(imghei-inity)/ny;

        array=new ImageLearner[nx][ny];
        for(int i=0;i<nx;i++){
            for(int j=0; j<ny;j++){
                array[i][j]=new ImageLearner(initx+i*stepx,inity+j*stepy,w,h,imgwid,imghei);
            }
        }
    }

    public void train(BufferedImage prev, BufferedImage cur){
        for(int i=0;i<nx;i++) {
            for (int j = 0; j < ny; j++) {
                array[i][j].train(prev,cur);
            }
            System.out.println("Training "+String.format("%3.2f",((((double) (i+1)))*100/nx))+ "% completed");
        }
    }


    private void setPixel(int i, int j, int dispx, int dispy, BufferedImage prev, BufferedImage next) {



        for (int k = -sq; k <= sq; k++) {
            for (int l = -sq; l <= sq; l++) {
                try{
                    next.setRGB(i-dispx+k,j-dispy+l,prev.getRGB(i+k,j+l));
                }
                catch (Exception e){}
            }
        }
    }



    private void calcDisp(ArrayList<ImageLearner> learners, int i, int j, BufferedImage prev, BufferedImage next){

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
            double[] dist=new double[learners.size()];
            double[] weights=new double[learners.size()];
            double total=0;
            for(int l=0;l<dist.length;l++){
                dist[l]=Math.sqrt((i-learners.get(l).x)*(i-learners.get(l).x)+(j-learners.get(l).y)*(j-learners.get(l).y));
                for(int k=0;k<dist.length;k++){
                    if(k==l){
                        continue;
                    }
                    weights[k]+=dist[l];
                    total+=dist[l];
                }
            }
            for(int l=0;l<dist.length;l++){
                dispx+=learners.get(l).getState().getNx()*weights[l]/total;
                dispy+=learners.get(l).getState().getNy()*weights[l]/total;
            }
        }
        setPixel(i,j,(int)dispx,(int)dispy,prev,next);
    }


    public void createImage4(BufferedImage prev){

        BufferedImage nextframe = new BufferedImage(imgwid,imghei,BufferedImage.TYPE_INT_RGB);

        for(int i=0;i<prev.getWidth();i++){
            for(int j=0;j<prev.getHeight();j++) {
                ArrayList<ImageLearner> learners = new ArrayList<ImageLearner>();
                int lx=(i-initx)/stepx;
                int ly=(j-inity)/stepy;
                if(lx>=0&&ly>=0&&lx<nx&&ly<ny){
                    learners.add(array[lx][ly]);
                }
                lx=(i-initx+stepx)/stepx;
                ly=(j-inity)/stepy;
                if(lx>=0&&ly>=0&&lx<nx&&ly<ny){
                    learners.add(array[lx][ly]);
                }
                lx=(i-initx)/stepx;
                ly=(j-inity+stepy)/stepy;
                if(lx>=0&&ly>=0&&lx<nx&&ly<ny){
                    learners.add(array[lx][ly]);
                }
                lx=(i-initx+stepx)/stepx;
                ly=(j-inity+stepy)/stepy;
                if(lx>=0&&ly>=0&&lx<nx&&ly<ny){
                    learners.add(array[lx][ly]);
                }

                calcDisp(learners, i, j, prev,nextframe);
            }
        }
        try {
            File outputfile = new File("D:\\result\\savednew4.jpg");
            ImageIO.write(nextframe, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void print(){
        System.out.println("Matrix of x displacements: ");
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                System.out.print(String.format("%3d",array[i][j].getState().getNx())+" ");
            }
            System.out.println();
        }
        System.out.println("Matrix of y displacements: ");
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                System.out.print(String.format("%3d",array[i][j].getState().getNy())+" ");
            }
            System.out.println();
        }

        System.out.println("Matrix of err: ");
        double avg=0;
        int count=0;
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                if(array[i][j].getState().getRes()!=-1) {
                    avg += array[i][j].getState().getRes();
                    count++;
                }
                System.out.print(String.format("%4.0f", array[i][j].getState().getRes()) + " ");
            }

            System.out.println();
        }
        avg=avg/(count);
        System.out.println("Average error: "+avg);
    }

}
