package org.kevoree.brain.reinforcement.example;
/* module for cat and mouse game.  used by applet, very specific parameters
   used by cat and mouse game applet.  controls RLearner. */

import javax.swing.*;

public class RLController extends Thread {
	public RLearner learner;
	public int epochswaiting = 0, epochsdone = 0, totaldone = 0;
	long delay;
	int UPDATE_EPOCHS = 100;
	
	public boolean newInfo;
	
	public RLController( RLWorld world, long waitperiod) {
		// create RLearner
		learner = new RLearner(world);
		delay = waitperiod;
	}
	
	public void run() {
		try {
			System.out.println("Learning started");
			while(true) {
			 	if (epochswaiting > 0) {
					System.out.println("Running "+epochswaiting+" epochs");
					learner.running = true;
					while(epochswaiting > 0) {
						epochswaiting--;
						epochsdone++;
						learner.runEpoch();
					}
					System.out.println("Learning ended");

					totaldone += epochsdone;
					epochsdone = 0;
					learner.running = false;
					newInfo = true;
				}

				
				sleep(delay);
			}
		} catch (InterruptedException e) {
			System.out.println("Controller interrupted.");
		}
	}
	
	public void setEpisodes(int episodes) { 
		System.out.println("Setting "+episodes+" episodes");
		this.epochswaiting += episodes;
	}
	public void stopLearner() {
		System.out.println("Stopping learner.");
		newInfo = false;
		epochswaiting = 0;
		totaldone += epochsdone;
		epochsdone = 0;


		learner.running = false;
	}
	
	public synchronized RLPolicy resetLearner() {
		totaldone = 0;
		epochsdone = 0;
		epochswaiting = 0;

		return learner.newPolicy();		
	}
	
}
