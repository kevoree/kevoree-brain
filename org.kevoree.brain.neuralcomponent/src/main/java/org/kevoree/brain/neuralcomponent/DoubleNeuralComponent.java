package org.kevoree.brain.neuralcomponent;

import org.kevoree.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

@ComponentType
@Library(name = "NeuralComponent")
public class DoubleNeuralComponent extends NeuralComponent{

    @Input
    public void in(Object i) {
       CommunicationMessage cm= (CommunicationMessage) i;
        inputVal.put( cm.getComponentName(),(Double) cm.getValue());
        double result=0;
        for(String w: weights.keySet())
        {
            if(inputVal.containsKey(w))
                result+= weights.get(w)*((Double) inputVal.get(w));
        }


        CommunicationMessage res = new CommunicationMessage();
        res.setComponentName(context.getInstanceName());
        res.setValue(result);
        out.send(res);
    }

}



