package com.anz.interview.fxCalculator.util;

import java.util.stream.IntStream;

public class Utils {

    public static String[] getBaseCurrencies(String[][] rates, int index){
        String[] column = new String[rates[0].length];
        for(int i=0; i<column.length; i++){
            column[i] = rates[i][index];
        }
        return column;
    }

    public static int findIndex(String[] a, String target)
    {
        return IntStream.range(0, a.length)
                .filter(i -> target.equalsIgnoreCase(a[i]))
                .findFirst()
                .orElse(-1);
    }
}
