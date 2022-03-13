package com.taka;

import com.taka.marketdata.MarketData;
import com.taka.pricer.PricerBS;
import com.taka.pricer.PricerMC;
import com.taka.pricer.Result;
import com.taka.product.ProductBarrier;
import com.taka.product.ProductVanilla;
import com.taka.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String jsonPathStr = "./json";
            if (args.length > 0) {
                jsonPathStr = args[0];
            }
            InputSet inputSet = JsonUtil.createInputSetFromJsonFiles(Path.of(jsonPathStr));
            LOG.info(inputSet.getPricingParams());
            LOG.info("Start the pricing-loop.");
            for (MarketData marketData : inputSet.getMarketDataList()) {
                LOG.info(marketData);
                PricerBS pricerBS = new PricerBS();
                PricerMC pricerMC = new PricerMC(inputSet.getPricingParams());
                LOG.info("Start pricing the vanilla products.\n");
                for (ProductVanilla productVanilla : inputSet.getProductVanillaList()) {
                    LOG.info("\n" + productVanilla);
                    Result resultBS = pricerBS.calc(productVanilla, marketData);
                    Result resultMC = pricerMC.calc(productVanilla, marketData);
                    LOG.info("BS: \n" + resultBS);
                    LOG.info("MC: \n" + resultMC + "\n");
                }
                LOG.info("Finished pricing the vanilla products.\n");
                LOG.info("Start pricing the barrier products.\n");
                for (ProductBarrier productBarrier : inputSet.getProductBarrierList()) {
                    LOG.info("\n" + productBarrier);
                    Result resultMC = pricerMC.calc(productBarrier, marketData);
                    LOG.info("MC: \n" + resultMC + "\n");
                }
                LOG.info("Finished pricing the barrier products.");
            }
            LOG.info("Finished the pricing-loop.");
        } catch (Throwable ex) {
            LOG.error("Exception: ", ex);
        }
    }
}
