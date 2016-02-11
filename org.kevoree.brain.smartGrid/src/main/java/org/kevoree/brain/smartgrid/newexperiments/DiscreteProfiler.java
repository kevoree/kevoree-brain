package org.kevoree.brain.smartgrid.newexperiments;

import org.kevoree.brain.smartgrid.newexperiments.mixture.Gaussian;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;

/**
 * Created by assaad on 19/02/15.
 */
public class DiscreteProfiler {


    private int features;
    private int timeSteps;
    private Gaussian[] gaussians;

    private int overallTotal;


    private void init(){
        gaussians=new Gaussian[timeSteps];
        for(int i=0;i<timeSteps;i++){
            gaussians[i]=new Gaussian(features);
        }

    }

    public ElectricMeasure getAvgElectricMeasure(int timeSlot){
        ElectricMeasure em = new ElectricMeasure();
        for(int i=0;i<features;i++){
            em.setFeature(i,getAvg(timeSlot,i));
        }
        return em;
    }

    public double getAvg(int timeslot, int feature){
        return gaussians[timeslot].getAvg(feature);
    }

    public int getTotal(int timeslot){
        return gaussians[timeslot].getTotal();
    }

    public DiscreteProfiler(int timeSteps, int features){
        this.timeSteps=timeSteps;
        this.features=features;
        init();
    }


    public void feed(int time, double[] feat){
        gaussians[time].feed(feat);
        overallTotal++;
    }


}
