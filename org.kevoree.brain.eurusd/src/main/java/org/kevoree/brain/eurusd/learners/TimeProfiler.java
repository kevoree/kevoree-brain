package org.kevoree.brain.eurusd.learners;

/**
 * Created by assaad on 06/02/15.
 */
public class TimeProfiler {
    private long duration;
    private int timeSlots;
    private long slotSize;

    private Profiler[] subProfiles;

    public TimeProfiler(long duration, int timeSlots){
        this.duration=duration;
        this.timeSlots=timeSlots;
        subProfiles= new Profiler[timeSlots];
        for(int i=0;i<timeSlots;i++){
            subProfiles[i]=new Profiler();
        }
        slotSize=duration/timeSlots;
    }

    private int getSlot(long time){
        return ((int)((time%duration)/slotSize));
    }

    public void feed(long time, double value){
        subProfiles[getSlot(time)].feed(value);
    }

    public double getAvg(long time){
        return subProfiles[getSlot(time)].getAvg();
    }

    public double getMax(long time){
        return subProfiles[getSlot(time)].getMin();
    }

    public double getMin(long time){
        return subProfiles[getSlot(time)].getMax();
    }

    public double[] getAvg(){
        double[] avg=new double[timeSlots];
        for(int i=0;i<timeSlots;i++){
            avg[i]=subProfiles[i].getAvg();
        }
        return avg;
    }

    public double[] getMin(){
        double[] min=new double[timeSlots];
        for(int i=0;i<timeSlots;i++){
            min[i]=subProfiles[i].getMin();
        }
        return min;
    }

    public double[] getMax(){
        double[] max=new double[timeSlots];
        for(int i=0;i<timeSlots;i++){
            max[i]=subProfiles[i].getMax();
        }
        return max;
    }







}
