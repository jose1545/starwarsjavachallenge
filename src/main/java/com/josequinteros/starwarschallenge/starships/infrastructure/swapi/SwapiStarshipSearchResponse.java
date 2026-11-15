package com.josequinteros.starwarschallenge.starships.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;
import com.josequinteros.starwarschallenge.starships.domain.StarshipPage;
import com.josequinteros.starwarschallenge.starships.domain.StarshipSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiStarshipSearchResponse(String message, List<SwapiStarshipSummaryResponse> result) {
	List<StarshipSummary> toSummaries() {
		if (result == null) throw new IllegalArgumentException("SWAPI.tech returned an invalid starship search response");
		return result.stream().map(summary -> {
			if (summary == null) throw new IllegalArgumentException("SWAPI.tech returned a null starship search result");
			return summary.toSummary();
		}).toList();
	}

	StarshipPage toStarshipPage(int page, int size) {
		PageSlice<StarshipSummary> slice = Pagination.slice(toSummaries(), page, size);
		return new StarshipPage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
	}
}
