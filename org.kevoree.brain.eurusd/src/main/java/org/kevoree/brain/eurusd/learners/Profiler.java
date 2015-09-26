package org.kevoree.brain.eurusd.learners;

/**
 * Created by assaad on 06/02/15.
 */
public class Profiler {
    private static int precision=10000;
    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getCount() {
        return count;
    }

    public double getAvg(){
        return sum/count;
    }

    private double min;
    private double max;
    private int count=0;
    private double sum=0;


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
        count++;
    }

    public Profiler copy(){
        Profiler newProfile = new Profiler();
        newProfile.min=min;
        newProfile.max=max;
        newProfile.count=count;
        newProfile.sum=sum;
        return newProfile;
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
