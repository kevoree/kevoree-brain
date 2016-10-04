package org.kevoree.brain.oven.util;

import org.kevoree.brain.smartgrid.newexperiments.mixture.Gaussian;

import java.text.DecimalFormat;
import java.util.TreeMap;

/**
 * Created by assaad on 03/10/16.
 */
public class MWGAttribute {
    private static DecimalFormat df=new DecimalFormat("#.#####");
    private String _name;
    private TreeMap<Long, Double> _timeTree;
    private Gaussian _profile=null;

    public MWGAttribute(String name){
        this._name=name;
        this._timeTree= new TreeMap<Long, Double>();
    }

    public String getName(){
        return _name;
    }

    public void setName(String name){
        this._name=name;
    }

    public void setValue(long time, double value){
        _timeTree.put(time,value);
    }

    public Double getValue(long time){
        Long key=_timeTree.floorKey(time);
        if(key!=null){
            return _timeTree.get(key);
        }
        else {
            return null;
        }
    }

    public Long getLastTime(){
        return _timeTree.lastKey();
    }

    public Long getFirstTime(){
        return _timeTree.firstKey();
    }

    public int getTimePoints(){
        return _timeTree.size();
    }


    public void train(){
        _profile=new Gaussian();
        double[] vals=new double[1];
        for(double d: _timeTree.values()){
            vals[0]=d;
            _profile.feed(vals);
        }
        System.out.println(_name+", "+df.format(_profile.getMin()[0])+", "+df.format(_profile.getMax()[0])+", "+df.format(_profile.getAvg()[0])+", "+getFirstTime()+", "+getLastTime()+", "+getTimePoints()+", "+getRate());
    }


    public double getRate() {
        double d=getLastTime()-getFirstTime();
        return d/getTimePoints();
    }
}
