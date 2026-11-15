package com.josequinteros.starwarschallenge.people.domain;

public record Person(
		String name,
		String height,
		String mass,
		String hairColor,
		String skinColor,
		String eyeColor,
		String birthYear,
		String gender) {
}
