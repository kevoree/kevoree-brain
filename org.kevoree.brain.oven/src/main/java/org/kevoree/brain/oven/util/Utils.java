package org.kevoree.brain.oven.util;

import org.kevoree.brain.smartgrid.newexperiments.mixture.Gaussian;

import java.text.DecimalFormat;

/**
 * Created by assaad on 10/10/16.
 */
public class Utils {

    private static DecimalFormat df=new DecimalFormat("#.#####");
    public static void trainAttribute(MWGAttribute attribute){

        Gaussian _profile=null;
        _profile=new Gaussian();
        double[] vals=new double[1];
        for(Object d: attribute.getValues()){
            vals[0]=(Double) d;
            _profile.feed(vals);
        }
        System.out.println(attribute.getName()+", "+df.format(_profile.getMin()[0])+", "+df.format(_profile.getMax()[0])+", "+df.format(_profile.getAvg()[0])+", "+attribute.getFirstTime()+", "+attribute.getLastTime()+", "+attribute.getTimePoints()+", "+attribute.getRate());
    }

    public static void trainObject(MWGObject obj) {
        System.out.println("");

        System.out.println("name, min, max, avg, first time, last time, timepoints, rate");
        for (int i = 0; i < obj.getAttributes().size(); i++) {
            trainAttribute(obj.getAttributes().get(i));
        }
        obj.calculateTimeStats();
        System.out.println("");
     }

}
