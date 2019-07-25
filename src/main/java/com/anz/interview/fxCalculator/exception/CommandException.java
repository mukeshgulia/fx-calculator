package com.anz.interview.fxCalculator.exception;

public class CommandException extends Exception {

    private static String usage = "Usage:<ccy1> <amount> in <ccy2>.\nExample: AUD 100.00 in USD";

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, String usage) {
        super(message);
        this.usage = usage;
    }

    public String getUsage() {
        return usage;
    }
}

