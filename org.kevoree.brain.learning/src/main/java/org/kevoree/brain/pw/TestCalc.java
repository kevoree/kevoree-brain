package org.kevoree.brain.pw;

import com.evolvingstuff.reccurent.autodiff.Graph;
import com.evolvingstuff.reccurent.loss.LossSumOfSquares;
import com.evolvingstuff.reccurent.matrix.Matrix;
import com.evolvingstuff.reccurent.model.Model;
import com.evolvingstuff.reccurent.model.SigmoidUnit;
import com.evolvingstuff.reccurent.trainer.Trainer;

/**
 * Created by assaad on 24/01/2017.
 */
public class TestCalc {
    public static void main(String[] arg) throws Exception {

        int input = 4;
        int output = 2;

        Model m = new TestLayer(input, output, new SigmoidUnit());

        Graph g = new Graph(true);
        Matrix inMatrix = new Matrix(input, 1);

        double val = 0.03;
        for (int i = 0; i < input; i++) {
            inMatrix.setW(i, 0, val);
            val += 0.03;
        }

        Matrix outMat = new Matrix(output, 1);
        val = 0.07;
        for (int i = 0; i < output; i++) {
            outMat.setW(i, 0, val);
            val += 0.07;
        }

        double delta=0.01;

        for(int i=0;i<100;i++) {
            Matrix calc = m.forward(inMatrix, g);
            LossSumOfSquares lsq = new LossSumOfSquares();


            double loss = lsq.measure(calc, outMat);
            System.out.println("loss: " + loss);


            lsq.backward(calc, outMat);
            g.backward();



            Trainer.updateModelParams(m, delta);
        }





        int x=0;
    }

}
