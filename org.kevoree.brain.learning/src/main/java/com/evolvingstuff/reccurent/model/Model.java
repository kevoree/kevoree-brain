package com.evolvingstuff.reccurent.model;
import java.io.Serializable;
import java.util.List;

import com.evolvingstuff.reccurent.matrix.Matrix;
import com.evolvingstuff.reccurent.autodiff.Graph;


public interface Model extends Serializable {
	Matrix forward(Matrix input, Graph g) throws Exception;
	void resetState();
	List<Matrix> getParameters();
}
