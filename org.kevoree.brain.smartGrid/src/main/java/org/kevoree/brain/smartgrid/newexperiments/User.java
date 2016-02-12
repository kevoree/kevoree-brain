package org.kevoree.brain.smartgrid.newexperiments;

import org.kevoree.brain.smartgrid.newexperiments.mixture.MixtureModel;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.TreeMap;

/**
 * Created by assaad on 09/02/16.
 */
public class User {
    private static int timeSteps=96;
    private static int elecFeatures=4;
    private static int timediff=900000;

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    private String userId;

    private DiscreteProfiler electricDiscreteProfiler;
    private DiscreteProfiler temperatureDiscreteProfiler;
    private Forecaster predictor;
    private TreeMap<Long,ElectricMeasure> historicalData;

    public User(String userId){
        setUserId(userId);
        electricDiscreteProfiler =new DiscreteProfiler(timeSteps,elecFeatures);
        temperatureDiscreteProfiler =new DiscreteProfiler(timeSteps,1);
        predictor=new Forecaster(timeSteps,elecFeatures);
        historicalData=new TreeMap<Long, ElectricMeasure>();
    }

    public void insert(ElectricMeasure currentCons, TreeMap<Long,Double> temperatureDb){
        long currentTime=currentCons.getTime();

        double currentTemp=temperatureDb.get(temperatureDb.floorKey(currentTime));

        ElectricMeasure prevCons;
        double prevTemp;
        int timeslot=currentCons.getIntTime(timeSteps);




        double[] tval=new double[1];
        tval[0]=currentTemp;
        temperatureDiscreteProfiler.feed(timeslot,tval); //feed the temperature profiler
        electricDiscreteProfiler.feed(timeslot,currentCons.getArrayFeatures());
        historicalData.put(currentCons.getTime(),currentCons);
    }




    public ElectricMeasure getConsumption(long time){
        try{
            return historicalData.get(historicalData.floorKey(time));
        }
        catch (Exception ex){

        }
        return null;
    }

}
