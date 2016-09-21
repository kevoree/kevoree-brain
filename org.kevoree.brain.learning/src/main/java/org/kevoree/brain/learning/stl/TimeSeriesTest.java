package org.kevoree.brain.learning.stl;

import com.github.brandtg.stl.StlConfig;
import com.github.brandtg.stl.StlDecomposition;
import com.github.brandtg.stl.StlResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by assaad on 16/06/16.
 */
public class TimeSeriesTest {
    public static void main(String[] arg){
        String csv="/Users/assaad/Documents/VM/MAC000007.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<Number> times=new ArrayList<Number>();
        List<Number> powers=new ArrayList<Number>();

        try {
            br = new BufferedReader(new FileReader(csv));

            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);
                times.add(Double.parseDouble(values[0]));
                powers.add(Double.parseDouble(values[1]));
            }



            StlDecomposition dec=new StlDecomposition(48);
            StlConfig config = dec.getConfig();
            config.setPeriodic(false);

            StlResult stl = dec.decompose(times,powers);

            String csvout="/Users/assaad/Documents/VM/dec.csv";
            PrintWriter out =new PrintWriter(new File(csvout));
            double [] t1=stl.getTimes();
            double [] t2=stl.getSeries();
            double [] t3=stl.getTrend();
            double [] t4=stl.getSeasonal();
            double [] t5=stl.getRemainder();

            out.println("times,series,trend,seasonal,remainder,sum,prod");
            for(int i=0;i<t1.length;i++){
                double sum=t3[i]+t4[i]+t5[i];
                double prod=t3[i]*t4[i]*t5[i];
                out.println(t1[i]+","+t2[i]+","+t3[i]+","+t4[i]+","+t5[i]+","+sum+","+prod);
            }
            out.flush();
            out.close();

            StlPlotter.plotOnScreen(stl, "Seasonal Decomposition");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
