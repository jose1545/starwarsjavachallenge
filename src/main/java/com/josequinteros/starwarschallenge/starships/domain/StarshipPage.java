package com.josequinteros.starwarschallenge.starships.domain;

import java.util.List;

public record StarshipPage(long count, Integer nextPage, Integer previousPage, List<StarshipSummary> results) {
	public StarshipPage {
		results = List.copyOf(results);
	}
}
