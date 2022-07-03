package com.fatihyilmaz.client.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final CheckedFunction0<ResponseEntity<String>> checkedSupplier;

    public ClientController(RestTemplate restTemplate, CircuitBreaker circuitBreaker) {
        checkedSupplier = CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> restTemplate.getForEntity("http://localhost:8090/server/get-resource", String.class));
        circuitBreaker.getEventPublisher()
                .onEvent(event -> System.out.println("Circuit breaker event occurred: " + event));
    }

    @GetMapping("/get-resource")
    public ResponseEntity<String> getResource() {
        ResponseEntity<String> response;
        try {
            response = Try.of(checkedSupplier).getOrElseThrow(ex -> new RuntimeException("Exception occurred: ", ex));
        } catch (RuntimeException ex) {
            response = ResponseEntity.internalServerError().body("Exception occurred: ex: " + ex);
            // fallback method!
        }
        return response;
    }
}
