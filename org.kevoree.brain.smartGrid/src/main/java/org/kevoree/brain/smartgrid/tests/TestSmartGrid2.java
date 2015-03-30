package org.kevoree.brain.smartgrid.tests;

import org.kevoree.brain.smartgrid.ElectricMeasure;
import org.kevoree.brain.smartgrid.Profilers.MinMaxProfiler;
import org.math.plot.Plot3DPanel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by assaad on 19/02/15.
 */
public class TestSmartGrid2 extends JFrame {

    private static final int DIM = 2;

    public TestSmartGrid2() {

        initUI();
    }





    private void initUI() {

        // some configuration for plotting
        setTitle("Smart Grid consumption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);



        String basePath="/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/";
        String csvfile="ds5.csv";
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<ElectricMeasure> samples = new ArrayList<ElectricMeasure>();
        double maxLoad=0;


try {
    BufferedReader br = new BufferedReader(new FileReader(basePath + csvfile));
    while ((line = br.readLine()) != null) {

        String[] vals = line.split(cvsSplitBy);
        Long t=Long.parseLong(vals[0]);
        ElectricMeasure em= new ElectricMeasure();
        em.setTime(t);
        em.aplus=Double.parseDouble(vals[1]);
        if(em.aplus>maxLoad){
            maxLoad=em.aplus;
        }
        samples.add(em);
    }
}catch (Exception ex){
    ex.printStackTrace();
}
        System.out.println("Loaded: "+samples.size());


        MinMaxProfiler ecp=new MinMaxProfiler();

        int numb=50;
        ecp.feed(samples,numb);





		/*
		 * Plot the distribution estimated by the sample model
		 */

        double xmax=24;
        double ymax=maxLoad;

        // first create a 100x100 grid
        double[] xArray = new double[100];
        double[] yArray = new double[100];
        for (int i = 0; i < 100; i++) {
            xArray[i] = i * 0.01*xmax;
            yArray[i] = i * 0.01*ymax;
        }
        // then evaluate the sample model at each point of the grid
        double[][] zArray = ecp.getProbabilities(xArray,yArray);

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

                TestSmartGrid2 ps = new TestSmartGrid2();
                ps.setVisible(true);
            }
        });
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
