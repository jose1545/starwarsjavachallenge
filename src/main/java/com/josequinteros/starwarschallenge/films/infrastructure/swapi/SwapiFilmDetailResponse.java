package com.josequinteros.starwarschallenge.films.infrastructure.swapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.films.domain.Film;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiFilmDetailResponse(String message, Result result) {
	Film toFilm() {
		if (result == null || result.properties() == null) {
			throw new IllegalArgumentException("SWAPI.tech returned an invalid film response");
		}
		return result.properties().toFilm();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Result(SwapiFilmResponse properties) {
	}
}
