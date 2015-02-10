package org.kevoree.brain.eurusd;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by assaad on 06/02/15.
 */
public class ForestTraining {

    private ArrayList<LinearTraining> forest = new ArrayList<LinearTraining>();
    private Random rand=new Random();

    private TreeMap<Long, Double> eurUsd;

    public void initialize(int forestSize,Long initTimeStamp, Long finalTimeStamp, int size, double alpha, int iteration, int space,TreeMap<Long, Double> eurUsd){
       this.eurUsd=eurUsd;
        for(int i=0;i<forestSize;i++){
           // Long initTimeStamp, Long finalTimeStamp, int size, double alpha, int iteration, int numPoints, int space,TreeMap<Long, Double> eurUsd){

            LinearTraining lt=new LinearTraining(initTimeStamp,finalTimeStamp,rand.nextInt(size)+10,alpha,iteration,rand.nextInt(space)+60000,eurUsd);
            forest.add(lt);
        }
    }
    public void train(int times){
        for(LinearTraining tree: forest){
            tree.train(times);
        }
    }

    public double predict(long val){
        double res=0;
        for(LinearTraining tree: forest){
            res+=tree.predict(val);
        }
        return res/forest.size();
    }


}
