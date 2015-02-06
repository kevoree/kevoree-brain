package org.kevoree.brain.eurusd;

/**
 * Created by assaad on 06/02/15.
 */
public class Range {
    public double min;
    public double max;
    public int precision=10000;


    public int getMaxInt(){
        return (int) ((max-min)*precision+1);
    }

    public int position(double val){
        return (int)((val-min)*precision);
    }

    public double value(int position){
        double pos = (double)position;
        pos=pos/precision;
        return min+pos;
    }
}
