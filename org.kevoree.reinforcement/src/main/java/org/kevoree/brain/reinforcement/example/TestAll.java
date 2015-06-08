package org.kevoree.brain.reinforcement.example;

/**
 * Created by assaad on 08/06/15.
 */
public class TestAll {
    public static void main(String[] args){
        int size=5;
        CatAndMouse2 world=new CatAndMouse2(size,size,3);
        world.printgame();
        long delay =100;
        RLController rlc = new RLController(world,delay);
        rlc.learner.setAlpha(0.6);
      //  CatAndMouseGame game = new CatAndMouseGame(delay,world,rlc.learner.policy);
        rlc.start();
        rlc.setEpisodes(5000000);




    }
}
