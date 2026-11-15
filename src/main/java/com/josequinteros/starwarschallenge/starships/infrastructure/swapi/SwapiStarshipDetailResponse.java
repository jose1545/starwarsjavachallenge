package com.josequinteros.starwarschallenge.starships.infrastructure.swapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.starships.domain.Starship;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiStarshipDetailResponse(String message, Result result) {
	Starship toStarship() {
		if (result == null || result.properties() == null) throw new IllegalArgumentException("SWAPI.tech returned an invalid starship response");
		return result.properties().toStarship();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Result(SwapiStarshipResponse properties) {
	}
}
