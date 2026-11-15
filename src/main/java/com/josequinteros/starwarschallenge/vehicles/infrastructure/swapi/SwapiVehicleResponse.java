package com.josequinteros.starwarschallenge.vehicles.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.josequinteros.starwarschallenge.vehicles.domain.Vehicle;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiVehicleResponse(
		String name,
		String model,
		String manufacturer,
		@JsonProperty("cost_in_credits") String costInCredits,
		String length,
		@JsonProperty("max_atmosphering_speed") String maxAtmospheringSpeed,
		String crew,
		String passengers,
		@JsonProperty("cargo_capacity") String cargoCapacity,
		String consumables,
		@JsonProperty("vehicle_class") String vehicleClass,
		List<String> pilots,
		List<String> films) {
	Vehicle toVehicle() {
		if (name == null || name.isBlank()) throw new IllegalArgumentException("SWAPI.tech returned an invalid vehicle detail");
		return new Vehicle(name, model, manufacturer, costInCredits, length, maxAtmospheringSpeed, crew,
				passengers, cargoCapacity, consumables, vehicleClass, pilots, films);
	}
}
