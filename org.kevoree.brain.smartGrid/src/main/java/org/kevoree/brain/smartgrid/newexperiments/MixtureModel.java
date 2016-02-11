package org.kevoree.brain.smartgrid.newexperiments;

import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 11/02/16.
 */
public class MixtureModel {
    private ArrayList<double[]> dataset= new ArrayList<double[]>();

    public void insert(ElectricMeasure em, double temp){
        double[] res = new double[6];
        res[0]=em.getIntTime(96);
        res[1]=temp;
        res[2]=em.aplus;
        res[3]=em.aminus;
        res[4]=em.rplus;
        res[5]=em.rminus;
    }


}
