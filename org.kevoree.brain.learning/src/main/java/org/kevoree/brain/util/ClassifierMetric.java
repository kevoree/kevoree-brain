package org.kevoree.brain.util;

/**
 * Created by assaad on 10/02/15.
 */
public class ClassifierMetric {
    //Square of confusion matrix
    private int tp; //True positive (predicted=1, y=1):
    private int tn; //True Negative (predicted=0, y=0)
    private int fp; //False positive (predicted=1, y=0)
    private int fn; //False Negative (predicted=0, y=1)

    private int total;

    private double precision;
    private double recall;
    private double accuracy;
    private double f1;



    public void addObservation(int prediction, int value){
        total++;
        if(prediction== 1 && value==0) {
            fp++;
        }
        else if(prediction== 0 && value==1) {
            fn++;
        }
        else if(prediction== 1 && value==1) {
            tp++;
        }
        else if(prediction== 0 && value==0) {
            tn++;
        }
        updateMetric();
    }

    private void updateMetric(){
        precision= ((double) tp)/(tp+fp);
        recall= ((double) tp)/(tp+fn);
        accuracy= ((double)(tn+tp))/total;
        f1=2*precision*recall/(precision+recall);
    }

    public void print(){
        System.out.println("True positive (predicted=1, y=1): "+tp+"/"+total+" "+ ((double) tp*100)/total + "%");
        System.out.println("True Negative (predicted=0, y=0): "+tn+"/"+total+" "+ ((double) tn*100)/total + "%");
        System.out.println("False positive (predicted=1, y=0): "+fp+"/"+total+" "+ ((double) fp*100)/total + "%");
        System.out.println("False Negative (predicted=0, y=1): "+fn+"/"+total+" "+ ((double) fn*100)/total + "%");
        System.out.println("Precision: "+precision);
        System.out.println("Recall: "+recall);
        System.out.println("F1 score: "+f1);
        System.out.println("Accuracy: "+(tp+tn) +"/"+total+ " "+ accuracy*100+"%");
    }
}
