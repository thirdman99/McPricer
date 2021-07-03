package com.taka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taka.marketdata.MarketData;
import com.taka.pricer.PricerBS;
import com.taka.pricer.PricerMC;
import com.taka.pricer.Result;
import com.taka.product.ProductBarrier;
import com.taka.product.ProductVanilla;
import com.taka.util.JsonUtil;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Test()
public class PricerTest {
    private static final boolean UpdateExpectedResults = false;
    private static final Logger LOG = Logger.getLogger(PricerTest.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // TODO: In the proper production code,
    //  the info on the MD and product would have been added into the expected and actual results,
    //  so that it will display which MD and product failed the test.
    @Test
    public void testAllPrices() throws Exception {
        LOG.info("testAllPrices() Started.");
        InputSet inputSet = JsonUtil.createInputSetFromJsonFiles(Path.of("./json"));

        List<Result> resultListActual = new ArrayList<>();
        for (MarketData marketData : inputSet.getMarketDataList()) {
            PricerBS pricerBS = new PricerBS();
            PricerMC pricerMC = new PricerMC(inputSet.getPricingParams());
            for (ProductVanilla productVanilla : inputSet.getProductVanillaList()) {
                resultListActual.add(pricerBS.calc(productVanilla, marketData));
                resultListActual.add(pricerMC.calc(productVanilla, marketData));
            }
            for (ProductBarrier productBarrier : inputSet.getProductBarrierList()) {
                resultListActual.add(pricerMC.calc(productBarrier, marketData));
            }
        }
        final String expectedResultPath = "./json/testAllPrices_expectedResult.json";
        if (UpdateExpectedResults) {
            try (FileWriter fileWriter = new FileWriter(expectedResultPath)) {
                gson.toJson(resultListActual, fileWriter);
            }
        }
        List<Result> resultListExpected = JsonUtil.getListFromJsonFile(expectedResultPath, Result.class);
        Assert.assertEquals(resultListActual.size(), resultListExpected.size());

        int rowCnt = -1;
        for (Result resultActual : resultListActual) {
            rowCnt++;
            Result resultExpected = resultListExpected.get(rowCnt);
            Assert.assertEquals(resultActual, resultExpected);
        }
        LOG.info("testAllPrices() Finished.");
    }

    @Test
    public void testMcAgainstBs() throws Exception {
        LOG.info("testMcAgainstBs() Started.");
        InputSet inputSet = JsonUtil.createInputSetFromJsonFiles(Path.of("./json"));

        for (MarketData marketData : inputSet.getMarketDataList()) {
            PricerBS pricerBS = new PricerBS();
            PricerMC pricerMC = new PricerMC(inputSet.getPricingParams());
            for (ProductVanilla productVanilla : inputSet.getProductVanillaList()) {
                Result resultMC = pricerMC.calc(productVanilla, marketData);
                Result resultBS = pricerBS.calc(productVanilla, marketData);
                checkValsMcAgainstBs(resultMC.getPv(), resultBS.getPv(), "PV", 0.005);
                checkValsMcAgainstBs(resultMC.getDelta(), resultBS.getDelta(), "Delta", 0.01);
                checkValsMcAgainstBs(resultMC.getGamma(), resultBS.getGamma(), "Gamma", 0.03);
                checkValsMcAgainstBs(resultMC.getVega(), resultBS.getVega(), "Vega", 0.01);
            }
        }
        LOG.info("testMcAgainstBs() Finished.");
    }

    private void checkValsMcAgainstBs(double valMc, double valBs, String greekName, double resultDiffTolerance) {
        double diffRatio = Math.abs((valBs - valMc) / valMc);
        if (diffRatio > resultDiffTolerance) {
            String errMsg = "The results are significantly different. greek=" + greekName
                    + ", diffRatio=" + diffRatio + ", valMc=" + valMc + ", valBs=" + valBs;
            Assert.assertEquals(valMc, valBs, errMsg);
        }
    }
}
