package org.kevoree.brain.api.classifier;

/**
 * Created by duke on 8/12/14.
 */
public interface Classifier {

    public void setFeatureNames(String[] features);

    public void setTrainingSize(int size);

    public void train(Object[] features, int supervisedClass);

    public void update();

    public int eval(Object[] features);

}
