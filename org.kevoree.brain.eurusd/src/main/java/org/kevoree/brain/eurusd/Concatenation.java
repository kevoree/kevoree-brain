package org.kevoree.brain.eurusd;

import org.kevoree.brain.util.PolynomialCompressor;
import org.kevoree.brain.util.Prioritization;
import org.kevoree.brain.util.TimeStamp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by assaa_000 on 07/10/2014.
 */
public class Concatenation {

    public static void main(String[] args) {

       /* Date d=new Date();
        d.setTime(Long.parseLong("991949460000"));*/

        long starttime;
        long endtime;
        double res;

        long timeOrigine = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        int degradeFactor = 60000;
        double toleratedError = 0.0001;
        int maxDegree = 20;

        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();


        starttime = System.nanoTime();
       // String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\EURUSD.csv";
        String csvFile =  "/Users/assaad/work/github/eurusd/Eur USD database/EURUSD_";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String dateSplit = "\\.";
        String hourSplit =":";


        try {
            FileWriter outFile = new FileWriter("/Users/assaad/work/github/eurusd/newEurUsd.csv");
            PrintWriter out = new PrintWriter(outFile);
            for(int year=2000;year<2016;year++) {

                br = new BufferedReader(new FileReader(csvFile + year + ".csv"));
                    while ((line = br.readLine()) != null) {
                    // use comma as separator 2000.05.30,17:35
                    String[] values = line.split(cvsSplitBy);
                    Long timestamp = TimeStamp.getTimeStamp(values[0]);
                    Double val = Double.parseDouble(values[1]);
                    eurUsd.put(timestamp, val);
                    out.println(timestamp + "," + val);
                }
                System.out.println();
            }
           out.close();
           outFile.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000000);
        System.out.println("Loaded :" + eurUsd.size() + " values in " + res + " s!");

    }
}



