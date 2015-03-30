package org.kevoree.brain.smartgrid.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by assaad on 23/02/15.
 */
public class SolutionComparator implements Comparable<SolutionComparator> {
    public double score;
    public String id;

    @Override
    public int compareTo(SolutionComparator o) {
        return Double.compare(o.score,this.score);
    }

    public static int rank (ArrayList<SolutionComparator> sol, String id){
        Collections.sort(sol);
        for(int i=0; i<sol.size();i++){
            if(sol.get(i).id.equals(id)){
                return i+1;
            }
        }
        return sol.size()+1;
    }

    public static boolean contain (ArrayList<SolutionComparator> sol, String id, int count){
        Collections.sort(sol);
        for(int i=0; i<count;i++){
            if(sol.get(i).id.equals(id)){
                return true;
            }
        }
        return false;
    }
}
