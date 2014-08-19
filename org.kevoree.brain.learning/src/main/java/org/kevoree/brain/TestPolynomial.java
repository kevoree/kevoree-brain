package org.kevoree.brain;

import org.kevoree.brain.util.PolynomialFit;

/**
 * Created by assaa_000 on 8/19/2014.
 */
public class TestPolynomial {
    public static void main(String [] arg){
        PolynomialFit pf= new PolynomialFit(3);

        double[] t={1, 2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
        double[] val={17.5,21.6,6.5,-41.6,-136.5,-292,-521.9,-840,-1260.1,-1796,-2461.5,-3270.4,-4236.5,-5373.6,-6695.5,-8216,-9948.9,-11908,-14107.1,-16560};

        //The function was: val = -2.3*(t^3)+4.2*(t^2)+7.6*(t)+8
        //So coefficients should be: -2.3 / 4.2 / 7.6 / 8

        pf.fit(t,val);
        double[] coef= pf.getCoef();

        System.out.println("Function is");
        for (double d : coef) System.out.println(d);

        double test=2.5;
        double result = -2.3* (test*test*test)+4.2*test*test+7.6*test+8;
        System.out.println("Function at t=2.5 "+ pf.calculate(test)+" real value is: "+result);




    }
}
