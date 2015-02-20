package org.kevoree.brain.smartgrid;

import java.util.ArrayList;

/**
 * Created by assaad on 20/02/15.
 */
public class MinMaxProfiler extends Profiler {
    @Override
    public void feedMeasure(ElectricMeasure sample) {

    }

    @Override
    public void feedArray(ArrayList<ElectricMeasure> samples) {

    }

    @Override
    public double getProba(ElectricMeasure em) {
        int time=em.getIntTime(timeStep);
       if(checkSample(em)){
           return getPotential(time);

        }
        return 0;
    }

    @Override
    public double[][] getProbabilities(double[] x, double[] y) {
        return new double[0][];
    }

      public double getMatchPercent(MinMaxProfiler newProfile) {
        double result=1;
        double[] d=new double[4];
        for(int time=0;time <timeStep;time++){
            d[0]=Math.min(apmax[time], newProfile.apmax[time])-Math.max(apmin[time],newProfile.apmin[time]);
            d[1]=Math.min(ammax[time], newProfile.ammax[time])-Math.max(ammin[time],newProfile.ammin[time]);
            d[2]=Math.min(rpmax[time], newProfile.rpmax[time])-Math.max(rpmin[time],newProfile.rpmin[time]);
            d[3]=Math.min(rmmax[time], newProfile.rmmax[time])-Math.max(rmmin[time],newProfile.rmmin[time]);
            ArrayList<Integer> dimensions = new ArrayList<Integer>();
            double proba=1;
            if(apmax[time]!=0){
                dimensions.add(0);
                if(Math.max(apmax[time] - apmin[time],newProfile.apmax[time] - newProfile.apmin[time])!=0) {
                    d[0] = d[0] / Math.max(apmax[time] - apmin[time],newProfile.apmax[time] - newProfile.apmin[time]);
                }
                else{
                    if(apmax[time]==newProfile.apmax[time]){
                        d[0]=1;
                    }
                    else {
                        d[0]=0;
                    }
                }
            }
            if(ammax[time]!=0){
                dimensions.add(1);
                if(Math.max(ammax[time] - ammin[time],newProfile.ammax[time] - newProfile.ammin[time])!=0) {
                    d[1] = d[1] / Math.max(ammax[time] - ammin[time],newProfile.ammax[time] - newProfile.ammin[time]);
                }
                else{
                    if(ammax[time]==newProfile.ammax[time]){
                        d[1]=1;
                    }
                    else {
                        d[1]=0;
                    }
                }
            }
            if(rpmax[time]!=0){
                dimensions.add(2);
                if(Math.max(rpmax[time] - rpmin[time],newProfile.rpmax[time] - newProfile.rpmin[time])!=0) {
                    d[2] = d[2] / Math.max(rpmax[time] - rpmin[time],newProfile.rpmax[time] - newProfile.rpmin[time]);
                }
                else{
                    if(rpmax[time]==newProfile.rpmax[time]){
                        d[2]=1;
                    }
                    else {
                        d[2]=0;
                    }
                }
            }
            if(rmmax[time]!=0){
                dimensions.add(3);
                if(Math.max(rmmax[time] - rmmin[time],newProfile.rmmax[time] - newProfile.rmmin[time])!=0) {
                    d[3] = d[3] / Math.max(rmmax[time] - rmmin[time],newProfile.rmmax[time] - newProfile.rmmin[time]);
                }
                else{
                    if(rmmax[time]==newProfile.rmmax[time]){
                        d[3]=1;
                    }
                    else {
                        d[3]=0;
                    }
                }
            }

            for(Integer dd: dimensions){
                if(d[dd]<0){
                    d[dd]=0;
                }
                proba=proba*d[dd];
            }
            result=result*proba;

        }
        return result;
    }
}
