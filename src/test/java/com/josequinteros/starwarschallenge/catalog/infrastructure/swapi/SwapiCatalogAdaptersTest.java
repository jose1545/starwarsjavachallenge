package com.josequinteros.starwarschallenge.catalog.infrastructure.swapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import com.josequinteros.starwarschallenge.films.infrastructure.swapi.SwapiFilmAdapter;
import com.josequinteros.starwarschallenge.starships.infrastructure.swapi.SwapiStarshipAdapter;
import com.josequinteros.starwarschallenge.vehicles.infrastructure.swapi.SwapiVehicleAdapter;

class SwapiCatalogAdaptersTest {

	@Test
	void mapsFilmDetailAndPage() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/films/1/"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":{"properties":{"title":"A New Hope","episode_id":4,"opening_crawl":"A long time ago...","director":"George Lucas","producer":"Gary Kurtz","release_date":"1977-05-25","characters":["https://www.swapi.tech/api/people/1"],"planets":[],"starships":[],"vehicles":[],"species":[]}}}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/films/"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":[{"properties":{"title":"A New Hope","url":"https://www.swapi.tech/api/films/1","episode_id":4},"uid":"1","_id":"5f63a117cf50d100047f9762"},{"properties":{"title":"The Empire Strikes Back","url":"https://www.swapi.tech/api/films/2","episode_id":5},"uid":"2","_id":"5f63a117cf50d100047f9763"}],"apiVersion":"1.0","timestamp":"2026-11-15T13:33:34.329Z","support":{"contact":"admin@swapi.tech"}}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/films/"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":[{"properties":{"title":"A New Hope","url":"https://www.swapi.tech/api/films/1"},"uid":"1"},{"properties":{"title":"The Empire Strikes Back","url":"https://www.swapi.tech/api/films/2"},"uid":"2"}],"apiVersion":"1.0"}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiFilmAdapter(builder.baseUrl("https://swapi.test/api/").build(), JsonMapper.builder().build());
		var film = adapter.findById(1);
		var page = adapter.listFilms(1, 1);
		var secondPage = adapter.listFilms(2, 1);

		assertThat(film.title()).isEqualTo("A New Hope");
		assertThat(film.episodeId()).isEqualTo(4);
		assertThat(film.openingCrawl()).isEqualTo("A long time ago...");
		assertThat(page.count()).isEqualTo(2);
		assertThat(page.nextPage()).isEqualTo(2);
		assertThat(page.previousPage()).isNull();
		assertThat(page.results().getFirst().uid()).isEqualTo("1");
		assertThat(page.results().getFirst().name()).isEqualTo("A New Hope");
		assertThat(page.results().getFirst().url()).isEqualTo("https://www.swapi.tech/api/films/1");
		assertThat(secondPage.results()).extracting("uid").containsExactly("2");
		assertThat(secondPage.previousPage()).isEqualTo(1);
		server.verify();
	}

	@Test
	void searchesFilmsByTitle() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/films/?title=hope"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":[{"properties":{"title":"A New Hope","url":"https://www.swapi.tech/api/films/1"},"uid":"1"}]}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiFilmAdapter(builder.baseUrl("https://swapi.test/api/").build(), JsonMapper.builder().build());
		var page = adapter.searchFilms("hope", 1, 10);

		assertThat(page.count()).isEqualTo(1);
		assertThat(page.results().getFirst().name()).isEqualTo("A New Hope");
		server.verify();
	}

	@Test
	void mapsStarshipDetailAndPage() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/starships/9/"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":{"properties":{"name":"Death Star","model":"DS-1 Orbital Battle Station","manufacturer":"Imperial Department of Military Research","cost_in_credits":"1000000000000","length":"120000","max_atmosphering_speed":"n/a","crew":"342953","passengers":"843342","cargo_capacity":"1000000000000","consumables":"3 years","hyperdrive_rating":"4.0","MGLT":"10","starship_class":"Deep Space Mobile Battlestation","pilots":[],"films":[]}}}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/starships/?page=1&limit=1&expanded=true"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","total_records":36,"total_pages":36,"next":"https://www.swapi.tech/api/starships?page=2&limit=1&expanded=true","previous":null,"results":[{"uid":"9","properties":{"name":"Death Star","model":"DS-1 Orbital Battle Station","url":"https://www.swapi.tech/api/starships/9"}}]}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/starships/?page=2&limit=1&expanded=true"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","total_records":36,"total_pages":36,"next":"https://www.swapi.tech/api/starships?page=3&limit=1&expanded=true","previous":"https://www.swapi.tech/api/starships?page=1&limit=1&expanded=true","results":[{"uid":"10","properties":{"name":"Millennium Falcon","model":"YT-1300 light freighter","url":"https://www.swapi.tech/api/starships/10"}}]}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiStarshipAdapter(builder.baseUrl("https://swapi.test/api/").build(), JsonMapper.builder().build());
		var starship = adapter.findById(9);
		var page = adapter.listStarships(1, 1);
		var secondPage = adapter.listStarships(2, 1);

		assertThat(starship.name()).isEqualTo("Death Star");
		assertThat(starship.costInCredits()).isEqualTo("1000000000000");
		assertThat(starship.mglt()).isEqualTo("10");
		assertThat(page.count()).isEqualTo(36);
		assertThat(page.results().getFirst().url()).endsWith("/starships/9");
		assertThat(page.results().getFirst().model()).isEqualTo("DS-1 Orbital Battle Station");
		assertThat(secondPage.results()).extracting("uid").containsExactly("10");
		server.verify();
	}

	@Test
	void searchesStarshipsByNameOrModelAndDeduplicatesByUid() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/starships/?name=falcon"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":[{"uid":"10","properties":{"name":"Millennium Falcon","model":"YT-1300 light freighter","url":"https://www.swapi.tech/api/starships/10"}},{"uid":"11","properties":{"name":"Falcon Shuttle","model":"shuttle","url":"https://www.swapi.tech/api/starships/11"}}]}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/starships/?model=YT-1300"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":[{"uid":"10","properties":{"name":"Millennium Falcon","model":"YT-1300 light freighter","url":"https://www.swapi.tech/api/starships/10"}},{"uid":"12","properties":{"name":"YT-1300 Freighter","model":"YT-1300","url":"https://www.swapi.tech/api/starships/12"}}]}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiStarshipAdapter(builder.baseUrl("https://swapi.test/api/").build(), JsonMapper.builder().build());
		var page = adapter.searchStarships("falcon", "YT-1300", 1, 10);

		assertThat(page.count()).isEqualTo(3);
		assertThat(page.results()).extracting("uid").containsExactly("10", "11", "12");
		assertThat(page.results().getFirst().model()).isEqualTo("YT-1300 light freighter");
		server.verify();
	}

	@Test
	void mapsVehicleDetailAndPage() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/vehicles/4/"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":{"properties":{"name":"Sand Crawler","model":"Digger Crawler","manufacturer":"Corellia Mining Corporation","cost_in_credits":"150000","length":"36.8","max_atmosphering_speed":"30","crew":"46","passengers":"30","cargo_capacity":"50000","consumables":"2 months","vehicle_class":"wheeled","pilots":[],"films":[]}}}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/vehicles/?page=1&limit=1&expanded=true"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","total_records":39,"total_pages":39,"next":"https://www.swapi.tech/api/vehicles?page=2&limit=1&expanded=true","previous":null,"results":[{"uid":"4","properties":{"name":"Sand Crawler","model":"Digger Crawler","url":"https://www.swapi.tech/api/vehicles/4"}}]}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/vehicles/?page=2&limit=1&expanded=true"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","total_records":39,"total_pages":39,"next":"https://www.swapi.tech/api/vehicles?page=3&limit=1&expanded=true","previous":"https://www.swapi.tech/api/vehicles?page=1&limit=1&expanded=true","results":[{"uid":"6","properties":{"name":"T-16 skyhopper","model":"T-16 skyhopper","url":"https://www.swapi.tech/api/vehicles/6"}}]}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiVehicleAdapter(builder.baseUrl("https://swapi.test/api/").build(), JsonMapper.builder().build());
		var vehicle = adapter.findById(4);
		var page = adapter.listVehicles(1, 1);
		var secondPage = adapter.listVehicles(2, 1);

		assertThat(vehicle.name()).isEqualTo("Sand Crawler");
		assertThat(vehicle.costInCredits()).isEqualTo("150000");
		assertThat(vehicle.vehicleClass()).isEqualTo("wheeled");
		assertThat(page.count()).isEqualTo(39);
		assertThat(page.results().getFirst().uid()).isEqualTo("4");
		assertThat(page.results().getFirst().model()).isEqualTo("Digger Crawler");
		assertThat(secondPage.results()).extracting("uid").containsExactly("6");
		server.verify();
	}

	@Test
	void searchesVehiclesByNameOrModelAndDeduplicatesByUid() {
		var builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://swapi.test/api/vehicles/?name=crawler"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":[{"uid":"4","properties":{"name":"Sand Crawler","model":"Digger Crawler","url":"https://www.swapi.tech/api/vehicles/4"}}]}
						""", MediaType.APPLICATION_JSON));
		server.expect(requestTo("https://swapi.test/api/vehicles/?model=digger"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{"message":"ok","result":[{"uid":"4","properties":{"name":"Sand Crawler","model":"Digger Crawler","url":"https://www.swapi.tech/api/vehicles/4"}},{"uid":"6","properties":{"name":"T-16 skyhopper","model":"T-16 skyhopper","url":"https://www.swapi.tech/api/vehicles/6"}}]}
						""", MediaType.APPLICATION_JSON));

		var adapter = new SwapiVehicleAdapter(builder.baseUrl("https://swapi.test/api/").build(), JsonMapper.builder().build());
		var page = adapter.searchVehicles("crawler", "digger", 1, 10);

		assertThat(page.count()).isEqualTo(2);
		assertThat(page.results()).extracting("uid").containsExactly("4", "6");
		server.verify();
	}
}
