package org.kevoree.brain.smartgrid.classifier;

/**
 * Created by assaad on 17/09/15.
 */
public abstract class Classifier {
    protected int numOfClasses;

    abstract public void initialize (int numOfClasses) ;

}
