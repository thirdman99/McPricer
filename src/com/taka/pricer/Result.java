package com.taka.pricer;

import java.util.Objects;

public class Result {
    private final double pv;
    private final double delta;
    private final double gamma;
    private final double vega;

    public Result(double pv, double delta, double gamma, double vega) {
        this.pv = pv;
        this.delta = delta;
        this.gamma = gamma;
        this.vega = vega;
    }

    public double getPv() {
        return pv;
    }

    public double getDelta() {
        return delta;
    }

    public double getGamma() {
        return gamma;
    }

    public double getVega() {
        return vega;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Double.compare(result.pv, pv) == 0 && Double.compare(result.delta, delta) == 0
                && Double.compare(result.gamma, gamma) == 0 && Double.compare(result.vega, vega) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pv, delta, gamma, vega);
    }

    @Override
    public String toString() {
        return "Result{" +
                "pv=" + pv +
                ", delta=" + delta +
                ", gamma=" + gamma +
                ", vega=" + vega +
                '}';
    }
}
