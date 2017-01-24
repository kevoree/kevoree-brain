package com.evolvingstuff.reccurent;

import com.evolvingstuff.reccurent.datasets.ModuloGeneration;
import com.evolvingstuff.reccurent.model.Model;
import com.evolvingstuff.reccurent.trainer.Trainer;
import com.evolvingstuff.reccurent.util.NeuralNetworkHelper;

import java.util.Random;

/**
 * Created by assaad on 12/01/2017.
 */
public class TestSequence {
    public static void main(String[] arg) {

        try {
            Random rng = new Random(1234);
            int training = 2000;
            int testing = 100;
            int inputsize = 16;
            int outputsize = 16;
            int timestep = 1;
            int seqsize = 32;

            String textSource = "modulo";
            String savePath = "saved_models/"+textSource+".ser";
            boolean initFromSaved = false; //set this to false to start with a fresh model
            boolean overwriteSaved = true;

            int hiddenDimension = inputsize;
            int hiddenLayers = timestep;
            double learningRate = 0.1;
            double initParamsStdDev = 0.08;

            ModuloGeneration data = new ModuloGeneration(inputsize, outputsize, timestep, seqsize, training, testing, rng);

            Model lstm = NeuralNetworkHelper.makeLstm(
                    data.inputDimension,
                    hiddenDimension, hiddenLayers,
                    data.outputDimension, data.getModelOutputUnitToUse(),
                    initParamsStdDev, rng);


            int reportEveryNthEpoch = 10;
            int trainingEpochs = 10000;


            Trainer.decayRate = 0.999;
            Trainer.smoothEpsilon = 1e-8;
            Trainer.gradientClipValue = 5;
            Trainer.regularization = 0.000001;


//            Trainer.decayRate = 1;
//            Trainer.smoothEpsilon = 0;
//            Trainer.gradientClipValue = 10;
//            Trainer.regularization = 0;


            Trainer.train(trainingEpochs, learningRate, lstm, data, reportEveryNthEpoch, initFromSaved, overwriteSaved, savePath, rng);

            System.out.println("done.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
