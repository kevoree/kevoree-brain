package org.kevoree.brain.smartgrid.classifier;

import org.kevoree.brain.smartgrid.Profilers.EuclideanDistanceProfiler;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;
import org.kevoree.brain.smartgrid.util.SolutionComparator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 17/09/15.
 */
public class EuclideanDistanceClassifier extends Classifier{
    EuclideanDistanceProfiler[] profilers;

    @Override
    public void initialize (int numOfClasses) {
        this.numOfClasses=numOfClasses;
        profilers=new EuclideanDistanceProfiler[numOfClasses];
        for(int i=0;i<numOfClasses;i++){
            profilers[i]=new EuclideanDistanceProfiler();
        }
    }

    public void trainArray(ArrayList<ElectricMeasure> electricMeasure, int classId){
        profilers[classId].feedArray(electricMeasure);
    }

    public  ArrayList<SolutionComparator> classifyArray(ArrayList<ElectricMeasure> electricMeasure,  HashMap<Integer,String> dictionary){
        ArrayList<SolutionComparator> results = new ArrayList<SolutionComparator>(numOfClasses);

        EuclideanDistanceProfiler temp = new EuclideanDistanceProfiler();
        temp.feedArray(electricMeasure);

        for(int i=0;i<numOfClasses;i++){
            SolutionComparator ss=new SolutionComparator();
            ss.id=dictionary.get(i);
            ss.isDistance=true;
            ss.score=temp.getDistance(profilers[i]);
            results.add(i,ss);
        }
        return results;
    }

}
