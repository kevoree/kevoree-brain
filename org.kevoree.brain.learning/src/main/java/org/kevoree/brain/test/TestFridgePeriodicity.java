package org.kevoree.brain.test;


import org.kevoree.brain.learning.periodicitydetection.JavaPeriodCalculatorFFT;
import org.kevoree.brain.learning.periodicitydetection.JavaPeriodCalculatorPearson;
import org.kevoree.brain.util.Loaders.CsvLoader;

import java.util.TreeMap;

/**
 * Created by assaad on 10/04/15.
 */
public class TestFridgePeriodicity {
    public static void main(String[] arg){
        String csvFile="/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.learning/resource/filtered.csv";
        TreeMap<Long,Double> fridgeData = CsvLoader.load(csvFile);
        System.out.println("size: "+fridgeData.size());
        long start=fridgeData.firstKey();
        long end= fridgeData.lastKey();

        int windowSize=400;
        int stepTime=10000;
        double[] values;

        int counter=0;
        for(long l=start;l<end-stepTime*windowSize;l+=stepTime*windowSize/4){
            values=new double[windowSize];
            for(int i=0;i<windowSize;i++){
                values[i]=fridgeData.get(fridgeData.floorKey(l + i * stepTime));
            }
            System.out.println(counter+" Period [FFT]: "+ JavaPeriodCalculatorFFT.getPeriod(values,2,399)*stepTime/1000+" s,"+ " [Pearson]:" + JavaPeriodCalculatorPearson.getPeriod(values, 2, 399)*stepTime/1000+" s");
            counter++;
        }

        windowSize=((int)(end-start))/stepTime;
        values=new double[windowSize];
        for(int i=0;i<windowSize;i++){
            values[i]=fridgeData.get(fridgeData.floorKey(start+i*stepTime));
        }
        System.out.println("All data: "+ JavaPeriodCalculatorFFT.getPeriod(values,2,windowSize-1)*stepTime/1000+" s"+ " Pearson:" + JavaPeriodCalculatorPearson.getPeriod(values, 2, windowSize-1)*stepTime/1000+" s");

        JavaPeriodCalculatorPearson.printAllPeriod(values,2,windowSize-1,stepTime/1000, windowSize);
        JavaPeriodCalculatorFFT.printAllPeriod(values, 2, windowSize - 1,stepTime/1000, windowSize);




    }
}
