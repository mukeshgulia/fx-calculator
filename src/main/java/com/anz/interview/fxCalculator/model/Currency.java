package com.anz.interview.fxCalculator.model;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Currency {

    private String name;
    private DecimalFormat format;
    private Map<Currency, Double> directConversionRates;

    public Currency(String name, DecimalFormat decimalPoints) {
        this.name = name;
        this.format = decimalPoints;
        this.directConversionRates = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public Map<Currency, Double> getDirectConversionRates() {
        return directConversionRates;
    }

    public void setDirectConversionRates(Map<Currency, Double> directConversionRates) {
        this.directConversionRates = directConversionRates;
    }

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        retVal.append("Currency:").append(name)
                .append("; Precision:").append(format.getMaximumFractionDigits());
        if(directConversionRates.size() > 0 ){
            retVal.append("; Rates per unit:");

            for (Map.Entry<Currency, Double> entry : directConversionRates.entrySet()) {
                retVal.append("(term:").append(entry.getKey().getName())
                        .append(", rate:").append(entry.getValue()).append(")\t");
            }

        }
        return retVal.toString();
    }
}
