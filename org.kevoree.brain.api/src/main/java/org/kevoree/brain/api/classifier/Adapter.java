package org.kevoree.brain.api.classifier;

/**
 * Created by assaa_000 on 8/20/2014.
 */
public interface Adapter {
    public void feed(double[] values);
    public void setLiveLearning(LiveLearning c);
    public void reset();
    public LiveLearning getLiveLearning();
}
