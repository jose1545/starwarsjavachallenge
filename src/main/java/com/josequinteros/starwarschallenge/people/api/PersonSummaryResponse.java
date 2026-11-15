package com.josequinteros.starwarschallenge.people.api;

import com.josequinteros.starwarschallenge.people.domain.PersonSummary;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PersonSummary", description = "A brief reference to a Star Wars person")
public record PersonSummaryResponse(
		@Schema(description = "SWAPI.tech person identifier", example = "1") String uid,
		@Schema(description = "Character's name", example = "Luke Skywalker") String name,
		@Schema(description = "SWAPI.tech URL for the full person resource", example = "https://www.swapi.tech/api/people/1") String url) {
	static PersonSummaryResponse from(PersonSummary person) {
		return new PersonSummaryResponse(person.uid(), person.name(), person.url());
	}
}
