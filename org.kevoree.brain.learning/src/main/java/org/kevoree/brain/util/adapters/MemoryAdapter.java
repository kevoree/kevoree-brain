package org.kevoree.brain.util.adapters;

import org.kevoree.brain.api.classifier.Adapter;
import org.kevoree.brain.api.classifier.Classifier;
import org.kevoree.brain.api.classifier.LiveLearning;

/**
 * Created by assaa_000 on 8/20/2014.
 */
public class MemoryAdapter implements Adapter{
    private int size;
    private double[] prevValues;

    private int counter=0;
    private LiveLearning liveLearning;



    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;

        reset();
    }

    @Override
    public void feed(double[] values) {
        if(counter<size){
            prevValues[counter]=values[0];
            counter++;
        }
        else{
            liveLearning.feed(prevValues,values[0]);
            for(int i=0;i<size-1;i++)
                prevValues[i]=prevValues[i+1];
            prevValues[size-1]=values[0];
        }

    }



    @Override
    public void reset() {
        counter=0;
        prevValues=new double[size];

    }


    @Override
    public LiveLearning getLiveLearning() {
        return liveLearning;
    }

    @Override
    public void setLiveLearning(LiveLearning liveLearning) {
        this.liveLearning = liveLearning;
    }
}
