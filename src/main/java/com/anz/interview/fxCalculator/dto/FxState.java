package com.anz.interview.fxCalculator.dto;

import com.anz.interview.fxCalculator.util.Utils;
import com.anz.interview.fxCalculator.exception.DataStateException;
import com.anz.interview.fxCalculator.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Repository
public final class FxState {

    private List<Currency> currencies;
    private String[][] rateMatrix;

    @Autowired
    public FxState() throws DataStateException {
        loadCurrencies("currency-precision.txt", "direct-conversion-rates.txt");
        generateRatesMatrix("matrix.txt");
    }

    public FxState(String... args) throws DataStateException {

        String currencyPrecisionFile = null;
        String ratesFile = null;
        String matrixFile = null;

        for (String param : args) {
            String[] paramValues = param.split("=");
            switch (paramValues[0].toLowerCase()) {
                case "--currencyPrecision":
                case "-cp":
                    currencyPrecisionFile = paramValues[1];
                    break;
                case "--rates":
                case "-r":
                    ratesFile = paramValues[1];
                    break;
                case "--matrix":
                case "-m":
                    matrixFile = paramValues[1];
                    break;
            }
        }

        if (currencyPrecisionFile == null) {
            currencyPrecisionFile = "currency-precision.txt";
        }
        if (ratesFile == null) {
            ratesFile = "direct-conversion-rates.txt";
        }
        if (matrixFile == null) {
            matrixFile = "matrix.txt";
        }

        loadCurrencies(currencyPrecisionFile, ratesFile);
        generateRatesMatrix(matrixFile);
    }

    private void loadCurrencies(String currencyPrecisionFile, String ratesFile) throws DataStateException {

        currencies = new ArrayList<>();

        try (InputStream cpfStream = getClass().getClassLoader().getResourceAsStream(currencyPrecisionFile);
             InputStream rStream = getClass().getClassLoader().getResourceAsStream(ratesFile)) {

            List<String> cpfDoc = new BufferedReader(new InputStreamReader(cpfStream, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            cpfDoc.forEach(item -> {
                String[] values = item.split("=");

                String currencyName = values[0];
                int precision = Integer.valueOf(values[1]);
                StringBuilder format = new StringBuilder("0"); //no decimal places
                if (precision > 1) {
                    format.append(".");
                    for (int i = 0; i < precision; i++) {
                        format.append("0");
                    }
                }
                DecimalFormat df = new DecimalFormat(format.toString());
                currencies.add(new Currency(currencyName, df));
            });

            List<String> rDoc = new BufferedReader(new InputStreamReader(rStream, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            rDoc.forEach(item -> {
                String[] values = item.split("=");
                String base = values[0].substring(0, 3);
                String term = values[0].substring(3, 6);
                Double rate = Double.valueOf(values[1]);
                Optional<Currency> baseCurrency = currencies.stream().filter(s -> base.equals(s.getName())).findFirst();

                if (baseCurrency.isPresent()) {
                    Optional<Currency> termCurrency = currencies.stream().filter(s -> term.equals(s.getName())).findFirst();
                    if (termCurrency.isPresent()) {
                        Map<Currency, Double> m = baseCurrency.get().getDirectConversionRates();
                        m.put(termCurrency.get(), rate);
                        baseCurrency.get().setDirectConversionRates(m);
                    } else {
                        throw new UnknownFormatConversionException("com.anz.interview.fxCalculator.model.Currency '" + term + "' in rates files does not exists in model-precision file");
                    }
                } else {
                    throw new UnknownFormatConversionException("com.anz.interview.fxCalculator.model.Currency '" + base + "' in rates files does not exists in model-precision file");
                }
            });

        } catch (NumberFormatException | IOException | UnknownFormatConversionException e) {
            throw new DataStateException("Unable to load currencies from currencyPrecisionFile:" + e.getMessage());
        }
    }

    private void generateRatesMatrix(String matrixFile) throws DataStateException {

        try (InputStream cpfStream = getClass().getClassLoader().getResourceAsStream(matrixFile)) {
            List<String> list = new BufferedReader(new InputStreamReader(cpfStream, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            //List<String> list = Files.readAllLines(matrixFile);
            rateMatrix = new String[list.size()][list.size()];

            IntStream.range(0, list.size())
                    .forEach(index -> rateMatrix[index] = list.get(index).split(","));

        } catch (IOException e) {
            throw new DataStateException(e.getMessage());
        }
    }

    public String[][] getRateMatrix() {
        return rateMatrix;
    }

    public int getBasePosition(String ccy) {

        if (getCurrencyByName(ccy).isPresent()) {
            String bases[] = Utils.getBaseCurrencies(rateMatrix, 0);
            return Utils.findIndex(bases, ccy);
        } else {
            return -1;
        }
    }

    public int getTermPosition(String ccy) {

        if (getCurrencyByName(ccy).isPresent()) {
            return Utils.findIndex(rateMatrix[0], ccy);
        } else {
            return -1;
        }
    }

    public Double getRate(String baseCurrencyName, String termCurrencyName) throws DataStateException {

        Optional<Currency> baseCurrency = getCurrencyByName(baseCurrencyName);
        Optional<Double> rate;
        if (baseCurrency.isPresent()) {
            rate = baseCurrency.get().getDirectConversionRates()
                    .entrySet().stream()
                    .filter((entry) -> termCurrencyName.equals(entry.getKey().getName()))
                    .map(Map.Entry::getValue)
                    .findFirst();

        } else {
            throw new DataStateException("Currency does not exist:" + baseCurrencyName);
        }

        if (rate.isPresent()) {
            return rate.get();
        } else {
            throw new DataStateException("Rate does not exist for base/term:" +
                    baseCurrencyName + "/" + termCurrencyName);
        }
    }

    public Optional<Currency> getCurrencyByName(String currencyName) {
        return currencies
                .stream()
                .filter(c -> c.getName().equalsIgnoreCase(currencyName))
                .findFirst();
    }
}
