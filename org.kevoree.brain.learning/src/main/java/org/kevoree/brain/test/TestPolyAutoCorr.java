package org.kevoree.brain.test;

import org.kevoree.brain.learning.periodicitydetection.Autocorrelation;
import org.kevoree.brain.util.PolynomialFit.PolynomialFitEjml;

/**
 * Created by assaa_000 on 8/19/2014.
 */
public class TestPolyAutoCorr {
    public static void main(String[] args) {
        //Period is 5.3
       double [] sindata = {0.926689607, 0.696551029, -0.403123429, -0.999560837, -0.348201635,0.737833279,0.90279783, -0.059240628, -0.947326354, -0.652822118,0.456629237,0.996049843,0.292056771, -0.776523863, -0.875734942,0.118273171,0.964635582,0.606800146, -0.508531119, -0.989040187    };
        double[] t={1, 2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};

        PolynomialFitEjml pf= new PolynomialFitEjml(t, sindata);

        //System.out.println("Degree "+ pf.getDegree());
        System.out.println("Model error EJML: " + pf.getError(t, sindata));

        double[] sindataEx = new double[191];
        double[] sindataExWeka = new double[191];
        double[] tEx= new double[191];



        for(int i=10 ; i<=200; i++){
            tEx[i-10]=((double) i)/10;
            sindataEx[i-10]=pf.calculate(tEx[i-10]);
            //System.out.println(sindataExWeka[i-10]);
        }

     /*   System.out.println("sindataEx");
        for (double d : sindataEx) System.out.println(d);

        System.out.println("time");
        for (double d : tEx) System.out.println(d);*/

        Autocorrelation ac= new Autocorrelation();
        double[] acresult= new double[191];

        ac.bruteForceAutoCorrelation(sindataEx,acresult);
     /*  System.out.println("Autocorrelation");
        for (double d : acresult) System.out.println(d);*/

        int period1= ac.detectPeriod(acresult);
        System.out.println("Period EJML is: "+ tEx[period1]);

        ac= new Autocorrelation();
        acresult= new double[191];
        ac.bruteForceAutoCorrelation(sindataExWeka,acresult);
        period1= ac.detectPeriod(acresult);
        System.out.println("Period Weka is: "+ tEx[period1]);



    }
}
