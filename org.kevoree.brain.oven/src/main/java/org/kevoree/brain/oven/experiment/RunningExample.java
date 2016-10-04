package org.kevoree.brain.oven.experiment;

import org.kevoree.brain.oven.util.MWGObject;
import org.kevoree.brain.oven.util.XLXLoader;
import org.kevoree.brain.smartgrid.newexperiments.mixture.Gaussian;
import org.mwg.ml.algorithm.preprocessing.PCA;
import org.mwg.ml.common.matrix.Matrix;

import java.io.File;
import java.io.PrintWriter;


/**
 * Created by assaad on 03/10/16.
 */
public class RunningExample {
    public static void main(String[] args) {
        String file = "/Users/assaad/work/github/data/NeuroPTF parameters_rev7_HO5.xlsx";

        MWGObject model = XLXLoader.loadFile(file);
        model.train();
        model.generateCsv("/Users/assaad/work/github/data/paulwurt.csv");
        Matrix m = model.generateMatrix();

        exportCov(m, model, "/Users/assaad/work/github/data/paulwurtCov.csv");


        long starttime = System.currentTimeMillis();
        PCA x = new PCA(m, PCA.NORMALIZE);
        long endtime = System.currentTimeMillis();
        double d = endtime - starttime;
        System.out.println("Analysis took " + d + " ms for a matrix of size: " + m.rows() + "x" + m.columns());
        exportCov(m, model, "/Users/assaad/work/github/data/paulwurtCovNorm.csv");


    }

    public static void exportCov(Matrix m, MWGObject model, String file) {
        Gaussian overallProfile = new Gaussian();
        double[] data = new double[m.columns()];
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.columns(); j++) {
                data[j] = m.get(i, j);
            }
            overallProfile.feed(data);
        }

        double[][] covariance = overallProfile.getCovariance(overallProfile.getAvg());


        try {

            PrintWriter out = new PrintWriter(new File(file));
            out.print(",");
            for (int i = 0; i < covariance.length; i++) {
                out.print(model.getAttributes().get(i + 2).getName() + ",");
            }
            out.println("");
            for (int i = 0; i < covariance.length; i++) {
                out.print(model.getAttributes().get(i + 2).getName() + ",");
                for (int j = 0; j < covariance.length; j++) {
                    out.print(covariance[i][j] + ",");
                }
                out.println("");
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
