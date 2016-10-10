package org.kevoree.brain.oven.util;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by assaad on 03/10/16.
 */
public class MWGAttribute {
    private String _name;
    private TreeMap<Long, Object> _timeTree;

    public MWGAttribute(String name){
        this._name=name;
        this._timeTree= new TreeMap<Long, Object>();
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

    public Object getValue(long time){
        if(time<=getFirstTime()){
            time=getFirstTime();
            return _timeTree.get(time);
        }
        else if(time>=getLastTime()){
            time=getLastTime();
            return _timeTree.get(time);
        }
        else {
            Long key = _timeTree.floorKey(time);
            return _timeTree.get(key);
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

    public Collection<Object> getValues(){
        return _timeTree.values();
    }



    public double getRate() {
        double d=getLastTime()-getFirstTime();
        return d/getTimePoints();
    }
}
