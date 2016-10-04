package org.kevoree.brain.oven.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 03/10/16.
 */
public class MWGObject {
    private ArrayList<MWGAttribute> _attributes=new ArrayList<MWGAttribute>();
    private HashMap<String,Integer> dictionary=new HashMap<String, Integer>();

    public void add(MWGAttribute att){
        _attributes.add(att);
        dictionary.put(att.getName(),_attributes.size()-1);
    }

    public ArrayList<MWGAttribute> getAttributes(){
        return _attributes;
    }

    public void train() {
        System.out.println("");
        System.out.println("name, min, max, avg, first time, last time, timepoints, rate");
        for(int i=0;i<_attributes.size();i++){
            _attributes.get(i).train();
        }
    }
}
