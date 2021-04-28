package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
public class GatlingController {

    @Autowired
    WeatherService weatherService;

    @GetMapping("/weather")
    public String randomNumberHandler() {
        log.info(" == " + Thread.currentThread().getName());
        Optional opt = weatherService.getTemperature("江苏", "苏州", "吴中");
        return opt.get().toString();
    }
}
