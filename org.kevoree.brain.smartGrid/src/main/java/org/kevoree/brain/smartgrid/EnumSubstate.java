package org.kevoree.brain.smartgrid;

/**
 * Created by assaad on 20/02/15.
 */
public class EnumSubstate {

    public int[] getCounter() {
        return counter;
    }

    public void setCounter(int[] counter) {
        this.counter = counter;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    private int[] counter;
    private int total=0;

    public void initialize(int number){
        counter=new int[number];
    }





    public double calculateProbability(Object feature) {
        Integer res=(Integer) feature;
        double p=counter[res];
        if(total!=0){
            return p/total;
        }
        else {
            return 0;
        }
    }


    public void train(Object feature) {
        Integer res=(Integer) feature;
        counter[res]++;
        total++;
    }


}
