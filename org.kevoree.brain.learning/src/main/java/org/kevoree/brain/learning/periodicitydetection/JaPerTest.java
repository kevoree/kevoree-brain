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
        Random rand = new Random();
        int maxGen=10000;

        double[] observationsDouble = new double[maxGen];
        int period= rand.nextInt(numberOfPeriods-1)+1;




        System.out.println("Testing on doubles: ");
        System.out.println("current period: "+period);



        for(int i=0;i<period;i++){
            double x=rand.nextDouble()*10000;
            observationsDouble[i]=x;
        }

        for(int i=period;i<maxGen;i++){
            double x=observationsDouble[i%period]+rand.nextDouble()*(10);
            observationsDouble[i]=x;
        }


        JaPerCalc.getSuggestedPeriod(observationsDouble, 3, numberOfPeriods, 0, 1);






    }
}
