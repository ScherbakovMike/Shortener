package ru.mikescherbakov.shortener.models.exceptions;

public class RequestIsWrongException extends RuntimeException {
    public RequestIsWrongException(String message) {
        super(message);
    }
}
