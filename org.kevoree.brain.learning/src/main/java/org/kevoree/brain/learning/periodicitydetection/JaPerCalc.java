package org.kevoree.brain.learning.periodicitydetection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 08/04/15.
 */
public class JaPerCalc {

    public static int getSuggestedPeriod(double[] entryTimeLine, int estimPerLow, int estimPerUp, int print , int showConfidence) {

        int returnValue = 0;

        //this value is used to compute the confidence at the end
        double sumPearsonCorr = 0;

        double[] observations = new double[entryTimeLine.length];
        double[] observations2 = new double[entryTimeLine.length];

        System.arraycopy(entryTimeLine, 0, observations, 0, entryTimeLine.length);
        System.arraycopy(entryTimeLine, 0, observations2, 0, entryTimeLine.length);



        JaPerCalcUtil jaPerCalc = new JaPerCalcUtil();

        //while loop for get through different assumed periods
        HashMap<Integer, Double> resultsHashMap = new HashMap<Integer, Double>();

        //this second hashMap is used for confidence calculation
        HashMap<Integer, Double> avgHashMap = new HashMap<Integer, Double>();
        ArrayList<Double> sumResults = new ArrayList<Double>();

        for(int estimPer = estimPerLow; estimPer <= estimPerUp; estimPer++){

            for(int offSet = 0; offSet <= estimPer-1; offSet++){
                double currentSum = jaPerCalc.sumItupDouble(observations, estimPer, offSet);
                sumResults.add(currentSum);
            }

            //now we got the sums for one estimated period (this is called component with length sumResults.length()), now compare this componends with parts of the origninal curve
            ArrayList<Double> pearson = new ArrayList<Double>();
            int numberOfSwaps = (int)observations2.length/sumResults.size();
            int offset = 0;
            for(int swap = 0; swap < numberOfSwaps; swap++) {

                //now cut parts from the current observation and correlate it with the component
                ArrayList<Double> currentOrigComponent = new ArrayList<Double>();
                for (int iii=0; iii<sumResults.size(); iii++) {
                    currentOrigComponent.add(observations2[iii+offset]);
                }
                offset = offset + sumResults.size();
                pearson.add(jaPerCalc.getPearsonDouble(currentOrigComponent, sumResults));
            }

            //now all parts of the observation curve have been correlated with the component
            //now we have to get the average of the pearson correlations
            double avgPearson = jaPerCalc.getAverageFromArrayList(pearson);

            resultsHashMap.put(estimPer, avgPearson);
            avgHashMap.put(estimPer, avgPearson);
            sumPearsonCorr = sumPearsonCorr + avgPearson;
//					System.out.println("putted in hashMap = " + jaPerCalc.getAverageFromArrayList(pearson));
            pearson.clear();
            sumResults.clear();

        }

        System.out.println("suggested period = " + jaPerCalc.getPeriodOfMaxValue(resultsHashMap));
        returnValue = (int) jaPerCalc.getPeriodOfMaxValue(resultsHashMap);


        //confidence calculation
        if (showConfidence == 1) {
            System.out.println("confidence: ");
            jaPerCalc.getConfidence(avgHashMap, sumPearsonCorr/(estimPerUp-estimPerLow));
        }

        if (print == 1) {
            //now print hashmap
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("following correlations have been found:");
            for (int i = estimPerLow; i <= estimPerUp; i++) {
                System.out.println("for periodicity = " + i + " average Pearson correlation = " + resultsHashMap.get(i));
            }
            System.out.println("-------------------------------------------------------------------------------------");
        }
        resultsHashMap.clear();
        return returnValue;
    }
}
