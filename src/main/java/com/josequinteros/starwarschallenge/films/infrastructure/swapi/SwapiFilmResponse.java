package com.josequinteros.starwarschallenge.films.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.josequinteros.starwarschallenge.films.domain.Film;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiFilmResponse(
		String title,
		@JsonProperty("episode_id") Integer episodeId,
		@JsonProperty("opening_crawl") String openingCrawl,
		String director,
		String producer,
		@JsonProperty("release_date") String releaseDate,
		List<String> characters,
		List<String> planets,
		List<String> starships,
		List<String> vehicles,
		List<String> species) {
	Film toFilm() {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("SWAPI.tech returned an invalid film detail");
		}
		return new Film(title, episodeId, openingCrawl, director, producer, releaseDate,
				characters, planets, starships, vehicles, species);
	}
}
