package org.kevoree.brain.eurusd;

import org.kevoree.brain.util.TimeStamp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by assaad on 25/09/15.
 */
public class DateReplace {
    public static void main(String[] arg){
        String csvFile =  "/Users/assaad/work/github/eurusd/Eur USD database/EURUSD_2015.csv";
        String output =  "/Users/assaad/work/github/eurusd/EURUSD_2015.csv";

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            PrintWriter out = new PrintWriter(output);
                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) {
                    // use comma as separator 2000.05.30,17:35
                    String[] values = line.split(cvsSplitBy);
                    String[] dates=values[0].split(" ");
                    String[] d1= dates[0].split("\\.");
                    String[] d2= dates[1].split(":");
                    out.println(d1[2]+"."+d1[1]+"."+d1[0]+" "+d2[0]+":"+d2[1]+","+values[1]+","+values[2]+","+values[3]+","+values[4]+","+values[5]);

                }
                System.out.println();

            out.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
