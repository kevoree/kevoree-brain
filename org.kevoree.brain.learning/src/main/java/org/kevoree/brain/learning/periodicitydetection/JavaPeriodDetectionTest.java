package org.kevoree.brain.learning.periodicitydetection;

import java.util.*;

/**
 * Created by assaad on 07/04/15.
 */
public class JavaPeriodDetectionTest {

    public static int maxPeriod = 20;


    public static void main(String[] arg){
        Random rand = new Random();
        int maxGen=1000;

        double[] observationsDouble = new double[maxGen];
        int period= rand.nextInt(maxPeriod-1)+1;

        System.out.println("current period: "+period);



        //To test randomly generated signal
        for(int i=0;i<period;i++){
            double x=rand.nextDouble()*10000;
            observationsDouble[i]=x;
        }
        for(int i=period;i<maxGen;i++){
            double x=observationsDouble[i%period]+rand.nextDouble()*(10);
            observationsDouble[i]=x;
        }


        //To test sinus
      /*  for(int i=0;i<maxGen;i++){
            observationsDouble[i]=Math.sin(i*2*Math.PI/period);
        }*/


        //To test Constant
        /*for(int i=0;i<maxGen;i++){
            observationsDouble[i]=50;
        }*/

        //To get only the best estimate of the period:
        long starttime= System.nanoTime();
        int estimatedPeriod = JavaPeriodCalculator.getPeriod(observationsDouble, 3, maxPeriod);
        long endtime= System.nanoTime();
        double result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Calculated completed in "+result+" s");
        System.out.println("Estimated period: "+estimatedPeriod);

        //To print all values and confidences
        SortedSet<Map.Entry<Integer, Double>> periods= JavaPeriodCalculator.getAllPeriods(observationsDouble, 3, maxPeriod);
        int count=0;
        for(Map.Entry ep: periods){
            if(count>10){
                break;
            }
            count++;
            System.out.println("Period "+ep.getKey()+" , Confidence: "+ep.getValue());
        }

    }


}
