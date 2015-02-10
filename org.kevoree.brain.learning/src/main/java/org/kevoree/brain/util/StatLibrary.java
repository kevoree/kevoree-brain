package org.kevoree.brain.util;

import org.kevoree.brain.api.classifier.Classifier;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/13/2014.
 */
public class StatLibrary {


    public static ClassifierMetric  testClassifier(ArrayList<Object[]> testSet, ArrayList<Integer> yVal,  Classifier classifier){
        ArrayList<Integer> prediction = new ArrayList<Integer>(yVal.size());
        int total=testSet.size();
        for(int i =0;i<total;i++){
            prediction.add(new Integer(classifier.evaluate(testSet.get(i))));
        }

        int tp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        int fp=0; // false positives - the ground truth label says it's not an anomaly, but our algorithm incorrectly classied it as an anomaly.
        int fn=0; // false negatives - the ground truth label says it's an anomaly, but our algorithm incorrectly classied it as not being anomalous.
        int tn=0; // true negatives

        ClassifierMetric cm = new ClassifierMetric();

        for(int i=0; i<total; i++){
           cm.addObservation(prediction.get(i),yVal.get(i));
        }
        cm.print();
        return cm;





    }




    public static double calculateF1(ArrayList<Object[]> testSet, ArrayList<Integer> yVal,  Classifier classifier){
        ArrayList<Integer> prediction = new ArrayList<Integer>(yVal.size());
        int total=testSet.size();
        for(int i =0;i<total;i++){
            prediction.add(new Integer(classifier.evaluate(testSet.get(i))));
        }

        int tp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        int fp=0; // false positives - the ground truth label says it's not an anomaly, but our algorithm incorrectly classied it as an anomaly.
        int fn=0; // false negatives - the ground truth label says it's an anomaly, but our algorithm incorrectly classied it as not being anomalous.


        for(int i=0; i<total; i++){
            if(prediction.get(i)== 1 && yVal.get(i)==0)
                fp++;
            if(prediction.get(i)== 0 && yVal.get(i)==1)
                fn++;
            if(prediction.get(i)== 1 && yVal.get(i)==1)
                tp++;

        }
        double prec= ((double) tp)/(tp+fp);
        double rec= ((double) tp)/(tp+fn);
        double f1=2*prec*rec/(prec+rec);
        return f1;
    }


    public static double calculateF1(ArrayList<Integer> prediction, ArrayList<Integer> yVal){
        int tp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        int fp=0; // false positives - the ground truth label says it's not an anomaly, but our algorithm incorrectly classied it as an anomaly.
        int fn=0; // false negatives - the ground truth label says it's an anomaly, but our algorithm incorrectly classied it as not being anomalous.


        for(int i=0; i<prediction.size(); i++){
            if(prediction.get(i)== 1 && yVal.get(i)==0)
                fp++;
            if(prediction.get(i)== 0 && yVal.get(i)==1)
                fn++;
            if(prediction.get(i)== 1 && yVal.get(i)==1)
                tp++;
        }
        double prec= ((double) tp)/(tp+fp);
        double rec= ((double) tp)/(tp+fn);
        double f1=2*prec*rec/(prec+rec);
        return f1;
    }

    public static int calculateTruePositives(ArrayList<Integer> prediction, ArrayList<Integer> yVal){
        int tp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        for(int i=0; i<prediction.size(); i++){
            if(prediction.get(i)== 1 && yVal.get(i)==1)
                tp++;
        }
        return tp;
    }
    public static int calculateTrueNegatives(ArrayList<Integer> prediction, ArrayList<Integer> yVal){
        int tn=0;
        for(int i=0; i<prediction.size(); i++){
            if(prediction.get(i)== 0 && yVal.get(i)==0)
                tn++;
        }
        return tn;
    }

    public static int calculateFalsePositives(ArrayList<Integer> prediction, ArrayList<Integer> yVal){
        int fp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        for(int i=0; i<prediction.size(); i++){
            if(prediction.get(i)== 1 && yVal.get(i)==0)
                fp++;
        }
        return fp;
    }

    public static int calculateFalseNegatives(ArrayList<Integer> prediction, ArrayList<Integer> yVal){
        int fn=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        for(int i=0; i<prediction.size(); i++){
            if(prediction.get(i)== 0 && yVal.get(i)==1)
                fn++;
        }
        return fn;
    }

    public static double calculatePrecision(ArrayList<Integer> prediction, ArrayList<Integer> yVal){
        int tp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        int fp=0; // false positives - the ground truth label says it's not an anomaly, but our algorithm incorrectly classied it as an anomaly.
        int fn=0; // false negatives - the ground truth label says it's an anomaly, but our algorithm incorrectly classied it as not being anomalous.


        for(int i=0; i<prediction.size(); i++){
            if(prediction.get(i)== 1 && yVal.get(i)==0)
                fp++;
            if(prediction.get(i)== 0 && yVal.get(i)==1)
                fn++;
            if(prediction.get(i)== 1 && yVal.get(i)==1)
                tp++;
        }
        double prec= ((double) tp)/(tp+fp);
        return prec;

    }
    public static double calculateRecall(ArrayList<Integer> prediction, ArrayList<Integer> yVal){
        int tp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        int fp=0; // false positives - the ground truth label says it's not an anomaly, but our algorithm incorrectly classied it as an anomaly.
        int fn=0; // false negatives - the ground truth label says it's an anomaly, but our algorithm incorrectly classied it as not being anomalous.


        for(int i=0; i<prediction.size(); i++){
            if(prediction.get(i)== 1 && yVal.get(i)==0)
                fp++;
            if(prediction.get(i)== 0 && yVal.get(i)==1)
                fn++;
            if(prediction.get(i)== 1 && yVal.get(i)==1)
                tp++;
        }
        double rec= ((double) tp)/(tp+fn);
        return rec;
    }

    public static double calculateAccuracy(ArrayList<Integer> prediction, ArrayList<Integer> yVal){
        int tp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        int tn=0; // true negatives

        for(int i=0; i<prediction.size(); i++){
            if(prediction.get(i)== 0 && yVal.get(i)==0)
                tn++;
            if(prediction.get(i)== 1 && yVal.get(i)==1)
                tp++;
        }
        double acc= ((double)(tn+tp))/prediction.size();
        return acc;
    }




    }
