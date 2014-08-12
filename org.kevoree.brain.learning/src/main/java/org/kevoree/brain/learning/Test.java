package org.kevoree.brain.learning;

import org.kevoree.brain.api.InputVector;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class Test {
    public static void main (String[] args){

        double[] temp1 = {1.0,2.0,3.0};
        double[] temp2 = {4.0,5.0,6.0};
        double[] temp3 = {7.0,8.0,9.0};
        double[] temp4 = {10.0,11.0,12.0};
        double[] temp5 = {13.0,14.0,15.0};

        InputVector iv1 = new InputVector();
        InputVector iv2 = new InputVector();
        InputVector iv3 = new InputVector();
        InputVector iv4 = new InputVector();
        InputVector iv5 = new InputVector();

        iv1.setFeatures(temp1);
        iv2.setFeatures(temp2);
        iv3.setFeatures(temp3);
        iv4.setFeatures(temp4);
        iv5.setFeatures(temp5);

        iv1.setSupervisedClass(0);
        iv1.setSupervisedClass(0);





    }
}
