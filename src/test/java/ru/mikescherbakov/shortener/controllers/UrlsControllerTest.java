package ru.mikescherbakov.shortener.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UrlsControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void generate_OnValidRequest_GiveValidResult() {
        Map<String, String> validRequest = getValidRequest();

        client.post()
                .uri("/generate")
                .body(BodyInserters.fromValue(validRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("link")
                .isNotEmpty();
    }

    @Test
    void generate_OnInvalidRequest_ThrownError() {
        Map<String, String> wrongRequest = getWrongRequest();

        client.post()
                .uri("/generate")
                .body(BodyInserters.fromValue(wrongRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(entityExchangeResult ->
                        assertTrue(String.valueOf(entityExchangeResult)
                                .contains("The body of request hasn't the field @original")
                        ));
    }

    private HashMap<String, String> getValidRequest() {
        var validRequest = new HashMap<String, String>();
        validRequest.put("original", "https://yandex.ru");
        return validRequest;
    }

    private HashMap<String, String> getWrongRequest() {
        var wrongRequest = new HashMap<String, String>();
        wrongRequest.put("original1", "https://google.com");
        return wrongRequest;
    }

    @Test
    void redirect_OnValidRequest() {
        Map<String, String> validRequest = getValidRequest();

        client.post()
                .uri("/generate")
                .body(BodyInserters.fromValue(validRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("link")
                .value(shortUrl -> client.get()
                        .uri((String) shortUrl)
                        .exchange()
                        .expectStatus().is3xxRedirection()
                        .expectBody()
                        .consumeWith(entityExchangeResult -> assertEquals(
                                Objects.requireNonNull(
                                        entityExchangeResult.getResponseHeaders().getLocation()).toString(),
                                validRequest.get("original"))));
    }

    @Test
    void redirect_OnWrongRequest() {
        Map<String, String> validRequest = getValidRequest();

        client.get()
                .uri("/l/wrongUrl")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(entityExchangeResult ->
                        assertTrue(String.valueOf(entityExchangeResult)
                                .contains("is not found in the system!")
                        ));
    }

    @Test
    void stats() throws InterruptedException {
        Map<String, String> validRequest = getValidRequest();
        var shortUrl = new AtomicReference<String>();
        client.post()
                .uri("/generate")
                .body(BodyInserters.fromValue(validRequest))
                .exchange()
                .expectBody()
                .jsonPath("link")
                .value(link -> shortUrl.set((String) link));

        client.get()
                .uri(shortUrl.get())
                .exchange()
                .returnResult(String.class);

        TimeUnit.SECONDS.sleep(1);
        client.get()
                .uri("/stats" + shortUrl.get())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("link").isNotEmpty()
                .jsonPath("original").isNotEmpty()
                .jsonPath("count").exists();
    }

    @Test
    void statsByPage() throws InterruptedException {
        Map<String, String> validRequest = getValidRequest();
        var shortUrl = new AtomicReference<String>();
        client.post()
                .uri("/generate")
                .body(BodyInserters.fromValue(validRequest))
                .exchange()
                .expectBody()
                .jsonPath("link")
                .value(link -> shortUrl.set((String) link));

        client.get()
                .uri(shortUrl.get())
                .exchange()
                .returnResult(String.class);

        TimeUnit.SECONDS.sleep(1);
        client.get()
                .uri("/stats?page=0&count=33")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(entityExchangeResult -> {
                    try {
                        var jsonTree = new ObjectMapper().readTree(entityExchangeResult.getResponseBody());
                        assertTrue(jsonTree.isArray());
                        assertEquals(1, jsonTree.size());
                        assertTrue(jsonTree.get(0).has("link"));
                        assertTrue(jsonTree.get(0).has("original"));
                        assertTrue(jsonTree.get(0).has("count"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}