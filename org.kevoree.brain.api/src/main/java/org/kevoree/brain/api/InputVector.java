package org.kevoree.brain.api;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class InputVector {
    private double[] features;
    private int supervisedClass;


    public int getFeaturesDimension(){
        if (features!=null)
            return features.length;

        return 0;
    }



    public int getSupervisedClass() {
        return supervisedClass;
    }

    public void setSupervisedClass(int supervisedClass) {
        this.supervisedClass = supervisedClass;
    }

    public void setFeatures(double[] features) {
        this.features = features;
    }

    public double[] getFeatures() {
       return features;
    }
}
