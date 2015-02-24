package org.kevoree.brain.smartgrid;

import de.tuhh.luethke.okde.Exceptions.EmptyDistributionException;
import de.tuhh.luethke.okde.model.BaseSampleDistribution;
import de.tuhh.luethke.okde.model.SampleModel;
import org.ejml.simple.SimpleMatrix;
import org.math.plot.Plot3DPanel;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by assaad on 19/02/15.
 */
public class TestSmartGrid extends JFrame {

    private static final int DIM = 2;

    public TestSmartGrid() {

        initUI();
    }



    private double convertTime(Long timestamp){
        java.sql.Timestamp tiempoint= new java.sql.Timestamp(timestamp);
        LocalDateTime ldt= tiempoint.toLocalDateTime();

        double res= ((double)ldt.getHour())/24+((double)ldt.getMinute())/(24*60)+((double)ldt.getSecond())/(24*60*60);
        return res;

    }


    private void initUI() {

        // some configuration for plotting
        setTitle("Smart Grid consumption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        // disable the forgetting factor
        double forgettingFactor = 1;
        // set the compression threshold
        double compressionThreshold = 0.02;

        // sample model object used for sample distribution estimation
        SampleModel sampleDistribution = new SampleModel(forgettingFactor,
                compressionThreshold);




        String basePath="/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/";
        String csvfile="ds5.csv";
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Long> timestamps = new ArrayList<Long>();
        ArrayList<Double> timesDouble= new ArrayList<Double>();
        ArrayList<Double> consum= new ArrayList<Double>();
        double maxLoad=0;


try {
    BufferedReader br = new BufferedReader(new FileReader(basePath + csvfile));
    while ((line = br.readLine()) != null) {

        String[] vals = line.split(cvsSplitBy);
        Long t=Long.parseLong(vals[0]);
        timesDouble.add(convertTime(t));
        timestamps.add(t);
        double elec=Double.parseDouble(vals[1]);
        if(elec>maxLoad){
            maxLoad=elec;
        }
                consum.add(elec);

    }
}catch (Exception ex){
    ex.printStackTrace();
}
        System.out.println("Loaded: "+timestamps.size());

        double[] avg = new double[96];
        int[] totavg = new int[96];

        for(int i=0;i<consum.size();i++){
            ElectricMeasure em = new ElectricMeasure();
            em.setTime(timestamps.get(i));
            int index=em.getIntTime(96);
            avg[index]+=consum.get(i);
            totavg[index]++;
        }
        for(int i=0;i<96;i++){
            avg[i]= avg[i]/totavg[i];
        }

        try {
            PrintStream out = new PrintStream(new FileOutputStream("resElecAvg.txt"));

            for (int i = 0; i < 96; i++) {
                double x = (i*0.25);
                double y = avg[i];

                out.println(x+","+y);
            }
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }



        // create a covariance matrix with all entries 0
        double[][] c = new double[DIM][DIM];
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c.length; j++) {
                c[i][j] = 0;
            }
        }
        ArrayList<SimpleMatrix> samples = new ArrayList<SimpleMatrix>();
        try {
            PrintStream out = new PrintStream(new FileOutputStream("resElec.txt"));

            for (int i = 0; i < timestamps.size(); i++) {
                double x = (24* timesDouble.get(i));
                double y = (consum.get(i));

                out.println(x+","+y);

                double[][] sampleArray = {{x},
                        {y}};
                SimpleMatrix sample = new SimpleMatrix(sampleArray);
                samples.add(sample);

            }
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

		/*
		 * Now the sample model is updated using the generated sample data.
		 */
        try {
            // Add three samples at once to initialize the sample model
            ArrayList<SimpleMatrix> initSamples = new ArrayList<SimpleMatrix>();
            initSamples.add(samples.get(0));
            initSamples.add(samples.get(1));
            initSamples.add(samples.get(2));
            double[] w = { 1, 1, 1 };

            sampleDistribution.setNoOfCompsThreshold(160);

            SimpleMatrix[] cov = { new SimpleMatrix(c), new SimpleMatrix(c),
                    new SimpleMatrix(c) };
            SimpleMatrix[] means=initSamples.toArray(new SimpleMatrix[3]);
            sampleDistribution.updateDistribution(
                    means, cov, w);

            int min=Math.min(samples.size(),10000);
            // Update the sample model with all generated samples one by one.
            for (int i = 3; i < min ; i++) {
                SimpleMatrix pos = samples.get(i);
                sampleDistribution.updateDistribution(pos, new SimpleMatrix(c),
                        1d);
            }
        } catch (EmptyDistributionException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

		/*
		 * Plot the distribution estimated by the sample model
		 */

        double xmax=24;
        double ymax=1100;

        // first create a 100x100 grid
        double[] xArray = new double[100];
        double[] yArray = new double[100];
        for (int i = 0; i < 100; i++) {
            xArray[i] = i * 0.01*xmax;
            yArray[i] = i * 0.01*ymax;
        }
        // then evaluate the sample model at each point of the grid
        double[][] zArray = evaluateSampleDistribution(xArray, yArray,
                sampleDistribution);

        Plot3DPanel plot = new Plot3DPanel("SOUTH");

        // add grid plot to the PlotPanel
        plot.addGridPlot("estimated sample distribution", xArray, yArray,
                zArray);
        plot.setFixedBounds(0,0,xmax);
        plot.setFixedBounds(1,0,ymax);

        setContentPane(plot);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                TestSmartGrid ps = new TestSmartGrid();
                ps.setVisible(true);
            }
        });
    }

    /**
     * Evaluates a given sample distribution at point in a given grid.
     *
     * @param x
     *            x-axis of the grid
     * @param y
     *            y-axis of the grid
     * @param dist
     *            sample distribution to evaluate
     * @return z-components of all evaluated grid points
     */
    private double[][] evaluateSampleDistribution(double[] x, double[] y,
                                                  BaseSampleDistribution dist) {
        double[][] z = new double[y.length][x.length];
        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < y.length; j++) {
                double[][] point = { { x[i] }, { y[j] } };
                SimpleMatrix pointVector = new SimpleMatrix(point);
                z[j][i] = dist.evaluate(pointVector);
            }
        return z;
    }

    /**
     * Return a real number from a Gaussian distribution with given mean and
     * standard deviation
     *
     * @param mean
     *            mean
     * @param stddev
     *            standard deviation
     * @return real number from Gaussian distribution with given mean and
     *         standard deviation
     */
    public static double gaussian(double mean, double stddev) {
        Random random = new Random();
        return mean + stddev * random.nextGaussian();
    }

}
