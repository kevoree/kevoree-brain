package com.evolvingstuff.reccurent.model;

import java.util.ArrayList;
import java.util.List;

import com.evolvingstuff.reccurent.matrix.Matrix;
import com.evolvingstuff.reccurent.autodiff.Graph;
import com.sun.org.apache.xpath.internal.operations.Mod;

public class NeuralNetwork implements Model {

	private static final long serialVersionUID = 1L;
	List<Model> layers = new ArrayList<>();
	
	public NeuralNetwork(List<Model> layers) {
		this.layers = layers;
	}


	public NeuralNetwork simplify(){
		List<Model> res=new ArrayList<>();

		for(int i=0;i<layers.size();i++){
			Model m=layers.get(i);
			if(m instanceof NeuralNetwork){
				NeuralNetwork nn = ((NeuralNetwork) m);
				for(int j=0;j<nn.layers.size();j++){
					res.add(nn.layers.get(j));
				}
			}
			else{
				res.add(m);
			}
		}
		this.layers=res;
		return this;
	}


	@Override
	public Matrix forward(Matrix input, Graph g) throws Exception {
		Matrix prev = input;
		for (Model layer : layers) {
			prev = layer.forward(prev, g);
		}
		return prev;
	}

	@Override
	public void resetState() {
		for (Model layer : layers) {
			layer.resetState();
		}
	}

	@Override
	public List<Matrix> getParameters() {
		List<Matrix> result = new ArrayList<>();
		for (Model layer : layers) {
			result.addAll(layer.getParameters());
		}
		return result;
	}
}
