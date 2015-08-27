package de.tuhh.luethke.okde.model;

import org.ejml.simple.SimpleMatrix;

import java.util.Random;

/**
 * Created by assaad on 29/07/15.
 */
public class Test {
    // disable the forgetting factor
    private static final double forgettingFactor = 1;
    // set the compression threshold
    private static final double compressionThreshold = 0.02;


    public static void main(String[] args) {

        SampleModel sm = new SampleModel(forgettingFactor, compressionThreshold);
        sm.setNoOfCompsThreshold(50);
        Random random = new Random();

        try {
            int sz=5;
            SimpleMatrix[] pos = new SimpleMatrix[sz];
            SimpleMatrix[] c = new SimpleMatrix[sz];
            double [] w= new double[sz];
            for (int i = 0; i < sz; i++) {
                pos[i]=new SimpleMatrix(2, 1);
                pos[i].set(0, random.nextGaussian() * 40 + 100);
                pos[i].set(1, random.nextGaussian() * 200 + 2000);
                c[i]= new SimpleMatrix(new double[2][2]);
                w[i]=1;
            }
            sm.updateDistribution(pos, c, w);
            System.out.println("done step 1");

            for(int i=sz;i<1000;i++){
                SimpleMatrix p=new SimpleMatrix(2, 1);
                p.set(0, random.nextGaussian() * 40 + 100);
                p.set(1, random.nextGaussian() * 200 + 2000);
                SimpleMatrix cov= new SimpleMatrix(new double[2][2]);
                double wf=1;
                sm.updateDistribution(p,cov,wf);
            }
            System.out.println("done");
            int size = 100;
            double[] x = new double[size];
            double[] y = new double[size];

            for (int i = 0; i < size; i++) {
                x[i] = 300 * i / size;
                y[i] = 4000 * i / size;
            }

            double[][] proba = getProbabilities(x, y, sm);
            int sx = 0;


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static double[][] getProbabilities(double[] x, double[] y, SampleModel sm) {
        double[][] z = new double[y.length][x.length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < y.length; j++) {
                SimpleMatrix pointVector = new SimpleMatrix(2, 1);
                pointVector.set(0, x[i]);
                pointVector.set(1, y[j]);
                z[j][i] = sm.evaluate(pointVector);
            }
        }
        return z;
    }
}
