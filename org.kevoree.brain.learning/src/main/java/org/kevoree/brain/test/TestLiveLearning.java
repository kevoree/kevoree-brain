package org.kevoree.brain.test;

import org.kevoree.brain.learning.livelearning.LinearRegressionLive;

import java.util.Objects;
import java.util.Random;

/**
 * Created by Ace Shooting on 8/19/2014.
 */
public class TestLiveLearning {
    private static Random rand = new Random();

    public static  double[] initRand(double max, int size){
        double[] res = new double[size];
        for(int i=0; i<size;i++){
            res[i]= rand.nextDouble()*max;
        }
        return res;
    }

    public static double generate(double[] params, double[] weights){
        double res=0;
        for(int i=0;i<params.length;i++){
            res+=weights[i]*params[i];
        }
        res+=weights[weights.length-1];
        return res;

    }

    public static void main(String[] arg){
        Integer size =20;
        int maxround=100000;
        double[] weights = initRand(1000,size+1);

        System.out.println("Printing initial weights");
        for(double d: weights){System.out.println(d);}


        LinearRegressionLive lrl = new LinearRegressionLive();
        lrl.setSize(size);

        for(int i=0; i<maxround; i++){
            double[] x=initRand(10,size);
            double value= generate(x,weights);
            //Feed the learning algorithm
            lrl.feed(x,value);
        }
        System.out.println("Printing learned weights");
        lrl.print();


    }
}
