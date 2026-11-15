package com.josequinteros.starwarschallenge.films.api;

import java.util.List;

import com.josequinteros.starwarschallenge.films.domain.Film;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Film", description = "Film details")
public record FilmResponse(
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
	static FilmResponse from(Film film) {
		return new FilmResponse(film.title(), film.episodeId(), film.openingCrawl(), film.director(),
				film.producer(), film.releaseDate(), film.characters(), film.planets(), film.starships(),
				film.vehicles(), film.species());
	}
}
