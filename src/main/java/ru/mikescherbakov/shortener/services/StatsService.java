package ru.mikescherbakov.shortener.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.mikescherbakov.shortener.configurations.AppConfig;
import ru.mikescherbakov.shortener.models.QueryLog;
import ru.mikescherbakov.shortener.models.Stats;
import ru.mikescherbakov.shortener.repositories.StatsRepository;

import java.util.List;
import java.util.Optional;

import static ru.mikescherbakov.shortener.utilities.Utilities.*;

@Service
public class StatsService {
    private final StatsRepository statsRepository;
    private final UrlsService urlsService;
    private final AppConfig appConfig;

    public StatsService(
            StatsRepository statsRepository,
            UrlsService urlsService,
            AppConfig appConfig
    ) {
        this.statsRepository = statsRepository;
        this.urlsService = urlsService;
        this.appConfig = appConfig;
    }

    public Optional<Stats> getStats(String shortUrl) {
        return statsRepository.getStatsByShortUrl(shortUrl);
    }

    public void updateStats(List<QueryLog> logs) {
        logs.forEach(queryLog -> {
                    var urlEntity = queryLog.getUrlEntity();
                    statsRepository.updateStats(urlsService.getShortUrl(urlEntity.getId()),
                            urlEntity.getLongUrl(),
                            urlEntity.getId());
                }
        );
    }

    public void updateRank() {
        statsRepository.updateRank();
    }

    public List<Stats> getStats(Integer page, Integer count) {
        var preparedResult = statsRepository.findAll(PageRequest.of(page, count)).getContent();
        return patchStatsResultToPrettyjson(preparedResult);
    }

    private List<Stats> patchStatsResultToPrettyjson(List<Stats> preparedResult) {
        preparedResult.forEach(stats ->
        {
            stats.setShortUrl(appendShortUrlPrefix(stats.getShortUrl(), appConfig.getShortUrlPrefix()));
            stats.setLongUrl(decodeUrl(stats.getLongUrl()));
        });
        return preparedResult;
    }
}
