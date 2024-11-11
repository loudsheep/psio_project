package org.loudsheep.psio_project.backend.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StockService {
    private static final String BASE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";

    // Fetch and parse stock data for a given symbol
    public StockData getStockData(String symbol) throws IOException {
        String urlString = BASE_URL + symbol;
        String jsonResponse = fetchJsonData(urlString);

        // Call a custom parse method to handle JSON processing
        return parseStockData(symbol, jsonResponse);
    }

    // Fetch JSON data from URL
    private String fetchJsonData(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Check the HTTP response code
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch data, HTTP response code: " + responseCode);
        }

        // Read response from input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    // Parse JSON response to StockData (you'll add the actual parsing logic here)
    private StockData parseStockData(String symbol, String jsonResponse) {
        List<DayStockData> dailyData = new ArrayList<>();

        // Example JSON parsing using JsonParser and manual processing (add your logic here)
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject resultArray = jsonObject.getAsJsonObject("chart").getAsJsonArray("result")
                .get(0).getAsJsonObject();

        JsonObject quoteObject = resultArray.getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject();

        for (int i = 0; i < resultArray.getAsJsonArray("timestamp").size(); i++) {
            long timestamp = resultArray.getAsJsonArray("timestamp").get(i).getAsLong();
            JsonElement low = quoteObject.getAsJsonArray("low").get(i);
            JsonElement high = quoteObject.getAsJsonArray("high").get(i);
            JsonElement open = quoteObject.getAsJsonArray("open").get(i);
            JsonElement close = quoteObject.getAsJsonArray("close").get(i);

            if (low.isJsonNull() || high.isJsonNull() || open.isJsonNull() || close.isJsonNull()) {
                continue;
            }

            dailyData.add(new DayStockData(timestamp, low.getAsDouble(), high.getAsDouble(), open.getAsDouble(), close.getAsDouble()));
        }

        // Create and return StockData object
        return new StockData(symbol, dailyData);
    }


}
