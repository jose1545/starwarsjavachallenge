package com.josequinteros.starwarschallenge.starships.domain;

import java.util.List;

public record Starship(
		String name,
		String model,
		String manufacturer,
		String costInCredits,
		String length,
		String maxAtmospheringSpeed,
		String crew,
		String passengers,
		String cargoCapacity,
		String consumables,
		String hyperdriveRating,
		String mglt,
		String starshipClass,
		List<String> pilots,
		List<String> films) {
}
