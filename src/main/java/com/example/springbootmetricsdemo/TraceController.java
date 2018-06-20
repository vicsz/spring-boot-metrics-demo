package com.example.springbootmetricsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/trace")
public class TraceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //For Testing Purposes call own endpoints instead of separate services
    private static final String SERVICE_URL = "http://localhost:8080";

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/service-a")
    public String serviceA(){

        logger.info("Calling Service A");

        randomDelay();

        return restTemplate.getForObject(SERVICE_URL + "/trace/service-b", String.class);
    }

    @RequestMapping("/service-b")
    public String serviceB(){

        logger.info("Calling Service B");

        randomDelay();

        return restTemplate.getForObject(SERVICE_URL + "/trace/service-c", String.class);
    }

    @RequestMapping("/service-c")
    public String serviceC(){

        logger.info("Calling Service C");

        randomDelay();

        return "service c result";
    }

    //TODO - Trace with failure !
    //TODO - Trace with parrallel Rest Call

    private void randomDelay(){

        int min_millisecond_delay = 50;
        int max_millisecond_delay = 150;

        int millisecond_delay = new Random().nextInt(max_millisecond_delay-min_millisecond_delay) + min_millisecond_delay;

        try {
            TimeUnit.MILLISECONDS.sleep(millisecond_delay);
        } catch (InterruptedException ignored) {}
    }

}
