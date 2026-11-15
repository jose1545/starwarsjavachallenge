package com.josequinteros.starwarschallenge.starships.infrastructure.swapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.starships.domain.StarshipSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiStarshipSummaryResponse(String uid, String name, String model, String url, Properties properties) {
	StarshipSummary toSummary() {
		String resolvedName = name != null ? name : properties == null ? null : properties.name();
		String resolvedModel = model != null ? model : properties == null ? null : properties.model();
		String resolvedUrl = url != null ? url : properties == null ? null : properties.url();
		if (uid == null || uid.isBlank() || resolvedName == null || resolvedName.isBlank() || resolvedUrl == null || resolvedUrl.isBlank()) {
			throw new IllegalArgumentException("SWAPI.tech returned an invalid starship summary");
		}
		return new StarshipSummary(uid, resolvedName, resolvedModel, resolvedUrl);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Properties(String name, String model, String url) {
	}
}
