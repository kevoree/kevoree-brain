package org.kevoree.brain.test;


import org.kevoree.brain.learning.MultivariateGaussian;
import weka.estimators.MultivariateEstimator;
import weka.estimators.MultivariateGaussianEstimator;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by assaad on 27/08/15.
 */
public class TestMultivariateCovariance {
    public static void main (String[] arg){

        int features=5;
        int size =10;

        double[][] trainset=new double[size][features];
        Random rand = new Random();

        MultivariateGaussian mg = new MultivariateGaussian();
        for(int i=0;i<size;i++){
            for(int j=0; j<features;j++){
                trainset[i][j]=rand.nextDouble()*(j*10+5);
            }
            mg.train(trainset[i]);
        }

        MultivariateGaussianEstimator mge = new MultivariateGaussianEstimator(new double[features],new double[features][features]);
        mge.estimate(trainset, null);

        System.out.println("Means: ");
        double[] mymeans=mg.getMeans();
        double[][] myCov =mg.getCovariance(mymeans);

        double[] wekameans =mge.getMean();
        double[][] wekaCov=mge.getCovariance();

        for(int i=0;i<features;i++){
            System.out.print(new DecimalFormat("#0.000000").format(mymeans[i])+" ");
        }
        System.out.println();
        for(int i=0;i<features;i++){
            System.out.print(new DecimalFormat("#0.000000").format(wekameans[i]) + " ");
        }
        System.out.println();


        System.out.println("Covariances: ");

        for(int i=0;i<features;i++){
            for(int j=0;j<features;j++) {
                System.out.print(new DecimalFormat("#0.000000").format(myCov[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println();

        for(int i=0;i<features;i++){
            for(int j=0;j<features;j++) {
                System.out.print(new DecimalFormat("#0.000000").format(wekaCov[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println();


    }

}
