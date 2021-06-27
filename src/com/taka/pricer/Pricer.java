package com.taka.pricer;

import com.taka.marketdata.MarketData;
import com.taka.product.Product;

public interface Pricer {
    Result calc(Product product, MarketData marketData);
}
