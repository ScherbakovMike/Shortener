package ru.mikescherbakov.shortener.services;

import org.springframework.stereotype.Service;
import ru.mikescherbakov.shortener.models.UpdateHistory;
import ru.mikescherbakov.shortener.repositories.UpdateHistoryRepository;

import java.util.Optional;

@Service
public class UpdateHistoryService {
    private final UpdateHistoryRepository updateHistoryRepository;

    public UpdateHistoryService(UpdateHistoryRepository updateHistoryRepository) {
        this.updateHistoryRepository = updateHistoryRepository;
    }


    public Optional<UpdateHistory> lastUpdateEntry() {
        return updateHistoryRepository.findFirstByOrderByIdDesc();
    }

    public void addStamp(long newUpdateTimeStamp) {
        updateHistoryRepository.save(new UpdateHistory(newUpdateTimeStamp));
    }
}
