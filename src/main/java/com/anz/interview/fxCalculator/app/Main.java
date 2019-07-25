package com.anz.interview.fxCalculator.app;

import com.anz.interview.fxCalculator.dto.FxState;

public class Main {

    public static void main(String... args) throws Exception {

        FxState fxData;

        if (args.length == 0) {
            fxData = new FxState();
        } else {
            fxData = new FxState(args);
        }

        Application app = new Application(fxData);
        app.start();
    }
}