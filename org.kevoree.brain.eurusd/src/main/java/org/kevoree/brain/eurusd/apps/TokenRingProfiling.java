package org.kevoree.brain.eurusd.apps;

import de.tuhh.luethke.okde.utility.Matrices.MatrixOps;
import org.kevoree.brain.eurusd.learners.Profiler;
import org.kevoree.brain.eurusd.learners.TokenRingProfile;
import org.kevoree.brain.eurusd.tools.Loader;
import org.kevoree.brain.learning.livelearning.LinearRegressionLive;

import java.text.DecimalFormat;

/**
 * Created by assaa_000 on 25/09/2015.
 */
public class TokenRingProfiling {
    public static void main(String[] arg) {
        String csvFile = "/Users/assaad/work/github/eurusd/newEurUsd.csv";
        csvFile = "C:\\Users\\assaa_000\\Documents\\GitHub\\eurusd\\newEurUsd.csv";


        double[] features = new double[13];
        LinearRegressionLive linearRegressionLive = new LinearRegressionLive(features.length);
        linearRegressionLive.setAlpha(0.00001);
        linearRegressionLive.setIteration(10);
        int delay = 60 * 24;
        double train = 0.9;
        double[] euroVals = Loader.loadContinuous(60000, csvFile);

        for (int j = 0; j < 10000; j++) {
            TokenRingProfile hourly = new TokenRingProfile(60);
            TokenRingProfile daily = new TokenRingProfile(60 * 24);
            TokenRingProfile weekly = new TokenRingProfile(60 * 24 * 7);
            TokenRingProfile monthly = new TokenRingProfile(60 * 24 * 30);
            TokenRingProfile yearly = new TokenRingProfile(60 * 24 * 365);
            Profiler eurUsdProfile = new Profiler();

            for (int i = 0; i < (euroVals.length) * train; i++) {
                eurUsdProfile.feed(euroVals[i]);
                hourly.feed(euroVals[i]);
                daily.feed(euroVals[i]);
                weekly.feed(euroVals[i]);
                monthly.feed(euroVals[i]);
                yearly.feed(euroVals[i]);

                double norm = eurUsdProfile.getAvg();
                features[0] = euroVals[i] - norm;
                features[1] = features[0] * features[0];
                features[2] = hourly.getAvg() - norm;
                features[3] = features[2] * features[2];
                features[4] = daily.getAvg() - norm;
                features[5] = features[4] * features[2];
                features[6] = weekly.getAvg() - norm;
                features[7] = features[6] * features[2];
                features[8] = monthly.getAvg() - norm;
                features[9] = features[8] * features[2];
                features[10] = yearly.getAvg() - norm;
                features[11] = features[10] * features[2];
                features[12] = eurUsdProfile.getAvg();

                linearRegressionLive.feed(features, euroVals[i + delay]);
            }
            if (j % 50 == 0) {

                System.out.print("Round " + j + ": ");
                linearRegressionLive.print(new DecimalFormat("#.####"));

                //calculate error:
                double err;
                double sumErr = 0;
                int count = 0;
                for (int i = 0; i < (euroVals.length) * train; i++) {
                    eurUsdProfile.feed(euroVals[i]);
                    hourly.feed(euroVals[i]);
                    daily.feed(euroVals[i]);
                    weekly.feed(euroVals[i]);
                    monthly.feed(euroVals[i]);
                    yearly.feed(euroVals[i]);

                    double norm = eurUsdProfile.getAvg();
                    features[0] = euroVals[i] - norm;
                    features[1] = features[0] * features[0];
                    features[2] = hourly.getAvg() - norm;
                    features[3] = features[2] * features[2];
                    features[4] = daily.getAvg() - norm;
                    features[5] = features[4] * features[2];
                    features[6] = weekly.getAvg() - norm;
                    features[7] = features[6] * features[2];
                    features[8] = monthly.getAvg() - norm;
                    features[9] = features[8] * features[2];
                    features[10] = yearly.getAvg() - norm;
                    features[11] = features[10] * features[2];
                    features[12] = eurUsdProfile.getAvg();

                    err = linearRegressionLive.calculate(features) - euroVals[i + delay];
                    sumErr += err * err;
                    count++;
                }
                err = Math.sqrt(sumErr / count);
                System.out.print(" err train: " + err);

                err = 0;
                sumErr = 0;
                count = 0;
                for (int i = ((int) (euroVals.length * train)); i < (euroVals.length - delay); i++) {
                    eurUsdProfile.feed(euroVals[i]);
                    hourly.feed(euroVals[i]);
                    daily.feed(euroVals[i]);
                    weekly.feed(euroVals[i]);
                    monthly.feed(euroVals[i]);
                    yearly.feed(euroVals[i]);

                    double norm = eurUsdProfile.getAvg();
                    features[0] = euroVals[i] - norm;
                    features[1] = features[0] * features[0];
                    features[2] = hourly.getAvg() - norm;
                    features[3] = features[2] * features[2];
                    features[4] = daily.getAvg() - norm;
                    features[5] = features[4] * features[2];
                    features[6] = weekly.getAvg() - norm;
                    features[7] = features[6] * features[2];
                    features[8] = monthly.getAvg() - norm;
                    features[9] = features[8] * features[2];
                    features[10] = yearly.getAvg() - norm;
                    features[11] = features[10] * features[2];
                    features[12] = eurUsdProfile.getAvg();

                    err = linearRegressionLive.calculate(features) - euroVals[i + delay];
                    sumErr += err * err;
                    count++;
                }
                err = Math.sqrt(sumErr / count);
                System.out.print(", err val: " + err);
                System.out.println();

            }
        }
    }
}
