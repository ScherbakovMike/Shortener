package ru.mikescherbakov.shortener.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mikescherbakov.shortener.models.QueryLog;

import java.util.List;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {
    List<QueryLog> getQueryLogsByQueryTimeAfter(Long timeStamp);
}
