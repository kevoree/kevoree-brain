package org.kevoree.brain.learning.livelearning.Recommender;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

/**
 * Java implementation of Edwin B. Wilson ranking algorithm
 * Created by assaa_000 on 03/05/2015.
 */
public class Util {

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
}
