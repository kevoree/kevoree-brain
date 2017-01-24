package org.kevoree.brain.pw;

import com.evolvingstuff.reccurent.autodiff.Graph;
import com.evolvingstuff.reccurent.matrix.Matrix;
import com.evolvingstuff.reccurent.model.Model;
import com.evolvingstuff.reccurent.model.Nonlinearity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by assaad on 24/01/2017.
 */
public class TestLayer implements Model {

    private static final long serialVersionUID = 1L;
    Matrix W;
    Matrix b;
    Nonlinearity f;

    public TestLayer(int inputDimension, int outputDimension, Nonlinearity f) {
        W = new Matrix(outputDimension, inputDimension);

        double val = 0.01;
        for (int i = 0; i < W.rows; i++) {
            for (int j = 0; j < W.cols; j++) {
                W.setW(i, j, val);
                val += 0.01;
            }
        }

        b = new Matrix(outputDimension, 1);
        val = 0.1;
        for (int i = 0; i < b.rows; i++) {
            b.setW(i, 0, val);
            val += 0.1;
        }
        this.f = f;
    }

    @Override
    public Matrix forward(Matrix input, Graph g) throws Exception {
        Matrix sum = g.add(g.mul(W, input), b);
        Matrix out = g.nonlin(f, sum);
        return out;
    }

    @Override
    public void resetState() {

    }

    @Override
    public List<Matrix> getParameters() {
        List<Matrix> result = new ArrayList<>();
        result.add(W);
        result.add(b);
        return result;
    }
}
