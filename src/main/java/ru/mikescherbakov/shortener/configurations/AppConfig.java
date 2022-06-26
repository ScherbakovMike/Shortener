package ru.mikescherbakov.shortener.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config")
@Getter
@Setter
public class AppConfig {
    private String alphabet;
    private String shortUrlPrefix;
    private Integer updateStatsRate;
    private Integer updateRankRate;
}
