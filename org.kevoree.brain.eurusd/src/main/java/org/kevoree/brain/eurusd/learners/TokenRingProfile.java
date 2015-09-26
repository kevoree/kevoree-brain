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
    public boolean deactivate = false;

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
        if(deactivate){
            return 0;
        }
        if(total!=0) {
            return sum / total;
        }
        else
            return 0;
    }

    public TokenRingProfile copy(){
        TokenRingProfile tokenRingProfile=new TokenRingProfile(size);
        tokenRingProfile.count=count;
        tokenRingProfile.total=total;
        tokenRingProfile.sum=sum;
        System.arraycopy(tokenring,0,tokenRingProfile.tokenring,0,size);

        return tokenRingProfile;
    }
}
