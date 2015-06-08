package org.kevoree.brain.reinforcement.example;


import java.util.Random;

/**
 * Created by assaad on 08/06/15.
 */
public class Maze implements RLWorld {
    private static final int NUM_OBJECTS = 1 ;
    private static final int NUM_ACTIONS = 6 ;
    private int bx=NUM_ACTIONS;

    int[][] Rmap=new int[NUM_ACTIONS][NUM_ACTIONS];

    int[] stateArray= new int[NUM_OBJECTS];

    int room;
    private double waitingReward;

    public void init(){

        for (int i=0;i<NUM_ACTIONS;i++){
            for(int j=0;j<NUM_ACTIONS;j++){
                Rmap[i][j]=-1;
            }
        }
        Rmap[0][4]=0;
        Rmap[1][3]=0;
        Rmap[1][5]=0;
        Rmap[2][3]=0;
        Rmap[3][1]=0;
        Rmap[3][2]=0;
        Rmap[3][4]=0;
        Rmap[4][0]=0;
        Rmap[4][3]=0;
        Rmap[4][5]=0;
        Rmap[5][0]=0;
        Rmap[5][4]=0;
        Rmap[5][5]=0;

        randPos();
    }

    @Override
    public int[] getDimension() {
        int[] retDim = new int[NUM_OBJECTS+1];
        int i;
        for (i=0; i<NUM_OBJECTS;) {
            retDim[i++] = bx;
        }
        retDim[i] = NUM_ACTIONS;

        return retDim;
    }

    @Override
    public int[] getNextState(int action) {
        if (legal(action)) {
            // move agent
            room=action;
        } else {
            //System.err.println("Illegal action: "+action);
        }
        // update world

        waitingReward = calcReward();
        stateArray= new int[NUM_OBJECTS];
        return getState();
    }

    public double calcReward() {
       if(room==(NUM_ACTIONS-1)){
           return 100;
       }
        return 0;
    }


    private boolean legal(int action) {
        return Rmap[stateArray[0]][action]==0;

    }

    @Override
    public double getReward() {
        return calcReward();
    }

    @Override
    public boolean validAction(int action) {
        return legal(action);
    }

    @Override
    public boolean endState() {
        return stateArray[0]==NUM_ACTIONS-1;
    }

    @Override
    public int[] resetState() {
        randPos();
        return getState();
    }

    private void randPos() {
        Random rand=new Random();
        room=rand.nextInt(NUM_ACTIONS-1);
    }

    @Override
    public double getInitValues() {
        return 0;
    }

    public int[] getState() {
        stateArray[0] = room;
        return stateArray;
    }
}
