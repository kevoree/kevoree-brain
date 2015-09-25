package org.kevoree.brain.eurusd.tools;

import org.kevoree.brain.eurusd.learners.Profiler;
import org.kevoree.brain.util.TimeStamp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by assaa_000 on 25/09/2015.
 */
public class Loader {
    public static Profiler load(TreeMap<Long, Double> eurUsd, String csvFile){
        Profiler profiler = new Profiler();
        long starttime;
        long endtime;
        double res;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        String ll="";

        int count=0;
        starttime = System.nanoTime();
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                count++;
                // use comma as separator 2000.05.30,17:35
                String[] values = line.split(cvsSplitBy);
                Long timestamp = Long.parseLong(values[0]);
                Double val = Double.parseDouble(values[1]);
                profiler.feed(val);
                eurUsd.put(timestamp, val);
                ll=line;
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000000);
        System.out.println("Loaded: " + eurUsd.size() + " values in " + res + " s!");
        System.out.println("Min: " + profiler.getMin() + " Max: " + profiler.getMax() + " Avg: " + profiler.getAvg());



        return profiler;

    }


    public static  double[] loadContinuous(long interval, String csvFile){
        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();
        if(interval<60000){
            interval=60000;
        }
        Profiler profiler = Loader.load(eurUsd, csvFile);
        Long initTimeStamp = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        Long finalTimeStamp = eurUsd.floorKey(TimeStamp.getTimeStamp(2050, 1, 1, 1, 1));
        double[] vals = new double[((int)((finalTimeStamp-initTimeStamp)/interval))+1];
        Calendar c= Calendar.getInstance();

        try {
            int i=0;
            for(long l=initTimeStamp;l<=finalTimeStamp;l+=interval){
                long ld= eurUsd.floorKey(l);
                c.setTimeInMillis(l);
                vals[i]= eurUsd.get(ld);
                i++;
            }
            System.out.println("Generated: "+i);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return vals;
    }

}
