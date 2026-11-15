package com.josequinteros.starwarschallenge.films.infrastructure.swapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.josequinteros.starwarschallenge.films.application.FilmPort;
import com.josequinteros.starwarschallenge.films.domain.Film;
import com.josequinteros.starwarschallenge.films.domain.FilmPage;
import com.josequinteros.starwarschallenge.shared.error.SwapiResourceNotFoundException;
import com.josequinteros.starwarschallenge.shared.error.UpstreamServiceException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class SwapiFilmAdapter implements FilmPort {
	private static final Logger logger = LoggerFactory.getLogger(SwapiFilmAdapter.class);
	private final RestClient restClient;
	private final JsonMapper objectMapper;

	public SwapiFilmAdapter(RestClient swapiRestClient, JsonMapper objectMapper) {
		this.restClient = swapiRestClient;
		this.objectMapper = objectMapper;
	}

	@Override
	public Film findById(long id) {
		try {
			SwapiFilmDetailResponse response = restClient.get().uri("/films/{id}/", id).retrieve().body(SwapiFilmDetailResponse.class);
			if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty film response");
			return response.toFilm();
		} catch (RestClientResponseException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) throw new SwapiResourceNotFoundException();
			logger.error("SWAPI.tech returned HTTP {} while fetching film id={}", exception.getStatusCode().value(), id, exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while fetching film id={}", id, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid film response for id={}", id, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	@Override
	public FilmPage listFilms(int page, int size) {
		try {
			String responseBody = restClient.get().uri("/films/").retrieve().body(String.class);
			if (responseBody == null || responseBody.isBlank()) {
				throw new IllegalArgumentException("SWAPI.tech returned an empty film page");
			}
			logger.info("SWAPI.tech film list response: {}", responseBody);
			SwapiFilmPageResponse response = objectMapper.readValue(responseBody, SwapiFilmPageResponse.class);
			if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty film page");
			return response.toFilmPage(page, size);
		} catch (RestClientResponseException exception) {
			logger.error("SWAPI.tech returned HTTP {} while listing films page={} size={}", exception.getStatusCode().value(), page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while listing films page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid film list for page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI.tech film list response for page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	@Override
	public FilmPage searchFilms(String title, int page, int size) {
		try {
			String responseBody = restClient.get()
					.uri(uri -> uri.path("/films/").queryParam("title", title).build())
					.retrieve().body(String.class);
			if (responseBody == null || responseBody.isBlank()) {
				throw new IllegalArgumentException("SWAPI.tech returned an empty film search response");
			}
			logger.info("SWAPI.tech film search response: {}", responseBody);
			SwapiFilmPageResponse response = objectMapper.readValue(responseBody, SwapiFilmPageResponse.class);
			if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty film search response");
			return response.toFilmPage(page, size);
		} catch (RestClientResponseException exception) {
			logger.error("SWAPI.tech returned HTTP {} while searching films title={} page={} size={}", exception.getStatusCode().value(), title, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while searching films title={} page={} size={}", title, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid film search response for title={} page={} size={}", title, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI.tech film search response for title={} page={} size={}", title, page, size, exception);
			throw new UpstreamServiceException(exception);
		}
	}
}
