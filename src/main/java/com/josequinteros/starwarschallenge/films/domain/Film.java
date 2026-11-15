package com.josequinteros.starwarschallenge.films.domain;

import java.util.List;

public record Film(
		String title,
		Integer episodeId,
		String openingCrawl,
		String director,
		String producer,
		String releaseDate,
		List<String> characters,
		List<String> planets,
		List<String> starships,
		List<String> vehicles,
		List<String> species) {
}
