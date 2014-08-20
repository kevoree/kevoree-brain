package org.kevoree.brain;

import org.kevoree.brain.util.PolynomialFit.PolynomialFitEjml;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/20/2014.
 */
public class MovingPolynomial4 {
    public static void main(String[] arg) {


        /*int bestdegr = 0;
        int bestpoly = 0;
        double besterr = -1;
        int bestInit = 0;
        ArrayList<double[]> bestcoef = new ArrayList<double[]>();*/


        String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\arduino.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Double> values = new ArrayList<Double>();
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] str = line.split(cvsSplitBy);
                Double px = Double.parseDouble(str[0]);
                values.add(px);
            }
            System.out.println("Loaded " + values.size() + " values");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        //for (int degradeFactor = 100; degradeFactor < 1000; degradeFactor += 100) {
        int degradeFactor = 360;
        ArrayList<Double> degraded = new ArrayList<Double>(values.size() / degradeFactor);
        for (int i = 0; i < values.size(); i += degradeFactor) {
            degraded.add(values.get(i));
        }

        PolynomialFitEjml pf;
        //for(int poly=4;poly<15;poly++){
        int poly = 5;
        //for(int size=poly+1; size<20; size++){
        int size = 10;

        double[] t = new double[size];
        for (int i = 0; i < size; i++) {
            t[i] = i;
        }

        ArrayList<double[]> coef = new ArrayList<double[]>();

        for (int i = 0; i < degraded.size() - size; i += size) {
            double[] val = new double[size];
            for (int j = 0; j < size; j++) {
                val[j] = degraded.get(i + j);
            }
            pf = new PolynomialFitEjml(poly);
            pf.fit(t, val);
            coef.add(pf.getCoef());
        }

        FileWriter outFile;
        FileWriter outFile2;

        try {
            outFile = new FileWriter("result.txt");
            outFile2 = new FileWriter("polynome.txt");
            PrintWriter out = new PrintWriter(outFile);
            PrintWriter out2 = new PrintWriter(outFile2);
            for (int i = 0; i < coef.size(); i++) {
                double[] dd = coef.get(i);
                for (double d : dd) {
                    out2.print(d + " , ");
                }
                out2.println();
            }
            out2.close();

        int counter = 0;
        double err = 0;
        double totalerr = 0;
        for (int i = 0; i < values.size(); i++) {
            int group = i / (size * degradeFactor);
            if (group >= coef.size())
                break;
            double time = ((double) (i - group * size * degradeFactor)) / (degradeFactor);
            double h = reconstruct(time, coef.get(group));
            double y = values.get(i);
            err = Math.sqrt((h - y) * (h - y))/y;
            out.println(i + " " + y + " " + h + " " + err);
            totalerr += err;
            counter++;
        }

        totalerr = totalerr / counter;
            System.out.println("Total error "+ totalerr);

            out.close();
        } catch (IOException ex) {
        }
    }


    private static double reconstruct(double t, double[] weights) {
        double result=0;
        double power=1;
        for(int j=0;j<weights.length;j++){
            result+= weights[j]*power;
            power=power*t;
        }
        return result;
    }

    private static double reconstruct(int i, int origin, int degradeFactor, double[] weights) {
        double result=0;
        double t= ((double)(i-origin*degradeFactor))/degradeFactor;
        double power=1;
        for(int j=0;j<weights.length;j++){
            result+= weights[j]*power;
            power=power*t;
        }
        return result;
    }
}
