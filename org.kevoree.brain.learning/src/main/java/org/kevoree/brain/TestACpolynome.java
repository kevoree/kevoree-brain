package org.kevoree.brain;

import org.kevoree.brain.util.Autocorrelation;
import org.kevoree.brain.util.PolynomialCompressor;
import org.kevoree.brain.util.Prioritization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/22/2014.
 */
public class TestACpolynome {


    public static void main(String[] arg) {


        /*int bestdegr = 0;
        int bestpoly = 0;
        double besterr = -1;
        int bestInit = 0;
        ArrayList<double[]> bestcoef = new ArrayList<double[]>();*/
        long starttime;
        long endtime;
        double res;


        String csvFile = "D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\arduino.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Double> values = new ArrayList<Double>();
        int timeOrigine = 0;
        int degradeFactor = 100;
        double toleratedError = 10;
        int maxDegree = 5;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] str = line.split(cvsSplitBy);
                Double px = Double.parseDouble(str[0]);
                values.add(px);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        int n=values.size()/degradeFactor;
        if(n%2==1){
            n--;
        }

        double[] val = new double[n];
        double[] valbackup = new double[n];

        for(int i=0;i<n;i++){
            int h =i*(values.size()/n);

            val[i]=values.get(h);
            valbackup[i]=val[i];
        }
        Autocorrelation.normalizeOverMax(valbackup);
        System.out.println("Loaded " + values.size() + " values, degraded to "+val.length);

        double[] acres = new double[n];


        starttime = System.nanoTime();
        Autocorrelation.fftAutoCorrelation(val,acres,2);
        endtime = System.nanoTime();
        res=((double)(endtime-starttime))/(1000000);
        System.out.println("FFT Autocorrelation On signal " +res+" ms!");
        System.out.println("Period "+Autocorrelation.detectPeriod(acres)*degradeFactor);




        PolynomialCompressor pt= new PolynomialCompressor(timeOrigine,degradeFactor,toleratedError,maxDegree);
        pt.setContinous(true);
        pt.setPrioritization(Prioritization.LOWDEGREES);

        starttime = System.nanoTime();
        for(int i=0; i<values.size();i++){
            pt.feed(i,values.get(i));
        }
        endtime = System.nanoTime();
        pt.finalsave();
        double pes=((double)(endtime-starttime))/(1000000);
        System.out.println("Decomposed in: " +pes+" ms!");
        ArrayList<double[]> wtemp = pt.w;
        ArrayList<Long> wtime=pt.origins;
        System.out.println("Number of polynomes: "+pt.w.size()+", Time compression:"+((double)((values.size()-pt.w.size())*100)/values.size()+" %"));
        System.out.println("Number of repeated polynomes: "+pt.similar());

        int total=0;
        int distinct=0;

        FileWriter outFile2;
        try {
            //outFile = new FileWriter("result.txt");
            outFile2 = new FileWriter("polynome.txt");

            // PrintWriter out = new PrintWriter(outFile);
            PrintWriter out2 = new PrintWriter(outFile2);
            for (int i = 0; i < pt.origins.size(); i++) {
                double[] dd = pt.w.get(i);
                int temp= pt.returnSimilar(dd,i);
                out2.print(pt.origins.get(i) + " : ");
                if(temp!=-1){
                    total++;
                    out2.print("poly number "+temp+ " at time "+pt.origins.get(temp));
                    out2.println();
                }
                else {
                    distinct++;
                    total += dd.length;

                    for (double d : dd) {
                        out2.print(d + " , ");
                    }
                    out2.println();
                }
            }
            out2.close();
        }
        catch (Exception ex){

        }


        System.out.println("Number of distinct polynomes: "+distinct);
        System.out.println("Number of double: "+total+" Disk compression: "+((double)(values.size()-total)*100)/(values.size())+" %");
        System.out.println("Average degrees of the polynomials: "+ ((double)total/pt.w.size()-1));

        double[] polval = new double[n];
        double[] polvalbackup = new double[n];
        double[] polacres = new double[n];

        for(int i=0;i<n;i++){
            int h =i*(values.size()/n);
            polvalbackup[i]=polval[i]=pt.reconstructFromSaved(h);
        }


        starttime = System.nanoTime();
        Autocorrelation.fftAutoCorrelation(polval,polacres,2);
        endtime = System.nanoTime();
        res=((double)(endtime-starttime))/(1000000);
        System.out.println("FFT Autocorrelation On signal " +res+" ms!");
        System.out.println("Period "+Autocorrelation.detectPeriod(polacres)*degradeFactor);





        FileWriter outFile;
        try {
            outFile = new FileWriter("autocorrelation.txt");
          //  outFile2 = new FileWriter("polynome.txt");

            PrintWriter out = new PrintWriter(outFile);
           // PrintWriter out2 = new PrintWriter(outFile2);

            for (int i=0; i<val.length;i++) {
                out.println(i+" "+valbackup[i] + " "+acres[i]);
            }
            out.close();
        }
        catch (Exception ex){

        }
    }
}
