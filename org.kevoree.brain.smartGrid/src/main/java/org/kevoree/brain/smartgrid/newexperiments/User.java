package org.kevoree.brain.smartgrid.newexperiments;

import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.TreeMap;

/**
 * Created by assaad on 09/02/16.
 */
public class User {
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    private String userId;

    private Profiler profiler;
    private Predictor predictor;
    private TreeMap<Long,ElectricMeasure> historicalData;

    public User(String userId){
        setUserId(userId);
        profiler=new Profiler();
        predictor=new Predictor();
        historicalData=new TreeMap<Long, ElectricMeasure>();
    }

}
