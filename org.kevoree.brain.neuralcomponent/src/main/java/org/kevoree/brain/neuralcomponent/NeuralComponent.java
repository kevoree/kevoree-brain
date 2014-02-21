package org.kevoree.brain.neuralcomponent;

import org.kevoree.annotation.*;

import java.util.HashMap;


public abstract class NeuralComponent {
    // Corresponding weights for the different incoming chains
    protected HashMap<String,Double> weights = new HashMap<String, Double>();
    protected HashMap<String,Object> inputVal = new HashMap<String, Object>();

    @KevoreeInject
    protected org.kevoree.api.Context context;


    public void setWeight(String element, Double weight)
    {
        weights.put(element,weight);
    }

    @Output
    org.kevoree.api.Port out;

    @Input
    public abstract void in(Object i);

    @Start
    public void start() {}

    @Stop
    public void stop() {}

    @Update
    public void update() {System.out.println("Param updated!");}

}



