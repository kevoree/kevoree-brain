package org.kevoree.brain.test;

import org.kevoree.brain.learning.livelearning.Perceptron;
import org.kevoree.brain.learning.livelearning.Winnow;
import org.kevoree.brain.util.StatLibrary;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ace Shooting on 8/19/2014.
 */
public class TestLivePerceptron {
    private static Random rand = new Random();

    public static  double[] initRand(double max, int size){
        double[] res = new double[size];
        for(int i=0; i<size;i++){
            res[i]= rand.nextDouble()*(max-1)+1;
            if(rand.nextBoolean()){
               res[i]=-res[i];
            }
        }
        return res;
    }


    public static double generate(double[] params, double[] weights){
        double res=0;
        for(int i=0;i<params.length;i++){
            res+=weights[i]*params[i];
        }
        res+=4;

        if(res>=0)
            return 1.0;
        else
            return 0;

    }


    public static void main(String[] arg){

        ArrayList<Object[]> feats= new ArrayList<Object[]>();
        ArrayList<Integer> ress = new ArrayList<Integer>();

        Integer size =4;
        int maxround=1000000;
        double[] weights = initRand(32,size);


        System.out.println("Printing initial weights");
        System.out.println("Bias: 4");
        for(double d: weights){System.out.println(d);}


        Perceptron lrl = new Perceptron();
        lrl.setMaxIteration(10);


        for(int i=0; i<maxround; i++){
            double[] x=initRand(32, size);
            double value= generate(x,weights);
            feats.add(conv(x));
            if(value==1.0){
                ress.add(new Integer(1));
            }
            else{
                ress.add(new Integer(0));
            }

            lrl.feed(x, value);
        }
        System.out.println("Printing learned weights");
        lrl.print();

        StatLibrary.testClassifier(feats,ress,lrl);




    }

    private static Object[] conv(double[] x) {
        Object[] obj = new Object[x.length];
        for(int i=0; i<x.length;i++){
            obj[i]=x[i];
        }
        return obj;
    }
}
