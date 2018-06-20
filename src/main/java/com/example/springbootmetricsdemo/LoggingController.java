package com.example.springbootmetricsdemo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logging")
public class LoggingController {

    @RequestMapping("/exception")
    public void throwException(){
        throw new RuntimeException("Exception is being thrown");
    }

    @RequestMapping("/crash")
    public void killJVM(){
        System.exit(1);
    }

}
