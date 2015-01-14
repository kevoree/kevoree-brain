package org.kevoree.brain.learning.livelearning;

import java.util.Random;

/**
 * Created by assaad on 13/01/15.
 */
public class WinnowBoolean {
    private double[] weights; //weigths to learn
    private double alpha=2; // the reward parameter
    private double beta = 2; //the penalty parameter


    public void setBeta(double beta){
        this.beta =beta;
    }

    public void setAlpha(double alpha){
        this.alpha=alpha;
    }


    public void  setWeights(double[] w){
        weights=new double[w.length];
        for(int i=0; i<w.length;i++){
            weights[i]=w[i];
        }
    }


    public void feed(boolean[] features, boolean result) {
        //If the model fits, than great continue

        if(weights==null){
            weights=new double[features.length];
            for(int i=0; i<weights.length;i++){
                weights[i]=2;
            }
        }

        if(calculate(features)==result)
            return;

        //Else update the weights

        if(result==false) {
            for (int i = 0; i < features.length; i++) {
                if(features[i]){
                    weights[i]=weights[i]/beta;
                }

            }
        }
        else{
            for (int i = 0; i < features.length; i++) {
                if(features[i]){
                    weights[i]=weights[i]*alpha;
                }

            }

        }
    }

    public boolean calculate(boolean[] features) {
        double result=0;
        for(int i=0; i<features.length;i++){
            if(features[i]) {
                result += weights[i];
            }
        }
        if(result>=features.length){
            return true;
        }
        else{
            return false;
        }
    }


    public void reset() {
        weights=null;
    }


    public void print() {
        for(double d: weights){System.out.println(d);}
    }


/*    public int evaluate(Object[] features) {
        boolean[] res = new boolean[features.length] ;

        for(int i=0;i<features.length;i++){
            res[i]= (Boolean) features[i];
        }

        if(calculate(res)==false)
            return 0;
        else
            return 1;

    }*/
}
