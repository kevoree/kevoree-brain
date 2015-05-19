package org.kevoree.brain.util;

import org.ejml.simple.SimpleMatrix;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Created by assaad on 19/05/15.
 */
public class Histogram {

    public static int[] calcHistogram(double[] data, double min, double max, int numBins) {
        final int[] result = new int[numBins];
        final double binSize = (max - min)/numBins;

        for (double d : data) {
            int bin = (int) ((d - min) / binSize);
            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin >= numBins) { /* this data point is bigger than max */ }
            else {
                result[bin] += 1;
            }
        }
        return result;
    }


    public static void calcHistogramArray(ArrayList<Double> data, int numBins) {
        final int[] result = new int[numBins];

        double max=data.get(0);
        double min=data.get(0);

        for (double d : data) {
            if(d>max){
                max=d;
            }
            if(d<min){
                min=d;
            }
        }

        final double binSize = (max - min)/numBins;

        for (double d : data) {
            int bin = (int) ((d - min) / binSize);
            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin >= numBins) { /* this data point is bigger than max */ }
            else {
                result[bin] += 1;
            }
        }


        try {
            PrintStream out = new PrintStream(new FileOutputStream("histogram.csv"));
            for (int i = 0; i < numBins; i++) {
                out.println(min+i*binSize+" , "+result[i]);
            }
            out.close();
            }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
