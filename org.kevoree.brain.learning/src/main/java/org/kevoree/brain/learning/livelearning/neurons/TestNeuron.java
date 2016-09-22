package org.kevoree.brain.learning.livelearning.neurons;

/**
 * Created by assaad on 21/09/16.
 */
public class TestNeuron {
    public static void main(String[] args) {

        NeuronNode root = NeuronNode.create(2, 4, 2, 3);
        //root.print();
        double[] input = {0.05, 0.10};
        double[] output = {0.01, 0.99, 0.5, 0.3};

        for(int i=0;i<1000;i++) {
            System.out.println("round "+i);
            root.predict(input);
            root.learn(input, output, 0.5);
            System.out.println();
        }

        root.predict(input);
        System.out.println();
        //root.print();
        int x = 0;



    }
}
