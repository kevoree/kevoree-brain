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
 * Created by assaad on 06/02/15.
 */
public class Analyzer {


    public static Range load(ArrayList<Long> timestamps, ArrayList<Double> valss ,  TreeMap<Long, Double> eurUsd , PolynomialCompressor pt, int degradeFactor){
        Range range = new Range();
        long starttime;
        long endtime;
        double res;
        pt.setContinous(true);
        pt.setPrioritization(Prioritization.LOWDEGREES);


        starttime = System.nanoTime();
        // String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\EURUSD.csv";
        String csvFile = "/Users/assaad/work/github/eurusd/newEurUsd.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";


        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator 2000.05.30,17:35
                String[] values = line.split(cvsSplitBy);
                Long timestamp = Long.parseLong(values[2]);
                Double val = Double.parseDouble(values[3]);
                timestamps.add(timestamp);
                valss.add(val);
                eurUsd.put(timestamp, val);
                pt.feed(timestamp, val);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        pt.finalsave();
        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000000);
        System.out.println("Loaded :" + eurUsd.size() + " values in " + res + " s!");


        double err = 0;
        double maxerr = 0;
        double currerr = 0;
        int counter = 0;
        long indec = 0;
        double v;
        int l = 0;
        starttime = System.nanoTime();

        double min = 2;
        double max = 0;
        for (int i = 0; i < eurUsd.size(); i++) {
            long t = timestamps.get(i);
            double val = valss.get(i);
            if (val > max) {
                max = val;
            }
            if (val < min) {
                if (min < 0.7) {
                    System.out.println(t);
                }
                min = val;
            }
            v = pt.fastReconstruct(t);
            currerr = Math.abs(v - val);
            if (currerr > maxerr) {
                maxerr = currerr;
                indec = t;
                l = i;
            }
            err += currerr;
            counter++;
        }
        range.max=max;
        range.min=min;
        System.out.println("Min: " + min + " , Max: " + max);
        err = err / counter;
        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000000);
        System.out.println("Number of polynoms: " + pt.polynomTree.size());
        System.out.println("Number of Val: " + pt.globalCounter);
        System.out.println("Average degrees: " + ((double) pt.degrees) / pt.polynomTree.size());
        System.out.println("Reconstructed in: " + res + " s!");
        System.out.println("Max error: " + maxerr + " at: " + indec + " line: " + l);
        System.out.println("Avg err: " + err);



     /*   Long initTimeStamp = TimeStamp.getTimeStamp(2001, 01, 01, 00, 00);
        Long finalTimeStamp = TimeStamp.getTimeStamp(2014, 9, 26, 00, 00);


        starttime = System.nanoTime();
        for (long i = initTimeStamp; i < finalTimeStamp; i += degradeFactor) {
            double val = eurUsd.get(eurUsd.floorKey(i));
        }
        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000);
        System.out.println("normal chain in: " + res + " ms!");


        starttime = System.nanoTime();
        for (long i = initTimeStamp; i < finalTimeStamp; i += degradeFactor) {
            double val = pt.fastReconstruct(i);
        }
        endtime = System.nanoTime();
        res = ((double) (endtime - starttime)) / (1000000);
        System.out.println("Polynomial chain in: " + res + " ms!");*/
        return range;

    }


    public static double getPerc(Range range, double[] acchist, double val){
        return acchist[range.position(val)];
    }


    public static double getVal(Range range, double[] acchist, double percent){
        for(int i=0; i<range.getMaxInt();i++){
            if(acchist[i]>=percent){
                return range.value(i);
            }
        }
        return -1;
    }


    public static void calc(boolean print, long initTimeStamp,long finalTimeStamp, int degradeFactor, Range range, TreeMap<Long, Double> eurUsd, double eurval, double moneyeur, double moneydol){
        int[] histogram = new int[range.getMaxInt()];
        int counter=0;
        for(long i=initTimeStamp; i<finalTimeStamp;i+=degradeFactor){
            //double val = pt.fastReconstruct(i);
            double val=eurUsd.get(eurUsd.floorKey(i));
            histogram[range.position(val)]++;
            counter++;
        }


        int max=0;
        int acc=0;
        System.out.println("Range: " + range.getMaxInt());
        for (int i = 0; i < range.getMaxInt(); i++) {
            if(histogram[i]>max){
                max=histogram[i];
            }
            acc+=histogram[i];
        }

        int aggregations=0;

        double [] hist=new double[range.getMaxInt()];
        double [] acchist=new double[range.getMaxInt()];


        try {
            if(print) {
                FileWriter outFile = new FileWriter("result.csv");
                PrintWriter out = new PrintWriter(outFile);
                out.println("eur,hist,acc");
                for (int i = 0; i < range.getMaxInt(); i++) {
                    aggregations += histogram[i];
                    hist[i] = ((double) (histogram[i] * 100)) / max;
                    acchist[i] = ((double) (aggregations * 100)) / acc;
                    out.println(String.format("%.4f", range.value(i)) + "," + String.format("%.6f", hist[i]) + "," + String.format("%.6f", acchist[i]));
                }
                out.close();
            }
            else{
                for (int i = 0; i < range.getMaxInt(); i++) {
                    aggregations += histogram[i];
                    hist[i] = ((double) (histogram[i] * 100)) / max;
                    acchist[i] = ((double) (aggregations * 100)) / acc;
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }


        System.out.println("At 00%: " +String.format( "%.4f", getVal(range, acchist, 0.00001)));
        System.out.println("At 25%: "+String.format("%.4f", getVal(range, acchist, 25)));
        System.out.println("At 50%: "+String.format("%.4f", getVal(range, acchist, 50)));
        System.out.println("At 75%: "+String.format("%.4f", getVal(range, acchist, 75)));
        System.out.println("At100%: "+String.format("%.4f", getVal(range, acchist, 100)));

        double perc=getPerc(range, acchist, eurval);
        System.out.println("Euro level at "+eurval+" is: "+String.format( "%.2f",perc)+" %");



        double tot=moneyeur*eurval+moneydol;
        double neweur = (100-perc)*tot/(100*eurval);
        double newdol=tot-neweur*eurval;

        if(neweur>moneyeur) {
            System.out.println("[BUY] "+String.format("%.4f",neweur-moneyeur)+" eur");
        }
        else
        {
            System.out.println("[SELL] "+String.format("%.4f",moneyeur-neweur)+" eur");
        }

        System.out.println("Equilibre eur: "+String.format("%.4f",neweur));
        System.out.println("Equilibre dol: "+String.format("%.4f",newdol));

    }

    public static void main(String[] args) {

        long timeOrigine = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        int degradeFactor = 60000;
        double toleratedError = 0.0001;
        int maxDegree = 20;

        ArrayList<Long> timestamps = new ArrayList<Long>();
        ArrayList<Double> valss = new ArrayList<Double>();
        TreeMap<Long, Double> eurUsd = new TreeMap<Long, Double>();
        PolynomialCompressor pt = new PolynomialCompressor(timeOrigine, degradeFactor, toleratedError, maxDegree);
        Range range=load(timestamps,valss,eurUsd,pt,degradeFactor);


        Long initTimeStamp = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
        Long finalTimeStamp = TimeStamp.getTimeStamp(2015, 01, 31, 23, 59);

        double eurval=1.1376;
        double moneyeur=1000;
        double moneydol=2000;

        calc(false,initTimeStamp,finalTimeStamp,degradeFactor,range,eurUsd,eurval,moneyeur,moneydol);


        initTimeStamp = TimeStamp.getTimeStamp(2010, 1, 1, 00, 0);
        finalTimeStamp = TimeStamp.getTimeStamp(2015, 01, 31, 23, 59);
        calc(false,initTimeStamp,finalTimeStamp,degradeFactor,range,eurUsd,eurval,moneyeur,moneydol);
        System.out.println("Done!");


    }



}
