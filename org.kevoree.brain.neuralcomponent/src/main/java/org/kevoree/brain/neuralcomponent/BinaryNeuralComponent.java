package org.kevoree.brain.neuralcomponent;

import org.kevoree.annotation.*;

import java.util.HashMap;
import java.util.Random;

@ComponentType
@Library(name = "NeuralComponent")
public class BinaryNeuralComponent extends NeuralComponent{

    @Param (defaultValue = "0.5")
    public double threshold=0.5;

    private Random random = new Random();

    @Override
    public void in(Object i) {
       CommunicationMessage cm= (CommunicationMessage) i;
        inputVal.put(cm.getComponentName(), (Boolean) cm.getValue());

        if(weights.containsKey(cm.getComponentName())==false)
        {
            weights.put(cm.getComponentName(), random.nextDouble());
        }


        double result=0;
        for(String w: weights.keySet())
        {
            if(inputVal.containsKey(w)&& ((Boolean)inputVal.get(w)))
                result+= weights.get(w);
        }
        CommunicationMessage res = new CommunicationMessage();
        res.setComponentName(context.getInstanceName());
        Boolean b = (result>threshold) ;
        res.setValue(b);
        out.send(res);
    }

}



