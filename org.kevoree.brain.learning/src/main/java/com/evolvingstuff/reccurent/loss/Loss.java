package com.evolvingstuff.reccurent.loss;

import java.io.Serializable;

import com.evolvingstuff.reccurent.matrix.Matrix;

public interface Loss extends Serializable {
	void backward(Matrix actualOutput, Matrix targetOutput) throws Exception;
	double measure(Matrix actualOutput, Matrix targetOutput) throws Exception;
}
