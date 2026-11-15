package com.josequinteros.starwarschallenge.people.infrastructure.swapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.people.domain.Person;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiPersonResponse(
		String name,
		String height,
		String mass,
		@JsonProperty("hair_color") String hairColor,
		@JsonProperty("skin_color") String skinColor,
		@JsonProperty("eye_color") String eyeColor,
		@JsonProperty("birth_year") String birthYear,
		String gender) {
	Person toPerson() {
		return new Person(name, height, mass, hairColor, skinColor, eyeColor, birthYear, gender);
	}
}
