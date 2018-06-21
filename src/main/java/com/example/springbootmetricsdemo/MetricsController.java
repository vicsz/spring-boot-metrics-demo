package com.example.springbootmetricsdemo;

import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("/metrics")
@EnableScheduling
public class MetricsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int PURCHASE_FREQUENCY_IN_MILLISECONDS = 10000;

    @Scheduled(fixedRate = PURCHASE_FREQUENCY_IN_MILLISECONDS)
    void performScheduledPurchases() {

        double purchaseAmount = getRandomPurchaseAmount();

        Metrics.counter("application.purchases.count").increment();
        Metrics.counter("application.purchases.dollarvalue").increment(purchaseAmount);
    }


    @RequestMapping("/count")
    public void count(){

        logger.info("Hit metrics count endpoint");

        Metrics.counter("application.sample.counter").increment();
    }

    //Random Purchase Amount with an hourly pattern
    private double getRandomPurchaseAmount(){

        double waveValue = (Math.cos(((double) LocalTime.now().getMinute() )/ 60 * (2 * Math.PI)) + 1 ) / 2;
        double totalValue = waveValue * 50 + (Math.random() * 12);
        double roundedValue = Math.round(totalValue*100)/100;

        return roundedValue;

    }


}

