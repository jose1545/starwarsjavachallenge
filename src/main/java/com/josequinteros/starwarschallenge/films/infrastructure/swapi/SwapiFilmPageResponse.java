package com.josequinteros.starwarschallenge.films.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.films.domain.FilmPage;
import com.josequinteros.starwarschallenge.films.domain.FilmSummary;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiFilmPageResponse(
		String message,
		List<SwapiFilmSummaryResponse> result) {
	FilmPage toFilmPage(int page, int size) {
		if (result == null) {
			throw new IllegalArgumentException("SWAPI.tech returned an invalid film page");
		}
		List<FilmSummary> summaries = result.stream().map(summary -> {
					if (summary == null) throw new IllegalArgumentException("SWAPI.tech returned a null film summary");
					return summary.toFilmSummary();
				}).toList();
		PageSlice<FilmSummary> slice = Pagination.slice(summaries, page, size);
		return new FilmPage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
	}
}
