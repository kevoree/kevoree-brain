package org.kevoree.brain.learning;

import org.kevoree.brain.api.InputTrainingSet;
import org.kevoree.brain.api.InputVector;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public abstract class AbstractLearning {
    public abstract void learn( InputTrainingSet its);

    public abstract int predict (InputVector iv);

}
