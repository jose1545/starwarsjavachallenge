package com.josequinteros.starwarschallenge.catalog.api;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.josequinteros.starwarschallenge.films.api.FilmController;
import com.josequinteros.starwarschallenge.films.application.FilmService;
import com.josequinteros.starwarschallenge.films.domain.Film;
import com.josequinteros.starwarschallenge.films.domain.FilmPage;
import com.josequinteros.starwarschallenge.films.domain.FilmSummary;
import com.josequinteros.starwarschallenge.starships.api.StarshipController;
import com.josequinteros.starwarschallenge.starships.application.StarshipService;
import com.josequinteros.starwarschallenge.starships.domain.Starship;
import com.josequinteros.starwarschallenge.starships.domain.StarshipPage;
import com.josequinteros.starwarschallenge.starships.domain.StarshipSummary;
import com.josequinteros.starwarschallenge.vehicles.api.VehicleController;
import com.josequinteros.starwarschallenge.vehicles.application.VehicleService;
import com.josequinteros.starwarschallenge.vehicles.domain.Vehicle;
import com.josequinteros.starwarschallenge.vehicles.domain.VehiclePage;
import com.josequinteros.starwarschallenge.vehicles.domain.VehicleSummary;

@WebMvcTest(controllers = { FilmController.class, StarshipController.class, VehicleController.class })
@AutoConfigureMockMvc(addFilters = false)
class CatalogControllersIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FilmService filmService;

	@MockitoBean
	private StarshipService starshipService;

	@MockitoBean
	private VehicleService vehicleService;

	@Test
	void returnsFilmDetailAndSummaryPage() throws Exception {
		when(filmService.findById(4)).thenReturn(new Film("A New Hope", 4, "Opening crawl", "George Lucas", "Gary Kurtz",
				"1977-05-25", List.of(), List.of(), List.of(), List.of(), List.of()));
		when(filmService.listFilms(1, 10)).thenReturn(new FilmPage(6, null, null,
				List.of(new FilmSummary("1", "A New Hope", "https://www.swapi.tech/api/films/1"))));

		mockMvc.perform(get("/api/films/4"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("A New Hope"))
				.andExpect(jsonPath("$.episodeId").value(4));
		mockMvc.perform(get("/api/films"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(6))
				.andExpect(jsonPath("$.results[0].uid").value("1"))
				.andExpect(jsonPath("$.results[0].title").doesNotExist());
		verify(filmService).listFilms(1, 10);
	}

	@Test
	void returnsStarshipDetailAndSummaryPage() throws Exception {
		when(starshipService.findById(9)).thenReturn(new Starship("Death Star", "DS-1", "Imperial", "1000000000000",
				"120000", "n/a", "342953", "843342", "1000000000000", "3 years", "4.0", "10",
				"Deep Space Mobile Battlestation", List.of(), List.of()));
		when(starshipService.listStarships(1, 10)).thenReturn(new StarshipPage(36, null, null,
				List.of(new StarshipSummary("9", "Death Star", "DS-1 Orbital Battle Station", "https://www.swapi.tech/api/starships/9"))));

		mockMvc.perform(get("/api/starships/9"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Death Star"))
				.andExpect(jsonPath("$.mglt").value("10"));
		mockMvc.perform(get("/api/starships"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(36))
				.andExpect(jsonPath("$.results[0].uid").value("9"))
				.andExpect(jsonPath("$.results[0].model").value("DS-1 Orbital Battle Station"));
		verify(starshipService).listStarships(1, 10);
	}

	@Test
	void returnsVehicleDetailAndSummaryPage() throws Exception {
		when(vehicleService.findById(4)).thenReturn(new Vehicle("Sand Crawler", "Digger Crawler", "Corellia Mining Corporation",
				"150000", "36.8", "30", "46", "30", "50000", "2 months", "wheeled", List.of(), List.of()));
		when(vehicleService.listVehicles(1, 10)).thenReturn(new VehiclePage(39, null, null,
				List.of(new VehicleSummary("4", "Sand Crawler", "Digger Crawler", "https://www.swapi.tech/api/vehicles/4"))));

		mockMvc.perform(get("/api/vehicles/4"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Sand Crawler"))
				.andExpect(jsonPath("$.vehicleClass").value("wheeled"));
		mockMvc.perform(get("/api/vehicles"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(39))
				.andExpect(jsonPath("$.results[0].uid").value("4"))
				.andExpect(jsonPath("$.results[0].model").value("Digger Crawler"));
		verify(vehicleService).listVehicles(1, 10);
	}

	@Test
	void acceptsPaginationParametersForFilms() throws Exception {
		when(filmService.listFilms(2, 3)).thenReturn(new FilmPage(6, null, 1, List.of()));

		mockMvc.perform(get("/api/films?page=2&size=3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(6))
				.andExpect(jsonPath("$.previousPage").value(1));
		verify(filmService).listFilms(2, 3);
	}

	@Test
	void rejectsPageSizeAboveMaximumForCatalogEndpoints() throws Exception {
		mockMvc.perform(get("/api/films?size=101")).andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/starships?size=101")).andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/vehicles?size=101")).andExpect(status().isBadRequest());
	}

	@Test
	void exposesFilmTitleSearchEndpoint() throws Exception {
		when(filmService.searchFilms("hope", 1, 10)).thenReturn(new FilmPage(1, null, null,
				List.of(new FilmSummary("1", "A New Hope", "https://www.swapi.tech/api/films/1"))));

		mockMvc.perform(get("/api/films/search?title=hope"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count").value(1))
				.andExpect(jsonPath("$.results[0].name").value("A New Hope"));
		verify(filmService).searchFilms("hope", 1, 10);
	}

	@Test
	void exposesStarshipSearchAcrossNameAndModel() throws Exception {
		when(starshipService.searchStarships("falcon", "YT-1300", 1, 10)).thenReturn(new StarshipPage(1, null, null,
				List.of(new StarshipSummary("10", "Millennium Falcon", "YT-1300 light freighter", "https://www.swapi.tech/api/starships/10"))));

		mockMvc.perform(get("/api/starships/search?name=falcon&model=YT-1300"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.results[0].name").value("Millennium Falcon"))
				.andExpect(jsonPath("$.results[0].model").value("YT-1300 light freighter"));
		verify(starshipService).searchStarships("falcon", "YT-1300", 1, 10);
	}

	@Test
	void exposesVehicleSearchAcrossNameAndModel() throws Exception {
		when(vehicleService.searchVehicles(null, "digger", 1, 10)).thenReturn(new VehiclePage(1, null, null,
				List.of(new VehicleSummary("4", "Sand Crawler", "Digger Crawler", "https://www.swapi.tech/api/vehicles/4"))));

		mockMvc.perform(get("/api/vehicles/search?model=digger"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.results[0].name").value("Sand Crawler"))
				.andExpect(jsonPath("$.results[0].model").value("Digger Crawler"));
		verify(vehicleService).searchVehicles(null, "digger", 1, 10);
	}

	@Test
	void rejectsSearchWithNoVehicleOrStarshipFilter() throws Exception {
		when(starshipService.searchStarships(null, null, 1, 10)).thenThrow(new IllegalArgumentException());
		when(vehicleService.searchVehicles(null, null, 1, 10)).thenThrow(new IllegalArgumentException());
		mockMvc.perform(get("/api/starships/search")).andExpect(status().isBadRequest());
		mockMvc.perform(get("/api/vehicles/search")).andExpect(status().isBadRequest());
	}
}
