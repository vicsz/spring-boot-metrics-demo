package com.example.springbootmetricsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

import static io.micrometer.core.instrument.Metrics.counter;

@RestController
@RequestMapping("/metrics")
@EnableScheduling
public class MetricsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int PURCHASE_FREQUENCY_IN_MILLISECONDS = 10000;

    @Scheduled(fixedRate = PURCHASE_FREQUENCY_IN_MILLISECONDS)
    void performScheduledPurchases() {

        double purchaseAmount = getRandomPurchaseAmount();

        counter("application.purchases.count").increment();
        counter("application.purchases.dollarvalue").increment(purchaseAmount);
    }


    @RequestMapping("/count")
    public void count(){

        logger.info("Hit metrics count endpoint");

        counter("application.sample.counter").increment();
    }

    //Random Purchase Amount with an hourly pattern
    private double getRandomPurchaseAmount(){

        double waveValue = (Math.cos(((double) LocalTime.now().getMinute() )/ 60 * (2 * Math.PI)) + 1 ) / 2;
        double totalValue = waveValue * 35 + (Math.random() * 50);
        double roundedValue = Math.round(totalValue*100)/100;

        return roundedValue;

    }


}

