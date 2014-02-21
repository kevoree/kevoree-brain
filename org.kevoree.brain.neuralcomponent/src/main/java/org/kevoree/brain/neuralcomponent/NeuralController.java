package org.kevoree.brain.neuralcomponent;

import org.kevoree.Channel;
import org.kevoree.ComponentInstance;
import org.kevoree.ContainerRoot;
import org.kevoree.annotation.*;
import org.kevoree.api.ModelService;
import org.kevoree.api.handler.UUIDModel;
import org.kevoree.api.handler.UpdateCallback;
import org.kevoree.kevscript.KevScriptEngine;
import org.kevoree.log.Log;

import java.util.ArrayList;
import java.util.Random;


@ComponentType
@Library(name = "NeuralComponent")
public class NeuralController {

    @Param(defaultValue = "Default Content")
    String message;

    @KevoreeInject
    org.kevoree.api.Context context;

    @KevoreeInject
    ModelService modelService;

    @Start
    public void start() {
        System.out.println(generateArchitecture("3,2,3,1"));
        modelService.submitScript(generateArchitecture("3,2,3,1"),new UpdateCallback() {
            @Override
            public void run(Boolean aBoolean) {
                Log.info(aBoolean.toString());
            }
        });
    }



    private String generateRandom(){
        Random random = new Random();
        StringBuffer command = new StringBuffer();

        int MAX=6;
        int x=0;
        //Add 6 components

        try
        {
            for(x=0;x<MAX;x++)
            {
                command.append("add node0.binaryComp"+x+" : BinaryNeuralComponent \n");
            }
            boolean[][] mat = new boolean[MAX][MAX];

            for(int i=0;i<MAX-1;i++)
            {
                command.append("add channel"+i +" : " +"AsyncBroadcast \n");
                command.append("bind node0.binaryComp"+i+".out channel"+i+"\n");
                for(int j=i+1; j<MAX; j++)
                {
                    command.append("bind node0.binaryComp"+j+".in channel"+i+"\n");
                }
            }


        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            Log.info(ex.getMessage());
        }
        return command.toString();
    }

    private String generateArchitecture(String chain){
        StringBuffer command = new StringBuffer();
        String[] stringNumbers= chain.split(",");
        int[]  numbers = new int[stringNumbers.length];
        if(numbers.length<2)
            return "error";

        int i=0;
        for(String s: stringNumbers)
        {
            numbers[i]=Integer.decode(s);
            i++;
            //System.out.println(numbers[0]);
        }


        for(i=0;i<numbers[0];i++)
        {
            command.append("add node0.inputComp"+i+" : InputComponent \n");
            command.append("add channeli"+i +" : " +"AsyncBroadcast \n");
            command.append("bind node0.inputComp"+i+".out channeli"+i+"\n");
        }

        int j;

        for(j=0;j<numbers[1];j++){
            command.append("add node0.binaryComp1"+j+" : BinaryNeuralComponent \n");
            command.append("add channel1"+j +" : " +"AsyncBroadcast \n");
            command.append("bind node0.binaryComp1"+j+".out channel1"+j+"\n");
            for(i=0;i<numbers[0];i++){
                command.append("bind node0.binaryComp1"+j+".in channeli"+i+"\n");
            }
        }

        for(int k=2;k<numbers.length;k++)
        {
            for(j=0;j<numbers[k];j++){
                command.append("add node0.binaryComp"+k+""+j+" : BinaryNeuralComponent \n");
                command.append("add channel"+k+""+j +" : " +"AsyncBroadcast \n");
                command.append("bind node0.binaryComp"+k+""+j+".out channel"+k+""+j+"\n");
                for(i=0;i<numbers[k-1];i++){
                    command.append("bind node0.binaryComp"+k+""+j+".in channel"+(k-1)+""+i+"\n");
                }
            }
        }

        for(j=0;j<numbers[numbers.length-1];j++){
            command.append("add node0.outputComp"+j+" : OutputComponent \n");
            command.append("bind node0.outputComp"+j+".in channel"+(numbers.length-1)+""+j+"\n");
        }






        return command.toString();
    }

    @Stop
    public void stop() {}

    @Update
    public void update() {System.out.println("Param updated!");}

}



