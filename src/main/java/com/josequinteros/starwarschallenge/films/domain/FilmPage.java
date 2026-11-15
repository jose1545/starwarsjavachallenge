package com.josequinteros.starwarschallenge.films.domain;

import java.util.List;

public record FilmPage(long count, Integer nextPage, Integer previousPage, List<FilmSummary> results) {
	public FilmPage {
		results = List.copyOf(results);
	}
}
