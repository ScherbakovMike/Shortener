package ru.mikescherbakov.shortener.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static ru.mikescherbakov.shortener.utilities.Utilities.currentTimeStamp;

@Entity
@Table(name = "logs")
@NoArgsConstructor
@Getter
@Setter
public class QueryLog {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;

    private Long queryTime;

    @ManyToOne
    @JoinColumn(name = "entity_id", nullable = false)
    private UrlEntity urlEntity;

    public QueryLog(UrlEntity urlEntity) {
        this.urlEntity = urlEntity;
        this.queryTime = currentTimeStamp();
    }
}
