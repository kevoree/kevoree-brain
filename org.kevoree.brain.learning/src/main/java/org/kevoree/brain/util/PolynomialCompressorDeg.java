package org.kevoree.brain.util;

import org.kevoree.brain.util.PolynomialFit.PolynomialFitEjml;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/21/2014.
 */
public class PolynomialCompressorDeg {
    private long timeOrigine;
    private int degradeFactor;
    private double toleratedError;
    private int maxDegree;

    public static ArrayList<Long> origins = new ArrayList<Long>();
    public static ArrayList<double[]> w= new ArrayList<double[]>();


    private ArrayList<Double> timetemp=new ArrayList<Double>();
   // private ArrayList<Double> valuetemp;


    private int counter=0;

    //private  ArrayList<Double> times;
   // private ArrayList<Double> values;

    private double[] weights;

    public PolynomialCompressorDeg(long timeOrigine, int degradeFactor, double toleratedError, int maxDegree){
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
       // double tol = toleratedError/Math.pow(2,degree+1);
        double tol = toleratedError/Math.pow(2,maxDegree - degree);
       // double tol = toleratedError*degree/(4*maxDegree);
        //double tol = toleratedError/(degree+1);
        //double tol = toleratedError/2;
       // double tol = toleratedError;
        if(Math.abs(reconstruction-value)<=tol)
            return true;
        else
            return false;
    }

    public void feed(long time, double value) {
        double t = ((double) (time - timeOrigine)) / degradeFactor;
        timetemp.add(new Double(t));

        //If this is the first point in the set, add it and return
        if (counter == 0) {
         /*   timetemp=new ArrayList<Double>();
            valuetemp=new ArrayList<Double>();
            timetemp.add(new Double(t));
            valuetemp.add(new Double(value));*/
            weights = new double[1];
            weights[0] = value;
            counter++;
            return;
        }

      /*  if(Math.abs(t-timetemp.get(timetemp.size()-1))>1) {
            timetemp.add(t);
            valuetemp.add(value);
        }*/


        int deg = Math.min(maxDegree, timetemp.size() - 1);


        //This value affects a lot on the maximum error and average error and on the number of polynoms
        // int val =Math.min(counter,deg);
        int val = Math.min(timetemp.size()-1, 2 * maxDegree);
        // int val =Math.min(counter,deg)*4;
        // int val =Math.min(counter,deg)+1;

        double[] times = new double[val + 1];
        double[] values = new double[val + 1];
        //fill values synthetically
        for (int i = 0; i < val; i++) {
            times[i] = timetemp.get(i * timetemp.size() / (val));
            values[i] = reconstruct(times[i]);
        }
        times[val] = t;
        values[val] = value;
        try {
            PolynomialFitEjml pf = new PolynomialFitEjml(deg);
            pf.fit(times, values);
            boolean tag = true;
            for (int i = 0; i < times.length; i++) {
                tag = tag && errorTest(pf.calculate(times[i]), values[i], deg);
                if (tag == false)
                    break;
            }
            if (tag == true) {
                weights = pf.getCoef();
                counter++;
                return;
            }
        }
        catch (Exception ex) {
        }
        //No polynomial with acceptable error was found
        counter=0;
        //Should save previous weights here
        origins.add(new Long(timeOrigine));
        w.add(weights);
        timeOrigine=time;
        feed(time,value);
        timetemp.clear();
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
