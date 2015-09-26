package org.kevoree.brain.learning.livelearning;

import org.kevoree.brain.api.classifier.LiveLearning;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by Ace Shooting on 8/19/2014.
 */
public class LinearRegressionLive implements LiveLearning {

    private int featuresize;
    private double[] weights; //weigths to learn
    private double const1=1;
    private double const2=200000;
    private double lasterror=0;
    private int iteration=100;
    private double alpha = const1/const2;//learning rate
    private boolean enableAlphaDegrade=false;

    private int counter=0;
    private Random random = new Random();

    public LinearRegressionLive(int featureSize){
        setSize(featureSize);
    }
    public void setSize(int size){
        featuresize= size;
        weights=new double[featuresize+1];
        for(int i=0; i<featuresize+1;i++){
            weights[i]=random.nextDouble()-0.5;
        }
    }

    public void setAlpha(double alpha){
        this.alpha=alpha;
        const1=alpha;
        const2=1;
    }

    public void setIteration(int iter){
        this.iteration=iter;
    }

    public void  setWeights(double[] w){
        featuresize= w.length-1;
        weights=new double[featuresize+1];
        for(int i=0; i<featuresize+1;i++){
            weights[i]=w[i];
        }
    }

    public double[] getWeights(){
        double[] val = new double[weights.length];
        for(int i=0; i<weights.length;i++){
            val[i]=weights[i];
        }
        return val;
    }


    public void setEnableAlphaDegrade(boolean value){
        this.enableAlphaDegrade=value;
    }


    @Override
    public void feed(double[] features, double result) {
        for(int j=0; j<iteration;j++) {
            double h = calculate(features);
            if(result!=0){
                lasterror=Math.abs((h-result)*100/result);
            }
            double err = -alpha * (h - result);
            for (int i = 0; i < featuresize; i++) {
                weights[i] = weights[i] + err * features[i];
            }
            weights[featuresize] = weights[featuresize] + err;
        }
        counter++;

        if(enableAlphaDegrade)
            alpha = const1/(counter+const2);

    }

    @Override
    public double calculate(double[] feature) {
        double result=0;
        for(int i=0;i<feature.length;i++){
            result+=weights[i]*feature[i];
        }
        result+=weights[featuresize];
        return result;
    }

    @Override
    public void reset() {

    }

    @Override
    public void print() {
        for(double d: weights){System.out.println(d);}
    }


    public double getLasterror() {
        return lasterror;
    }

    public void print(DecimalFormat df) {
        for(double d: weights){System.out.print(df.format(d)+" , ");}

    }

    public double getAlpha() {
        return alpha;
    }
}
