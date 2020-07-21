package com.example.springbootmetricsdemo;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/metrics")
@EnableScheduling
public class MetricsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int PURCHASE_FREQUENCY_IN_MILLISECONDS = 10000;

    @PostConstruct
    public void gauge(){

        logger.info("Setting up metrics gauge");

        Metrics.gauge("sample.gauge", Runtime.getRuntime(), Runtime::freeMemory);

        //Example event - { "name": "sample_gauge", "value": 313759208 }

    }

    //Add Common Tags to all Meters -- i.e. things like region, ip address, env, etc
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() throws UnknownHostException{

        InetAddress inetAddress = InetAddress.getLocalHost();

        return registry -> {
            registry
                    .config()
                    .commonTags("ip", inetAddress.getHostAddress())
                    .commonTags("hostname", inetAddress.getHostName());

        };
    }

    @RequestMapping("/count")
    public void count(){

        logger.info("Hit metrics count endpoint");

        Metrics.counter("sample.counter").increment();

        //Example event - { "avg": 168.837394, "max": 213.738641, "name": "sample_timer", "count": 5, "sum": 844.186968 } (after 5 calls)

    }

    @RequestMapping("/timer")
    public void timer(){

        logger.info("Hit metrics timer endpoint");

        Metrics.timer("sample.timer").record(this::randomDelay);

        //Example event - { "name": "sample_counter", "count": 4 } (after 4 calls)
    }

    @RequestMapping("/summary")
    public void distributionSummary() {

        logger.info("Hit metrics distributionSummary endpoint");

        Metrics.summary("sample.distributionsummary").record(getRandomPurchaseAmount());

        //Example event -- { "avg": 2943, "max": 5018, "name": "sample_distributionsummary", "count": 18, "sum": 52974 }  (after 18 random calls)

    }

    @Scheduled(fixedRate = PURCHASE_FREQUENCY_IN_MILLISECONDS)
    public void performScheduledPurchases() {

        //Sample Distribution Summary example with a label

        Metrics.summary("purchase","product_name", getRandomPurchaseName()).record(getRandomPurchaseAmount());

        //Example event - { "avg": 40, "max": 61, "name": "purchase", "count": 2, "sum": 80, "product_name": "House" }  (after 2 random calls)

    }

    //Random Purchase Amount with an hourly pattern
    private double getRandomPurchaseAmount(){

        double waveValue = (Math.cos(((double) LocalTime.now().getMinute() )/ 60 * (2 * Math.PI)) + 1 ) / 2;

        double totalValue = waveValue * 35 + (Math.random() * 50);

        return Math.round(totalValue);

    }

    private String getRandomPurchaseName() {

        List<String> purchaseNames = Arrays.asList("car", "boat", "house", "goat", "dog");

        return purchaseNames.get((int)(Math.random() * purchaseNames.size()));
    }


    private void randomDelay(){

        int min_millisecond_delay = 50;
        int max_millisecond_delay = 250;

        int millisecond_delay = new Random().nextInt(max_millisecond_delay-min_millisecond_delay) + min_millisecond_delay;

        try {
            TimeUnit.MILLISECONDS.sleep(millisecond_delay);
        } catch (InterruptedException ignored) {}
    }


}

