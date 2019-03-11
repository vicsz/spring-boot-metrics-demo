package com.example.springbootmetricsdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class RootController {

    @Value("${vcap.application.name:local_app}")
    private String applicationName;

    @Value("${vcap.application.space_name:local_space}")
    private String spaceName;

    @Value("${vcap.application.instance_id:localInstanceId}")
    private String instanceId;

    @Value("${vcap.application.instance_index:localInstanceIndex}")
    private String instanceIndex;

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {

        model.put("applicationName", applicationName);
        model.put("spaceName", spaceName);
        model.put("instanceId", instanceId);
        model.put("instanceIndex", instanceIndex);
        model.put("jvmUpTime", getJvmUpTime());

        return "index";
    }

    private String getJvmUpTime() {

        long uptimeInMilliSec = ManagementFactory.getRuntimeMXBean().getUptime();

        long seconds = (uptimeInMilliSec / 1000) % 60;
        long minutes = (uptimeInMilliSec / (1000 * 60)) % 60;
        long hours = (uptimeInMilliSec / (1000 * 60 * 60)) % 24;
        long days = (uptimeInMilliSec / (1000 * 60 * 60 * 24));

        return "Days " + days + " Hours " + hours + " Minutes " + minutes + " Seconds " + seconds;

    }

}