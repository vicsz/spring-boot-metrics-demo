package com.example.springbootmetricsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/trace")
public class TraceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("http://${vcap.application.application_uris[0]:localhost:8080}")
    private String application_url;

    @Autowired
    private WebClient webClient;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @RequestMapping("/service-success")
    public String serviceA(){

        logger.info("Calling Service A");

        randomDelay();

        return webClient.get().uri(application_url + "/trace/service-b").retrieve().bodyToMono(String.class).block();
    }

    @RequestMapping("/service-b")
    public String serviceB(){

        logger.info("Calling Service B");

        randomDelay();

        Mono<String> serviceCallMono = webClient.get().uri(application_url + "/trace/service-c").retrieve().bodyToMono(String.class);

        //2 concurrent calls to Service C
        return Mono.zip(serviceCallMono, serviceCallMono, (a,b) -> a + b).block();

    }

    @RequestMapping("/service-c")
    public String serviceC(){

        logger.info("Calling Service C");

        randomDelay();

        return "service c result";
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
