package com.taka.pricer;

public class PricingParams {
    private final int simulationNum;
    private final int seed;
    private final double spotShiftRatio;
    private final double volShiftRatio;

    public PricingParams(int simulationNum, int seed, double spotShiftRatio, double volShiftRatio) {
        this.simulationNum = simulationNum;
        this.seed = seed;
        this.spotShiftRatio = spotShiftRatio;
        this.volShiftRatio = volShiftRatio;
    }

    public int getSimulationNum() {
        return simulationNum;
    }

    public int getSeed() {
        return seed;
    }

    public double getSpotShiftRatio() {
        return spotShiftRatio;
    }

    public double getVolShiftRatio() {
        return volShiftRatio;
    }

    @Override
    public String toString() {
        return "PricingParams{" +
                "simulationNum=" + simulationNum +
                ", seed=" + seed +
                ", spotShiftRatio=" + spotShiftRatio +
                ", volShiftRatio=" + volShiftRatio +
                '}';
    }
}
