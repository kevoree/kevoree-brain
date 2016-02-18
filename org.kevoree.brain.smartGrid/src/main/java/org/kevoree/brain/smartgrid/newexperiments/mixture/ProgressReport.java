package org.kevoree.brain.smartgrid.newexperiments.mixture;

/**
 * Created by assaad on 18/02/16.
 */
public interface ProgressReport {
    public void updateProgress(double progress);
    public boolean isCancelled();
}
