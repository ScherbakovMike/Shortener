package ru.mikescherbakov.shortener.services;

import org.springframework.stereotype.Service;
import ru.mikescherbakov.shortener.models.UrlEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CacheService {
    private final Map<String, Map<String, Object>> longToShortUrlCache;
    private final Map<String, Map<String, Object>> shortToLongUrlCache;

    public CacheService(
            Map<String, Map<String, Object>> longToShortUrlCache,
            Map<String, Map<String, Object>> shortToLongUrlCache) {
        this.longToShortUrlCache = longToShortUrlCache;
        this.shortToLongUrlCache = shortToLongUrlCache;
    }

    public Optional<Map<String, Object>> getWithLongUrl(String longUrl) {
        return Optional.ofNullable(longToShortUrlCache.get(longUrl));
    }

    public Optional<Map<String, Object>> getWithShortUrl(String shortUrl) {
        return Optional.ofNullable(shortToLongUrlCache.get(shortUrl));
    }

    public void saveWithLongUrl(String longUrl, String shortUrl, UrlEntity urlEntity) {
        var cacheObject = new HashMap<String, Object>();
        cacheObject.put("url", shortUrl);
        cacheObject.put("entity", urlEntity);
        longToShortUrlCache.put(longUrl, cacheObject);
    }

    public void saveWithShortUrl(String shortUrl, String longUrl, UrlEntity urlEntity) {
        var cacheObject = new HashMap<String, Object>();
        cacheObject.put("url", longUrl);
        cacheObject.put("entity", urlEntity);
        shortToLongUrlCache.put(shortUrl, cacheObject);
    }
}
