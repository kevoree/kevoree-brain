package org.kevoree.brain.eurusd;

import org.kevoree.brain.util.TimeStamp;

import java.util.TreeMap;

/**
 * Created by assaad on 06/02/15.
 */
public class TestTrain {
    public static void main(String[] arg){

        long timeOrigine = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        int degradeFactor = 60000;
        double toleratedError = 0.0001;
        int maxDegree = 20;

        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();
        Profiler profiler =Analyzer.load(eurUsd);


        Long initTimeStamp = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        Long finalTimeStamp = TimeStamp.getTimeStamp(2015, 01, 31, 23, 59);


        //Long initTimeStamp, Long finalTimeStamp, int size, double alpha, int iteration, int space,TreeMap<Long, Double> eurUsd){

        LinearTraining lt = new LinearTraining(initTimeStamp,finalTimeStamp,10,0.0001,50,24*60*60*1000,eurUsd);


        long test=(finalTimeStamp+initTimeStamp)/2;
        lt.train(10);
        System.out.println("real: "+ String.format("%.2f",eurUsd.get(eurUsd.floorKey(test)))+" predicted: "+String.format("%.2f", lt.predict(test)));


        ForestTraining forest=new ForestTraining();
        forest.initialize(100,initTimeStamp,finalTimeStamp,50,0.0001,50,60*60*1000,eurUsd);
        forest.train(10);
        System.out.println("real: "+ String.format("%.4f",eurUsd.get(eurUsd.floorKey(test)))+" predicted: "+String.format("%.4f", forest.predict(test)));

        Long test1 = TimeStamp.getTimeStamp(2005, 01, 01, 00, 00);
        Long test2 = TimeStamp.getTimeStamp(2015, 01, 00, 00, 00);

        for(Long tt=test1;tt<test2;tt+=60000*60*24*30){
            System.out.println("real: "+ String.format("%.4f",eurUsd.get(eurUsd.floorKey(tt)))+" predicted: "+String.format("%.4f", forest.predict(tt)));
        }
    }
}
