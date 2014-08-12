package org.kevoree.brain.api;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class InputTrainingSet {
    private ArrayList<InputVector> training;

    public ArrayList<InputVector> getTraining() {
        return training;
    }

    public void setTraining(ArrayList<InputVector> training) {
        this.training = training;
    }

    public int getFeaturesDimension(){
        if (training!=null)
            return training.get(0).getFeaturesDimension();

        return 0;
    }

    public int getTrainingSetSize(){
        return training.size();
    }
}
