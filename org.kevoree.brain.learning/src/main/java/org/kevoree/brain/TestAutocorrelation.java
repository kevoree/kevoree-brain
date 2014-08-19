package org.kevoree.brain;

import org.kevoree.brain.util.Autocorrelation;

/**
 * Created by assaa_000 on 8/19/2014.
 */
public class TestAutocorrelation {
    public static void main(String[] args) {
        //double [] data = { 1, -81, 2, -15, 8, 2, -9, 0};
        double [] data = { 1, -5, 7, 0.1,-0.5,0.7};
        double [] ac1 = new double [data.length];
        double [] ac2 = new double [data.length];
        Autocorrelation ac = new Autocorrelation();
        ac.bruteForceAutoCorrelation(data, ac1);
        ac.fftAutoCorrelation(data, ac2);
        ac.print("bf", ac1);
        ac.print("fft", ac2);
        double err = 0;
        for (int i = 0; i < ac1.length; i++)
            err += (ac1[i] - ac2[i])*(ac1[i] - ac2[i]);
        System.out.println("err = " + err);
    }
}
