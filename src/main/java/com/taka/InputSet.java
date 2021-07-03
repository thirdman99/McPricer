package com.taka;

import com.taka.marketdata.MarketData;
import com.taka.pricer.PricingParams;
import com.taka.product.ProductBarrier;
import com.taka.product.ProductVanilla;

import java.util.List;

public class InputSet {
    private final PricingParams pricingParams;
    private final List<MarketData> marketDataList;
    private final List<ProductVanilla> productVanillaList;
    private final List<ProductBarrier> productBarrierList;

    public InputSet(PricingParams pricingParams, List<MarketData> marketDataList,
                    List<ProductVanilla> productVanillaList, List<ProductBarrier> productBarrierList) {
        this.pricingParams = pricingParams;
        this.marketDataList = marketDataList;
        this.productVanillaList = productVanillaList;
        this.productBarrierList = productBarrierList;
    }

    public PricingParams getPricingParams() {
        return pricingParams;
    }

    public List<MarketData> getMarketDataList() {
        return marketDataList;
    }

    public List<ProductVanilla> getProductVanillaList() {
        return productVanillaList;
    }

    public List<ProductBarrier> getProductBarrierList() {
        return productBarrierList;
    }
}
