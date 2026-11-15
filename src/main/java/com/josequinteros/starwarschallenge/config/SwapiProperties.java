package com.josequinteros.starwarschallenge.config;

import java.net.URI;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "swapi")
public record SwapiProperties(
		@NotNull URI baseUrl,
		@NotNull Duration connectionTimeout,
		@NotNull Duration readTimeout) {
}
