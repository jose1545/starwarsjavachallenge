package com.josequinteros.starwarschallenge.starships.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;
import com.josequinteros.starwarschallenge.starships.domain.StarshipPage;
import com.josequinteros.starwarschallenge.starships.domain.StarshipSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiStarshipPageResponse(
		@JsonProperty("total_records") Long totalRecords,
		@JsonProperty("total_pages") Integer totalPages,
		String next,
		String previous,
		List<SwapiStarshipSummaryResponse> results) {
	StarshipPage toStarshipPage(int page, int size) {
		List<StarshipSummary> summaries = toSummaries();
		PageSlice<StarshipSummary> slice = Pagination.fromPage(totalRecords, summaries, page, size);
		return new StarshipPage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
	}

	List<StarshipSummary> toSummaries() {
		if (totalRecords == null || totalRecords < 0 || totalPages == null || totalPages < 0 || results == null) {
			throw new IllegalArgumentException("SWAPI.tech returned an invalid starship page");
		}
		return results.stream().map(summary -> {
			if (summary == null) throw new IllegalArgumentException("SWAPI.tech returned a null starship summary");
			return summary.toSummary();
		}).toList();
	}
}
