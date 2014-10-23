package org.kevoree.brain.eurUsd;

import org.kevoree.brain.util.polynomialRepresentation.StatClass;
import org.kevoree.brain.util.polynomialRepresentation.PolynomialModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by assaa_000 on 23/10/2014.
 */
public class ErrorGraph {

    public static Long getTimeStamp(int year, int month, int day, int hour, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, min, 0);
        Date date = cal.getTime(); // get back a Date object
        Long timestamp = date.getTime() - date.getTime() % 1000;
        return timestamp;
    }

    public static void main(String[] args) {

       /* Date d=new Date();
        d.setTime(Long.parseLong("991949460000"));*/

        long starttime;
        long endtime;
        double res;

        long timeOrigine = getTimeStamp(2000, 5, 30, 17, 27);
        int degradeFactor = 60000;
        double toleratedError = 0.001;
        int maxDegree = 10;

        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();


        ArrayList<Long> timestamps = new ArrayList<Long>();
        ArrayList<Double> valss = new ArrayList<Double>();

        starttime = System.nanoTime();
        String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\neweur.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] values = line.split(cvsSplitBy);
                Long timestamp = Long.parseLong(values[2]);
                Double val = Double.parseDouble(values[3]);
                eurUsd.put(timestamp, val);
                timestamps.add(timestamp);
                valss.add(val);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000000);
        System.out.println("Loaded :" + eurUsd.size() + " values in " + res + " s!");

        ArrayList<StatClass> errors= new ArrayList<StatClass>();

      /*  System.out.println("Total number of Polynoms: " + global.polynoms);
        System.out.println("Total doubles in polynoms: " +  global.storage);
        System.out.println("Average degrees in polynoms: " + global.avgDegree);
        System.out.println("Time points compression: " +  global.timeCompression+ " %");
        System.out.println("Disk compression: " + global.diskCompression + " %");
        System.out.println("Maximum error: " + global.maxErr + " at time: " + global.time + " original value was: " + global.value + " calculated value: " + global.calculatedValue);
        System.out.println("Average error: " + global.avgError);
        */


        try {

            FileWriter outFile = new FileWriter("errors.txt");
            PrintWriter printFile = new PrintWriter(outFile);
            System.out.println("MaxErr, Nbr_Polynoms , Nbr_doubles, Avg_degree, Time_compress , Disk_compress, Max_err , Avg_err");
            printFile.println("MaxErr, Nbr_Polynoms , Nbr_doubles, Avg_degree, Time_compress , Disk_compress, Max_err , Avg_err");


            for (double maxErr = 0.0001; maxErr <= 1.65; maxErr = maxErr * 2) {
                StatClass err = calculateStat(timestamps, valss, maxErr, degradeFactor, maxDegree);
                errors.add(err);
                System.out.println(maxErr + " , " + err.polynoms + " , " + err.storage + " , " + err.avgDegree + " , " + err.timeCompression + " , " + err.diskCompression + " , " + err.maxErr + " , " + err.avgError);
                printFile.println(maxErr + " , " + err.polynoms + " , " + err.storage + " , " + err.avgDegree + " , " + err.timeCompression + " , " + err.diskCompression + " , " + err.maxErr + " , " + err.avgError);
                printFile.flush();
            }
            printFile.close();
            outFile.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }


    }

    public static StatClass calculateStat(ArrayList<Long> timestamps, ArrayList<Double> valss, double toleratedError, int degradeFactor, int maxDegree){
        PolynomialModel pm= new PolynomialModel(degradeFactor,toleratedError,maxDegree);
        for(int i=0; i<timestamps.size();i++){
            pm.feed(timestamps.get(i),valss.get(i));
        }
        pm.finalSave();
        return pm.displayStatistics(false);

    }
}
