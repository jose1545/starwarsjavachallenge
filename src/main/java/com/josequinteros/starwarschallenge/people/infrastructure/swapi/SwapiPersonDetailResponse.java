package com.josequinteros.starwarschallenge.people.infrastructure.swapi;

import com.josequinteros.starwarschallenge.people.domain.Person;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiPersonDetailResponse(String message, Result result) {
	Person toPerson() {
		if (result == null || result.properties() == null
				|| result.properties().name() == null || result.properties().name().isBlank()) {
			throw new IllegalArgumentException("SWAPI returned an invalid person detail");
		}
		return result.properties().toPerson();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Result(SwapiPersonResponse properties) {
	}
}
