package com.josequinteros.starwarschallenge.starships.infrastructure.swapi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.josequinteros.starwarschallenge.shared.error.SwapiResourceNotFoundException;
import com.josequinteros.starwarschallenge.shared.error.UpstreamServiceException;
import com.josequinteros.starwarschallenge.starships.application.StarshipPort;
import com.josequinteros.starwarschallenge.starships.domain.Starship;
import com.josequinteros.starwarschallenge.starships.domain.StarshipPage;
import com.josequinteros.starwarschallenge.starships.domain.StarshipSummary;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;
import tools.jackson.databind.json.JsonMapper;

@Component
public class SwapiStarshipAdapter implements StarshipPort {
	private static final Logger logger = LoggerFactory.getLogger(SwapiStarshipAdapter.class);
	private final RestClient restClient;
	private final JsonMapper objectMapper;

	public SwapiStarshipAdapter(RestClient swapiRestClient, JsonMapper objectMapper) {
		this.restClient = swapiRestClient;
		this.objectMapper = objectMapper;
	}

	@Override
	public Starship findById(long id) {
		try {
			String responseBody = restClient.get().uri("/starships/{id}/", id).retrieve().body(String.class);
			if (responseBody == null || responseBody.isBlank()) throw new IllegalArgumentException("SWAPI.tech returned an empty starship response");
			logger.info("SWAPI.tech starship detail response for id={}: {}", id, responseBody);
			SwapiStarshipDetailResponse response = objectMapper.readValue(responseBody, SwapiStarshipDetailResponse.class);
			if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty starship response");
			return response.toStarship();
		} catch (RestClientResponseException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				logger.info("SWAPI.tech starship not found for id={} response={}", id, exception.getResponseBodyAsString());
				throw new SwapiResourceNotFoundException();
			}
			logger.error("SWAPI.tech returned HTTP {} while fetching starship id={} response={}", exception.getStatusCode().value(), id, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while fetching starship id={}", id, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid starship response for id={}", id, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI.tech starship response for id={}", id, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	@Override
	public StarshipPage listStarships(int page, int size) {
		try {
			String responseBody = restClient.get()
					.uri(uri -> uri.path("/starships/").queryParam("page", page).queryParam("limit", size).queryParam("expanded", true).build())
					.retrieve().body(String.class);
			if (responseBody == null || responseBody.isBlank()) throw new IllegalArgumentException("SWAPI.tech returned an empty starship page");
			logger.info("SWAPI.tech starship list response for page={} size={}: {}", page, size, responseBody);
			SwapiStarshipPageResponse response = objectMapper.readValue(responseBody, SwapiStarshipPageResponse.class);
			if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty starship page");
			return response.toStarshipPage(page, size);
		} catch (RestClientResponseException exception) {
			logger.error("SWAPI.tech returned HTTP {} while listing starships page={} size={} response={}", exception.getStatusCode().value(), page, size, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while listing starships page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid starship list for page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI.tech starship list for page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	@Override
	public StarshipPage searchStarships(String name, String model, int page, int size) {
		try {
			Map<String, StarshipSummary> matches = new LinkedHashMap<>();
			if (name != null) addSearchMatches("name", name, matches, page, size);
			if (model != null) addSearchMatches("model", model, matches, page, size);

			List<StarshipSummary> summaries = new ArrayList<>(matches.values());
			PageSlice<StarshipSummary> slice = Pagination.slice(summaries, page, size);
			return new StarshipPage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
		} catch (RestClientResponseException exception) {
			logger.error("SWAPI.tech returned HTTP {} while searching starships name={} model={} page={} size={} response={}",
					exception.getStatusCode().value(), name, model, page, size, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while searching starships name={} model={} page={} size={}", name, model, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid starship search response for name={} model={} page={} size={}", name, model, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI.tech starship search response for name={} model={} page={} size={}", name, model, page, size, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	private void addSearchMatches(String field, String term, Map<String, StarshipSummary> matches, int page, int size) throws Exception {
		String responseBody = restClient.get()
				.uri(uri -> uri.path("/starships/").queryParam(field, term).build())
				.retrieve().body(String.class);
		if (responseBody == null || responseBody.isBlank()) throw new IllegalArgumentException("SWAPI.tech returned an empty starship search response");
		logger.info("SWAPI.tech starship search response for field={} term={} page={} size={}: {}", field, term, page, size, responseBody);
		SwapiStarshipSearchResponse response = objectMapper.readValue(responseBody, SwapiStarshipSearchResponse.class);
		if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty starship search response");
		for (StarshipSummary summary : response.toSummaries()) matches.putIfAbsent(summary.uid(), summary);
	}
}
