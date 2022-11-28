package com.fatihyilmaz.client.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final RestTemplate restTemplate;

    public ClientController( RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/get-resource")
    @RateLimiter(name = "rateLimiterApi")
    public ResponseEntity<String> getResource() {
        return restTemplate.getForEntity("http://localhost:8090/server/get-resource", String.class);
    }
}
