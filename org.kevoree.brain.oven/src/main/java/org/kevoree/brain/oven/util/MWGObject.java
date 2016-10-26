package org.kevoree.brain.oven.util;


import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.struct.Matrix;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 03/10/16.
 */
public class MWGObject {
    private ArrayList<MWGAttribute> _attributes = new ArrayList<MWGAttribute>();
    private HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

    private long _initTime;
    private long _lastTime;
    private int _rate;


    public void add(MWGAttribute att) {
        _attributes.add(att);
        dictionary.put(att.getName(), _attributes.size() - 1);
    }

    public ArrayList<MWGAttribute> getAttributes() {
        return _attributes;
    }



    public void generateCsv(String file) {
        try {
            PrintWriter out = new PrintWriter(new File(file));
            for (int i = 0; i < _attributes.size(); i++) {
                if (i == _attributes.size() - 1) {
                    out.print(_attributes.get(i).getName());
                } else {
                    out.print(_attributes.get(i).getName() + ",");
                }
            }
            out.println();

            long dt = _lastTime - _initTime;
            dt = dt / _rate;

            for (long t = _initTime; t <= _lastTime; t += dt) {
                out.print(t + ",");
                for (int i = 0; i < _attributes.size(); i++) {
                    if (i == _attributes.size() - 1) {
                        out.print(_attributes.get(i).getValue(t));
                    } else {
                        out.print(_attributes.get(i).getValue(t) + ",");
                    }
                }
                out.println();
            }
            out.flush();
            out.close();
            System.out.println("Csv exported");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void calculateTimeStats(){
        for (int i = 0; i < _attributes.size(); i++) {
            if (i == 0) {
                _initTime = _attributes.get(i).getFirstTime();
                _lastTime = _attributes.get(i).getLastTime();
                _rate = _attributes.get(i).getTimePoints();
            } else {
                if (_attributes.get(i).getFirstTime() < _initTime) {
                    _initTime = _attributes.get(i).getFirstTime();
                }
                if (_attributes.get(i).getLastTime() > _lastTime) {
                    _lastTime = _attributes.get(i).getLastTime();
                }
                if (_attributes.get(i).getTimePoints() > _rate) {
                    _rate = _attributes.get(i).getTimePoints();
                }
            }
        }
        System.out.println("Data range from: " + _initTime + " till: " + _lastTime + " max number of points: " + _rate);
    }

    public Matrix generateMatrix() {
        long dt = _lastTime - _initTime;
        dt = dt / _rate;
        int init = 2;
        Matrix m = VolatileMatrix.wrap(null, _rate+1, _attributes.size() - init);

        int count = 0;
        for (long t = _initTime; t <= _lastTime; t += dt) {
            for (int i = init; i < _attributes.size(); i++) {
                m.set(count, i-init, (Double) _attributes.get(i).getValue(t));
            }
            count++;
        }
        System.out.println("Matrix created");
        return m;
    }


}
