package ru.mikescherbakov.shortener.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.mikescherbakov.shortener.models.exceptions.RequestIsWrongException;
import ru.mikescherbakov.shortener.models.exceptions.UnknownShortUrlException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UnknownShortUrlException.class })
    protected ResponseEntity<Object> handleUnknownShortUrl(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = String.format("The short url is not found. Details:%n%s", ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.EXPECTATION_FAILED, request);
    }

    @ExceptionHandler(value = {RequestIsWrongException.class })
    protected ResponseEntity<Object> handleJsonProcessing(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = String.format(
                "Request processing exception has been happened. Details:%n%s", ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.EXPECTATION_FAILED, request);
    }
}