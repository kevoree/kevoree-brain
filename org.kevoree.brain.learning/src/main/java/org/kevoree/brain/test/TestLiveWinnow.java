package org.kevoree.brain.test;

import org.kevoree.brain.learning.livelearning.LinearRegressionLive;
import org.kevoree.brain.learning.livelearning.Winnow;
import org.kevoree.brain.util.StatLibrary;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ace Shooting on 8/19/2014.
 */
public class TestLiveWinnow {
    private static Random rand = new Random();

    public static  double[] initRand(double max, int size){
        double[] res = new double[size];
        for(int i=0; i<size;i++){
            if(rand.nextBoolean()){
                res[i]= rand.nextDouble()*(max-1)+1;
            }
            else{
                res[i]= rand.nextDouble();
            }
        }
        return res;
    }

    public static  double[] getRand(int size){
        double[] res = new double[size];
        for(int i=0; i<size;i++){
            if(rand.nextBoolean()){
                res[i]= 1.0;
            }
            else{
                res[i]=0.0;
            }

        }
        return res;
    }

    public static double generate(double[] params, double[] weights){
        double res=0;
        for(int i=0;i<params.length;i++){
            res+=weights[i]*params[i];
        }

        if(res>=params.length)
            return 1.0;
        else
            return 0.0;

    }


    public static void main(String[] arg){

        ArrayList<Object[]> feats= new ArrayList<Object[]>();
        ArrayList<Integer> ress = new ArrayList<Integer>();

        Integer size =10;
        int maxround=100000;
        double[] weights = initRand(32,size);

        System.out.println("Printing initial weights");
        for(double d: weights){System.out.println(d);}


        Winnow lrl = new Winnow();

        for(int i=0; i<maxround; i++){
            double[] x=getRand(size);
            double value= generate(x,weights);
            feats.add(conv(x));
            if(value==0.0){
                ress.add(new Integer(0));
            }
            else{
                ress.add(new Integer(1));
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
