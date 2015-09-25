package org.kevoree.brain.eurusd.tools;

import org.kevoree.brain.util.TimeStamp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.TreeMap;

/**
 * Created by assaa_000 on 07/10/2014.
 */
public class Concatenation {

    public static void main(String[] args) {
        String loc="/Users/assaad/work/github/eurusd/";

        loc="C:\\Users\\assaa_000\\Documents\\GitHub\\eurusd\\";

       /* Date d=new Date();
        d.setTime(Long.parseLong("991949460000"));*/

        long starttime;
        long endtime;
        double res;
        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();


        starttime = System.nanoTime();
        String csvFile =  loc+"Eur USD database/EURUSD_";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            FileWriter outFile = new FileWriter(loc+"newEurUsddate.csv");
            FileWriter outFile2 = new FileWriter(loc+"newEurUsd.csv");
            PrintWriter out = new PrintWriter(outFile);
            PrintWriter out2 = new PrintWriter(outFile2);
            for (int year = 2000; year < 2016; year++) {

                br = new BufferedReader(new FileReader(csvFile + year + ".csv"));
                while ((line = br.readLine()) != null) {
                    // use comma as separator 2000.05.30,17:35
                    String[] values = line.split(cvsSplitBy);
                    Long timestamp = TimeStamp.getTimeStamp(values[0]);
                    Double val = Double.parseDouble(values[1]);
                    eurUsd.put(timestamp, val);
                    out2.println(timestamp + "," + val);
                    out.println(values[0] + "," + val);

                }
                System.out.println();
            }
            out.close();
            out2.close();
            outFile2.close();
            outFile.close();

        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000000);
        System.out.println("Loaded :" + eurUsd.size() + " values in " + res + " s!");

    }
}



