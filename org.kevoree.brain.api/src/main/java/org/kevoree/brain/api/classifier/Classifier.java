package org.kevoree.brain.api.classifier;

/**
 * Created by duke on 8/12/14.
 */
public interface Classifier {

    public void setFeatureNames(String[] features);

    public void addTrainingSet(Object[] features, int supervisedClass);

    public Byte[] getState();
    public void setState(Byte[] state);
    public void train() throws Exception;

    public void printState();

    public int evaluate(Object[] features);

}
