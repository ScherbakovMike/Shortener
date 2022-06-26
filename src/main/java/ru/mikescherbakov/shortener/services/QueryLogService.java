package ru.mikescherbakov.shortener.services;

import org.springframework.stereotype.Service;
import ru.mikescherbakov.shortener.models.QueryLog;
import ru.mikescherbakov.shortener.models.UrlEntity;
import ru.mikescherbakov.shortener.repositories.QueryLogRepository;

import java.time.Instant;
import java.util.List;

@Service
public class QueryLogService {

    private final QueryLogRepository queryLogRepository;

    public QueryLogService(
            QueryLogRepository queryLogRepository
    ) {
        this.queryLogRepository = queryLogRepository;
    }

    public void saveLog(QueryLog queryLog) {
        queryLogRepository.save(queryLog);
    }

    public void saveLog(UrlEntity urlEntity) {
        var queryLog = new QueryLog();
        queryLog.setQueryTime(Instant.now().getEpochSecond());
        queryLog.setUrlEntity(urlEntity);
        queryLogRepository.save(queryLog);
    }

    public List<QueryLog> getQueryLogsAfter(Long timestampSince) {
        return queryLogRepository.getQueryLogsByQueryTimeAfter(timestampSince);
    }
}
