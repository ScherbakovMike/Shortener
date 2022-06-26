package ru.mikescherbakov.shortener.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "stats", indexes = @Index(columnList = "shortUrl"))
@NoArgsConstructor
@Getter
@Setter
public class Stats {
    @Id
    @JsonProperty("link")
    private String shortUrl;

    @ManyToOne
    @JoinColumn(name = "entity_id", nullable = false)
    @JsonIgnore
    private UrlEntity urlEntity;

    @JsonProperty("original")
    private String longUrl;

    @JsonProperty("rank")
    private Long rank;

    @JsonProperty("count")
    private Long count;
}

