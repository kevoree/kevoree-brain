package org.kevoree.brain.smartgrid.newexperiments;

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

    private Profiler electricProfiler;
    private Profiler temperatureProfiler;
    private Forecaster predictor;
    private TreeMap<Long,ElectricMeasure> historicalData;
    private MixtureModel mixtureModel;

    public User(String userId){
        setUserId(userId);
        electricProfiler=new Profiler(timeSteps,elecFeatures);
        temperatureProfiler=new Profiler(timeSteps,1);
        predictor=new Forecaster(timeSteps,elecFeatures);
        historicalData=new TreeMap<Long, ElectricMeasure>();
        mixtureModel=new MixtureModel();
    }

    public void insert(ElectricMeasure currentCons, TreeMap<Long,Double> temperatureDb){
        long currentTime=currentCons.getTime();

        double currentTemp=temperatureDb.get(temperatureDb.floorKey(currentTime));

        ElectricMeasure prevCons;
        double prevTemp;
        int timeslot=currentCons.getIntTime(timeSteps);

        mixtureModel.insert(currentCons,currentTemp);


       /* try {
            prevCons=historicalData.get(historicalData.floorKey(currentCons.getTime()-1));
            prevTemp=temperatureDb.get(temperatureDb.floorKey(prevCons.getTime()));
            if(currentCons.getTime()-prevCons.getTime()==timediff){
                predictor.feed(timeslot,electricProfiler,temperatureProfiler,prevCons,currentCons, prevTemp,currentTemp);
            }
        }
        catch (Exception e){

        }*/

        double[] tval=new double[1];
        tval[0]=currentTemp;
        temperatureProfiler.feed(timeslot,tval); //feed the temperature profiler
        electricProfiler.feed(timeslot,currentCons.getArrayFeatures());
        historicalData.put(currentCons.getTime(),currentCons);
    }

  /*  public ElectricMeasure predict(int timeSlot){
        int prevSlot=(timeSlot-1);
        if(prevSlot<0){
            prevSlot+=timeSteps;
        }

        ElectricMeasure prev=electricProfiler.getAvgElectricMeasure(prevSlot);
        ElectricMeasure now=electricProfiler.getAvgElectricMeasure(timeSlot);
       // prev.print("Previous");
        now.print("Now");


        ElectricMeasure predict = predictor.predict(timeSlot,electricProfiler,temperatureProfiler,prev,temperatureProfiler.getAvg(prevSlot,0)+0.1,temperatureProfiler.getAvg(timeSlot,0)+0.2);

        predict.print("Predict");
        System.out.println();
        return predict;
    }*/

    public ElectricMeasure getConsumption(long time){
        try{
            return historicalData.get(historicalData.floorKey(time));
        }
        catch (Exception ex){

        }
        return null;
    }

}
