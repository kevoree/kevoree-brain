package org.kevoree.brain.learning.livelearning.neurons;

import java.text.DecimalFormat;

/**
 * Created by assaad on 21/09/16.
 */
public class TestNeuronAsMemory {

    //This test is kind if memory test, to show that if you give the same input and output
    // several times to the NN, it will end up memorizing the output. -> Overfitting the output to the input
    // I run a loop of 1000 times, where each time i give the same input output, and at the end we can see how
    // the NN converges to output

    public static void main(String[] args) {

        NeuronNode root = NeuronNode.create(2, 4, 2, 3);
        //root.print();
        double[] input = {0.05, 0.10};
        double[] output = {0.01, 0.99, 0.5, 0.3};

        for (int i = 0; i < 1000; i++) {
            root.predict(input);
            root.learn(input, output, 0.5);
        }

        double[] res = root.predict(input);
        System.out.println();

        DecimalFormat df = new DecimalFormat("#.####");

        System.out.println("Real values are: " + df.format(output[0])+ " "+ df.format(output[1])+ " "+ df.format(output[2])+ " "+ df.format(output[3])+ " ");
        System.out.println("Predicted values are: " + df.format(res[0])+ " "+df.format(res[1])+ " "+df.format(res[2])+ " "+df.format(res[3]));
        //root.print();
        int x = 0;


    }
}
