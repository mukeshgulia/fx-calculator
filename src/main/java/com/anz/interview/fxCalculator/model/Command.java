package com.anz.interview.fxCalculator.model;

public class Command {

    private String ccy1;
    private String ccy2;
    private Double amount;
    private boolean quitCommand;

    public Command(boolean quitCommand) {
        this.quitCommand = quitCommand;
    }

    public Command(String ccy1, String ccy2, Double amount) {
        this.ccy1 = ccy1;
        this.ccy2 = ccy2;
        this.amount = amount;
    }

    public String getCcy1() {
        return ccy1;
    }

    public String getCcy2() {
        return ccy2;
    }

    public Double getAmount() {
        return amount;
    }

    public boolean isQuitCommand() {
        return quitCommand;
    }
}
