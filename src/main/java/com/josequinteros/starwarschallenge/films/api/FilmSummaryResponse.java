package com.josequinteros.starwarschallenge.films.api;

import com.josequinteros.starwarschallenge.films.domain.FilmSummary;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FilmSummary", description = "A brief reference to a film")
public record FilmSummaryResponse(String uid, String name, String url) {
	static FilmSummaryResponse from(FilmSummary film) {
		return new FilmSummaryResponse(film.uid(), film.name(), film.url());
	}
}
