package com.josequinteros.starwarschallenge.people.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.josequinteros.starwarschallenge.people.domain.Person;
import com.josequinteros.starwarschallenge.people.domain.PeoplePage;

class PeopleServiceTest {

	private PeoplePort peoplePort;
	private PeopleService peopleService;

	@BeforeEach
	void setUp() {
		peoplePort = mock(PeoplePort.class);
		peopleService = new PeopleService(peoplePort);
	}

	@Test
	void findsPersonById() {
		Person luke = new Person("Luke Skywalker", "172", "77", "blond", "fair", "blue", "19BBY", "male");
		when(peoplePort.findById(1)).thenReturn(luke);

		assertThat(peopleService.findById(1)).isEqualTo(luke);
		verify(peoplePort).findById(1);
	}

	@Test
	void rejectsNonPositiveId() {
		assertThatThrownBy(() -> peopleService.findById(0))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void rejectsNonPositivePage() {
		assertThatThrownBy(() -> peopleService.listPeople(0, 10))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void rejectsPageSizeAboveMaximum() {
		assertThatThrownBy(() -> peopleService.listPeople(1, 101))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void listsRequestedPeoplePage() {
		PeoplePage page = new PeoplePage(0, null, null, java.util.List.of());
		when(peoplePort.listPeople(2, 5)).thenReturn(page);

		assertThat(peopleService.listPeople(2, 5)).isEqualTo(page);
		verify(peoplePort).listPeople(2, 5);
	}
}
