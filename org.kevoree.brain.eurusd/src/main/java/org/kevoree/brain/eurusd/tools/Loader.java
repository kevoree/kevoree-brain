package org.kevoree.brain.eurusd.tools;

import org.kevoree.brain.eurusd.learners.Profiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeMap;

/**
 * Created by assaa_000 on 25/09/2015.
 */
public class Loader {
    public static Profiler load(TreeMap<Long, Double> eurUsd){
        Profiler profiler = new Profiler();
        long starttime;
        long endtime;
        double res;


        String csvFile = "/Users/assaad/work/github/eurusd/newEurUsd.csv";
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
        System.out.println("Min: " + profiler.getMin() + " Max: " + profiler.getMax() + " Avg: " + profiler.getAverage());



        return profiler;

    }


}
