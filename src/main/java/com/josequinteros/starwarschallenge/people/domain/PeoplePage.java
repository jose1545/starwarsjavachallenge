package com.josequinteros.starwarschallenge.people.domain;

import java.util.List;

public record PeoplePage(long count, Integer nextPage, Integer previousPage, List<PersonSummary> results) {
	public PeoplePage {
		results = List.copyOf(results);
	}
}
