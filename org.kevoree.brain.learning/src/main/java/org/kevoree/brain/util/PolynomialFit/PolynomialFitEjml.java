package org.kevoree.brain.util.PolynomialFit;


import java.util.Random;

/**
 * Created by assaa_000 on 8/19/2014.
 */

public class PolynomialFitEjml  implements Polynomial {
    // Vandermonde matrix
    DenseMatrix64F A;
    // matrix containing computed polynomial coefficients
    DenseMatrix64F coef;
    // observation matrix
    DenseMatrix64F y;
    // solver used to compute
    AdjLinearSolverQr solver;

    public PolynomialFitEjml(int degree) {
        coef = new DenseMatrix64F(degree + 1, 1);
        A = new DenseMatrix64F(1, degree + 1);
        y = new DenseMatrix64F(1, 1);
        // create a solver that allows elements to be added or removed efficiently
        solver = new AdjLinearSolverQr();
    }

    public double[] getCoef() {
        return coef.data;
    }

    public PolynomialFitEjml(double samplePoints[] , double[] observations){

        int degree=findbestdegree(samplePoints,observations);


        coef = new DenseMatrix64F(degree+1,1);
        A = new DenseMatrix64F(1,degree+1);
        y = new DenseMatrix64F(1,1);

        // create a solver that allows elements to be added or removed efficiently
        solver = new AdjLinearSolverQr();
        this.fit(samplePoints,observations);

    }

    private static int findbestdegree(double samplePoints[] , double[] observations){
        int degree=1;
        double err = -1;
        int bestdeg=0;
        PolynomialFitEjml best=null;


        if (samplePoints.length<2){
            return 1;

        }
        int maxdegree = Math.min(samplePoints.length,25);

        for(degree=1; degree<maxdegree; degree++){
            double error =0;
            for(int count=0; count<10; count++) {
                int testsize = Math.max(3, (int)(samplePoints.length*0.1));
                double [] sampleTraining = new double[samplePoints.length-testsize];
                double [] observationTraining = new double[samplePoints.length-testsize];
                double [] sampleCsv = new double[testsize];
                double [] observationCsv = new double[testsize];

                int[] all=new int[samplePoints.length];
                for(int i=0; i<all.length;i++)
                    all[i]=i;

                int temp = all[0];
                all[0]=all[all.length-2];
                all[all.length-2]=temp;

                Random rand = new Random();
                for(int i=1; i<all.length-2;i++){
                    temp = all[i];
                    int r= rand.nextInt(all.length-1);
                    all[i]=all[r];
                    all[r]=temp;
                }


                for(int i=0;i <testsize;i++){
                    sampleCsv[i]=samplePoints[all[i]];
                    observationCsv[i]= observations[all[i]];
                }
                for(int i=testsize;i <samplePoints.length;i++){
                    sampleTraining[i-testsize]=samplePoints[all[i]];
                    observationTraining[i-testsize]= observations[all[i]];
                }

                PolynomialFitEjml pf = new PolynomialFitEjml(degree);
                pf.fit(sampleTraining, observationTraining);

                error += pf.getError(sampleCsv, observationCsv);
            }

            if(err<0 || error<err){
                err=error;
                bestdeg=degree;
            }

        }

        return bestdeg;
    }


    public void fit(double samplePoints[], double[] observations) {
        y.reshape(observations.length, 1, false);
        System.arraycopy(observations, 0, y.data, 0, observations.length);
        A.reshape(y.numRows, coef.numRows, false);
        // set up the A matrix
        for (int i = 0; i < observations.length; i++) {
            double obs = 1;
            for (int j = 0; j < coef.numRows; j++) {
                A.set(i, j, obs);
                obs *= samplePoints[i];
            }
        }
        // process the A matrix and see if it failed
        solver.setA(A);
        solver.solve(y, coef);
    }

    @Override
    public double getError(double[] samplePoints, double[] observations) {
        double err = 0;
        double val = 0;

        for (int i = 0; i < samplePoints.length; i++) {
            val = calculate(samplePoints[i]);
            err += ((val - observations[i]) * (val - observations[i]));
        }
        err = Math.sqrt(err);

        return err / samplePoints.length;
    }

    @Override
    public double calculate(double t) {
        double[] factor = getCoef();
        double result = 0;
        double powT = 1;

        for (double d : factor) {
            result += d * powT;
            powT = powT * t;
        }

        return result;
    }

    public int getDegree() {
        return getCoef().length - 1;
    }

    @Override
    public void print() {
        double[] coef = this.getCoef();
        System.out.println("Function is");
        int counter = 0;
        for (double d : coef) {
            System.out.println(d + " * t" + counter);
            counter++;
        }

    }
}
