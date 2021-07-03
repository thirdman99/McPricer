package com.taka.marketdata;

public class MarketData implements Cloneable {
    private String quoteDate; // This is also the calc-date of the pricing.
    private double spot; // underlying spot
    private double vol; // volatility
    private double rfr; // risk-free-rate

    public MarketData(String quoteDate, double spot, double vol, double rfr) {
        this.quoteDate = quoteDate;
        this.spot = spot;
        this.vol = vol;
        this.rfr = rfr;
    }

    public MarketData clone() {
        try {
            return (MarketData) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public String getQuoteDate() {
        return quoteDate;
    }

    public void setSpot(double spot) {
        this.spot = spot;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public void setRfr(double rfr) {
        this.rfr = rfr;
    }

    public double getSpot() {
        return spot;
    }

    public double getVol() {
        return vol;
    }

    public double getRfr() {
        return rfr;
    }

    @Override
    public String toString() {
        return "MarketData{" +
                "quoteDate=" + quoteDate +
                ", spot=" + spot +
                ", vol=" + vol +
                ", rfr=" + rfr +
                '}';
    }
}
