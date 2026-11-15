package com.josequinteros.starwarschallenge.people.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.people.domain.PeoplePage;
import com.josequinteros.starwarschallenge.people.domain.PersonSummary;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiPeopleSearchResponse(String message, List<SwapiPersonSummaryResponse> result) {
	PeoplePage toPeoplePage(int page, int size) {
		if (result == null) throw new IllegalArgumentException("SWAPI returned an invalid people search response");
		List<PersonSummary> summaries = result.stream().map(person -> {
			if (person == null) throw new IllegalArgumentException("SWAPI returned a null person search result");
			return person.toPersonSummary();
		}).toList();
		PageSlice<PersonSummary> slice = Pagination.slice(summaries, page, size);
		return new PeoplePage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
	}
}
