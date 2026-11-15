package com.josequinteros.starwarschallenge.films.application;

import org.springframework.stereotype.Service;

import com.josequinteros.starwarschallenge.films.domain.Film;
import com.josequinteros.starwarschallenge.films.domain.FilmPage;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;

@Service
public class FilmService {
	private final FilmPort filmPort;

	public FilmService(FilmPort filmPort) {
		this.filmPort = filmPort;
	}

	public Film findById(long id) {
		if (id < 1) throw new IllegalArgumentException("Film id must be positive");
		return filmPort.findById(id);
	}

	public FilmPage listFilms(int page, int size) {
		Pagination.validate(page, size);
		return filmPort.listFilms(page, size);
	}

	public FilmPage searchFilms(String title, int page, int size) {
		Pagination.validate(page, size);
		if (title == null || title.isBlank()) throw new IllegalArgumentException("Title search term must not be blank");
		return filmPort.searchFilms(title.trim(), page, size);
	}
}
