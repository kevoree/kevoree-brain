package org.kevoree.brain.util;

import org.kevoree.brain.util.PolynomialFit.PolynomialFitEjml;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by assaa_000 on 8/21/2014.
 */
public class PolynomialCompressor {
    private long timeOrigine;
    private int degradeFactor;
    private double toleratedError;
    private int maxDegree;

    private Prioritization prioritization=Prioritization.LOWDEGREES;

    private boolean continous=true;


    public TreeMap<Long, double[]> polynomTree = new TreeMap<Long, double[]>();

    public  ArrayList<Long> origins = new ArrayList<Long>();
    public  ArrayList<double[]> w= new ArrayList<double[]>();

    private double lastvalue=0;
    private long lastTime=0;


  //  private ArrayList<Double> timetemp;
   // private ArrayList<Double> valuetemp;


    private int counter=0;
    public int globalCounter=0;
    public int degrees=0;

    //private  ArrayList<Double> times;
   // private ArrayList<Double> values;

    private double[] weights;

    public PolynomialCompressor(long timeOrigine, int degradeFactor, double toleratedError, int maxDegree){
        this.timeOrigine=timeOrigine;
        if(degradeFactor==0)
            degradeFactor=1;
        this.degradeFactor=degradeFactor;
        this.toleratedError=toleratedError;
        this.maxDegree=maxDegree;
       // this.prioritization=prioritization;
        counter=0;
    }

    public boolean errorTest(double reconstruction, double value){
       if(Math.abs(reconstruction-value)<=toleratedError)
           return true;
        else
           return false;
    }

    public boolean errorTest(double reconstruction, double value, int degree){
      //This value affects a lot on the maximum error and average error
       // double tol = toleratedError/Math.pow(2,degree+0.5);
      //  double tol = toleratedError/Math.pow(2,maxDegree - degree);
      //  double tol = toleratedError*degree/(4*maxDegree);
        //double tol = toleratedError/(degree+1);
        //double tol = toleratedError/2;
       // double tol = toleratedError;

        double tol=toleratedError;

        if(prioritization==Prioritization.HIGHDEGREES)
            tol = toleratedError/Math.pow(2,maxDegree - degree);
        else if(prioritization==Prioritization.LOWDEGREES)
            tol = toleratedError/Math.pow(2,degree+0.5);
        else if(prioritization==Prioritization.SAMEPRIORITY)
            tol = toleratedError*degree*2/(2*maxDegree);

        if(continous==false)
            tol=tol/2;


        if(Math.abs(reconstruction-value)<=tol)
            return true;
        else
            return false;
    }

    public void feed(long time, double value){
        double t =((double)(time-timeOrigine))/degradeFactor;
        globalCounter++;

        //If this is the first point in the set, add it and return
        if(counter==0){
         /*   timetemp=new ArrayList<Double>();
            valuetemp=new ArrayList<Double>();
            timetemp.add(new Double(t));
            valuetemp.add(new Double(value));*/
            weights=new double[1];
            weights[0]=value;
            counter++;
            lastvalue=value;
            lastTime=time;
            return;
        }

      /*  if(Math.abs(t-timetemp.get(timetemp.size()-1))>1) {
            timetemp.add(t);
            valuetemp.add(value);
        }*/

        //If the current model fits well the new value, return
        if(errorTest(reconstruct(t),value,weights.length-1)) {
            counter++;
            lastvalue=value;
            lastTime=time;
            return;
        }

        //If not, first check if we can increase the degree

        if(counter<weights.length*degradeFactor*10&&weights.length<=maxDegree){
            int deg=weights.length;

            //This value affects a lot on the maximum error and average error and on the number of polynoms
           // int val =Math.min(counter,deg);
            int val =Math.min(counter,deg)*2;
            // int val =Math.min(counter,deg)*4;
           // int val =Math.min(counter,deg)+1;

            double[] times= new double[val+1];
            double[] values=new double[val+1];
            //fill values synthetically
            for(int i=0;i<val;i++){
                times[i]= i*t/(val);
                values[i]=reconstruct(times[i]);
            }
            times[val]=t;
            values[val]=value;
            PolynomialFitEjml pf=new PolynomialFitEjml(deg);
            pf.fit(times,values);
            boolean tag=true;
            for(int i=0;i<times.length;i++){
                tag = tag && errorTest(pf.calculate(times[i]),values[i],deg);
                if(tag==false)
                    break;
            }
            if(tag==true){
                weights=pf.getCoef();
                counter++;
                lastvalue=value;
                lastTime=time;
                return;
            }

        }
        //No polynomial with acceptable error was found
            counter=0;
            //Should save previous weights here



        Long tt=new Long(timeOrigine);
        degrees+=weights.length;
        polynomTree.put(tt,weights);
        origins.add(tt);
        w.add(weights);
        //added
        if(continous) {
            timeOrigine = lastTime;
            feed(lastTime, lastvalue, time, value);
        }
        else{
            timeOrigine=time;
            feed(time,value);
        }
        //added
            return;
    }

    private void feed(long l, double v, long time, double v1) {
        counter+=2;
        double[] times= new double[2];
        double[] values=new double[2];

        times[0] =((double)(l-timeOrigine))/degradeFactor;
        times[1] =((double)(time-timeOrigine))/degradeFactor;
        values[0]=v;
        values[1]=v1;
        PolynomialFitEjml pf=new PolynomialFitEjml(1);
        pf.fit(times,values);
        boolean tag=true;
        for(int i=0;i<times.length;i++){
            tag = tag && errorTest(pf.calculate(times[i]),values[i],1);
            if(tag==false)
                break;
        }
        if(tag==true){
            weights=pf.getCoef();
            counter++;
            lastvalue=v1;
            return;
        }

       // System.out.println("Error occured");

        return;
    }


    public void finalsave(){
        Long tt=new Long(timeOrigine);
        if(weights!=null) {
            degrees += weights.length;
            polynomTree.put(tt, weights);
            origins.add(tt);
            w.add(weights);
        }
    }

    public double reconstruct (double t) {
        double result=0;
        double power=1;
        for(int j=0;j<weights.length;j++){
            result+= weights[j]*power;
            power=power*t;
        }
        return result;
    }

    public static double reconstruct (long time, long timeOrigin, double[] weights, int degradeFactor) {
        double result=0;
        double t= ((double)(time-timeOrigin))/degradeFactor;
        double power=1;
        for(int j=0;j<weights.length;j++){
            result+= weights[j]*power;
            power=power*t;
        }
        return result;
    }

    public double reconstructFromSaved (long time){
        int ind=0;
        while((ind+1)<origins.size()&&time>origins.get(ind+1)){
            ind++;
        }
        double[] wt= w.get(ind);
        long timeO = origins.get(ind);
        return reconstruct(time,timeO,wt,degradeFactor);
    }

    public double fastReconstruct(long time){
        long timeO = polynomTree.floorKey(time);
        double[] wt= polynomTree.get(timeO);
        return reconstruct(time,timeO,wt,degradeFactor);
    }


    public int returnSimilar(double[] poly, int loc){
        for(int j=0; j<loc; j++){
            if(comparePolynome(poly,w.get(j),1e-5)){
                return j;
            }
        }
        return -1;
    }

    public int similar(){
        int x=0;
        for(int i=1; i<w.size();i++){
            for(int j=0; j<i; j++){
                if(comparePolynome(w.get(i),w.get(j),1e-5)){
                    x++;
                    break;
                }
            }
        }
        return x;
    }

    public static boolean comparePolynome(double[] w1, double[] w2, double err){
        if(w1.length!=w2.length){
            return false;
        }
        for(int i=0; i<w1.length;i++){
            if(Math.abs(w1[i]-w2[i])>err)
                return false;
        }
        return true;
    }



    private double reconstruct (long time) {
        double result=0;
        double t= ((double)(time-timeOrigine))/degradeFactor;
        double power=1;
        for(int j=0;j<weights.length;j++){
            result+= weights[j]*power;
            power=power*t;
        }
        return result;
    }

    public Prioritization getPrioritization() {
        return prioritization;
    }

    public void setPrioritization(Prioritization prioritization) {
        this.prioritization = prioritization;
    }

    public boolean isContinous() {
        return continous;
    }

    public void setContinous(boolean continous) {
        this.continous = continous;
    }
}
