package com.josequinteros.starwarschallenge.films.infrastructure.swapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.films.domain.FilmSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiFilmSummaryResponse(String uid, Properties properties) {
	FilmSummary toFilmSummary() {
		if (uid == null || uid.isBlank() || properties == null || properties.title() == null
				|| properties.title().isBlank() || properties.url() == null || properties.url().isBlank()) {
			throw new IllegalArgumentException("SWAPI.tech returned an invalid film summary");
		}
		return new FilmSummary(uid, properties.title(), properties.url());
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Properties(String title, String url) {
	}
}
