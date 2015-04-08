package org.kevoree.brain.learning.periodicitydetection;

import java.util.Map;
import java.util.Random;
import java.util.SortedSet;

/**
 * Created by assaad on 07/04/15.
 */
public class JavaPeriodDetectionTestFFT {

    public static int maxPeriod = 1000;


    public static void main(String[] arg){
        Random rand = new Random();
        int maxGen=10000;

        double[] observationsDouble = new double[maxGen];
        int period= rand.nextInt(maxPeriod-1)+1;

        System.out.println("current period: "+period);



        //To test randomly generated signal
        for(int i=0;i<period;i++){
            double x=rand.nextDouble()*10000;
            observationsDouble[i]=x;
        }
        for(int i=period;i<maxGen;i++){
            double x=observationsDouble[i%period]+rand.nextDouble()*(100);
            observationsDouble[i]=x;
        }


        long starttime= System.nanoTime();
        int estimatedPeriod = JavaPeriodCalculatorPearson.getPeriod(observationsDouble, 3, maxPeriod);
        long endtime= System.nanoTime();
        double result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Calculated completed in "+result+" s");
        System.out.println("Estimated period: "+estimatedPeriod);
        SortedSet<Map.Entry<Integer, Double>> periods= JavaPeriodCalculatorPearson.getAllPeriods(observationsDouble, 3, maxPeriod);
        int count=0;
        for(Map.Entry ep: periods){
            if(count>10){
                break;
            }
            count++;
            System.out.println("Period "+ep.getKey()+" , Confidence: "+ep.getValue());
        }

        System.out.println();


        System.out.println("Using FFT: ");

        starttime= System.nanoTime();
        estimatedPeriod = JavaPeriodCalculatorFFT.getPeriod(observationsDouble, 3, maxPeriod);
        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Calculated completed in "+result+" s");
        System.out.println("Estimated period: "+estimatedPeriod);
        periods= JavaPeriodCalculatorFFT.getAllPeriods(observationsDouble, 3, maxPeriod);
        count=0;
        for(Map.Entry ep: periods){
            if(count>10){
                break;
            }
            count++;
            System.out.println("Period "+ep.getKey()+" , Confidence: "+ep.getValue());
        }



    }


}
