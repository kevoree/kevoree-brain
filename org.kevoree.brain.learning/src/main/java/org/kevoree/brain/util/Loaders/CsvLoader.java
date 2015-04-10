package org.kevoree.brain.util.Loaders;

import org.kevoree.brain.util.TimeStamp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.TreeMap;

/**
 * Created by assaad on 10/04/15.
 */
public class CsvLoader {
    public static TreeMap<Long,Double> load(String csvFile){
        TreeMap<Long,Double> treeMap=new TreeMap<Long, Double>();

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
             br = new BufferedReader(new FileReader(csvFile));

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(cvsSplitBy);
                    treeMap.put(Long.parseLong(values[0]),Double.parseDouble(values[1]));
                }


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return treeMap;

    }
}
