package org.kevoree.brain.eurusd.apps;

import de.tuhh.luethke.okde.utility.Matrices.MatrixOps;
import org.kevoree.brain.eurusd.learners.Profiler;
import org.kevoree.brain.eurusd.learners.TimeProfiler;
import org.kevoree.brain.eurusd.learners.TokenRingProfile;
import org.kevoree.brain.eurusd.tools.Loader;
import org.kevoree.brain.learning.livelearning.LinearRegressionLive;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.server.ExportException;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by assaa_000 on 25/09/2015.
 */
public class TokenRingProfiling {
    public static  String loc="/Users/assaad/work/github/eurusd/";

    public static void main(String[] arg) {
        loc="C:\\Users\\assaa_000\\Documents\\GitHub\\eurusd\\";

        String csvFile = "/Users/assaad/work/github/eurusd/newEurUsd.csv";
        csvFile = "C:\\Users\\assaa_000\\Documents\\GitHub\\eurusd\\newEurUsd.csv";

        double[] euroVals = Loader.loadContinuous(3600000, csvFile);

        try {
            FileWriter outFile = new FileWriter(loc + "old.csv");
            PrintWriter out = new PrintWriter(outFile);
            int count=0;
            for(int i=0;i<euroVals.length;i+=24){
                out.println(count+","+euroVals[i]);
                count++;
            }
            out.close();
            outFile.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }



        predict(euroVals,356*2);


    }

    public static void predict(double[] euroVals, int days) {


        double min = 0.2;
        double[] bestWeight;
        int delay = 24;

        boolean b = false;
        Profiler eurbackup = null;
        TokenRingProfile[] profilesBk = null;

        Random random = new Random();

        double[] features = new double[13];
        LinearRegressionLive linearRegressionLive = new LinearRegressionLive(features.length);
        linearRegressionLive.setAlpha(2.97E-6);
        linearRegressionLive.setIteration(1);
        linearRegressionLive.setWeights(new double[]{0.42800628949 , 0.11972953653 , 0.26557095336 , 0.10597061921 , 0.41206750289 , 0.26892922272 , -0.22720082323 , -0.34041373915 , 0.13039594221 , -0.08960627774 , 0.11753502082 , -0.27177916719 , 0.93847345615 , 0.05899712439});


        TokenRingProfile[] profiles = new TokenRingProfile[5];
        Profiler eurUsdProfile;

            // profiles[0] = new TokenRingProfile(60); //hourly
            profiles[0] = new TokenRingProfile(24); //daily
            profiles[1] = new TokenRingProfile(24 * 7); //wekly
            profiles[2] = new TokenRingProfile(24 * 30); //monthly
            profiles[3] = new TokenRingProfile(24 * 365); //yearly
            profiles[4] = new TokenRingProfile(5 * 24 * 365); //5 years
            eurUsdProfile = new Profiler();

            for (int i = 0; i < (euroVals.length) -delay; i++) {
                feed(euroVals[i], eurUsdProfile, profiles);
                featureExtraction(features, eurUsdProfile, euroVals[i], profiles);
                linearRegressionLive.feed(features, euroVals[i + delay]);
            }
        try {
            FileWriter outFile = new FileWriter(loc + "prediction.csv");
            PrintWriter out = new PrintWriter(outFile);

            double v=euroVals[euroVals.length-1];
            double vv;
            for(int i=0;i<days;i++){
                featureExtraction(features, eurUsdProfile,v , profiles);
                vv = linearRegressionLive.calculate(features);
                feed(vv, eurUsdProfile, profiles);
                v = vv;
                out.println(i+","+vv);
            }
            out.close();
            outFile.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }


        }



    public static void play(double[] euroVals){
        double min = 0.2;
        double[] bestWeight;
        int delay = 24;
        double train = 0.9;

        boolean b = false;
        Profiler eurbackup = null;
        TokenRingProfile[] profilesBk = null;

        Random random = new Random();
        for (int k = 0; k < 100000; k++) {
            double[] features = new double[13];
            LinearRegressionLive linearRegressionLive = new LinearRegressionLive(features.length);
            linearRegressionLive.setAlpha(random.nextInt(1000) * 0.00000001);
            linearRegressionLive.setIteration(1);


            TokenRingProfile[] profiles = new TokenRingProfile[5];
            Profiler eurUsdProfile;
            double errIn = 100;
            for (int j = 0; j < 10000; j++) {

                // profiles[0] = new TokenRingProfile(60); //hourly
                profiles[0] = new TokenRingProfile( 24); //daily
                profiles[1] = new TokenRingProfile( 24 * 7); //wekly
                profiles[2] = new TokenRingProfile( 24 * 30); //monthly
                profiles[3] = new TokenRingProfile( 24 * 365); //yearly
                profiles[4] = new TokenRingProfile( 5*24 * 365); //5 years
                eurUsdProfile = new Profiler();

                for (int i = 0; i < (euroVals.length) * train; i++) {
                    feed(euroVals[i], eurUsdProfile, profiles);
                    featureExtraction(features, eurUsdProfile, euroVals[i], profiles);
                    linearRegressionLive.feed(features, euroVals[i + delay]);
                }
                if (j % 10 == 0) {
                    eurUsdProfile = new Profiler();
                    // System.out.print("Round " + j + ": ");
                    //  linearRegressionLive.print(new DecimalFormat("#.####"));

                    //calculate error:
                    double err;
                    double sumErr = 0;
                    int count = 0;

                    if (!b) {
                        for (int i = 0; i < (euroVals.length) * train; i++) {
                            feed(euroVals[i], eurUsdProfile, profiles);
                            // featureExtraction(features, eurUsdProfile, euroVals[i], profiles);
                            // err = linearRegressionLive.calculate(features) - euroVals[i + delay];
                            //  sumErr += err * err;
                            // count++;
                        }
                        eurbackup = eurUsdProfile.copy();
                        profilesBk = new TokenRingProfile[profiles.length];
                        for (int i = 0; i < profilesBk.length; i++) {
                            profilesBk[i] = profiles[i].copy();
                        }
                        b = true;
                    } else {
                        eurUsdProfile = eurbackup.copy();
                        for (int i = 0; i < profilesBk.length; i++) {
                            profiles[i] = profilesBk[i].copy();
                        }
                    }
                    //  err = Math.sqrt(sumErr / count);
                    // System.out.print(" err train: " + err);


             /*       Profiler eurbackup = eurUsdProfile.copy();
                    TokenRingProfile[] profilesBk = new TokenRingProfile[5];
                    for (int i = 0; i < profilesBk.length; i++) {
                        profilesBk[i] = profiles[i].copy();
                    }

                     err = 0;
                    sumErr = 0;
                    count = 0;
                    for (int i = ((int) (euroVals.length * train)); i < (euroVals.length - delay); i++) {
                        feed(euroVals[i], eurUsdProfile, profiles);
                        featureExtraction(features, eurUsdProfile, euroVals[i], profiles);
                        err = linearRegressionLive.calculate(features) - euroVals[i + delay];
                        sumErr += err * err;
                        count++;
                    }
                    err = Math.sqrt(sumErr / count);*/
                    //   System.out.print(", err val: " + err);

                    err = 0;
                    sumErr = 0;
                    count = 0;
                    double v = euroVals[((int) (euroVals.length * train))];
                    //  profilesBk[0].deactivate = true; //deactivate hourly profile

                    double vv;
                    for (int i = ((int) (euroVals.length * train)); i < (euroVals.length - delay); i += delay) {
                        //   featureExtraction(features, eurbackup, v, profilesBk);
                        featureExtraction(features, eurUsdProfile, v, profiles);
                        vv = linearRegressionLive.calculate(features);
                        feed(vv, eurUsdProfile, profiles);
                        err = vv - euroVals[i + delay];
                        sumErr += err * err;
                        count++;
                        v = vv;
                    }
                    err = Math.sqrt(sumErr / count);
                    if (err < min) {
                        min = err;
                        bestWeight = linearRegressionLive.getWeights();
                        linearRegressionLive.print(new DecimalFormat("#.###########"));
                        System.out.println(" err-all: " + err + " alpha: " + linearRegressionLive.getAlpha() + " k=" + k + " j= " + j+" count:"+count);
                    }

                    if (err < errIn) {
                        errIn = err;
                    } else {
                        break;
                    }

                }
                //       System.out.print(", err overall: " + err + ", count: " + count);
                //       System.out.println();
            }
        }
    }



    public static void feed( double euroVal, Profiler eurUsdProfile,TokenRingProfile[] profiles){
        eurUsdProfile.feed(euroVal);
        for(TokenRingProfile p: profiles){
            p.feed(euroVal);
        }
    }
    public static void featureExtraction(double[] features, Profiler eurUsdProfile, double euroVal,TokenRingProfile[] profiles){
        double norm = eurUsdProfile.getAvg();
        features[0] = euroVal - norm;
        features[1] = features[0] * features[0];
        for(int i=0;i<profiles.length;i++){
            features[2*i+2]=profiles[i].getAvg() - norm;
            features[2*i+3]=features[2*i+2]*features[2*i+2];
        }
        features[features.length-1]=norm;

    }
}
