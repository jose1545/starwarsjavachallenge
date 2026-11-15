package com.josequinteros.starwarschallenge.people.application;

import org.springframework.stereotype.Service;

import com.josequinteros.starwarschallenge.people.domain.Person;
import com.josequinteros.starwarschallenge.people.domain.PeoplePage;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;

@Service
public class PeopleService {

	private final PeoplePort peoplePort;

	public PeopleService(PeoplePort peoplePort) {
		this.peoplePort = peoplePort;
	}

	public Person findById(long id) {
		if (id < 1) {
			throw new IllegalArgumentException("Person id must be a positive number");
		}
		return peoplePort.findById(id);
	}

	public PeoplePage listPeople(int page, int size) {
		Pagination.validate(page, size);
		return peoplePort.listPeople(page, size);
	}

	public PeoplePage searchPeople(String name, int page, int size) {
		Pagination.validate(page, size);
		if (name == null || name.isBlank()) throw new IllegalArgumentException("Name search term must not be blank");
		return peoplePort.searchPeople(name.trim(), page, size);
	}
}
