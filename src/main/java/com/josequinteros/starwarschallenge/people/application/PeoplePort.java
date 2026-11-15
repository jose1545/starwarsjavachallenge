package com.josequinteros.starwarschallenge.people.application;

import com.josequinteros.starwarschallenge.people.domain.Person;
import com.josequinteros.starwarschallenge.people.domain.PeoplePage;

public interface PeoplePort {
	Person findById(long id);
	PeoplePage listPeople(int page, int size);
	PeoplePage searchPeople(String name, int page, int size);
}
