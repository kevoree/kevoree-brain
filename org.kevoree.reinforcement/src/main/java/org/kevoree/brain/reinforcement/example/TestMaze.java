package org.kevoree.brain.reinforcement.example;

/**
 * Created by assaad on 08/06/15.
 */
public class TestMaze {
    public static void main(String[] args){
        int size=5;
        Maze world=new Maze();
        world.init();

        long delay =100;
        RLController rlc = new RLController(world,delay);
        rlc.learner.setLearningMethod(RLearner.SARSA);
        rlc.learner.setGamma(0.9);
        rlc.learner.setEpsilon(0.1);
        rlc.learner.setAlpha(0.6);
        //  CatAndMouseGame game = new CatAndMouseGame(delay,world,rlc.learner.policy);
        rlc.start();
        rlc.setEpisodes(500000);

    }
}
