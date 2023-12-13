package com.example.demo.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.model.Donation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
public class ClientController {

	private final WebClient webClient;
    private final ObjectMapper objectMapper; // ObjectMapper for JSON serialization

    public ClientController(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
        this.objectMapper = objectMapper; // Inject ObjectMapper
    }

    @PostMapping(value = "/send-data", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<String>> sendEmployeeData(@RequestBody Donation donationdata) {
        System.out.println("Sending the data from client app");
        try {
            // Convert Donation object to JSON string
            String donationdataJson = objectMapper.writeValueAsString(donationdata);
            
            // Forward the converted JSON donation data to another application via WebClient
            Mono<String> response = webClient.post()
                    .uri("/donations/submit")
                    .contentType(MediaType.APPLICATION_JSON) // Set Content-Type as JSON
                    .body(Mono.just(donationdataJson), String.class)
                    .retrieve()
                    .bodyToMono(String.class);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (JsonProcessingException e) {
            // Handle JSON serialization exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Mono.just("Error converting to JSON"));
        }
    }
}