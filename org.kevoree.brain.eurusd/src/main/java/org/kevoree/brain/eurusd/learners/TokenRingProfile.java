package org.kevoree.brain.eurusd.learners;

/**
 * Created by assaa_000 on 25/09/2015.
 */
public class TokenRingProfile {
    private int size;

    private int count;
    private double[] tokenring;

    private double sum;
    private int total;

    public TokenRingProfile(int size){
        this.size=size;
        tokenring=new double[size];
    }

    public boolean isFull(){
        return total==size;
    }

    public void feed(double value){
        sum=sum-tokenring[count];
        tokenring[count]=value;
        sum=sum+value;
        if(total<size){
            total++;
        }
        count=(count+1)%size;
    }

    public double getAvg(){
        if(total!=0) {
            return sum / total;
        }
        else
            return 0;
    }
}
