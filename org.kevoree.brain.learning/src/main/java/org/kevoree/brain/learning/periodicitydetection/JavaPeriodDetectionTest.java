package org.kevoree.brain.learning.periodicitydetection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by assaad on 07/04/15.
 */
public class JavaPeriodDetectionTest {



    public static void main(String[] arg){



        int period= 1000; // TODO /:period
        List<Integer> rangesMax = new ArrayList<Integer>();
        rangesMax.add((int)((((double)3)/2)*period));
        rangesMax.add((int)((((double)15)/4)*period));


        List<Integer> rangesMin = new ArrayList<Integer>();
        rangesMin.add((int)((((double)2)/3)*period));
        rangesMin.add((int)((((double)1)/3)*period));

        List<Integer> nbPeriodInSamples = new ArrayList<Integer>();
//        nbPeriodInSamples.add(3);
//        nbPeriodInSamples.add(4);
//        nbPeriodInSamples.add(5);
//        nbPeriodInSamples.add(8);
//        nbPeriodInSamples.add(10);
//        nbPeriodInSamples.add(30);
        nbPeriodInSamples.add(100);

        List<Double> noises = new ArrayList<Double>();
        noises.add(0.);
        noises.add(0.05);
        noises.add(0.1);
        noises.add(0.2);

        for (Integer i : nbPeriodInSamples) {
            for (int j = 0; j < rangesMax.size(); j++) {
                for (double noise : noises) {
                    try {
                        calculate(period, i, rangesMax.get(j), rangesMin.get(j), noise);
                    }catch (ArrayIndexOutOfBoundsException e){
                        // know and normal problem with the lists
                    }
                }
            }
        }
    }


    public static void calculate(int period, int nbPeriodInSample, int rangeMax, int rangeMin, double noise){
        Random rand = new Random();

//        int period= rand.nextInt(maxPeriod-1)+1;
        int maxGen = period * nbPeriodInSample; // TODO nb points = nb point/period * nb Period

        double[] observationsDouble = new double[maxGen];
        System.out.println();
        System.out.print("current period: " + period);
        System.out.print("\t-  nbPeriod: " + nbPeriodInSample);
        System.out.print("\t-  rangeMax: " + rangeMax);
        System.out.print("\t-  rangeMin: " + rangeMin);
        System.out.println("\t-  noise: " + noise);



//        //To test randomly generated signal
//        for(int i=0;i<period;i++){
//            double x=rand.nextDouble()*10000;
//            observationsDouble[i]=x;
//        }
//        for(int i=period;i<maxGen;i++){
//            double x=observationsDouble[i%period]+rand.nextDouble()*noise;
//            observationsDouble[i]=x;
//        }


        //To test sinus
        for(int i=0;i<period;i++){
            observationsDouble[i]=Math.sin(i*2*Math.PI/period);
        }for(int j=period;j<maxGen;j++){
            double x=observationsDouble[j%period]+rand.nextDouble()*noise;
            observationsDouble[j]=x;
        }


        //To test Constant
        /*for(int i=0;i<maxGen;i++){
            observationsDouble[i]=50;
        }*/


        //To get only the best estimate of the period:
        long starttime2= System.nanoTime();
        int estimatedPeriod2 = JavaPeriodCalculatorFFT.getPeriod(observationsDouble, rangeMin, rangeMax);
        long endtime2= System.nanoTime();
        double result2= ((double)(endtime2-starttime2))/(1000000000);
        System.out.println("FFT - Calculated completed in "+result2+" s");
        System.out.print(estimatedPeriod2 == period);System.out.println("  - FFT - Estimated period: " + estimatedPeriod2);
        if(nbPeriodInSample<50) {
            //To get only the best estimate of the period:
            long starttime = System.nanoTime();
            int estimatedPeriod = JavaPeriodCalculatorPearson.getPeriod(observationsDouble, rangeMin, rangeMax);
            long endtime = System.nanoTime();
            double result = ((double) (endtime - starttime)) / (1000000000);
            System.out.println("Person - Calculated completed in " + result + " s");
            System.out.print(estimatedPeriod == period);
            System.out.println("  - Person - Estimated period: " + estimatedPeriod);
        }
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
