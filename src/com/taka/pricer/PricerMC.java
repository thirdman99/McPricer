package com.taka.pricer;

import com.taka.marketdata.MarketData;
import com.taka.product.Product;
import com.taka.util.JsonUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Monte-Carlo pricer.
// Can handle multiple-types of products (Vanilla, Barrier, etc) as long as they are correctly defined in Product***.java classes.
public class PricerMC implements Pricer {

    final private PricingParams pricingParams;
    private SimPathParams simPathParams;

    public PricerMC(PricingParams pricingParams) {
        this.pricingParams = pricingParams;
    }

    @Override
    public Result calc(Product product, MarketData marketData) {
        createSimPathList(product, marketData);

        double pvBase = calcOnePv(product, marketData);

        // The actual shift-amounts below can be changed in pricingParams.json.
        double spotShiftRatio = pricingParams.getSpotShiftRatio();
        double volShiftRatio = pricingParams.getVolShiftRatio();
        double spotShiftAmt = marketData.getSpot() * spotShiftRatio;
        double volShiftAmt = marketData.getVol() * volShiftRatio;

        double pvSpotUp = calcOneShiftedPv(product, marketData,
                md -> md.setSpot(md.getSpot() + spotShiftAmt));
        double pvSpotDown = calcOneShiftedPv(product, marketData,
                md -> md.setSpot(md.getSpot() - spotShiftAmt));
        double delta = (pvSpotUp - pvBase) / spotShiftAmt;
        double gamma = (pvSpotUp - pvBase * 2. + pvSpotDown) / (spotShiftAmt * spotShiftAmt);

        double pvVolUp = calcOneShiftedPv(product, marketData,
                md -> md.setVol(md.getVol() + volShiftAmt));
        double vega = (pvVolUp - pvBase) / volShiftAmt;

        // The below is necessary to avoid the unnecessary memory-growth from repeated pricing.
        simPathParams = null;

        return new Result(pvBase, delta, gamma, vega);
    }

    private double calcOneShiftedPv(Product product, MarketData marketData,
                                    Consumer<MarketData> marketDataShiftFunc) {
        MarketData marketDataShifted = marketData.clone();
        marketDataShiftFunc.accept(marketDataShifted);
        return calcOnePv(product, marketDataShifted);
    }

    private double calcOnePv(Product product, MarketData marketData) {
        double sum = 0.;
        for (int simCnt = 0; simCnt < pricingParams.getSimulationNum(); simCnt++) {
            double onePathPv = calcOnePath(product, marketData, simPathParams.randomNumArr2d[simCnt]);
            sum += onePathPv;
        }
        return sum / pricingParams.getSimulationNum();
    }

    private double calcOnePath(Product product, MarketData marketData, double[] randomNumArr) {
        int timeStepNum = simPathParams.timeSteps.size();
        double spot = marketData.getSpot();
        double rfr = marketData.getRfr();
        double vol = marketData.getVol();
        double timeToSim = 0.;
        boolean knockedIn = false;
        for (int timeStepCnt = 0; timeStepCnt < timeStepNum; timeStepCnt++) {
            double timeStep = simPathParams.timeSteps.get(timeStepCnt);
            timeToSim += timeStep;

            // Apply the drift-process
            spot *= Math.exp(timeStep * (rfr - 0.5 * vol * vol));

            // Apply a Brownian motion.
            double gaussianRandom = randomNumArr[timeStepCnt];
            spot *= Math.exp(Math.sqrt(vol * vol * timeStep) * gaussianRandom);

            // Check if it's knocked-in if the product has that feature (ex. Barrier).
            if (!knockedIn) {
                knockedIn = product.isKnockedIn(spot);
            }

            // Check if it's knocked-out if the product has that feature (ex. Barrier).
            double knockOutPayout = product.getKnockOutPayout(spot);
            if (!Double.isNaN(knockOutPayout)) {
                // Discount the payout to the PV.
                return knockOutPayout * Math.exp(-rfr * timeToSim);
            }
        }
        // Calculate the payout according to the product definition.
        double payout = product.payout(spot, knockedIn);
        // Discount the payout to the PV.
        return payout * Math.exp(-rfr * simPathParams.timeToExpiry);
    }

    private void createSimPathList(Product product, MarketData marketData) {
        LocalDate date1 = LocalDate.parse(marketData.getQuoteDate(), JsonUtil.DateFormat);
        LocalDate date2 = LocalDate.parse(product.getExpiryDate(), JsonUtil.DateFormat);
        int daysToExpiry = (int) ChronoUnit.DAYS.between(date1, date2);
        int timeStepNum = 1;
        if (product.isPathDependent()) {
            timeStepNum = daysToExpiry;
        }

        // TODO: Time to expiry (in years) is approximated with 365.5 days/year days here,
        // which should be properly calculated based on the calendar.
        double numDaysInYear = 365.25;
        double timeToExpiry = (double) daysToExpiry / numDaysInYear;
        // TODO: In a proper model, the time-steps should follow only the business-dates.
        // which should follow the specific exchange's calendar(s) designated in the product.
        double timeStep = timeToExpiry / timeStepNum;
        List<Double> timeSteps = IntStream.range(0, timeStepNum).asDoubleStream()
                .mapToObj(idx -> timeStep).collect(Collectors.toList());

        // TODO: This is not a pseudo-random sequence like Sobol, which would have a better convergence.
        RandomGenerator randomGenerator = new RandomGenerator(pricingParams.getSeed());
        double[][] randomNumArr2d = new double[pricingParams.getSimulationNum()][timeStepNum];
        for (int simCnt = 0; simCnt < pricingParams.getSimulationNum(); simCnt++) {
            for (int timeStepCnt = 0; timeStepCnt < timeStepNum; timeStepCnt++) {
                double gaussianRandom = randomGenerator.getNum();
                randomNumArr2d[simCnt][timeStepCnt] = gaussianRandom;
            }
        }
        simPathParams = new SimPathParams(daysToExpiry, timeToExpiry, timeSteps, randomNumArr2d);
    }

    private static class SimPathParams {
        private final int daysToExpiry;
        private final double timeToExpiry;
        private final List<Double> timeSteps;
        private final double[][] randomNumArr2d;

        private SimPathParams(int daysToExpiry, double timeToExpiry, List<Double> timeSteps, double[][] randomNumArr2d) {
            this.daysToExpiry = daysToExpiry;
            this.timeToExpiry = timeToExpiry;
            this.timeSteps = timeSteps;
            this.randomNumArr2d = randomNumArr2d;
        }
    }
}
