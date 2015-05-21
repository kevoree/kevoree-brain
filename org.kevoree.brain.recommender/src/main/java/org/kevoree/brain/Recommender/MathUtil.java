package org.kevoree.brain.Recommender;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Java implementation of Edwin B. Wilson ranking algorithm
 * Created by assaa_000 on 03/05/2015.
 */
public class MathUtil {


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


    public static void calcHistogramArray(double[] data,double[] dataRandom, double[] dataratings, int numBins, String name) {
        final int[] result = new int[numBins+1];
        final int[] resultRandom = new int[numBins+1];
        final int[] rating = new int[numBins+1];

        double max=5;
        double min=-5;


        final double binSize = (max - min)/numBins;

        for (double d : data) {
            int bin = (int) ((d - min) / binSize);
            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin > numBins) { /* this data point is bigger than max */ }
            else {
                result[bin] += 1;
            }
        }

        for (double d : dataRandom) {
            int bin = (int) ((d - min) / binSize);
            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin > numBins) { /* this data point is bigger than max */ }
            else {
                resultRandom[bin] += 1;
            }
        }

        for (double d : dataratings) {
            int bin = (int) ((d - min) / binSize);
            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin > numBins) { /* this data point is bigger than max */ }
            else {
                rating[bin] += 1;
            }
        }

        try {
            PrintStream out = new PrintStream(new FileOutputStream(name));
            for (int i = 0; i <= numBins; i++) {
                out.println(min+i*binSize+" , "+result[i]+" , "+resultRandom[i]+" , "+rating[i]);
            }
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static double InverseNormalCDF(double q) {
        double b[] = {
                1.570796288,
                0.03706987906,
                -0.8364353589e-3,
                -0.2250947176e-3,
                0.6841218299e-5,
                0.5824238515e-5,
                -0.104527497e-5,
                0.8360937017e-7,
                -0.3231081277e-8,
                0.3657763036e-10,
                0.6936233982e-12
        };

        if (q < 0.0 || 1.0 < q || q == 0.5) {
            return 0.0;
        }

        double w1 = q;
        if (q > 0.5) {
            w1 = 1.0 - q;
        }

        double w3 = -Math.log(4.0 * w1 * (1.0 - w1));
        w1 = b[0];
        for (int i = 1; i < 11; i++) {
            w1 += b[i] * Math.pow(w3, i);
        }

        return q > 0.5 ? Math.sqrt(w1 * w3) : -Math.sqrt(w1 * w3);
    }


    public static double wilsonApache(int positive, int negative, double confidenceLevel) {
        int numberOfTrials = positive + negative;
        double alpha = (1.0 - confidenceLevel) / 2;
        NormalDistribution normalDistribution = new NormalDistribution();
        double z = normalDistribution.inverseCumulativeProbability(1 - alpha);
        double zSquared = FastMath.pow(z, 2);
        double mean = (double) positive / (double) numberOfTrials;
        double factor = 1.0 / (1 + (1.0 / numberOfTrials) * zSquared);
        double modifiedSuccessRatio = mean + (1.0 / (2 * numberOfTrials)) * zSquared;
        final double difference = z * FastMath.sqrt(1.0 / numberOfTrials * mean * (1 - mean) + (1.0 / (4 * FastMath.pow(numberOfTrials, 2)) * zSquared));
        double lowerBound = factor * (modifiedSuccessRatio - difference);
        double upperBound = factor * (modifiedSuccessRatio + difference);
        System.out.println("lower: "+lowerBound+" upper: "+upperBound+" value: "+(lowerBound+upperBound)/2);
        return (lowerBound+upperBound)/2;

    }

    public static double wilsonRank(int positive, int negative, double confidence) {
        int n = positive + negative;
        if (n == 0) {
            return 0.0;
        }

        double z = InverseNormalCDF(1.0 - confidence / 2.0);
        System.out.println(z);
        double p_hat = (1.0 * positive) / n;
        return (p_hat + z * z / (2.0 * n) -
                z * Math.sqrt((p_hat * (1.0 - p_hat) + z * z / (4.0 * n)) / n)) /
                (1.0 + z * z / n);
    }

    /*
    public static void main(String[] args){
        double x=0.95;
        double y=1-x/2;
        System.out.println(Util.InverseNormalCDF(y));
        NormalDistribution nd = new NormalDistribution(0,1);
        System.out.println(nd.inverseCumulativeProbability(x));
        System.out.println("wilson " + Util.wilsonRank(900,100,0.95));
        Util.wilsonApache(900, 100,0.95);
    }*/
}
