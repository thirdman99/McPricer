package com.taka.product;

public abstract class Product {
    private final CallPut callPut;
    private final String expiryDate;
    private final double strike;

    protected Product(CallPut callPut, String expiryDate, double strike) {
        this.callPut = callPut;
        this.expiryDate = expiryDate;
        this.strike = strike;
    }

    abstract public double payout(double spot, boolean knockedIn);

    public CallPut getCallPut() {
        return callPut;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public double getStrike() {
        return strike;
    }

    public boolean isPathDependent() {
        return false;
    }

    public boolean isKnockedIn(double spot) {
        return false;
    }

    // No knock-out feature by default (i.e. NaN).
    public double getKnockOutPayout(double spot) {
        return Double.NaN;
    }

    @Override
    public String toString() {
        return "Product{" +
                "callPut=" + callPut +
                ", expiryDate=" + expiryDate +
                ", strike=" + strike +
                '}';
    }
}
