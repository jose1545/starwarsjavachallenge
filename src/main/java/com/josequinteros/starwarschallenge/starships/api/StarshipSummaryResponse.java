package com.josequinteros.starwarschallenge.starships.api;

import com.josequinteros.starwarschallenge.starships.domain.StarshipSummary;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StarshipSummary", description = "A brief reference to a starship")
public record StarshipSummaryResponse(String uid, String name,
		@Schema(description = "Starship model, included in expanded SWAPI.tech list results") String model,
		String url) {
	static StarshipSummaryResponse from(StarshipSummary starship) {
		return new StarshipSummaryResponse(starship.uid(), starship.name(), starship.model(), starship.url());
	}
}
