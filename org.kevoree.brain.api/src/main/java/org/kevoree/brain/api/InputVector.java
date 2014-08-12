package org.kevoree.brain.api;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class InputVector {
    private ArrayList<Double> features;
    private int supervisedClass;


    public double getFeature(int index){
        return features.get(index);
    }

    public void setFeature(int index, double value){
        features.set(index, new Double(value));
    }


    public ArrayList<Double> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Double> features) {
        this.features = features;
    }

    public int getSupervisedClass() {
        return supervisedClass;
    }

    public void setSupervisedClass(int supervisedClass) {
        this.supervisedClass = supervisedClass;
    }
}
