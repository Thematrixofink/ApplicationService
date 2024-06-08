package com.hitices.pressure.common;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.Calculator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PercentageCalculator extends Calculator {


    private ArrayList<Long> responseTimes;


    public PercentageCalculator() {
        super();
        responseTimes = new ArrayList<>();
    }

    public PercentageCalculator(String label) {
        super(label);
        responseTimes = new ArrayList<>();
    }

    @Override
    public void addSample(SampleResult res) {
        super.addSample(res);
        synchronized (responseTimes) {
            responseTimes.add(res.getTime());
        }
    }

    @Override
    public void clear() {
        super.clear();
//        responseTimes.clear();
    }


    public double[] getPercentile(double[] tileList) {
        double[] res = new double[tileList.length];
        Collections.sort(responseTimes);
        for(int i = 0; i < tileList.length; i++) {
            double tile = tileList[i];
            int index = (int) (tile * responseTimes.size());
            res[i] =  responseTimes.get(index - 1);
        }
        return res;
    }

}
