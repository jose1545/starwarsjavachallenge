package com.josequinteros.starwarschallenge.films.application;

import com.josequinteros.starwarschallenge.films.domain.Film;
import com.josequinteros.starwarschallenge.films.domain.FilmPage;

public interface FilmPort {
	Film findById(long id);
	FilmPage listFilms(int page, int size);
	FilmPage searchFilms(String title, int page, int size);
}
