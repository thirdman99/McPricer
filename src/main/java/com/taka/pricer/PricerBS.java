package com.taka.pricer;

import com.taka.marketdata.MarketData;
import com.taka.product.CallPut;
import com.taka.product.Product;
import com.taka.product.ProductVanilla;
import com.taka.util.JsonUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Black-Scholes formula. Only handles Vanilla options.
public class PricerBS implements Pricer {
    @Override
    public Result calc(Product product, MarketData marketData) {
        if (!(product instanceof ProductVanilla)) {
            throw new IllegalArgumentException("Unsupported option type: " + product.getClass().getSimpleName());
        }
        ProductVanilla productVanilla = (ProductVanilla) product;
        CallPut callPut = productVanilla.getCallPut();
        LocalDate date1 = LocalDate.parse(marketData.getQuoteDate(), JsonUtil.DateFormat);
        LocalDate date2 = LocalDate.parse(productVanilla.getExpiryDate(), JsonUtil.DateFormat);
        int daysToExpiry = (int) ChronoUnit.DAYS.between(date1, date2);
        // TODO: Time to expiry (in years) is approximated with 365.25 days/year days here,
        // which should be properly calculated based on the calendar.
        double numDaysInYear = 365.25;
        double timeToExpiry = (double) daysToExpiry / numDaysInYear;

        double basePv;
        double delta;
        double spot = marketData.getSpot();
        double rfr = marketData.getRfr();
        double vol = marketData.getVol();
        double strike = productVanilla.getStrike();
        D1AndD2 d1AndD2 = getD1AndD2(spot, strike, rfr, vol, timeToExpiry);
        if (callPut == CallPut.Call) {
            basePv = spot * cumulativeNormalDist(d1AndD2.d1) -
                    strike * Math.exp(-rfr * timeToExpiry) * cumulativeNormalDist(d1AndD2.d2);
            delta = cumulativeNormalDist(d1AndD2.d1);
        } else if (callPut == CallPut.Put) {
            basePv = -spot * cumulativeNormalDist(-d1AndD2.d1) +
                    strike * Math.exp(-rfr * timeToExpiry) * cumulativeNormalDist(-d1AndD2.d2);
            delta = cumulativeNormalDist(d1AndD2.d1) - 1;
        } else {
            throw new IllegalArgumentException("Unexpected CallPut: " + callPut);
        }
        double gamma = probabilityDensity(d1AndD2.d1) / (spot * vol * Math.sqrt(timeToExpiry));
        double vega = spot * probabilityDensity(d1AndD2.d1) * Math.sqrt(timeToExpiry);
        return new Result(basePv, delta, gamma, vega);
    }

    // d1 and d2 portions of BlackScholes formula.
    private D1AndD2 getD1AndD2(double spot, double strike, double rfr, double vol, double timeToExpiry) {
        double d1 = (Math.log(spot / strike) + (rfr + 0.5 * vol * vol) * timeToExpiry)
                /
                (vol * (Math.sqrt(timeToExpiry)));
        double d2 = (Math.log(spot / strike) + (rfr - 0.5 * vol * vol) * timeToExpiry)
                /
                (vol * (Math.sqrt(timeToExpiry)));
        return new D1AndD2(d1, d2);
    }

    // Approximation function of the cumulative normal distribution, following the recipe from John C. Hull.
    private double cumulativeNormalDist(double x) {
        if (x < 0.) {
            return 1.0 - cumulativeNormalDist(-x);
        }
        double k = 1.0 / (1.0 + 0.2316419 * x);
        double k_sum = k * (0.319381530 + k * (-0.356563782 + k * (1.781477937 +
                k * (-1.821255978 + 1.330274429 * k))));

        return (1.0 - (1.0 / (Math.pow(2 * Math.PI, 0.5))) * Math.exp(-0.5 * x * x) * k_sum);
    }

    private double probabilityDensity(double x) {
        return (1. / Math.sqrt(2. * Math.PI)) * Math.exp(-x * x / 2.);
    }

    private static class D1AndD2 {
        private final double d1;
        private final double d2;

        public D1AndD2(double d1, double d2) {
            this.d1 = d1;
            this.d2 = d2;
        }
    }
}
