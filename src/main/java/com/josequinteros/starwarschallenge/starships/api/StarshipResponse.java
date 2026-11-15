package com.josequinteros.starwarschallenge.starships.api;

import java.util.List;

import com.josequinteros.starwarschallenge.starships.domain.Starship;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Starship", description = "Starship details")
public record StarshipResponse(
		String name, String model, String manufacturer, String costInCredits, String length,
		String maxAtmospheringSpeed, String crew, String passengers, String cargoCapacity,
		String consumables, String hyperdriveRating, String mglt, String starshipClass,
		List<String> pilots, List<String> films) {
	static StarshipResponse from(Starship starship) {
		return new StarshipResponse(starship.name(), starship.model(), starship.manufacturer(),
				starship.costInCredits(), starship.length(), starship.maxAtmospheringSpeed(), starship.crew(),
				starship.passengers(), starship.cargoCapacity(), starship.consumables(), starship.hyperdriveRating(),
				starship.mglt(), starship.starshipClass(), starship.pilots(), starship.films());
	}
}
