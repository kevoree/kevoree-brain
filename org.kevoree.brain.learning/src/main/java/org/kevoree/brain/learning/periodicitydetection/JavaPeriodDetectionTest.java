package org.kevoree.brain.learning.periodicitydetection;

import java.util.*;

/**
 * Created by assaad on 07/04/15.
 */
public class JavaPeriodDetectionTest {

    public static int maxPeriod = 20;


    public static void main(String[] arg){
        Random rand = new Random();
        // TODO : int nbPointPerPeriod = 1000000;
        int maxGen=1000000; // TODO nb points = nb point/period * nb Period

        double[] observationsDouble = new double[maxGen];
        int period= 100;
//        int period= rand.nextInt(maxPeriod-1)+1;

        int rangeMin = 3; // TODO : range change
        int rangeMax = maxGen;

        System.out.println("current period: "+period);



        //To test randomly generated signal
        for(int i=0;i<period;i++){
            double x=rand.nextDouble()*10000;
            observationsDouble[i]=x;
        }
        for(int i=period;i<maxGen;i++){
            double x=observationsDouble[i%period]+rand.nextDouble()*(10); // TODO : bruit
            observationsDouble[i]=x;
        }


        //To test sinus
        for(int i=0;i<maxGen;i++){ // TODO : change sampling
            observationsDouble[i]=Math.sin(i*2*Math.PI/period); // TODO : signal
        }


        //To test Constant
        /*for(int i=0;i<maxGen;i++){
            observationsDouble[i]=50;
        }*/

        //To get only the best estimate of the period:
        long starttime= System.nanoTime();
        int estimatedPeriod = JavaPeriodCalculatorPearson.getPeriod(observationsDouble, rangeMin, rangeMax);
        long endtime= System.nanoTime();
        double result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Calculated completed in "+result+" s");
        System.out.println("Estimated period: "+estimatedPeriod);
        //To get only the best estimate of the period:
        long starttime2= System.nanoTime();
        int estimatedPeriod2 = JavaPeriodCalculatorFFT.getPeriod(observationsDouble, rangeMin, rangeMax);
        long endtime2= System.nanoTime();
        double result2= ((double)(endtime2-starttime2))/(1000000000);
        System.out.println("FFT - Calculated completed in "+result2+" s");
        System.out.println("FFT - Estimated period: "+estimatedPeriod2);

        /*
        //To print all values and confidences
        SortedSet<Map.Entry<Integer, Double>> periods= JavaPeriodCalculatorFFT.getAllPeriods(observationsDouble, 3, maxPeriod);
        int count=0;
        for(Map.Entry ep: periods){
            if(count>10){
                break;
            }
            count++;
            System.out.println("Period "+ep.getKey()+" , Confidence: "+ep.getValue());
        }
        */

    }


}
