package org.kevoree.brain.learning.livelearning;

import org.kevoree.brain.api.classifier.Classifier;
import org.kevoree.brain.api.classifier.LiveLearning;

/**
 * Created by assaad on 14/01/15.
 */
public class Perceptron implements LiveLearning, Classifier {

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public int getMaxIteration() {
        return maxIteration;
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    private double[] weights;
    private double bias;
    private int maxIteration=200;




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
    public int evaluate(Object[] features) {
        double res=0;

        for(int i=0; i<features.length;i++){
            res = res + weights[i]*((Double)features[i]);
        }
        res = res + bias;
        if(res>=0){
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public void feed(double[] features, double result) {
        if(weights==null){
            weights=new double[features.length];
            bias=0;
        }

        if(result==0){
            result=-1;
        }

        for(int i=0; i<maxIteration; i++){
            double a= calculate(features);
            if(a==0){
                a=-1;
            }
            if(a*result<=0){
                for(int j=0; j<features.length;j++){
                    weights[j]=weights[j]+result*features[j];
                }
                bias=bias+result;
            }
        }
    }

    @Override
    public double calculate(double[] features) {

        double res=0;

        for(int i=0; i<features.length;i++){
            res = res + weights[i]*features[i];
        }
        res = res + bias;
        if(res>=0){
            return 1.0;
        }
        else {
            return 0;
        }
    }

    @Override
    public void reset() {
        weights=null;

    }

    @Override
    public void print() {
        for(double d: weights){System.out.println(d);}
        System.out.println("bias: "+bias);

    }
}
