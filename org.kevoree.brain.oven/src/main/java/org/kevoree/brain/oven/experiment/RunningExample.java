package org.kevoree.brain.oven.experiment;

import org.kevoree.brain.oven.util.MWGObject;
import org.kevoree.brain.oven.util.XLXLoader;


/**
 * Created by assaad on 03/10/16.
 */
public class RunningExample {
    public static void main(String[] args){
        String file="/Users/assaad/work/github/data/NeuroPTF parameters_rev7_HO5.xlsx";

        MWGObject model=XLXLoader.loadFile(file);
        model.train();


    }
}
