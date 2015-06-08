package org.kevoree.brain.reinforcement.example;

import java.util.Vector;

class sampleWorlds {
	public Vector samples;
	
	public sampleWorlds() {
		samples = new Vector();
		
		String title;
		
		// sample wall sets:
		
		title = "Wall (5x5)";
		boolean[][] wallsA = {{false, false, false, false, false},
		         {false, false, true , false, false},
		         {false, false, true , false, false},
		         {false, false, true , false, false},
		         {false, false, false, false, false}};
		samples.addElement(new wallSample(wallsA, title));
							  
		title = "Cross (5x5)";
		boolean[][] wallsB = {{false, false, false, false, false},
		         {false, false, true , false, false},
		         {false, true , true , true , false},
		         {false, false, true , false, false},
		         {false, false, false, false, false}};
		samples.addElement(new wallSample(wallsB, title));
							  
		title = "Corridors (8x8)";
		boolean[][] wallsCC = {{true , false, true , false, true , false, true , false},
		         {false, true , false, true , false, true , false, true},
		         {false, true , false, true , false, true , false, true},
		         {false, true , false, true , false, true , false, true},
		         {false, true , false, true , false, true , false, true},
		         {false, true , false, true , false, true , false, true},
		         {false, true , false, true , false, true , false, true},
		         {true , false, true , false, true , false, true , false}};
		samples.addElement(new wallSample(wallsCC, title));
							  
		title = "Trap (8x8)";
		boolean[][] wallsC = {{false, false, false, false, false, false, false, false},
		         {false, false, false, false, false, false, false, false},
		         {false, false, true , true , true , true , true , false},
		         {false, false, true , false, false, false, true , false},
		         {false, false, true , false, true , true , false, false},
		         {false, false, true , false, false, true , false, false},
		         {false, false, true , true , true , true , false, false},
		         {false, false, false, false, false, false, false, false}};
		samples.addElement(new wallSample(wallsC, title));
							  
		title = "Boxes (8x8)";
		boolean[][] wallsD = {{false, false, false, false, false, false, false, false},
		         {false, true , true , true , false, true , true , false},
		         {false, true , false, false, false, false, true , false},
		         {false, false, false, true , true , false, true , false},
		         {false, true , false, true , true , false, false, false},
		         {false, true , false, false, false, false, true , false},
		         {false, true , true , false, true , true , true , false},
		         {false, false, false, false, false, false, false, false}};
		samples.addElement(new wallSample(wallsD, title));
							  
	}

	public boolean[][] getWalls(int i) { 
		return ((wallSample)samples.elementAt(i)).walls;
	}
	public String getTitle(int i) { 
		return ((wallSample)samples.elementAt(i)).title;
	}
	
	public int numSamples() { return samples.size(); }
}

class wallSample {
	public boolean[][] walls;
	public String title;
	
	public wallSample(boolean[][] w, String t) {walls = w; title = t;}	
}
