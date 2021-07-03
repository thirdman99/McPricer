package com.taka.product;

// TODO: For the expediency of the coding-task, this only supports single-barrier, not double.
public class ProductBarrier extends Product {
    private final BarrierType barrierType;
    private final double barrier;
    private final double koPayout;

    protected ProductBarrier(CallPut callPut, String expiryDate, double strike,
                             BarrierType barrierType, double barrier, double koPayout) {
        super(callPut, expiryDate, strike);
        this.barrierType = barrierType;
        this.barrier = barrier;
        this.koPayout = koPayout;
    }

    @Override
    public double payout(double spot, boolean knockedIn) {
        if (barrierType.withKiFeature() && !knockedIn) {
            return 0.;
        }
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
    public boolean isPathDependent() {
        return true;
    }

    @Override
    public boolean isKnockedIn(double spot) {
        if (!barrierType.withKiFeature()) {
            return false;
        }
        if (barrierType == BarrierType.DownIn) {
            return spot <= barrier;
        } else if (barrierType == BarrierType.UpIn) {
            return spot >= barrier;
        } else {
            throw new IllegalArgumentException("Unexpected BarrierType: " + barrierType);
        }
    }

    @Override
    public double getKnockOutPayout(double spot) {
        if (!barrierType.withKoFeature()) {
            return Double.NaN;
        }
        boolean barrierBroken;
        if (barrierType == BarrierType.DownOut) {
            barrierBroken = spot <= barrier;
        } else if (barrierType == BarrierType.UpOut) {
            barrierBroken = spot >= barrier;
        } else {
            throw new IllegalArgumentException("Unexpected BarrierType: " + barrierType);
        }
        return barrierBroken ? koPayout : Double.NaN;
    }

    @Override
    public String toString() {
        return "ProductBarrier{ " + super.toString() +
                ", barrierType=" + barrierType +
                ", barrier=" + barrier +
                ", koPayout=" + koPayout +
                " }";
    }

    public BarrierType getBarrierType() {
        return barrierType;
    }

    public double getBarrier() {
        return barrier;
    }

    public double getKoPayout() {
        return koPayout;
    }
}
