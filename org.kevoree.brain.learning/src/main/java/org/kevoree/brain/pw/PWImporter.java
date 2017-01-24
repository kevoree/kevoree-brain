package org.kevoree.brain.pw;

import com.evolvingstuff.reccurent.autodiff.Graph;
import com.evolvingstuff.reccurent.datastructs.DataSequence;
import com.evolvingstuff.reccurent.datastructs.DataSet;
import com.evolvingstuff.reccurent.datastructs.DataStep;
import com.evolvingstuff.reccurent.loss.LossSumOfSquares;
import com.evolvingstuff.reccurent.matrix.Matrix;
import com.evolvingstuff.reccurent.model.Model;
import com.evolvingstuff.reccurent.model.Nonlinearity;
import org.kevoree.brain.learning.GaussianProfile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by assaad on 24/01/2017.
 */
public class PWImporter extends DataSet {
    public static int NO_NORMALIZE = 0;
    public static int NORMALIZE_MIN_MAX = 1;
    public static int NORMALIZE_AVG_SIGMA = 2;

    private GaussianProfile gpinput = new GaussianProfile();
    private GaussianProfile gpoutput = new GaussianProfile();
    private HashMap<Integer, Integer> mapDStoCast = new HashMap<>();
    private List<DataSequence> originalds = new ArrayList<>();
    private double[] inAvg;
    private double[] inSigma;
    private double[] outAvg;
    private double[] outSigma;

    private int normalizeInput;
    private int normalizeOutput;

    private double[] rmseTest;
    private int countTest;

    public PWImporter(String csvFile, int percentTrain, boolean randomize, int normalizeInput, int normalizeOutput, int testIncrease ,Random rnd) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line = br.readLine(); //read header
            int oldcastid = 0;
            int newcastid;
            this.normalizeInput=normalizeInput;
            this.normalizeOutput=normalizeOutput;
            this.rmseTest=new double[testIncrease];
            countTest=0;


            DataSequence tempds = new DataSequence();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                newcastid = Integer.parseInt(data[0]);
                if (newcastid != oldcastid) {
                    tempds = new DataSequence();
                    mapDStoCast.put(originalds.size(), newcastid);
                    originalds.add(tempds);
                    oldcastid = newcastid;
                }
                this.inputDimension = data.length - 2;
                this.outputDimension = 1;

                double[] inp = new double[inputDimension];
                double[] outp = new double[outputDimension];

                for (int i = 0; i < inputDimension; i++) {
                    inp[i] = Double.parseDouble(data[i + 1]);
                }
                outp[0] = Double.parseDouble(data[data.length - 1]);
                gpinput.learn(inp);
                gpoutput.learn(outp);

                DataStep ds = new DataStep(inp, outp);
                tempds.steps.add(ds);
            }

            this.training = new ArrayList<>();
            this.testing = new ArrayList<>();

            inAvg = gpinput.getAvg();
            inSigma = gpinput.getSigma(inAvg);
            outAvg = gpoutput.getAvg();
            outSigma = gpoutput.getSigma(outAvg);

            this.lossTraining=new LossSumOfSquares();
            this.lossReporting=new LossSumOfSquares();

            if (randomize) {
                for (int i = 0; i < originalds.size(); i++) {
                    if (rnd.nextInt(100) < percentTrain) {
                        this.training.add(getNormilized(originalds.get(i)));
                    } else {
                        this.testing.add(getNormilized(originalds.get(i)));
                    }
                }

            } else {
                int tempi = originalds.size() * percentTrain / 100;

                for (int i = 0; i < tempi; i++) {
                    this.training.add(getNormilized(originalds.get(i)));
                }
                for (int i = tempi; i < originalds.size(); i++) {
                    this.testing.add(getNormilized(originalds.get(i)));
                }
            }
            System.out.println("Ouput sigma: "+outSigma[0]);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private double[] normalizeInput(double[] input){
        if (normalizeInput == NO_NORMALIZE) {
            double[] newin = new double[input.length];
            System.arraycopy(input, 0, newin, 0, input.length);
            return newin;
        } else if (normalizeInput == NORMALIZE_MIN_MAX) {
            double[] newin = gpinput.normalizeMinMax(input);
            return newin;
        } else if (normalizeInput == NORMALIZE_AVG_SIGMA) {
            double[] newin = gpinput.normalize(input, inAvg, inSigma);
            return newin;
        }
        throw new RuntimeException("Unknown normalize type of input");
    }

    private double[] normalizeOutput(double[] output){
        if (normalizeOutput == NO_NORMALIZE) {
            double[] newout = new double[output.length];
            System.arraycopy(output, 0, newout, 0, output.length);
            return newout;
        } else if (normalizeOutput == NORMALIZE_MIN_MAX) {
            double[] newout = gpoutput.normalizeMinMax(output);
            return newout;
        } else if (normalizeOutput == NORMALIZE_AVG_SIGMA) {
            double[] newout = gpoutput.normalize(output, outAvg, outSigma);
            return newout;
        }
        throw new RuntimeException("Unknown normalize type of output");
    }

    private double[] inverseNormalizeInput(double[] input){
        if (normalizeInput == NO_NORMALIZE) {
            double[] newin = new double[input.length];
            System.arraycopy(input, 0, newin, 0, input.length);
            return newin;
        } else if (normalizeInput == NORMALIZE_MIN_MAX) {
            double[] newin = gpinput.inverseNormalizeMinMax(input);
            return newin;
        } else if (normalizeInput == NORMALIZE_AVG_SIGMA) {
            double[] newin = gpinput.inverseNormalize(input, inAvg, inSigma);
            return newin;
        }
        throw new RuntimeException("Unknown normalize type of input");
    }

    private double[] inverseNormalizeOutput(double[] output){
        if (normalizeOutput == NO_NORMALIZE) {
            double[] newout = new double[output.length];
            System.arraycopy(output, 0, newout, 0, output.length);
            return newout;
        } else if (normalizeOutput == NORMALIZE_MIN_MAX) {
            double[] newout = gpoutput.inverseNormalizeMinMax(output);
            return newout;
        } else if (normalizeOutput == NORMALIZE_AVG_SIGMA) {
            double[] newout = gpoutput.inverseNormalize(output, outAvg, outSigma);
            return newout;
        }
        throw new RuntimeException("Unknown normalize type of output");
    }



    private DataSequence getNormilized(DataSequence original) {
        if (normalizeInput == NO_NORMALIZE && normalizeOutput == NO_NORMALIZE) {
            return original;
        }

        DataSequence result = new DataSequence();
        for (int i = 0; i < original.steps.size(); i++) {
            DataStep dsi = original.steps.get(i);
            DataStep dsn = new DataStep(normalizeInput(dsi.input.w), normalizeOutput(dsi.targetOutput.w));
            result.steps.add(dsn);
        }
        return result;
    }

    @Override
    public void DisplayReport(Model model, Random rng) throws Exception {
        System.out.println();
        double d1= DisplayReportData(model,rng,training,"training");
        double d2= DisplayReportData(model,rng,testing,"testing");

        if(countTest<rmseTest.length){
            rmseTest[countTest]=d2;
            countTest++;
        }
        else {
            for(int i=0;i<rmseTest.length-1;i++){
                rmseTest[i]=rmseTest[i+1];
            }
            rmseTest[rmseTest.length-1]=d2;
        }

        System.out.println();
    }


    public double DisplayReportData(Model model, Random rng, List<DataSequence> lds, String name) throws Exception {
        double totalerr=0;
        double totalerrSq=0;
        int total=0;
        for(int i=0;i<lds.size();i++){
            DataSequence ds= lds.get(i);
            Graph g = new Graph(false);

            for(int j=0;j<ds.steps.size();j++){
                Matrix res=model.forward(ds.steps.get(j).input,g);
                double err=calcErr(res,ds.steps.get(j).targetOutput);
                totalerr+=err;
                totalerrSq+=err*err;
                total++;
            }
            model.resetState();
        }
        double rmse=Math.sqrt(totalerrSq/total);
        System.out.println(name +" Avg RMSE: "+rmse+"                           Avg Err: "+(totalerr/total));
        return rmse;
    }

    private double calcErr(Matrix res, Matrix targetOutput) {
        double[] resi = inverseNormalizeOutput(res.w);
        double[] outi=inverseNormalizeOutput(targetOutput.w);
        if(resi.length!=1||outi.length!=1){
            throw new RuntimeException("Error in output");
        }
       // System.out.println(resi[0]+" "+outi[0]);
        return Math.abs(resi[0]-outi[0]);
    }

    @Override
    public Nonlinearity getModelOutputUnitToUse() {
        return null;
    }

    @Override
    public boolean continueTraining() {
        if(countTest<rmseTest.length){
            return true;
        }
        else {
            for(int i=0;i<rmseTest.length-1;i++){
                if(rmseTest[i]>rmseTest[i+1]){
                    return true;
                }
            }
        }
        return false;
    }
}
