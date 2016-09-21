package org.kevoree.brain.learning.livelearning.neurons;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by assaad on 21/09/16.
 */
public class NeuronNode {


    private static Random random = new Random();


    public static String NAME = "NeuralNode";


    private static int idCounter = -1;
    private static int msgIdCounter = -1;

    private static int generateId() {
        idCounter++;
        return idCounter;
    }

    private int generateMsgId() {
        msgIdCounter++;
        return msgIdCounter;
    }

    private long id;
    private int layerId;

    private NeuronType type;

    private ArrayList<NeuronNode> inputs = new ArrayList<NeuronNode>();
    private ArrayList<NeuronNode> outputs = new ArrayList<NeuronNode>();
    private HashMap<Long, Integer> inputDictionary;
    private HashMap<Long, Integer> outputDictionary;
    private double[] weights;
    private double[] forwardBuffer;
    private double integrationBuffer;
    private double activationBuffer;
    private double[] backwardBuffer;


    private NeuronNode(long id, int layerId, NeuronType type) {
        this.id = id;
        this.layerId = layerId;
        this.type = type;
    }


    public void learn(double[] input, double[] output, double learningRate) {
        int msgId = generateMsgId();

        for (int i = 0; i < inputs.size(); i++) {
            inputs.get(i).injectOutput(id, msgId, output[i]);
        }

        for (int i = 0; i < outputs.size(); i++) {
            outputs.get(i).receive(id, msgId, input[i], true, learningRate);
        }

        System.out.println("Learning done!");
    }


    private void injectOutput(long id, int msgId, double output) {
        backwardBuffer[0] = output;
    }


    public void print() {
        printInternal(0);
    }


    private void printArray(double[] array, String name) {
        System.out.print(name + ": [");
        for (int j = 0; j < array.length; j++) {
            DecimalFormat df = new DecimalFormat("#.####");
            System.out.print(df.format(array[j]));
            if (j != array.length - 1) {
                System.out.print(", ");
            } else {
                System.out.print("]");
            }
        }
    }

    private void printInternal(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.print(type + " id: " + id + ", layer: " + layerId);

        System.out.print(", ");
        printArray(backwardBuffer, "backwardBuffer");

        System.out.print(", ");
        printArray(forwardBuffer, "forwardbuffer");

        if (type != NeuronType.INPUT && type!=NeuronType.ROOT) {
            System.out.print(", ");
            printArray(weights, "weights");
        }

        System.out.println();
        if (type != NeuronType.OUTPUT) {
            for (int i = 0; i < outputs.size(); i++) {
                outputs.get(i).printInternal(level + 1);
            }
        }

    }


    public static NeuronNode create(int inputs, int outputs, int hiddenlayers, int nodesPerLayer) {
        NeuronNode root = new NeuronNode(generateId(), -1, NeuronType.ROOT);

        ArrayList<NeuronNode> internalNodes = new ArrayList<NeuronNode>(inputs + outputs + hiddenlayers * nodesPerLayer + 1);
        internalNodes.add(root);
        int layer = 0;

        ArrayList<NeuronNode> previousLayer = new ArrayList<NeuronNode>();

        //create input layers:
        for (int i = 0; i < inputs; i++) {
            NeuronNode inputNode = new NeuronNode(generateId(), layer, NeuronType.INPUT);
            root.forwardConnect(inputNode);
            internalNodes.add(inputNode);
            previousLayer.add(inputNode);
        }

        ArrayList<NeuronNode> nextLayer = new ArrayList<NeuronNode>();
        layer++;


        //Create hidden layers
        for (int i = 0; i < hiddenlayers; i++) {
            for (int j = 0; j < nodesPerLayer; j++) {
                NeuronNode hidden = new NeuronNode(generateId(), layer, NeuronType.HIDDEN);
                nextLayer.add(hidden);
                internalNodes.add(hidden);

                for (int k = 0; k < previousLayer.size(); k++) {
                    previousLayer.get(k).forwardConnect(hidden);
                }
            }

            previousLayer = nextLayer;
            nextLayer = new ArrayList<NeuronNode>();
            layer++;
        }

        //Create output layers
        for (int i = 0; i < outputs; i++) {
            NeuronNode output = new NeuronNode(generateId(), layer, NeuronType.OUTPUT);
            for (int k = 0; k < previousLayer.size(); k++) {
                previousLayer.get(k).forwardConnect(output);
            }
            output.forwardConnect(root);
            internalNodes.add(output);
        }

        for (int i = 0; i < internalNodes.size(); i++) {
            internalNodes.get(i).initWeightsRadomly(0.1);
        }


        return root;
    }


    private double IntegrationFct(double[] buffer, double[] weights) {
        // Should be a big use case according to the type of Neural node calculation here
        // by default this mult fct without truncation
        //todo implement a generic function interface

        double value = 0;
        for (int i = 0; i < weights.length - 1; i++) {
            value += weights[i] * buffer[i];
        }
        value += weights[weights.length - 1];
        return value;
    }

    private double activationFunction(double value) {
        return 1 / (1 + Math.exp(-value)); //Sigmoid by default, todo to be changed later to a generic activation
    }

    private double derivateActivationFunction(double fctVal, double value) {
        return fctVal * (1 - fctVal);
    }


    private double calculateErr(double calculated, double target) {
        return (target - calculated) * (target - calculated) / 2;
    }

    private double calculateDerivativeErr(double calculated, double target) {
        return -(target - calculated);
    }

    //todo add time sensitivity
    private void receive(long senderId, int inputMsgId, double msg, boolean forwardPropagation, double learningRate) {
        if (forwardPropagation) {
            if (type == NeuronType.INPUT) {
                for (int i = 0; i < outputs.size(); i++) {
                    outputs.get(i).receive(this.id, inputMsgId, msg, forwardPropagation, learningRate);
                }
            } else if (type == NeuronType.HIDDEN) {
                int pos = inputDictionary.get(senderId);
                forwardBuffer[pos] = msg;
                forwardBuffer[inputs.size()]++;
                if (forwardBuffer[inputs.size()] == inputs.size()) {
                    integrationBuffer = IntegrationFct(forwardBuffer, weights);
                    activationBuffer = activationFunction(integrationBuffer);

                    //System.out.println("Node " + id + " forward calculated: " + activationBuffer);
                    forwardBuffer[inputs.size()] = 0;
                    for (int i = 0; i < outputs.size(); i++) {
                        outputs.get(i).receive(this.id, inputMsgId, activationBuffer, forwardPropagation, learningRate);
                    }
                    forwardBuffer[inputs.size()] = 0;
                }
            } else if (type == NeuronType.OUTPUT) {
                int pos = inputDictionary.get(senderId);
                forwardBuffer[pos] = msg;
                forwardBuffer[inputs.size()]++;
                if (forwardBuffer[inputs.size()] == inputs.size()) {
                    integrationBuffer = IntegrationFct(forwardBuffer, weights);
                    activationBuffer = activationFunction(integrationBuffer);
                    double err = calculateErr(activationBuffer, backwardBuffer[0]);

                    for (int i = 0; i < outputs.size(); i++) {
                        outputs.get(i).receive(this.id, inputMsgId, err, forwardPropagation, learningRate);
                    }
                    forwardBuffer[inputs.size()] = 0;

                }
            } else if (type == NeuronType.ROOT) {
                int pos = inputDictionary.get(senderId);
                forwardBuffer[pos] = msg;
                forwardBuffer[inputs.size()]++;
                if (forwardBuffer[inputs.size()] == inputs.size()) {
                    double sumErr = 0;
                    for (int i = 0; i < forwardBuffer.length - 1; i++) {
                        sumErr += forwardBuffer[i];
                    }
                    System.out.println("Feed forward done with total error: " + sumErr);
                    //Lunch back prop here

                    for (int i = 0; i < inputs.size(); i++) {
                        inputs.get(i).receive(this.id, inputMsgId, forwardBuffer[i], false, learningRate);
                    }
                    forwardBuffer[inputs.size()] = 0;

                }
            }


        } else {
            if (type == NeuronType.INPUT) {
                int pos = outputDictionary.get(senderId);
                backwardBuffer[pos] = msg;
                backwardBuffer[outputs.size()]++;
                if (backwardBuffer[outputs.size()] == outputs.size()) {
                    inputs.get(0).receive(this.id, inputMsgId, 1.0, false, learningRate);
                    backwardBuffer[outputs.size()] = 0;
                }


            } else if (type == NeuronType.HIDDEN) {
                int pos = outputDictionary.get(senderId);
                backwardBuffer[pos] = msg;
                backwardBuffer[outputs.size()]++;
                if (backwardBuffer[outputs.size()] == outputs.size()) {
                    double delta = 0;
                    for (int i = 0; i < backwardBuffer.length - 1; i++) {
                        delta += backwardBuffer[i];
                    }
                    delta = delta * derivateActivationFunction(activationBuffer, integrationBuffer);
                   // System.out.println();
                    double[] newWeight = new double[weights.length];
                    for (int i = 0; i < weights.length - 1; i++) {
                        newWeight[i] = weights[i] - learningRate * delta * forwardBuffer[i];
                      //  System.out.println("output "+id+": "+newWeight[i]);
                    }
                    newWeight[weights.length - 1] = weights[weights.length - 1] - delta;//update bias
                  //  System.out.println("output "+id+": "+newWeight[newWeight.length - 1]);

                    for (int i = 0; i < inputs.size(); i++) {
                        inputs.get(i).receive(this.id, inputMsgId, delta * weights[i], false, learningRate);
                    }
                    weights = newWeight;
                    backwardBuffer[outputs.size()] = 0;
                    //to update weights after sending

                }

            } else if (type == NeuronType.OUTPUT) {

                double overallDerivative = calculateDerivativeErr(activationBuffer, backwardBuffer[0]);

                //System.out.println("Output Node " + id + " forward calculated: " + activationBuffer + ", expected: " + backwardBuffer[0] + " , error: " + msg + ", derivative: " + overallDerivative);
                forwardBuffer[inputs.size()] = 0;


                // System.out.println();
                double activationDerivative = derivateActivationFunction(activationBuffer, integrationBuffer);
                double delta = overallDerivative * activationDerivative;
                double[] newWeight = new double[weights.length];
                for (int i = 0; i < weights.length - 1; i++) {
                    newWeight[i] = weights[i] - learningRate * delta * forwardBuffer[i];
                    // System.out.println("output "+id+": "+newWeight[i]);
                }
                newWeight[weights.length - 1] = weights[weights.length - 1] - delta;//update bias
                // System.out.println("output "+id+": "+newWeight[newWeight.length - 1]);

                for (int i = 0; i < inputs.size(); i++) {
                    inputs.get(i).receive(this.id, inputMsgId, delta * weights[i], false, learningRate);
                }
                weights = newWeight;
                //to update weights after sending


            } else if (type == NeuronType.ROOT) {
                int pos = outputDictionary.get(senderId);
                backwardBuffer[pos] = msg;
                backwardBuffer[outputs.size()]++;
                if (backwardBuffer[outputs.size()] == outputs.size()) {
                    backwardBuffer[outputs.size()] = 0;
                }
            }

        }


    }


    private long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    private void forwardConnect(NeuronNode nextNode) {
        this.outputs.add(nextNode);
        nextNode.inputs.add(this);
    }

    private NeuronType getType() {
        return type;
    }

    private void setType(NeuronType type) {
        this.type = type;
    }


    private void initVariables() {

        forwardBuffer = new double[inputs.size() + 1];
        backwardBuffer = new double[outputs.size() + 1];


        inputDictionary = new HashMap<Long, Integer>(inputs.size());
        outputDictionary = new HashMap<Long, Integer>(outputs.size());

        for (int i = 0; i < inputs.size(); i++) {
            inputDictionary.put(inputs.get(i).getId(), i);
        }

        for (int i = 0; i < outputs.size(); i++) {
            outputDictionary.put(outputs.get(i).getId(), i);
        }

    }


    private void initWeightsRadomly(double max) {
        if (type == NeuronType.HIDDEN || type == NeuronType.OUTPUT) {
            this.weights = new double[inputs.size() + 1];
            for (int i = 0; i < weights.length; i++) {
                //  weights[i] = random.nextDouble() * max;
                weights[i] = InitRandom.next();
            }
        }
        initVariables();
    }
}
