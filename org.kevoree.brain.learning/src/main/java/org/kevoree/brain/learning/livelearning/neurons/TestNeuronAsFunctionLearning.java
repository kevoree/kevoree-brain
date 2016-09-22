package org.kevoree.brain.learning.livelearning.neurons;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by assaad on 21/09/16.
 */
public class TestNeuronAsFunctionLearning {

    //This test is to see if a NN is capable of learning 2 functions: sin and cosine.

    public static void main(String[] args) {

        NeuronNode root = NeuronNode.create(1, 1, 2, 5);
        //root.print();
        double[] input = new double[1];
        double[] output = new double[1];

        Random random = new Random();

        for (int i = 0; i < 500000; i++) {
            input[0] = random.nextDouble();
            output[0] = 0.5 * (Math.sin(input[0] * Math.PI * 2) + 1);


            if (i % 10000 == 0) {
                System.out.println(i + " " + root.err);
            }


            //System.out.println("round "+i);
            root.predict(input);
            root.learn(input, output, 0.5);
            //System.out.println();
        }


        System.out.println();
        for (int i = 0; i < 10; i++) {
            input[0] = random.nextDouble();
            output[0] = 0.5 * (Math.sin(input[0] * Math.PI * 2) + 1);
            double[] res = root.predict(input);
            DecimalFormat df = new DecimalFormat("#.####");
            System.out.println("Real values are: " + df.format(output[0]));
            System.out.println("Predicted values are: " + df.format(res[0]));
            System.out.println();
        }
        //root.print();
        int x = 0;


    }
}
