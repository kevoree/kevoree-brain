package org.kevoree.brain.util.PolynomialFit;


import weka.classifiers.functions.LinearRegression;
import weka.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by assaa_000 on 8/19/2014.
 */
public class PolynomialFitWeka implements Polynomial {
    private static int numDimensions =10;
    private  LinearRegression lr;

    public PolynomialFitWeka (double samplePoints[] , double[] observations){
      fit(samplePoints,observations);

    }

    @Override
    public double[] getCoef() {
        return lr.coefficients();
    }

    @Override
    public void fit(double[] samplePoints, double[] observations) {
        int numInstances= samplePoints.length;

        FastVector atts = new FastVector();
        List<Instance> instances = new ArrayList<Instance>();
        for(int dim = 0; dim < numDimensions+1; dim++)
        {
            // Create new attribute / dimension
            Attribute current = new Attribute("t" + dim, dim);
            // Create an instance for each data object
            if(dim == 0)
            {
                for(int obj = 0; obj < numInstances; obj++)
                {
                    instances.add(new SparseInstance(numDimensions+1));
                }
            }

            // Fill the value of dimension "dim" into each object
            for(int obj = 0; obj < numInstances; obj++)
            {
                double val=0;
                if(dim==0)
                    val=observations[obj];
                else
                    val=Math.pow(samplePoints[obj],dim);
                instances.get(obj).setValue(current, val);
            }

            // Add attribute to total attributes
            atts.addElement(current);
        }

// Create new dataset
        Instances newDataset = new Instances("Dataset", atts, instances.size());
        newDataset.setClassIndex(0);
        for(Instance inst : instances)
            newDataset.add(inst);

        lr = new LinearRegression();
        lr.setAttributeSelectionMethod(new SelectedTag(LinearRegression.SELECTION_NONE,LinearRegression.TAGS_SELECTION));



        try {
            lr.buildClassifier(newDataset);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void print(){
        System.out.println(lr);
    }

    @Override
    public double getError(double[] samplePoints, double[] observations) {
        double err=0;
        double val=0;

        for(int i=0; i< samplePoints.length; i++){
            val = calculate(samplePoints[i]);
            err+= ((val-observations[i])*(val-observations[i]));
        }
        err=Math.sqrt(err);

        return err;



    }

    @Override
    public double calculate(double t) {
        Instance instance=new SparseInstance(numDimensions+1);

        for(int dim = 1; dim < numDimensions+1; dim++)
        {
            double val=Math.pow(t,dim);
            instance.setValue(dim,val);
        }
        try {
            return lr.classifyInstance(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getDegree() {
        return numDimensions;
    }
}
