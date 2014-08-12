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
}
