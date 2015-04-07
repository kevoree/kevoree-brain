package org.kevoree.brain.learning.periodicitydetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by assaad on 07/04/15.
 */
public class JaPerTest {

    public static int numberOfPeriods = 1000;


    public static void main(String[] arg){
        ArrayList<Integer> observations = new ArrayList<Integer>();
        Random rand = new Random();

        int maxGen=10000;
        int period= rand.nextInt(numberOfPeriods-1)+1;
        System.out.println("current period: "+period);

        for(int i=0;i<period;i++){
            int x=rand.nextInt(10000);
            observations.add(x);
        }

        for(int i=period;i<maxGen;i++){
            int x=observations.get(i%period)+rand.nextInt(10);
            observations.add(x);
        }


       JaPerCalc.getSuggestedPeriod(observations, 3, numberOfPeriods, 0, 1);

        System.out.println("Testing on doubles: ");

        ArrayList<Double> observationsDouble = new ArrayList<Double>();

        period= rand.nextInt(numberOfPeriods-1)+1;
        System.out.println("current period: "+period);

        for(int i=0;i<period;i++){
            double x=rand.nextDouble()*10000;
            observationsDouble.add(x);
        }

        for(int i=period;i<maxGen;i++){
            double x=observationsDouble.get(i%period)+rand.nextDouble()*(10);
            observationsDouble.add(x);
        }


        JaPerCalcDouble.getSuggestedPeriod(observationsDouble, 3, numberOfPeriods, 0, 1);


    }
}
