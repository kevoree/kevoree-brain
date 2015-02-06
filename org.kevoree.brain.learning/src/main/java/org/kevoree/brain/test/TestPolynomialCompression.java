package org.kevoree.brain.test;

import org.kevoree.brain.util.PolynomialCompressor;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/21/2014.
 */
public class TestPolynomialCompression {
    public static void main(String[] arg) {


        /*int bestdegr = 0;
        int bestpoly = 0;
        double besterr = -1;
        int bestInit = 0;
        ArrayList<double[]> bestcoef = new ArrayList<double[]>();*/
        long starttime;
        long endtime;

        String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\arduino.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Double> values = new ArrayList<Double>();
        int timeOrigine=0;
        int degradeFactor=10;
        double toleratedError=5;
        int maxDegree=5;
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

        PolynomialCompressor pt= new PolynomialCompressor(timeOrigine,degradeFactor,toleratedError,maxDegree);


        pt.setContinous(true);

        starttime = System.nanoTime();
        for(int i=0; i<values.size();i++){
            values.get(i);
        }
        endtime = System.nanoTime();
        double res=((double)(endtime-starttime))/(1000000);
        System.out.println("Read array in: " +res+" ms!");
        starttime = System.nanoTime();
        for(int i=0; i<values.size();i++){
            pt.feed(i,values.get(i));
        }
        endtime = System.nanoTime();
        pt.finalsave();
        double pes=((double)(endtime-starttime))/(1000000);
        System.out.println("Decomposed in: " +pes+" ms!");
        System.out.println("Polynomial overhead " + (pes-res)+" ms!");
        ArrayList<double[]> wtemp = pt.w;
        ArrayList<Long> wtime=pt.origins;
        System.out.println("Number of polynomes: "+pt.w.size()+", Time compression:"+((double)((values.size()-pt.w.size())*100)/values.size()+" %"));

        FileWriter outFile;
        FileWriter outFile2;
        try {
            outFile = new FileWriter("result.txt");
            outFile2 = new FileWriter("polynome.txt");

            PrintWriter out = new PrintWriter(outFile);
            PrintWriter out2 = new PrintWriter(outFile2);
            int total=0;

            for(int i=0;i<pt.origins.size();i++){
                double[] dd = pt.w.get(i);
                total+=dd.length;
                out2.print(pt.origins.get(i)+" : ");
                for (double d : dd) {
                    out2.print(d + " , ");
                }
                out2.println();
            }
            out2.close();

            double temp=0;
         System.out.println("Number of double: "+total+" Disk compression: "+((double)(values.size()-total)*100)/(values.size())+" %");
         System.out.println("Average degrees of the polynomials: "+ ((double)total/pt.w.size()-1));
            double maxerr=0;

        for(int i=0; i<values.size();i++){

            int ind=pt.origins.size()-1;
            while(i<pt.origins.get(ind)){
                ind--;
            }
            double h=PolynomialCompressor.reconstruct(i,pt.origins.get(ind),pt.w.get(ind),degradeFactor);
            double y = values.get(i);
            double err = Math.abs(h - y);
            if(err>maxerr){
                maxerr=err;
            }


            temp+=err;
            out.println(i + " " + y + " " + h + " " + err);
        }
            temp = temp/values.size();
            System.out.println("Maximum error "+maxerr);
            System.out.println("Average error "+temp);


        } catch (IOException ex) {
        }

    }
}
