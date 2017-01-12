package com.evolvingstuff.reccurent.datasets;

import com.evolvingstuff.reccurent.autodiff.Graph;
import com.evolvingstuff.reccurent.datastructs.DataSequence;
import com.evolvingstuff.reccurent.datastructs.DataSet;
import com.evolvingstuff.reccurent.datastructs.DataStep;
import com.evolvingstuff.reccurent.loss.LossSoftmax;
import com.evolvingstuff.reccurent.matrix.Matrix;
import com.evolvingstuff.reccurent.model.LinearUnit;
import com.evolvingstuff.reccurent.model.Model;
import com.evolvingstuff.reccurent.model.Nonlinearity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by assaad on 12/01/2017.
 */
public class ModuloGeneration extends DataSet {
    private int timesteps;
    private int seqsize;
    private Random rnd;

    public ModuloGeneration(int inputVecSize, int outputVecSize, int timesteps, int seqsize, int training, int testing, Random rnd) {
        this.inputDimension = inputVecSize;
        this.outputDimension = outputVecSize;


        this.timesteps = timesteps;
        this.seqsize = seqsize;
        this.rnd = rnd;
        this.training = generate(training);
        this.testing = generate(testing);
        this.lossTraining = new LossSoftmax();
        this.lossReporting = new LossSoftmax();

    }


    private List<DataSequence> generate(int size) {
        List<DataSequence> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ArrayList<DataStep> dataSteps = new ArrayList<>();
            int[] seeds = new int[timesteps];
            int totalseed = 0;
            int modulo = 0;

            for (int j = 0; j < seqsize; j++) {
                modulo = j % timesteps;
                totalseed -= seeds[modulo];
                seeds[modulo] = rnd.nextInt(inputDimension);
                totalseed += seeds[modulo];
                if (j < timesteps) {
                    dataSteps.add(new DataStep(getArray(seeds[modulo], inputDimension), null));
                } else {
                    dataSteps.add(new DataStep(getArray(seeds[modulo], inputDimension), getArray(totalseed, outputDimension)));
                }
            }
            DataSequence dataSequence = new DataSequence(dataSteps);
            list.add(dataSequence);
        }
        return list;
    }

    private double[] getArray(int seed, int dim) {
        double[] arr = new double[dim];
        arr[seed % dim] = 1;
        return arr;
    }



    private int getPosition(double[] output){
        double max=output[0];
        int pos=0;
        for(int i=1;i<output.length;i++){
            if(max<output[i]){
                max=output[i];
                pos=i;
            }
        }
        return pos;
    }

    @Override
    public void DisplayReport(Model model, Random rng) throws Exception {
        DataSequence seq = generate(1).get(0);
        model.resetState();
        Graph g = new Graph(false);
        String seqreal="Real seq: ";
        String seqpred="Pred seq: ";

        int total=0;
        int totalmatch=0;


        for (DataStep step : seq.steps) {
            Matrix output = model.forward(step.input, g);
            if (step.targetOutput != null) {
                int x=getPosition(step.targetOutput.w);
                int y=getPosition(output.w);
                seqreal+=x+", ";
                seqpred+=y+", ";
                if(x==y){
                    totalmatch++;
                }
                total++;
            }
        }
        System.out.println(seqreal);
        System.out.println(seqpred);
        System.out.println("matched: "+totalmatch+ " / "+total);


    }

    @Override
    public Nonlinearity getModelOutputUnitToUse() {
        return new LinearUnit();
    }
}
