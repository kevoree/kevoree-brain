package org.kevoree.brain.eurusd.apps;

import org.kevoree.brain.eurusd.learners.Profiler;
import org.kevoree.brain.eurusd.learners.TimeProfiler;
import org.kevoree.brain.eurusd.tools.Loader;
import org.kevoree.brain.util.TimeStamp;

import java.text.DecimalFormat;
import java.util.TreeMap;

/**
 * Created by assaad on 25/09/15.
 */
public class TrendCalculation {
    private static long YEAR= 31536000000l;
    private static long MONTH= 2628000000l;
    private static long WEEK= 604800000l;
    private static long DAY= 86400000l;
    private static long HOUR= 3600000;
    private static long MIN= 60000;
    private static long SECOND=1000;

    public static void main(String[] arg){
        int degradeFactor = 60000;
        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();
        Loader.load(eurUsd);
        Long initTimeStamp = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        Long finalTimeStamp = eurUsd.floorKey(TimeStamp.getTimeStamp(2050, 1, 1, 1, 1));


        TimeProfiler yearly = new TimeProfiler(YEAR,365);
        TimeProfiler monthly= new TimeProfiler(MONTH,30);
        TimeProfiler weekly = new TimeProfiler(WEEK,7);
        TimeProfiler daily = new TimeProfiler(DAY,24);

        Profiler all= new Profiler();



        try {
            long count=0;
            for(long l=initTimeStamp;l<=finalTimeStamp;l+=degradeFactor){
                long ld= eurUsd.floorKey(l);
                double v= eurUsd.get(ld);
                yearly.feed(l,v);
                monthly.feed(l,v);
                weekly.feed(l,v);
                daily.feed(l,v);
                all.feed(v);
                count++;
            }

            System.out.println("Generated: " + count);
            print("Yearly: ", yearly.getAvg());
            print("Monthly: " , monthly.getAvg());
            print("Weekly: " ,weekly.getAvg());
            print("Daily: " , daily.getAvg());
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }



    private static DecimalFormat formatter = new DecimalFormat("#0.00");

    public static void print(String s, double[] val){
        System.out.print(s);
        for(double v: val){
            System.out.print(formatter.format(v)+" , ");
        }
        System.out.println();
    }
}
