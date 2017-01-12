package com.evolvingstuff.reccurent;

import com.evolvingstuff.reccurent.datasets.ModuloGeneration;
import com.evolvingstuff.reccurent.datasets.TextGenerationUnbroken;
import com.evolvingstuff.reccurent.datastructs.DataSet;
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
            int inputsize = 13;
            int outputsize = 7;
            int timestep = 4;
            int seqsize = 16;

            String textSource = "modulo";
            String savePath = "saved_models/"+textSource+".ser";
            boolean initFromSaved = false; //set this to false to start with a fresh model
            boolean overwriteSaved = true;


            int bottleneckSize = inputsize; //one-hot input is squeezed through this
            int hiddenDimension = (inputsize+outputsize)/2;
            int hiddenLayers = 1;
            double learningRate = 0.001;
            double initParamsStdDev = 0.08;

            ModuloGeneration data = new ModuloGeneration(inputsize, outputsize, timestep, seqsize, training, testing, rng);

            Model lstm = NeuralNetworkHelper.makeLstmWithInputBottleneck(
                    data.inputDimension, bottleneckSize,
                    hiddenDimension, hiddenLayers,
                    data.outputDimension, data.getModelOutputUnitToUse(),
                    initParamsStdDev, rng);


            int reportEveryNthEpoch = 50;
            int trainingEpochs = 10000;

            Trainer.train(trainingEpochs, learningRate, lstm, data, reportEveryNthEpoch, initFromSaved, overwriteSaved, savePath, rng);

            System.out.println("done.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
