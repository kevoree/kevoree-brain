package org.kevoree.brain.learning.livelearning;

import org.kevoree.brain.api.classifier.Classifier;
import org.kevoree.brain.api.classifier.LiveLearning;

import java.util.Random;

/**
 * Created by assaad on 13/01/15.
 */
public class Winnow implements LiveLearning, Classifier {
    private double[] weights; //weigths to learn

    private double alpha=2; //(1+alpha) is the parameter

    private int counter=0;
    private Random random = new Random();


    public void setAlpha(double alpha){
        this.alpha=alpha;
    }


    public void  setWeights(double[] w){
        weights=new double[w.length];
        for(int i=0; i<w.length;i++){
            weights[i]=w[i];
        }
    }


    @Override
    public void feed(double[] features, double result) {
        //If the model fits, than great continue

        if(weights==null){
            weights=new double[features.length];
            for(int i=0; i<weights.length;i++){
                weights[i]=1;
            }
        }

        if(calculate(features)==result)
            return;

        //Else update the weights

        if(result==0) {
            for (int i = 0; i < features.length; i++) {
                if(features[i]!=0){
                    weights[i]=weights[i]/alpha;
                }

            }
        }
        else{
            for (int i = 0; i < features.length; i++) {
                if(features[i]!=0){
                    weights[i]=weights[i]*alpha;
                }

            }

        }
    }

    @Override
    public double calculate(double[] features) {
        double result=0;
        for(int i=0; i<features.length;i++){
            result+= weights[i]*features[i];
        }
        if(result>=features.length){
            return 1.0;
        }
        else{
            return 0.0;
        }
    }

    @Override
    public void reset() {
        weights=null;
    }

    @Override
    public void print() {
        for(double d: weights){System.out.println(d);}
    }

    @Override
    public void setFeatureNames(String[] features) {

    }

    @Override
    public void addTrainingSet(Object[] features, int supervisedClass) {

    }

    @Override
    public Byte[] getState() {
        return new Byte[0];
    }

    @Override
    public void setState(Byte[] state) {

    }

    @Override
    public void train() throws Exception {

    }

    @Override
    public void printState() {
        print();
    }

    @Override
    public int evaluate(Object[] features) {
        double[] res = new double[features.length] ;

        for(int i=0;i<features.length;i++){
            res[i]= (Double) features[i];
        }

        if(calculate(res)==0.0)
            return 0;
        else
            return 1;

    }
}
