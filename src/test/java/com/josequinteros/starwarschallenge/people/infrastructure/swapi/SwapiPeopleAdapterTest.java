package com.josequinteros.starwarschallenge.people.infrastructure.swapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.josequinteros.starwarschallenge.shared.error.SwapiResourceNotFoundException;
import com.josequinteros.starwarschallenge.shared.error.UpstreamServiceException;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

class SwapiPeopleAdapterTest {

	@Test
	void mapsSwapiPersonResponse() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/people/1/"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":{"properties":{"name":"Luke Skywalker","height":"172","mass":"77","hair_color":"blond","skin_color":"fair","eye_color":"blue","birth_year":"19BBY","gender":"male"}}}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiPeopleAdapter(builder.baseUrl("https://swapi.test/api").build(), JsonMapper.builder().build());
		var person = adapter.findById(1);

		assertThat(person.name()).isEqualTo("Luke Skywalker");
		assertThat(person.hairColor()).isEqualTo("blond");
		assertThat(person.birthYear()).isEqualTo("19BBY");
		server.verify();
	}

	@Test
	void mapsNotFoundResponse() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/people/999/"))
				.andRespond(withStatus(HttpStatus.NOT_FOUND));

		var adapter = new SwapiPeopleAdapter(builder.baseUrl("https://swapi.test/api").build(), JsonMapper.builder().build());
		assertThatThrownBy(() -> adapter.findById(999))
				.isInstanceOf(SwapiResourceNotFoundException.class);
		server.verify();
	}

	@Test
	void mapsUpstreamServerError() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/people/1/"))
				.andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

		var adapter = new SwapiPeopleAdapter(builder.baseUrl("https://swapi.test/api").build(), JsonMapper.builder().build());
		assertThatThrownBy(() -> adapter.findById(1))
				.isInstanceOf(UpstreamServiceException.class);
		server.verify();
	}

	@Test
	void requestsRequestedUpstreamPeoplePageAndMapsPaginationMetadata() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/people/?page=1&limit=2"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","total_records":82,"total_pages":41,"next":"https://www.swapi.tech/api/people?page=2&limit=2","previous":null,"results":[{"uid":"1","name":"Luke Skywalker","url":"https://www.swapi.tech/api/people/1"},{"uid":"2","name":"C-3PO","url":"https://www.swapi.tech/api/people/2"}]}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/people/?page=2&limit=2"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","total_records":82,"total_pages":41,"next":"https://www.swapi.tech/api/people?page=3&limit=2","previous":"https://www.swapi.tech/api/people?page=1&limit=2","results":[{"uid":"3","name":"R2-D2","url":"https://www.swapi.tech/api/people/3"},{"uid":"4","name":"Darth Vader","url":"https://www.swapi.tech/api/people/4"}]}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiPeopleAdapter(builder.baseUrl("https://swapi.test/api/").build(), JsonMapper.builder().build());
		var firstPage = adapter.listPeople(1, 2);
		var secondPage = adapter.listPeople(2, 2);

		assertThat(firstPage.count()).isEqualTo(82);
		assertThat(firstPage.nextPage()).isEqualTo(2);
		assertThat(firstPage.previousPage()).isNull();
		assertThat(firstPage.results()).extracting("uid").containsExactly("1", "2");
		assertThat(secondPage.count()).isEqualTo(82);
		assertThat(secondPage.nextPage()).isEqualTo(3);
		assertThat(secondPage.previousPage()).isEqualTo(1);
		assertThat(secondPage.results()).extracting("uid").containsExactly("3", "4");
		server.verify();
	}

	@Test
	void rejectsMalformedPeoplePage() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/people/?page=1&limit=10"))
				.andRespond(withSuccess("""
						{"message":"ok","total_records":null,"total_pages":9,"next":null,"previous":null,"results":[]}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiPeopleAdapter(builder.baseUrl("https://swapi.test/api").build(), JsonMapper.builder().build());
		assertThatThrownBy(() -> adapter.listPeople(1, 10))
				.isInstanceOf(UpstreamServiceException.class);
		server.verify();
	}

	@Test
	void searchesPeopleByNameUsingPartialMatchField() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/people/?name=luke"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":[{"properties":{"name":"Luke Skywalker","url":"https://www.swapi.tech/api/people/1"},"uid":"1","_id":"person-1"}],"apiVersion":"1.0"}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiPeopleAdapter(builder.baseUrl("https://swapi.test/api/").build(), JsonMapper.builder().build());
		var page = adapter.searchPeople("luke", 1, 10);

		assertThat(page.count()).isEqualTo(1);
		assertThat(page.results()).extracting("name").containsExactly("Luke Skywalker");
		server.verify();
	}
}
