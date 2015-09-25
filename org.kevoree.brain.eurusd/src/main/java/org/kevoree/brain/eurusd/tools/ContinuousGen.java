package org.kevoree.brain.eurusd.tools;

import org.kevoree.brain.eurusd.learners.Profiler;
import org.kevoree.brain.eurusd.apps.Analyzer;
import org.kevoree.brain.util.TimeStamp;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by assaad on 25/09/15.
 */
public class ContinuousGen {
    public static void main(String[] arg){
        int degradeFactor = 3600000;

        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();
        Profiler profiler = Analyzer.load(eurUsd);
        Long initTimeStamp = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        Long finalTimeStamp = eurUsd.floorKey(TimeStamp.getTimeStamp(2050, 1, 1, 1, 1));

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar c= Calendar.getInstance();

        try {
            long count=0;
            FileWriter outFile = new FileWriter("/Users/assaad/work/github/eurusd/EurUsdContHour.csv");
            PrintWriter out = new PrintWriter(outFile);
            for(long l=initTimeStamp;l<=finalTimeStamp;l+=degradeFactor){
                long ld= eurUsd.floorKey(l);
                c.setTimeInMillis(l);
                double v= eurUsd.get(ld);
                out.println(df.format(c.getTime())+ "," + v);
                count++;
            }

            System.out.println("Generated: "+count);
            out.close();
            outFile.close();

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


    }
}
