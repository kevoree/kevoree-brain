package org.kevoree.brain.reinforcement.example;

import java.awt.*;
import javax.swing.*;

public class CatAndMouseGame extends Thread {
	long delay;
	RLPolicy policy;
	CatAndMouseWorld world;
	static final int GREEDY=0, SMART=1; // type of mouse to use
	int mousetype = SMART;

	public boolean gameOn = false, single=false, gameActive, newInfo = false;

	public CatAndMouseGame(long delay, CatAndMouseWorld w, RLPolicy policy) {
		world = w;

		this.delay = delay;
		this.policy = policy;
	}

	/* Thread Functions */
	public void run() {
		System.out.println("--Game thread started");
		// start game
		try {
			while(true) {
				while(gameOn) {
					gameActive = true;
					resetGame();
					runGame();
					gameActive = false;
					newInfo = true;
					sleep(delay);
				}
				sleep(delay);
			}
		} catch (InterruptedException e) {
			System.out.println("interrupted.");
		}
		System.out.println("== Game finished.");
	}

	public void runGame() {
		while(!world.endGame()) {
			//System.out.println("Game playing. Making move.");
			int action=-1;
			if (mousetype == GREEDY) {
				action = world.mouseAction();
			} else if (mousetype == SMART) {
				action = policy.getBestAction(world.getState());
			} else {
				System.err.println("Invalid mouse type:"+mousetype);
			}
			world.getNextState(action);

			//a.updateBoard();

			try {
				sleep(delay);
			} catch (InterruptedException e) {
				System.out.println("interrupted.");
			}
		}

		// turn off gameOn flag if only single game
		if (single) gameOn = false;
	}

	public void interrupt() {
		super.interrupt();
		System.out.println("(interrupt)");
	}
	
	/* end Thread Functions */

	public void setPolicy(RLPolicy p) {	policy = p; }

	public Dimension getMouse() { return new Dimension(world.mx, world.my); }
	public Dimension getCat() { return new Dimension(world.cx, world.cy); }
	public Dimension getCheese() { return new Dimension(world.chx, world.chy); }
	public Dimension getHole() { return new Dimension(world.hx, world.hy); }
	public boolean[][] getWalls() { return world.walls; }

	public void makeMove() {
		world.moveMouse();
		world.moveCat();
	}

	public void resetGame() {
		world.resetState();
	}
}

