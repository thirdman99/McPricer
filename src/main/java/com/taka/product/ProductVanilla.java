package com.taka.product;

public class ProductVanilla extends Product {

    public ProductVanilla(CallPut callPut, String expiryDate, double strike) {
        super(callPut, expiryDate, strike);
    }

    @Override
    public double payout(double spot, boolean knockedIn) {
        CallPut callPut = getCallPut();
        if (callPut == CallPut.Call) {
            return Math.max(spot - getStrike(), 0);
        } else if (callPut == CallPut.Put) {
            return Math.max(getStrike() - spot, 0);
        } else {
            throw new IllegalArgumentException("Unexpected CallPut: " + callPut);
        }
    }

    @Override
    public String toString() {
        return "ProductVanilla{}: " + super.toString();
    }
}
