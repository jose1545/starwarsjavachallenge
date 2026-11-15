package com.josequinteros.starwarschallenge.vehicles.infrastructure.swapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.vehicles.domain.VehicleSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiVehicleSummaryResponse(String uid, String name, String model, String url, Properties properties) {
	VehicleSummary toSummary() {
		String resolvedName = name != null ? name : properties == null ? null : properties.name();
		String resolvedModel = model != null ? model : properties == null ? null : properties.model();
		String resolvedUrl = url != null ? url : properties == null ? null : properties.url();
		if (uid == null || uid.isBlank() || resolvedName == null || resolvedName.isBlank() || resolvedUrl == null || resolvedUrl.isBlank()) {
			throw new IllegalArgumentException("SWAPI.tech returned an invalid vehicle summary");
		}
		return new VehicleSummary(uid, resolvedName, resolvedModel, resolvedUrl);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Properties(String name, String model, String url) {
	}
}
