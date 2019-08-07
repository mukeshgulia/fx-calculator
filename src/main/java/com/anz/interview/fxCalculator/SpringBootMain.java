package com.anz.interview.fxCalculator;

import com.anz.interview.fxCalculator.app.FxCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class SpringBootMain {

    @Autowired
    private FxCalculatorService fxCalculatorService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMain.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> fxCalculatorService.start();
    }

}