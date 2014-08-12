package org.kevoree.brain.learning;

import org.ejml.simple.SimpleMatrix;
import org.kevoree.brain.api.InputTrainingSet;
import org.kevoree.brain.api.InputVector;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class GaussianKernelLearning extends AbstractLearning {
    @Override
    public void learn(InputTrainingSet its) {

        int dim = its.getFeaturesDimension();
        int m= its.getTrainingSetSize();

        SimpleMatrix x = new SimpleMatrix(m,dim);
        int i=0;
        for(InputVector xi: its.getTraining()){
            x.setRow(i,0,xi.getFeatures());
            i++;
        }












    }

    @Override
    public int predict(InputVector iv) {
        return 0;
    }
}
