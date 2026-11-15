package com.josequinteros.starwarschallenge.starships.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.josequinteros.starwarschallenge.starships.domain.Starship;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiStarshipResponse(
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
		@JsonProperty("hyperdrive_rating") String hyperdriveRating,
		@JsonProperty("MGLT") String mglt,
		@JsonProperty("starship_class") String starshipClass,
		List<String> pilots,
		List<String> films) {
	Starship toStarship() {
		if (name == null || name.isBlank()) throw new IllegalArgumentException("SWAPI.tech returned an invalid starship detail");
		return new Starship(name, model, manufacturer, costInCredits, length, maxAtmospheringSpeed, crew,
				passengers, cargoCapacity, consumables, hyperdriveRating, mglt, starshipClass, pilots, films);
	}
}
