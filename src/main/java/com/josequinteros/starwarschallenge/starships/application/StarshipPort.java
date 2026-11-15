package com.josequinteros.starwarschallenge.starships.application;

import com.josequinteros.starwarschallenge.starships.domain.Starship;
import com.josequinteros.starwarschallenge.starships.domain.StarshipPage;

public interface StarshipPort {
	Starship findById(long id);
	StarshipPage listStarships(int page, int size);
	StarshipPage searchStarships(String name, String model, int page, int size);
}
