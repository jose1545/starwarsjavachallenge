package com.josequinteros.starwarschallenge.people.infrastructure.swapi;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.josequinteros.starwarschallenge.people.application.PeoplePort;
import com.josequinteros.starwarschallenge.people.domain.Person;
import com.josequinteros.starwarschallenge.people.domain.PeoplePage;
import com.josequinteros.starwarschallenge.shared.error.SwapiResourceNotFoundException;
import com.josequinteros.starwarschallenge.shared.error.UpstreamServiceException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class SwapiPeopleAdapter implements PeoplePort {

	private static final Logger logger = LoggerFactory.getLogger(SwapiPeopleAdapter.class);

	private final RestClient restClient;
	private final JsonMapper objectMapper;

	public SwapiPeopleAdapter(RestClient swapiRestClient, JsonMapper objectMapper) {
		this.restClient = swapiRestClient;
		this.objectMapper = objectMapper;
	}

	@Override
	public Person findById(long id) {
		try {
			String responseBody = restClient.get()
					.uri("/people/{id}/", id)
					.retrieve()
					.body(String.class);
			if (responseBody == null || responseBody.isBlank()) {
				logger.error("SWAPI returned an empty or invalid person response for id={}", id);
				throw new UpstreamServiceException();
			}
			logger.info("SWAPI people detail response for id={}: {}", id, responseBody);
			SwapiPersonDetailResponse response = objectMapper.readValue(responseBody, SwapiPersonDetailResponse.class);
			return response.toPerson();
		} catch (RestClientResponseException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				logger.info("SWAPI person not found for id={} (HTTP {}) response={}", id, exception.getStatusCode().value(), exception.getResponseBodyAsString());
				throw new SwapiResourceNotFoundException();
			}
			logger.error("SWAPI returned HTTP {} while fetching person id={} response={}",
					exception.getStatusCode().value(), id, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI request failed while fetching person id={}", id, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI returned an invalid person response for id={}", id, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI person response for id={}", id, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	@Override
	public PeoplePage listPeople(int page, int size) {
		try {
			String responseBody = restClient.get()
					.uri(uri -> uri.path("/people/").queryParam("page", page).queryParam("limit", size).build())
					.retrieve()
					.body(String.class);
			if (responseBody == null || responseBody.isBlank()) {
				logger.error("SWAPI returned an empty people list for page={} size={}", page, size);
				throw new UpstreamServiceException();
			}
			logger.info("SWAPI people list response for page={} size={}: {}", page, size, responseBody);
			SwapiPeoplePageResponse response = objectMapper.readValue(responseBody, SwapiPeoplePageResponse.class);
			return response.toPeoplePage(page, size);
		} catch (RestClientResponseException exception) {
			logger.error("SWAPI returned HTTP {} while listing people page={} size={} response={}",
					exception.getStatusCode().value(), page, size, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI request failed while listing people page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI returned an invalid people list for page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI people list response for page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	@Override
	public PeoplePage searchPeople(String name, int page, int size) {
		try {
			String responseBody = restClient.get()
					.uri(uri -> uri.path("/people/").queryParam("name", name).build())
					.retrieve()
					.body(String.class);
			if (responseBody == null || responseBody.isBlank()) throw new IllegalArgumentException("SWAPI returned an empty people search page");
			logger.info("SWAPI people search response for name={} page={} size={}: {}", name, page, size, responseBody);
			SwapiPeopleSearchResponse response = objectMapper.readValue(responseBody, SwapiPeopleSearchResponse.class);
			if (response == null) throw new IllegalArgumentException("SWAPI returned an empty people search page");
			return response.toPeoplePage(page, size);
		} catch (RestClientResponseException exception) {
			logger.error("SWAPI returned HTTP {} while searching people name={} page={} size={} response={}",
					exception.getStatusCode().value(), name, page, size, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI request failed while searching people name={} page={} size={}", name, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI returned an invalid people search response for name={} page={} size={}", name, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI people search response for name={} page={} size={}", name, page, size, exception);
			throw new UpstreamServiceException(exception);
		}
	}
}
