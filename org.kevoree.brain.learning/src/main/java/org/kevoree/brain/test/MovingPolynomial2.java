package org.kevoree.brain.test;

import org.kevoree.brain.util.PolynomialFit.PolynomialFitEjml;

import java.io.*;
import java.util.ArrayList;


/**
 * Created by assaa_000 on 8/20/2014.
 */
public class MovingPolynomial2 {
    public static void main(String[] arg) {
        int degradeFactor=1000;
        int polyDegree=1;
        int initialLearn =21;


        String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\arduino.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Double> values=new ArrayList<Double>();
        ArrayList<double[]> polynomial = new ArrayList<double[]>();

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] str = line.split(cvsSplitBy);
                Double px = Double.parseDouble(str[0]);
                values.add(px);
            }
            System.out.println("Loaded "+values.size()+" values");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        ArrayList<Double> degraded = new ArrayList<Double>(values.size()/degradeFactor);

        for(int i=0; i<values.size(); i+=degradeFactor){
            degraded.add(values.get(i));
        }

        double[] t= new double[initialLearn];
        double[] val = new double[initialLearn];
        for(int i=0; i<initialLearn;i++){
            t[i]=i;
            val[i]=degraded.get(i);
        }

        PolynomialFitEjml pf;

        double err=0;

        for(int j=0; j<initialLearn;j++) {
            pf = new PolynomialFitEjml(polyDegree);
            pf.fit(t,val);
            polynomial.add(pf.getCoef());
            err+=pf.getError(t,val);
            //System.out.println("Error: "+ pf.getError(t,val));

            for(int i=0;i< initialLearn; i++)
                t[i]--;
        }


       for(int j=initialLearn; j <degraded.size();j++){
           t=new double[j+1];
           val=new double[j+1];
           for(int i=0; i<=j;i++){
               t[i]=i-j;
               val[i]=degraded.get(i);
           }
           pf = new PolynomialFitEjml(polyDegree);
           pf.fit(t,val);
          // System.out.println("Best degree "+pf.getDegree());
           err=pf.getError(t,val);
           polynomial.add(pf.getCoef());
        }

        System.out.println("Final Error Creation: "+err);

        double reconstErr=0;

        int counter =0;

        FileWriter outFile;
        try {
            outFile = new FileWriter("result.txt");
            PrintWriter out = new PrintWriter(outFile);

        for(int i=0; i<values.size(); i++){
            int origin= (i/degradeFactor)+1;
            if(origin>=polynomial.size())
                break;
               // origin--;
            double[] weights= polynomial.get(origin);
            double h = reconstruct(i,origin,degradeFactor,weights);
            double y=values.get(i);
            out.println(i+" "+ y+" "+h);
            double er = Math.sqrt( (h-y)*(h-y));
            reconstErr+=er;
            counter++;
        }
        reconstErr=reconstErr/counter;
        System.out.println("Reconstruction Error: "+reconstErr);
            out.close();
        } catch (IOException ex) {
        }

    }

    private static double reconstruct(int i, int origin, int degradeFactor, double[] weights) {
        double result=0;
        double t= ((double)(i-origin*degradeFactor))/degradeFactor;
        double power=1;
        for(int j=0;j<weights.length;j++){
            result+= weights[j]*power;
            power=power*t;
        }
        return result;
    }
}
