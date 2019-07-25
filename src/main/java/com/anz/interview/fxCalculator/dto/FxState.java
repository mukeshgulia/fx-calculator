package com.anz.interview.fxCalculator.dto;

import com.anz.interview.fxCalculator.util.Utils;
import com.anz.interview.fxCalculator.exception.DataStateException;
import com.anz.interview.fxCalculator.model.Currency;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class FxState {

    private List<Currency> currencies;
    private String[][] rateMatrix;

    public FxState() throws DataStateException {
        Path currencyPrecisionFile = null;
        Path ratesFile = null;
        Path matrixFile = null;
        try {
            currencyPrecisionFile = getPathFromResources("currency-precision.txt");
            ratesFile = getPathFromResources("direct-conversion-rates.txt");
            matrixFile = getPathFromResources("matrix.txt");
        } catch (URISyntaxException e) {
            throw new DataStateException("Missing File:" + e.getMessage());
        }

        loadCurrencies(currencyPrecisionFile, ratesFile);
        generateRatesMatrix(matrixFile);
    }

    public FxState(String... args) throws DataStateException {

        Path currencyPrecisionFile = null;
        Path ratesFile = null;
        Path matrixFile = null;

        try {
            for (String param : args) {
                String[] paramValues = param.split("=");
                switch (paramValues[0].toLowerCase()) {
                    case "--currencyPrecision":
                    case "-cp":
                        currencyPrecisionFile = Paths.get(paramValues[1]);
                        break;
                    case "--rates":
                    case "-r":
                        ratesFile = Paths.get(paramValues[1]);
                        break;
                    case "--matrix":
                    case "-m":
                        matrixFile = Paths.get(paramValues[1]);
                        break;
                }
            }

            if (currencyPrecisionFile == null) {
                currencyPrecisionFile = getPathFromResources("currency-precision.txt");
            }
            if (ratesFile == null) {
                ratesFile = getPathFromResources("direct-conversion-rates.txt");
            }
            if (matrixFile == null) {
                matrixFile = getPathFromResources("matrix.txt");
            }
        } catch (InvalidPathException | URISyntaxException e) {
            throw new DataStateException("Unable to start application: " + e.getMessage());
        }

        loadCurrencies(currencyPrecisionFile, ratesFile);
        generateRatesMatrix(matrixFile);
    }

    private void loadCurrencies(Path currencyPrecisionFile, Path ratesFile) throws DataStateException {

        currencies = new ArrayList<>();
        try (Stream<String> cpfStream = Files.lines(currencyPrecisionFile);
             Stream<String> rStream = Files.lines(ratesFile)) {

            cpfStream.forEach(item -> {
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

            rStream.forEach(item -> {
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

    private void generateRatesMatrix(Path matrixFile) throws DataStateException {

        try {
            List<String> list = Files.readAllLines(matrixFile);
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

    private Path getPathFromResources(String fileName) throws URISyntaxException {


        Path path = Paths.get(getClass().getClassLoader()
                .getResource(fileName).toURI());

        return path;
    }
}
