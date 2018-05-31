package com.example.springbootmetricsdemo;

import io.micrometer.core.instrument.Metrics;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @RequestMapping
    public void defaultPath(){

        Metrics.counter("my.default.counter").increment();
    }

    @RequestMapping("/counter")
    public void counter(int value){

        Metrics.counter("my.value.counter").increment(value);
    }
}

