package com.josequinteros.starwarschallenge.starships.application;

import org.springframework.stereotype.Service;

import com.josequinteros.starwarschallenge.starships.domain.Starship;
import com.josequinteros.starwarschallenge.starships.domain.StarshipPage;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;

@Service
public class StarshipService {
	private final StarshipPort starshipPort;

	public StarshipService(StarshipPort starshipPort) {
		this.starshipPort = starshipPort;
	}

	public Starship findById(long id) {
		if (id < 1) throw new IllegalArgumentException("Starship id must be positive");
		return starshipPort.findById(id);
	}

	public StarshipPage listStarships(int page, int size) {
		Pagination.validate(page, size);
		return starshipPort.listStarships(page, size);
	}

	public StarshipPage searchStarships(String name, String model, int page, int size) {
		Pagination.validate(page, size);
		name = normalize(name);
		model = normalize(model);
		if (name == null && model == null) throw new IllegalArgumentException("At least one search term is required");
		return starshipPort.searchStarships(name, model, page, size);
	}

	private static String normalize(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
