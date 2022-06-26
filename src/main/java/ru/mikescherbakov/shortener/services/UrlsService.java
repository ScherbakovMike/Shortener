package ru.mikescherbakov.shortener.services;

import org.springframework.stereotype.Service;
import ru.mikescherbakov.shortener.configurations.AppConfig;
import ru.mikescherbakov.shortener.models.QueryLog;
import ru.mikescherbakov.shortener.models.UrlEntity;
import ru.mikescherbakov.shortener.models.exceptions.UnknownShortUrlException;
import ru.mikescherbakov.shortener.repositories.UrlsRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static ru.mikescherbakov.shortener.utilities.Utilities.*;

@Service
public class UrlsService {

    private final UrlsRepository urlsRepository;
    private final QueryLogService queryLogService;
    private final AppConfig appConfig;
    private final CacheService cacheService;

    public UrlsService(
            UrlsRepository urlsRepository,
            AppConfig appConfig,
            QueryLogService queryLogService,
            CacheService cacheService
    ) {
        this.urlsRepository = urlsRepository;
        this.queryLogService = queryLogService;
        this.appConfig = appConfig;
        this.cacheService = cacheService;
    }

    public String generateShortUrl(String longUrl) {
        var cacheData = cacheService.getWithLongUrl(longUrl);
        if(cacheData.isPresent()) {
            var cacheObject = cacheData.get();
            var entity = (UrlEntity) cacheObject.get("entity");
            var queryLog = new QueryLog(entity);
            queryLogService.saveLog(queryLog);
            return appendShortUrlPrefix((String) cacheObject.get("url"), appConfig.getShortUrlPrefix());
        }

        var urlEntity = new UrlEntity();
        urlEntity.setLongUrl(encodeUrl(longUrl));
        urlsRepository.save(urlEntity);

        var shortUrl = getShortUrl(urlEntity.getId());
        cacheService.saveWithShortUrl(shortUrl, longUrl, urlEntity);
        cacheService.saveWithLongUrl(longUrl, shortUrl, urlEntity);

        return appendShortUrlPrefix(shortUrl, appConfig.getShortUrlPrefix());
    }

    public String getLongUrl(String shortUrl) {
        var cacheData = cacheService.getWithShortUrl(shortUrl);

        if(cacheData.isPresent()) {
            var cacheObject = cacheData.get();
            var entity = (UrlEntity) cacheObject.get("entity");
            var queryLog = new QueryLog(entity);
            queryLogService.saveLog(queryLog);
            return (String) cacheObject.get("url");
        }

        long id = getIdByShortUrl(shortUrl);
        var urlEntity = urlsRepository.findById(id)
                .orElseThrow(() ->
                        new UnknownShortUrlException(appendShortUrlPrefix(shortUrl, appConfig.getShortUrlPrefix())));
        var longUrl = decodeUrl(urlEntity.getLongUrl());

        cacheService.saveWithShortUrl(shortUrl, longUrl, urlEntity);
        queryLogService.saveLog(urlEntity);

        return longUrl;
    }

    public String getShortUrl(Long id) {
        var alphabet = appConfig.getAlphabet().toCharArray();
        var alphabetLength = alphabet.length;
        var digits = new ArrayList<Integer>();
        long idToConvert = id;
        while (idToConvert > 0) {
            var reminder = (int) (idToConvert % alphabetLength);
            digits.add(reminder);
            idToConvert = idToConvert / alphabetLength;
        }
        return digits.stream()
                .map(digit -> String.valueOf(alphabet[digit]))
                .collect(Collectors.joining());
    }

    private long getIdByShortUrl(String shortUrl) {
        var alphabet = appConfig.getAlphabet();
        var alphabetLength = alphabet.length();

        long id = 0L;
        var shortUrlLength = shortUrl.length();
        for (int i = 0; i < shortUrlLength; i++) {
            var symbol = shortUrl.substring(i, i + 1);
            var digit = alphabet.indexOf(symbol);
            id += digit * Math.pow(alphabetLength, i);
        }
        return id;
    }
}
