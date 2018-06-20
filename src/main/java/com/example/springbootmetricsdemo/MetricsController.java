package com.example.springbootmetricsdemo;

import io.micrometer.core.instrument.Metrics;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("/metrics")
@EnableScheduling
public class MetricsController {

    private static final int PURCHASE_FREQUENCY_IN_MILLISECONDS = 5000;

    @Scheduled(fixedRate = PURCHASE_FREQUENCY_IN_MILLISECONDS)
    void performScheduledPurchases() {

        double purchaseAmount = getRandomPurchaseAmount();

        System.out.println("Purchasing : " + purchaseAmount);

        Metrics.counter("application.purchases.count").increment();
        Metrics.counter("application.purchases.dollarvalue").increment(purchaseAmount);
    }


    @RequestMapping("/count")
    public void count(){

        System.out.println("Hitting counter endpoint!");

        Metrics.counter("application.sample.counter").increment();
    }

    private double getRandomPurchaseAmount(){

        System.out.println("Minute - " + LocalTime.now().getSecond());

        //Adding some pattern to purchase amount cycle every 14 minutes
        return (Math.cos(LocalTime.now().getSecond()) + 1) * 50;
    }


}

