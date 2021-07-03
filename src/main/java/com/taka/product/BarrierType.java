package com.taka.product;

public enum BarrierType {
    UpIn, DownIn, UpOut, DownOut;

    boolean withKiFeature() {
        return this == UpIn || this == DownIn;
    }

    boolean withKoFeature() {
        return this == UpOut || this == DownOut;
    }
}
