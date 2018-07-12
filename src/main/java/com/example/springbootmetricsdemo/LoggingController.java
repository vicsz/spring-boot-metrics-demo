package com.example.springbootmetricsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logging")
public class LoggingController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/exception")
    public void throwException(){
        throw new RuntimeException("Exception is being thrown");
    }

    @RequestMapping("/crash")
    public void killJVM(){
        System.exit(1);
    }

    @RequestMapping("/client-error")
    public void logClientError(@RequestBody String body){
        logger.error(body);
    }

}
