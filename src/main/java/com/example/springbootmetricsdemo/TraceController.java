package com.example.springbootmetricsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/trace")
public class TraceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("http://${vcap.application.application_uris[0]:localhost:8080}")
    private String application_url;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/service-success")
    public String serviceA(){

        logger.info("Calling Service A");

        randomDelay();

        return restTemplate.getForObject(application_url + "/trace/service-b", String.class);
    }

    @RequestMapping("/service-b")
    public String serviceB(){

        logger.info("Calling Service B");

        randomDelay();

        return restTemplate.getForObject(application_url + "/trace/service-c", String.class);
    }

    @RequestMapping("/service-c")
    public String serviceC(){

        logger.info("Calling Service C");

        randomDelay();

        return "service c result";
    }

    private void randomDelay(){

        int min_millisecond_delay = 50;
        int max_millisecond_delay = 150;

        int millisecond_delay = new Random().nextInt(max_millisecond_delay-min_millisecond_delay) + min_millisecond_delay;

        try {
            TimeUnit.MILLISECONDS.sleep(millisecond_delay);
        } catch (InterruptedException ignored) {}
    }

}
