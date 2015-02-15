package org.kevoree.brain.imageprocessnew.structure;

/**
 * Created by assaa_000 on 15/02/2015.
 */
public class KeyDuplicateException extends Exception {

    protected KeyDuplicateException() {
        super("Key already in tree");
    }
}