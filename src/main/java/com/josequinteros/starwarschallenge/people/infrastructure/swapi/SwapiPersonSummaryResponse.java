package com.josequinteros.starwarschallenge.people.infrastructure.swapi;

import com.josequinteros.starwarschallenge.people.domain.PersonSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiPersonSummaryResponse(String uid, String name, String url, Properties properties) {
	PersonSummary toPersonSummary() {
		String resolvedName = name != null ? name : properties == null ? null : properties.name();
		String resolvedUrl = url != null ? url : properties == null ? null : properties.url();
		if (uid == null || uid.isBlank() || resolvedName == null || resolvedName.isBlank() || resolvedUrl == null || resolvedUrl.isBlank()) {
			throw new IllegalArgumentException("SWAPI returned an invalid person summary");
		}
		return new PersonSummary(uid, resolvedName, resolvedUrl);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Properties(String name, String url) {
	}
}
