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
        DataSet pw = new PWImporter(csvfile, 80, false, PWImporter.NORMALIZE_AVG_SIGMA, PWImporter.NORMALIZE_AVG_SIGMA, 3, rng);

        System.out.println("Input dim: " + pw.inputDimension);

        double initParamsStdDev = 0.1;


        List<Model> layers = new ArrayList<>();

        //       layers.add(NeuralNetworkHelper.makeFeedForward(pw.inputDimension, 1, 1, 1, new SigmoidUnit(), new LinearUnit(), initParamsStdDev, rng));

        layers.add(NeuralNetworkHelper.makeFeedForward(pw.inputDimension, 30, 1, 1, new SigmoidUnit(), new LinearUnit(), initParamsStdDev, rng));
       // layers.add(NeuralNetworkHelper.makeLstm(30, 10, 1, 4, new LinearUnit(), initParamsStdDev, rng));
       // layers.add(NeuralNetworkHelper.makeFeedForward(4, 4, 1, 1, new SigmoidUnit(), new LinearUnit(), initParamsStdDev, rng));

        Model pwnn = new NeuralNetwork(layers).simplify();

        int trainingEpochs = 100000;
        int reportEveryNthEpoch = 5;
        double learningRate = 0.0001;

        try {
            double loss = Trainer.train(trainingEpochs, learningRate, pwnn, pw, reportEveryNthEpoch, false, true, "pw2.ser", rng);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
