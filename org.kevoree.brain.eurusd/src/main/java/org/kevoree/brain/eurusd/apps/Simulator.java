package org.kevoree.brain.eurusd.apps;

import org.kevoree.brain.eurusd.learners.Portfolio;
import org.kevoree.brain.eurusd.learners.Profiler;
import org.kevoree.brain.eurusd.tools.Loader;
import org.kevoree.brain.util.TimeStamp;

import java.util.TreeMap;

/**
 * Created by assaad on 06/02/15.
 */
public class Simulator {


    public static Portfolio simulate(long initTimeStamp, long trainTimeStamp, long finalTimeStamp, int degradeFactor, Profiler profiler, TreeMap<Long, Double> eurUsd, Portfolio portfolio){
        int[] histogram = new int[profiler.getMaxInt()];
        int counter=0;
        for(long i=initTimeStamp; i<trainTimeStamp;i+=degradeFactor){
            //double val = pt.fastReconstruct(i);
            double val=eurUsd.get(eurUsd.floorKey(i));
            histogram[profiler.position(val)]++;
            counter++;
        }

        double moneyeur=portfolio.euro;
        double moneydol=portfolio.dollar;
        int op=0;
        double firstval=0;
        for(long i=trainTimeStamp; i<finalTimeStamp;i+=degradeFactor){
            double val=eurUsd.get(eurUsd.floorKey(i));
            if(op==0){
                firstval=val;
                System.out.println("val: " + String.format("%.2f", val) +" euro: " + String.format("%.2f", moneyeur) + " , dollar: " + String.format("%.2f",  moneydol)+" , all: "+String.format("%.2f",moneyeur*val+moneydol));
            }
            histogram[profiler.position(val)]++;
            counter++;
            if(op%(60*24*10)==0) {
                double[] acchist = new double[profiler.getMaxInt()];

                acchist[0] = ((double) (histogram[0] * 100)) / counter;
                for (int j = 1; j < profiler.getMaxInt(); j++) {
                    acchist[j] = ((double) (histogram[j] * 100)) / counter + acchist[j - 1];
                }

                double perc = Analyzer.getPerc(profiler, acchist, val);


                double tot = moneyeur * val + moneydol;
                double neweur = (100 - perc) * tot / (100 * val);
                double newdol = tot - neweur * val;
                moneyeur = neweur;
                moneydol = newdol;
                //System.out.println("val: " + String.format("%.2f", val) +" euro: " + String.format("%.2f", neweur) + " , dollar: " + String.format("%.2f",  newdol)+" , all: "+String.format("%.2f",neweur*val+newdol));
            }
            op++;
        }
        Portfolio p =new Portfolio();
        p.dollar=moneydol;
        p.euro=moneyeur;
        System.out.println("val: " + String.format("%.2f", firstval) +" euro: " + String.format("%.2f", moneyeur) + " , dollar: " + String.format("%.2f",  moneydol)+" , all: "+String.format("%.2f",moneyeur*firstval+moneydol));
        return p;
    }

    public static void main(String[] args) {

        int degradeFactor = 60000;

        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();
        String csvFile = "/Users/assaad/work/github/eurusd/newEurUsd.csv";
        Profiler profiler = Loader.load(eurUsd,csvFile);


        Long initTimeStamp = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        Long trainTimeStamp = TimeStamp.getTimeStamp(2014, 01, 01, 00, 00);
        Long finalTimeStamp = TimeStamp.getTimeStamp(2015, 01, 31, 23, 59);

        Portfolio p = new Portfolio();
        p.euro=3000;
        p.dollar=17000;

        Portfolio q=simulate(initTimeStamp,trainTimeStamp,finalTimeStamp,degradeFactor, profiler,eurUsd,p);
       // System.out.println("euro: "+q.euro+" , "+q.dollar);


        System.out.println("Done!");


    }


}
