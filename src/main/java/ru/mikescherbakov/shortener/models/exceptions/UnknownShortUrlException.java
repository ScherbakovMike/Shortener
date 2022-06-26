package ru.mikescherbakov.shortener.models.exceptions;

public class UnknownShortUrlException extends RuntimeException {
    public UnknownShortUrlException(String shortUrl) {
        super(String.format("The short URL%n%s%nis not found in the system!", shortUrl));
    }
}
