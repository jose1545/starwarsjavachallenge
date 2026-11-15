package com.josequinteros.starwarschallenge.people.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

import com.josequinteros.starwarschallenge.people.application.PeopleService;
import com.josequinteros.starwarschallenge.people.domain.Person;
import com.josequinteros.starwarschallenge.people.domain.PersonSummary;
import com.josequinteros.starwarschallenge.people.domain.PeoplePage;

@WebMvcTest(PeopleController.class)
@AutoConfigureMockMvc(addFilters = false)
class PeopleControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PeopleService peopleService;

	@Test
	void returnsPersonAsApiResponse() throws Exception {
		when(peopleService.findById(1)).thenReturn(
				new Person("Luke Skywalker", "172", "77", "blond", "fair", "blue", "19BBY", "male"));

		mockMvc.perform(get("/api/people/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Luke Skywalker"))
				.andExpect(jsonPath("$.hairColor").value("blond"));
	}

	@Test
	void rejectsNonPositivePersonId() throws Exception {
		mockMvc.perform(get("/api/people/0"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Invalid request"));
	}

	@Test
	void returnsPaginatedPeopleList() throws Exception {
		when(peopleService.listPeople(1, 10)).thenReturn(new PeoplePage(82, 2, null,
				java.util.List.of(new PersonSummary("1", "Luke Skywalker", "https://www.swapi.tech/api/people/1"))));

		mockMvc.perform(get("/api/people"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(82))
				.andExpect(jsonPath("$.nextPage").value(2))
				.andExpect(jsonPath("$.previousPage").value(org.hamcrest.Matchers.nullValue()))
				.andExpect(jsonPath("$.results[0].uid").value("1"))
				.andExpect(jsonPath("$.results[0].name").value("Luke Skywalker"))
				.andExpect(jsonPath("$.results[0].url").value("https://www.swapi.tech/api/people/1"))
				.andExpect(jsonPath("$.results[0].height").doesNotExist());
	}

	@Test
	void rejectsNonPositivePeoplePage() throws Exception {
		mockMvc.perform(get("/api/people?page=0"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Invalid request"));
	}

	@Test
	void acceptsPageAndCustomSize() throws Exception {
		when(peopleService.listPeople(2, 5)).thenReturn(new PeoplePage(12, 3, 1, java.util.List.of()));

		mockMvc.perform(get("/api/people?page=2&size=5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(12))
				.andExpect(jsonPath("$.nextPage").value(3))
				.andExpect(jsonPath("$.previousPage").value(1));
	}

	@Test
	void rejectsPageSizeAboveMaximum() throws Exception {
		mockMvc.perform(get("/api/people?size=101"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Invalid request"));
	}

	@Test
	void searchesPeopleByName() throws Exception {
		when(peopleService.searchPeople("luke", 1, 10)).thenReturn(new PeoplePage(1, null, null,
				java.util.List.of(new PersonSummary("1", "Luke Skywalker", "https://www.swapi.tech/api/people/1"))));

		mockMvc.perform(get("/api/people/search?name=luke"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1))
				.andExpect(jsonPath("$.results[0].name").value("Luke Skywalker"));
	}

	@Test
	void rejectsBlankPeopleSearchTerm() throws Exception {
		mockMvc.perform(get("/api/people/search").param("name", " "))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Invalid request"));
	}
}
