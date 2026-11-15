package com.josequinteros.starwarschallenge.vehicles.infrastructure.swapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.vehicles.domain.Vehicle;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiVehicleDetailResponse(String message, Result result) {
	Vehicle toVehicle() {
		if (result == null || result.properties() == null) throw new IllegalArgumentException("SWAPI.tech returned an invalid vehicle response");
		return result.properties().toVehicle();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Result(SwapiVehicleResponse properties) {
	}
}
