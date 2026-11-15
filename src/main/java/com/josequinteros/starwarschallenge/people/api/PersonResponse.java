package com.josequinteros.starwarschallenge.people.api;

import com.josequinteros.starwarschallenge.people.domain.Person;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Person", description = "Star Wars character details")
public record PersonResponse(
		@Schema(description = "Character's name", example = "Luke Skywalker") String name,
		@Schema(description = "Height in centimeters", example = "172") String height,
		@Schema(description = "Mass in kilograms", example = "77") String mass,
		@Schema(description = "Hair color", example = "blond") String hairColor,
		@Schema(description = "Skin color", example = "fair") String skinColor,
		@Schema(description = "Eye color", example = "blue") String eyeColor,
		@Schema(description = "Birth year in the Star Wars calendar", example = "19BBY") String birthYear,
		@Schema(description = "Gender", example = "male") String gender) {
	static PersonResponse from(Person person) {
		return new PersonResponse(person.name(), person.height(), person.mass(), person.hairColor(),
				person.skinColor(), person.eyeColor(), person.birthYear(), person.gender());
	}
}
