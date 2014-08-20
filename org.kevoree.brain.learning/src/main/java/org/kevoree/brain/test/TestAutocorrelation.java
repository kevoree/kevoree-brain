package org.kevoree.brain.test;

import org.kevoree.brain.util.Autocorrelation;

/**
 * Created by assaa_000 on 8/19/2014.
 */
public class TestAutocorrelation {
    public static void main(String[] args) {
        //double [] data = { 1, -81, 2, -15, 8, 2, -9, 0};
        double [] data = { 1, -5, 7, 0.1,-0.5,0.7};
       // double [] data = {75.75, -38.99999999999999,-0.93,15.0,-0.9300000000000002,-38.99999999999999};

        double [] sindata = {0.926689607, 0.696551029, -0.403123429, -0.999560837, -0.348201635,0.737833279,0.90279783, -0.059240628, -0.947326354, -0.652822118,0.456629237,0.996049843,0.292056771, -0.776523863, -0.875734942,0.118273171,0.964635582,0.606800146, -0.508531119, -0.989040187    };

        /*double [] ac1 = new double [sindata.length];
        double [] ac2 = new double [sindata.length];
        Autocorrelation ac = new Autocorrelation();
        ac.bruteForceAutoCorrelation(sindata, ac1);
        ac.fftAutoCorrelation(sindata, ac2);
        ac.print("bf", ac1);
        ac.print("fft", ac2);
        double err = 0;
        for (int i = 0; i < ac1.length; i++)
            err += (ac1[i] - ac2[i])*(ac1[i] - ac2[i]);
        System.out.println("err = " + err);*/

        double [] acn = new double [sindata.length];
        double [] acn1 = new double [sindata.length];

        acn=sindata;
        for(int i=0 ;i< 10; i++){
            Autocorrelation ac2 = new Autocorrelation();
            ac2.bruteForceAutoCorrelation(acn, acn1);
            ac2.normalize(acn1);
            acn=acn1;
            ac2.print("Round "+i, acn1);
            System.out.println("Period "+ ac2.detectPeriod(acn1));
            acn1 = new double [sindata.length];
            System.out.println();
        }



    }
}
