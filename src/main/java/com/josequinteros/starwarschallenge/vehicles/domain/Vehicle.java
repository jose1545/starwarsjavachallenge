package com.josequinteros.starwarschallenge.vehicles.domain;

import java.util.List;

public record Vehicle(
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
		String vehicleClass,
		List<String> pilots,
		List<String> films) {
}
