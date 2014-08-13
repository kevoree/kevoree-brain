package org.kevoree.brain.api.classifier;

/**
 * Created by duke on 8/12/14.
 */
public interface Classifier {

    public void setFeatureNames(String[] features);

    public void addTrainingSet(Object[] features, int supervisedClass);
    public void addCrossValSet(Object[] features, int supervisedClass);
    public void addTestSet(Object[] features, int supervisedClass);

    public void update();

    public void print();
    public void testAccuracy();

    public int eval(Object[] features);

}
