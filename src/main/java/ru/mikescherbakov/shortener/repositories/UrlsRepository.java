package ru.mikescherbakov.shortener.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mikescherbakov.shortener.models.UrlEntity;

@Repository
public interface UrlsRepository extends CrudRepository<UrlEntity, Long> {
}
