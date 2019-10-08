package com.example.springbootmetricsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/trace")
public class TraceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${vcap.application.application_uris[0]:localhost:8080}")
    private String application_url;

    @Autowired
    private WebClient webClient;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        return builder.build();
    }

    @RequestMapping("/service-a")
    public String serviceA(HttpServletRequest request){

        logger.info("Calling Service A");

        randomDelay();

        return webClient.get().uri(getFullUrl(request,"/trace/service-b")).retrieve().bodyToMono(String.class).block();
    }

    private String getFullUrl(HttpServletRequest request, String path){

        //Use Secure (https) protocol if inbound request is Secure to prevent redirects which WebClient won't do
        return (request.isSecure() ? "https" : "http") + "://"+ application_url + path;
    }

    @RequestMapping("/service-b")
    public String serviceB(HttpServletRequest request){

        logger.info("Calling Service B");

        randomDelay();

        Mono<String> serviceCallMono = webClient.get().uri(getFullUrl(request,"/trace/service-c")).retrieve().bodyToMono(String.class);

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
