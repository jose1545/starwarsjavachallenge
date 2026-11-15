package com.josequinteros.starwarschallenge.starships.api;

import java.util.List;

import com.josequinteros.starwarschallenge.starships.domain.StarshipPage;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StarshipPage", description = "A page of starship summaries")
public record StarshipPageResponse(long count, Integer nextPage, Integer previousPage, List<StarshipSummaryResponse> results) {
	static StarshipPageResponse from(StarshipPage page) {
		return new StarshipPageResponse(page.count(), page.nextPage(), page.previousPage(),
				page.results().stream().map(StarshipSummaryResponse::from).toList());
	}
}
