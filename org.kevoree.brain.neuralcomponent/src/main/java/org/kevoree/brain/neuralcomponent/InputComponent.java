package org.kevoree.brain.neuralcomponent;

import org.kevoree.annotation.*;

@ComponentType
@Library(name = "NeuralComponent")
public class InputComponent {

    @Param(defaultValue = "Default Content")
    String message;


    @Output
    org.kevoree.api.Port out;



    @Start
    public void start() {}

    @Stop
    public void stop() {}

    @Update
    public void update() {System.out.println("Param updated!");}

}



