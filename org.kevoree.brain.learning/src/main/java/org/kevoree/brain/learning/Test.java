package org.kevoree.brain.learning;


import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLInt32;
import org.kevoree.brain.statistic.StatLibrary;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class Test {
    public static void main (String[] args){
        GaussianKernelLearning gkl = new GaussianKernelLearning();

        ArrayList<Object[]> xTest= new ArrayList<Object[]>();
        ArrayList<Integer> yTest= new ArrayList<Integer>();

        try {
            MatFileReader matfilereader = new MatFileReader("D:\\workspace\\Github\\kevoree-brain\\org.kevoree.brain.learning\\src\\main\\resources\\ex8data2.mat");


            Map<String, MLArray> mlArrayRetrived = matfilereader.getContent();


            MLDouble X = (MLDouble) mlArrayRetrived.get("X");
            MLDouble Xval = (MLDouble) mlArrayRetrived.get("Xval");
            MLDouble Yval = (MLDouble) mlArrayRetrived.get("yval");



            double[][] x= X.getArray();
            double[][] xval = Xval.getArray();
            double[][] yval = Yval.getArray();

            for(int i=0;i<x.length;i++){
                Double[] temp = new Double[x[i].length];
                for(int j=0; j<x[i].length;j++){
                    temp[j]=new Double(x[i][j]);
                }
                gkl.addTrainingSet(temp,0);
            }

            for(int i=0;i<xval.length*3/4;i++){
                Double[] temp = new Double[xval[i].length];
                for(int j=0; j<xval[i].length;j++){
                    temp[j]=new Double(xval[i][j]);
                }
                gkl.addTrainingSet(temp, (int) yval[i][0]);
            }

            for(int i=xval.length*3/4;i<xval.length;i++){
                Double[] temp = new Double[xval[i].length];
                for(int j=0; j<xval[i].length;j++){
                    temp[j]=new Double(xval[i][j]);
                }
                xTest.add(temp);
                yTest.add(new Integer ((int) yval[i][0]));
            }


        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }


        long startTime = System.nanoTime();
        try {
            gkl.train();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        gkl.printState();
        StatLibrary.testClassifier(xTest, yTest, gkl);
        System.out.println("Duration: "+(double)duration / 1000000000.0+" seconds");









    }
}
