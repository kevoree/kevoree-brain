package org.kevoree.brain;

import org.kevoree.brain.util.PolynomialFit.PolynomialFitEjml;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/21/2014.
 */
public class PolynomialCompressor {
    private long timeOrigine;
    private int degradeFactor;
    private double toleratedError;
    private int maxDegree;

    public static ArrayList<Long> origins = new ArrayList<Long>();
    public static ArrayList<double[]> w= new ArrayList<double[]>();


  //  private ArrayList<Double> timetemp;
   // private ArrayList<Double> valuetemp;


    private int counter=0;

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
        double tol = toleratedError/Math.pow(2,degree+1);
        //double tol = toleratedError/(degree+1);
        //double tol = toleratedError/2;
       // double tol = toleratedError;
        if(Math.abs(reconstruction-value)<=tol)
            return true;
        else
            return false;
    }

    public void feed(long time, double value){
        double t =((double)(time-timeOrigine))/degradeFactor;

        //If this is the first point in the set, add it and return
        if(counter==0){
         /*   timetemp=new ArrayList<Double>();
            valuetemp=new ArrayList<Double>();
            timetemp.add(new Double(t));
            valuetemp.add(new Double(value));*/
            weights=new double[1];
            weights[0]=value;
            counter++;
            return;
        }

      /*  if(Math.abs(t-timetemp.get(timetemp.size()-1))>1) {
            timetemp.add(t);
            valuetemp.add(value);
        }*/

        //If the current model fits well the new value, return
        if(errorTest(reconstruct(t),value,weights.length-1)) {
            counter++;
            return;
        }

        //If not, first check if we can increase the degree
        if(weights.length<=maxDegree){
            int deg=weights.length;

            //This value affects a lot on the maximum error and average error and on the number of polynoms
            int val =Math.min(counter,deg);
            //int val =Math.min(counter,deg)*2;
            //int val =Math.min(counter,deg)*4;
            //int val =Math.min(counter,deg)+1;

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
                return;
            }

        }
        //No polynomial with acceptable error was found
            counter=0;
            //Should save previous weights here
            origins.add(new Long(timeOrigine));
            w.add(weights);
            timeOrigine=time;
            feed(time,value);
            return;
    }


    public void finalsave(){
        origins.add(new Long(timeOrigine));
        w.add(weights);

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




    public double reconstruct (long time) {
        double result=0;
        double t= ((double)(time-timeOrigine))/degradeFactor;
        double power=1;
        for(int j=0;j<weights.length;j++){
            result+= weights[j]*power;
            power=power*t;
        }
        return result;
    }

}
