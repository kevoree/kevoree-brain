package org.kevoree.brain.smartgrid.newexperiments.mixture;

/**
 * Created by assaad on 11/02/16.
 */
public abstract class Component {
    protected double weight;
    public double getWeight(){
        return weight;
    }
    public void setWeight(double weight){
        this.weight=weight;
    }

    public void incWeight(){
        weight+=1;
    }

    public void multWeight(double alpha){
        weight=weight*alpha;
    }

    public abstract boolean checkInside(double[] features,double[] err);

    public abstract void feed(double[] features);
    public abstract double[] getAvg();
    public abstract double[][] getCovariance(double[] means);


}
