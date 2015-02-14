package org.kevoree.brain.imageprocess;

/**
 * Created by assaa_000 on 14/02/2015.
 */
public class ImageLearnerState {
    private int nx;
    private int ny;
    private double res;


    public void AddResult(int x, int y, double res){
        //Later create averaging here
        this.nx=x;
        this.ny=y;
        this.res=res;
    }


    public int getNx() {
        return nx;
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public int getNy() {
        return ny;
    }

    public void setNy(int ny) {
        this.ny = ny;
    }

    public double getRes() {
        return res;
    }

    public void setRes(double res) {
        this.res = res;
    }

    public void print(){
        System.out.println("x displacement: " + nx);
        System.out.println("y displacement: " + ny);
        System.out.println("Result "+res);
    }
}
