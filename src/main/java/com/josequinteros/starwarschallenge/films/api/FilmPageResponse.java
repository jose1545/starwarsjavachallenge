package com.josequinteros.starwarschallenge.films.api;

import java.util.List;

import com.josequinteros.starwarschallenge.films.domain.FilmPage;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FilmPage", description = "A page of film summaries")
public record FilmPageResponse(long count, Integer nextPage, Integer previousPage, List<FilmSummaryResponse> results) {
	static FilmPageResponse from(FilmPage page) {
		return new FilmPageResponse(page.count(), page.nextPage(), page.previousPage(),
				page.results().stream().map(FilmSummaryResponse::from).toList());
	}
}
