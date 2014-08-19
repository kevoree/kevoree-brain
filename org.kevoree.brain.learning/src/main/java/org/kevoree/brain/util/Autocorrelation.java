package org.kevoree.brain.util;

/**
 * Created by assaa_000 on 8/19/2014.
 */
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import java.util.Arrays;

public class Autocorrelation {

    void print(String msg, double [] x) {
        System.out.println(msg);
        for (double d : x) System.out.println(d);
    }

    /**
     * This is a "wrapped" signal processing-style autocorrelation.
     * For "true" autocorrelation, the data must be zero padded.
     */
    public void bruteForceAutoCorrelation(double [] x, double [] ac) {
        Arrays.fill(ac, 0);
        int n = x.length;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                ac[j] += x[i] * x[(n + i - j) % n];
            }
        }
    }

    private double sqr(double x) {
        return x * x;
    }

    public void fftAutoCorrelation(double [] x, double [] ac) {
        int n = x.length;
        // Assumes n is even.
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(x);
        ac[0] = sqr(x[0]);
        // ac[0] = 0;  // For statistical convention, zero out the mean
        ac[1] = sqr(x[1]);
        for (int i = 2; i < n; i += 2) {
            ac[i] = sqr(x[i]) + sqr(x[i+1]);
            ac[i+1] = 0;
        }
        DoubleFFT_1D ifft = new DoubleFFT_1D(n);
        ifft.realInverse(ac, true);
        // For statistical convention, normalize by dividing through with variance
        //for (int i = 1; i < n; i++)
        //    ac[i] /= ac[0];
        //ac[0] = 1;
    }

    void test() {
        //double [] data = { 1, -81, 2, -15, 8, 2, -9, 0};
        double [] data = { 1, -5, 7, 0.1,-0.5,0.7};
        double [] ac1 = new double [data.length];
        double [] ac2 = new double [data.length];
        bruteForceAutoCorrelation(data, ac1);
        fftAutoCorrelation(data, ac2);
        print("bf", ac1);
        print("fft", ac2);
        double err = 0;
        for (int i = 0; i < ac1.length; i++)
            err += sqr(ac1[i] - ac2[i]);
        System.out.println("err = " + err);
    }

    public static void main(String[] args) {
        new Autocorrelation().test();
    }
}