package com.josequinteros.starwarschallenge.people.api;

import java.util.List;

import com.josequinteros.starwarschallenge.people.domain.PeoplePage;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PeoplePage", description = "A page of Star Wars people")
public record PeoplePageResponse(
		@Schema(description = "Total number of people", example = "82") long count,
		@Schema(description = "Number of the next page, or null when this is the last page", example = "2") Integer nextPage,
		@Schema(description = "Number of the previous page, or null on the first page", example = "1") Integer previousPage,
		@Schema(description = "People summaries on this page") List<PersonSummaryResponse> results) {
	static PeoplePageResponse from(PeoplePage page) {
		return new PeoplePageResponse(page.count(), page.nextPage(), page.previousPage(),
				page.results().stream().map(PersonSummaryResponse::from).toList());
	}
}
