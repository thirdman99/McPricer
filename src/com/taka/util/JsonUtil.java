package com.taka.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.taka.InputSet;
import com.taka.marketdata.MarketData;
import com.taka.pricer.PricingParams;
import com.taka.product.ProductBarrier;
import com.taka.product.ProductVanilla;
import spals.shaded.com.google.common.collect.Streams;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class JsonUtil {
    private static final Gson gson = new Gson();
    private static final JsonParser parser = new JsonParser();
    public static final DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // I could not find a way of specifying the generic type,
    // which also works with gson.fromJson() class parameter.
    public static <Cls> List<Cls> getListFromJsonFile(String fileName, Class cls) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            JsonElement jsonElement = parser.parse(content);
            if (jsonElement == null) {
                throw new IOException("Json file content is empty:  " + fileName);
            }
            JsonArray jsonArray = parser.parse(content).getAsJsonArray();
            return Streams.stream(jsonArray).map(element -> (Cls) gson.fromJson(element.toString(), cls))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static InputSet createInputSetFromJsonFiles(Path basePath) {
        try {
            PricingParams pricingParams;
            try (FileReader fileReader = new FileReader(basePath.resolve("pricingParams.json").toString())) {
                pricingParams = gson.fromJson(fileReader, PricingParams.class);
            }
            List<MarketData> marketDataList = JsonUtil.
                    getListFromJsonFile(basePath.resolve("marketdata.json").toString(), MarketData.class);
            List<ProductVanilla> productVanillaList = JsonUtil.
                    getListFromJsonFile(basePath.resolve("productsVanilla.json").toString(), ProductVanilla.class);
            List<ProductBarrier> productBarrierList = JsonUtil.
                    getListFromJsonFile(basePath.resolve("productsBarrier.json").toString(), ProductBarrier.class);
            return new InputSet(pricingParams, marketDataList, productVanillaList, productBarrierList);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
