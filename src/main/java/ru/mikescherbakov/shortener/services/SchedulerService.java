package ru.mikescherbakov.shortener.services;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.mikescherbakov.shortener.models.QueryLog;
import ru.mikescherbakov.shortener.models.UpdateHistory;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class SchedulerService {

    private final UpdateHistoryService updateHistoryService;
    private final QueryLogService queryLogService;
    private final StatsService statsService;

    public SchedulerService(
            UpdateHistoryService updateHistoryService,
            QueryLogService queryLogService,
            StatsService statsService
    ) {
        this.updateHistoryService = updateHistoryService;
        this.queryLogService = queryLogService;
        this.statsService = statsService;
    }

    @Scheduled(fixedRateString = "${config.updatestatsrate}")
    public void updateStats() {
        long lastUpdateTimeStamp = getLastUpdateTimeStamp();
        var lastLogs = queryLogService.getQueryLogsAfter(lastUpdateTimeStamp);

        statsService.updateStats(lastLogs);

        var newUpdateTimeStamp = lastLogs.stream().mapToLong(QueryLog::getQueryTime).max();
        newUpdateTimeStamp.ifPresent(updateHistoryService::addStamp);
    }

    @Scheduled(fixedRateString = "${config.updaterankrate}", timeUnit = TimeUnit.SECONDS)
    public void updateRank() {
        statsService.updateRank();
    }

    private long getLastUpdateTimeStamp() {
        var lastUpdateEntry = updateHistoryService.lastUpdateEntry();
        return lastUpdateEntry.map(UpdateHistory::getId).orElse(0L);
    }
}