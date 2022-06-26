package ru.mikescherbakov.shortener.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mikescherbakov.shortener.configurations.AppConfig;
import ru.mikescherbakov.shortener.models.exceptions.RequestIsWrongException;
import ru.mikescherbakov.shortener.models.exceptions.UnknownShortUrlException;
import ru.mikescherbakov.shortener.services.StatsService;
import ru.mikescherbakov.shortener.services.UrlsService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.mikescherbakov.shortener.utilities.Utilities.*;

@RestController
public class UrlsController {

    private final UrlsService urlsService;
    private final StatsService statsService;
    private final AppConfig appConfig;

    public UrlsController(UrlsService urlsService, StatsService statsService, AppConfig appConfig) {
        this.urlsService = urlsService;
        this.statsService = statsService;
        this.appConfig = appConfig;
    }

    @PostMapping(value = "/generate")
    ResponseEntity<String> generate(@RequestBody JsonNode body) throws JsonProcessingException {
        return prepareAnswerForGenerate(body);
    }

    @GetMapping("/l/{url}")
    void redirect(@PathVariable String url, HttpServletResponse response) throws IOException {
        response.sendRedirect(urlsService.getLongUrl(url));
    }

    @GetMapping("/stats/l/{url}")
    ResponseEntity<String> stats(@PathVariable String url) throws JsonProcessingException {
        var stats = statsService.getStats(url);
        if (stats.isPresent()) {
            var urlEntity = stats.get();
            urlEntity.setShortUrl(appConfig.getShortUrlPrefix() + urlEntity.getShortUrl());
            urlEntity.setLongUrl(decodeUrl(urlEntity.getLongUrl()));
            return new ResponseEntity<>(prettyJson(urlEntity), HttpStatus.OK);
        } else throw new UnknownShortUrlException(appendShortUrlPrefix(url, appConfig.getShortUrlPrefix()));
    }

    @GetMapping("/stats")
    ResponseEntity<String> stats(@RequestParam Integer page, @RequestParam Integer count) throws JsonProcessingException {
        var stats = statsService.getStats(page, count);
        return new ResponseEntity<>(prettyJson(stats), HttpStatus.OK);
    }

    private ResponseEntity<String> prepareAnswerForGenerate(JsonNode body) throws JsonProcessingException {

        if(!body.hasNonNull("original")) {
            throw new RequestIsWrongException("The body of request hasn't the field @original");
        }

        var longUrl = body.get("original").asText();
        var result = new ObjectMapper().createObjectNode();
        var shortUrl = urlsService.generateShortUrl(longUrl);
        result.put("link", shortUrl);
        return new ResponseEntity<>(prettyJson(result), HttpStatus.OK);
    }
}
