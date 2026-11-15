package com.josequinteros.starwarschallenge.people.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.josequinteros.starwarschallenge.people.domain.PeoplePage;
import com.josequinteros.starwarschallenge.people.domain.PersonSummary;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiPeoplePageResponse(
		@JsonProperty("total_records") Long totalRecords,
		@JsonProperty("total_pages") Integer totalPages,
		String next,
		String previous,
		List<SwapiPersonSummaryResponse> results) {
	PeoplePage toPeoplePage(int page, int size) {
		if (totalRecords == null || totalRecords < 0 || totalPages == null || totalPages < 0 || results == null) {
			throw new IllegalArgumentException("SWAPI returned an invalid people page");
		}
		List<PersonSummary> summaries = results.stream().map(person -> {
					if (person == null) {
						throw new IllegalArgumentException("SWAPI returned a null person summary");
					}
					return person.toPersonSummary();
				}).toList();
		PageSlice<PersonSummary> slice = Pagination.fromPage(totalRecords, summaries, page, size);
		return new PeoplePage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
	}
}
