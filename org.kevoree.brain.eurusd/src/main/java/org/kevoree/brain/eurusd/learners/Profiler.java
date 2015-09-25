package org.kevoree.brain.eurusd.learners;

/**
 * Created by assaad on 06/02/15.
 */
public class Profiler {
    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getCount() {
        return count;
    }

    public double getAverage(){
        return sum/count;
    }

    private double min;
    private double max;
    private int count=0;
    private double sum=0;
    private double sumSquare=0;
    private int precision=10000;

    public void feed(double value){
        if(count==0){
            min=value;
            max=value;
        }
        else {
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
        sum+=value;
        sumSquare+=value*value;
        count++;
    }

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
