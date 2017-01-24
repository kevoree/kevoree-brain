package org.kevoree.brain.pw;

import com.evolvingstuff.reccurent.datastructs.DataSet;
import com.evolvingstuff.reccurent.model.*;
import com.evolvingstuff.reccurent.trainer.Trainer;
import com.evolvingstuff.reccurent.util.NeuralNetworkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by assaad on 24/01/2017.
 */
public class PWExec {
    public static void main(String[] arg) {

        String csvfile = "/Users/assaad/work/github/furnace-poc/casts.csv";
        Random rng = new Random(1234);
        DataSet pw = new PWImporter(csvfile, 80, false, PWImporter.NORMALIZE_AVG_SIGMA, PWImporter.NORMALIZE_MIN_MAX, rng);

        int hiddenDim = pw.inputDimension;
        int hiddenLayers = 1;
        int outDim1 = 20;
        Nonlinearity hiddenunit = new SigmoidUnit();
        Nonlinearity decoderunit = new LinearUnit();
        double initParamsStdDev = 0.08;

        int outDim2 = 1;

        List<Model> layers = new ArrayList<>();

        layers.add(NeuralNetworkHelper
                .makeFeedForward(pw.inputDimension, hiddenDim, hiddenLayers, outDim1, hiddenunit, decoderunit, initParamsStdDev, rng));
        layers.add(NeuralNetworkHelper.makeLstm(outDim1, outDim2, 1, pw.outputDimension, new LinearUnit(), initParamsStdDev, rng));

        Model pwnn= new NeuralNetwork(layers).simplify();

        int trainingEpochs=1000;
        int reportEveryNthEpoch = 10;
        double learningRate=0.00001;

        try {
            double loss = Trainer.train(trainingEpochs,learningRate,pwnn,pw,reportEveryNthEpoch,false,true,"pw.ser",rng);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
