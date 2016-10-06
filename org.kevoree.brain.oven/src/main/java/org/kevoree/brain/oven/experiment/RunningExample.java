package org.kevoree.brain.oven.experiment;

import org.kevoree.brain.oven.util.MWGObject;
import org.kevoree.brain.oven.util.XLXLoader;
import org.kevoree.brain.smartgrid.newexperiments.mixture.Gaussian;
import org.mwg.ml.algorithm.preprocessing.PCA;
import org.mwg.ml.common.matrix.Matrix;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;


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

        Matrix backup = m.clone();

        exportCov(m, model, "/Users/assaad/work/github/data/paulwurtCov.csv", false);

        long starttime = System.currentTimeMillis();
        PCA x = new PCA(m, PCA.NORMALIZE);
        long endtime = System.currentTimeMillis();
        double d = endtime - starttime;
        System.out.println("Analysis took " + d + " ms for a matrix of size: " + m.rows() + "x" + m.columns());
        exportCov(m, model, "/Users/assaad/work/github/data/paulwurtCovNorm.csv", true);

        Matrix mrand = new Matrix(null, m.rows(), m.columns());
        Random rand = new Random();
        for (int i = 0; i < mrand.rows(); i++) {
            for (int j = 0; j < mrand.columns(); j++) {
                mrand.set(i, j, rand.nextGaussian());
            }
        }

        System.out.println();
        System.out.println("Generating pure random");
        exportCov(mrand, model, "/Users/assaad/work/github/data/random.csv", false);
        PCA y = new PCA(mrand, PCA.NORMALIZE);
        exportCov(mrand, model, "/Users/assaad/work/github/data/randomNorm.csv", false);


        for (int dim = 1; dim < backup.columns(); dim++) {
            Matrix temp = backup.clone();
            x.setDimension(dim);
            temp = x.convertSpace(temp);
            Matrix itemp = x.inverseConvertSpace(temp);

            double err = 0;
            double total = 0;
            for (int i = 0; i < backup.rows(); i++) {
                for (int j = 0; j < backup.columns(); j++) {
                    err += (backup.get(i, j) - itemp.get(i, j)) * (backup.get(i, j) - itemp.get(i, j));
                    total += backup.get(i, j) * backup.get(i, j);
                }
            }
            total=Math.sqrt(total);
            err = Math.sqrt(err);
            System.out.println("features dim: "+dim+", error: " +err*100/total+"%");
        }


        // exportCov(temp,model,"/Users/assaad/work/github/data/convertedSpace.csv",true);


    }

    public static void exportCov(Matrix m, MWGObject model, String file, boolean abs) {
        Gaussian overallProfile = new Gaussian();
        double[] data = new double[m.columns()];
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.columns(); j++) {
                data[j] = m.get(i, j);
            }
            overallProfile.feed(data);
        }

        double[][] covariance = overallProfile.getCovariance(overallProfile.getAvg());

        int[] perm = new int[covariance.length];
        for (int i = 0; i < perm.length; i++) {
            perm[i] = i;
        }

        if (abs) {
            for (int i = 0; i < covariance.length; i++) {
                for (int j = 0; j < covariance.length; j++) {
                    covariance[i][j] = Math.abs(covariance[i][j]);

                }
            }
        }

        double tempd = 0;
        int tempi = 0;

        //sort:
        for (int i = 0; i < covariance.length; i++) {
            for (int j = i + 1; j < covariance.length; j++) {
                if (covariance[j][j] > covariance[i][i]) {

                    tempi = perm[i];
                    perm[i] = perm[j];
                    perm[j] = tempi;

                    for (int k = 0; k < covariance.length; k++) {
                        tempd = covariance[i][k];
                        covariance[i][k] = covariance[j][k];
                        covariance[j][k] = tempd;
                    }

                    for (int k = 0; k < covariance.length; k++) {
                        tempd = covariance[k][i];
                        covariance[k][i] = covariance[k][j];
                        covariance[k][j] = tempd;
                    }
                }
            }
        }


        try {

            PrintWriter out = new PrintWriter(new File(file));
            out.print(",");
            for (int i = 0; i < covariance.length; i++) {
                out.print(model.getAttributes().get(perm[i] + 2).getName() + ",");
            }
            out.println("");
            for (int i = 0; i < covariance.length; i++) {
                out.print(model.getAttributes().get(perm[i] + 2).getName() + ",");
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
