package org.kevoree.brain.test;

import org.kevoree.brain.learning.livelearning.LinearRegressionLive;
import org.kevoree.brain.util.PolynomialFit.PolynomialFitEjml;
import org.kevoree.brain.util.adapters.PolynomialAdapter;

import java.util.Random;

/**
 * Created by assaa_000 on 8/19/2014.
 */
public class TestPolynomialAdapter {

    private static double[] initialweights={-0.3,4,7,2,5,7};

    public static void main(String [] arg){

        int maxiter=100000;
        double maxDouble=5;

        PolynomialAdapter pa= new PolynomialAdapter();
        pa.setDegree(initialweights.length-1);
        LinearRegressionLive lr= new LinearRegressionLive(initialweights.length-1);
        pa.setLiveLearning(lr);
        Random rand = new Random();

        for(int i=0;i<maxiter;i++){
            double t[] = new double [2];
            t[0]=rand.nextDouble()*maxDouble;
            t[1]=polynomial(t[0]);
            pa.feed(t);
        }

        System.out.println("Original weights");
        for(int i=0;i<initialweights.length;i++){
            System.out.println(initialweights[i]);
        }

        System.out.println("Learned weights");
        lr.print();
        System.out.println("last error %"+lr.getLasterror());


    }

    public static double polynomial(double x){
        double result= initialweights[initialweights.length-1];
        double temp=x;
        for(int i=initialweights.length-2;i>=0;i--){
            result+= initialweights[i]*temp;
            temp=temp*x;
        }
        return result;
    }
}
