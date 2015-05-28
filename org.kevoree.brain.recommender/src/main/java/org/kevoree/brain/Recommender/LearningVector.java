package org.kevoree.brain.Recommender;

import java.util.Random;

/**
 * Created by assaad on 19/05/15.
 */
public class LearningVector {
    public static double min=0;
    public static double max=1;



    private static Random rand = new Random();



    public double sum;
    public int counter;
    public double[] taste;


    public LearningVector(int numOfFeatures) {
        taste=new double[numOfFeatures];
        for(int i=0; i<numOfFeatures;i++){
            taste[i]=rand.nextDouble()*(max-min)+min; //*Math.sqrt(5/numOfFeatures);
        }
    }

    public double getAverage(){
        if (counter!=0){
            return sum/counter;
        }
        else return 0;
    }

    public static double multiply(LearningVector lv1, LearningVector lv2){
        double val=0;
        for(int i=0;i<lv1.taste.length;i++){
            val=val+lv1.taste[i]*lv2.taste[i];
        }
        return val;
    }
}
