package ru.mikescherbakov.shortener.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mikescherbakov.shortener.models.UpdateHistory;

import java.util.Optional;

@Repository
public interface UpdateHistoryRepository extends JpaRepository<UpdateHistory, Long> {
    Optional<UpdateHistory> findFirstByOrderByIdDesc();
}
