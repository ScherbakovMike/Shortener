package ru.mikescherbakov.shortener.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mikescherbakov.shortener.models.Stats;

import java.util.Optional;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    Optional<Stats> getStatsByShortUrl(String shortUrl);

    @Transactional
    @Modifying
    @Query(value = "insert into stats " +
            "(short_url, long_url, entity_id, count) " +
            "values(?1, ?2, ?3, 1) " +
            "on duplicate key update count=count+1",
            nativeQuery = true)
    void updateStats(String shortUrl, String longUrl, long entityId);

    @Transactional
    @Modifying
    @Query(value = "update stats as source " +
            "set source.rank=" +
            "select t2.rank " +
            "from (select short_url, RANK() over (order by count desc) as rank from stats) as t2 " +
            "where source.short_url=t2.short_url", nativeQuery = true)
    void updateRank();
}
