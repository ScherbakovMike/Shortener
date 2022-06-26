package ru.mikescherbakov.shortener.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "url_entities", indexes = @Index(columnList = "longUrl"))
@NoArgsConstructor
@Getter
@Setter
public class UrlEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;
    private String longUrl;
}
