package org.kevoree.brain.smartgrid.Profilers;

import org.kevoree.brain.smartgrid.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 19/02/15.
 */
public abstract class Profiler {
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    public String userId;

    protected static double maxmultiplier=1.0;
   // protected static int maxSteps=100;

    protected static int timeStep=12;

    protected double[] apmax=new double[timeStep];
    protected double[] ammax=new double[timeStep];
    protected double[] rpmax=new double[timeStep];
    protected double[] rmmax=new double[timeStep];

    protected double[] apmin=new double[timeStep];
    protected double[] ammin=new double[timeStep];
    protected double[] rpmin=new double[timeStep];
    protected double[] rmmin=new double[timeStep];

    public abstract void feedMeasure(ElectricMeasure sample);
    public abstract void feedArray(ArrayList<ElectricMeasure> samples);


    public String getVector(){
        StringBuilder sb= new StringBuilder(userId+" ");
        for(int i=0;i<timeStep;i++){
            sb.append(apmin[i]+" ");
        }
        for(int i=0;i<timeStep;i++){
            sb.append(apmax[i]+" ");
        }
        for(int i=0;i<timeStep;i++){
            sb.append(ammin[i]+" ");
        }
        for(int i=0;i<timeStep;i++){
            sb.append(ammax[i]+" ");
        }
        for(int i=0;i<timeStep;i++){
            sb.append(rpmin[i]+" ");
        }
        for(int i=0;i<timeStep;i++){
            sb.append(rpmax[i]+" ");
        }
        for(int i=0;i<timeStep;i++){
            sb.append(rmmin[i]+" ");
        }
        for(int i=0;i<timeStep;i++){
            sb.append(rmmax[i]+" ");
        }
        return sb.toString();
    }

    protected boolean checkSample(ElectricMeasure em){
        int time=em.getIntTime(timeStep);
       if(em.aplus>apmax[time]*maxmultiplier||em.aminus>ammax[time]*maxmultiplier||em.rplus>rpmax[time]*maxmultiplier||em.rminus>rmmax[time]*maxmultiplier||em.aplus<apmin[time]/maxmultiplier||em.aminus<ammin[time]/maxmultiplier||em.rplus<rpmin[time]/maxmultiplier||em.rminus<rmmin[time]/maxmultiplier){
       //  if(em.aplus>apmax[time]*maxmultiplier||em.aminus>ammax[time]*maxmultiplier||em.rplus>rpmax[time]*maxmultiplier||em.rminus>rmmax[time]*maxmultiplier){

                return false;
        }
        return true;
    }


    private void update(ElectricMeasure em){
        int time=em.getIntTime(timeStep);
        if(em.aplus>apmax[time]){
            apmax[time]=em.aplus;
        }
        if(em.aminus>ammax[time]){
            ammax[time]=em.aminus;
        }
        if(em.rplus>rpmax[time]){
            rpmax[time]=em.rplus;
        }
        if(em.rminus>rmmax[time]){
            rmmax[time]=em.rminus;
        }

        if(em.aplus<apmin[time]){
            apmin[time]=em.aplus;
        }
        if(em.aminus<ammin[time]){
            ammin[time]=em.aminus;
        }
        if(em.rplus<rpmin[time]){
            rpmin[time]=em.rplus;
        }
        if(em.rminus<rmmin[time]){
            rmmin[time]=em.rminus;
        }

    }

    public void feed(ArrayList<ElectricMeasure> samples, int numb){
        for(int i=0;i<timeStep;i++){
            apmin[i]=1e9;
            ammin[i]=1e9;
            rpmin[i]=1e9;
            rmmin[i]=1e9;
        }
        ArrayList<ElectricMeasure> init=new ArrayList<ElectricMeasure>();
        for(int i=0;i<numb;i++){
            update(samples.get(i));
            init.add(samples.get(i));
        }
        feedArray(init);

        for(int i=numb;i<samples.size();i++){
            update(samples.get(i));
            feedMeasure(samples.get(i));
        }

       /* for(int i=0;i<96;i++){
            System.out.println(apmax[i]+","+ammax[i]+","+rpmax[i]+","+rmmax[i]);
        }*/
    }



    private double sim(double[] a, double[] b){
        double res=0;
        for(int i=0;i<a.length;i++){
            res=res+(a[i]-b[i])*(a[i]-b[i]);
        }
        return res;
    }




    public double similarities(Profiler x){
        double val=0;
        val+=sim(apmax,x.apmax);
        val+=sim(apmin,x.apmin);
        val+=sim(ammax,x.ammax);
        val+=sim(ammin,x.ammin);
        val+=sim(rpmax,x.rpmax);
        val+=sim(rpmin,x.rpmin);
        val+=sim(rmmax,x.rmmax);
        val+=sim(rmmin,x.rmmin);
        return Math.sqrt(val/(8*rmmin.length));
    }


    protected double getPotential(int time){
        double d=(apmax[time]+ammax[time]+rpmax[time]+rmmax[time]-apmin[time]-ammin[time]-rmmin[time]-rpmin[time]);
        double max=0;
        for(int i=0;i<timeStep;i++){
            if(apmax[i]>max){
                max=apmax[i];
            }
            if(ammax[i]>max){
                max=ammax[i];
            }
            if(rpmax[i]>max){
                max=rpmax[i];
            }
            if(rmmax[i]>max){
                max=rmmax[i];
            }

        }
        // double dd= max/(max+0.00001*d);
        double dd= max/(max+0.2*d);
        return dd;


    }

    public abstract double getProba(ElectricMeasure sample);

    //TODO can be accelerated if sorted by time or energy - to re-implement later
    public double getProbaForArray(ArrayList<ElectricMeasure>  samples){
        double res=0;
        double temp;
        for(ElectricMeasure em: samples){
            temp=getProba(em);
            res=res+temp;

        }
        return res;
    }
    public abstract double[][] getProbabilities(double[] x, double[] y);


}
