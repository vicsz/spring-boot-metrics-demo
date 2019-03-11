package com.example.springbootmetricsdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class RootController {

    @Value("${vcap.application.name:local_app}")
    private String applicationName;

    @Value("${vcap.application.space_name:local_space}")
    private String spaceName;

    @Value("${vcap.application.instance_id:localInstanceId}")
    private String instanceId;

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {

        model.put("applicationName", applicationName);
        model.put("spaceName", spaceName);
        model.put("instanceId", instanceId);

        return "index";
    }

}