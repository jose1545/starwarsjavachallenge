package com.josequinteros.starwarschallenge.vehicles.infrastructure.swapi;

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
import com.josequinteros.starwarschallenge.vehicles.application.VehiclePort;
import com.josequinteros.starwarschallenge.vehicles.domain.Vehicle;
import com.josequinteros.starwarschallenge.vehicles.domain.VehiclePage;
import com.josequinteros.starwarschallenge.vehicles.domain.VehicleSummary;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;
import tools.jackson.databind.json.JsonMapper;

@Component
public class SwapiVehicleAdapter implements VehiclePort {
	private static final Logger logger = LoggerFactory.getLogger(SwapiVehicleAdapter.class);
	private final RestClient restClient;
	private final JsonMapper objectMapper;

	public SwapiVehicleAdapter(RestClient swapiRestClient, JsonMapper objectMapper) {
		this.restClient = swapiRestClient;
		this.objectMapper = objectMapper;
	}

	@Override
	public Vehicle findById(long id) {
		try {
			String responseBody = restClient.get().uri("/vehicles/{id}/", id).retrieve().body(String.class);
			if (responseBody == null || responseBody.isBlank()) throw new IllegalArgumentException("SWAPI.tech returned an empty vehicle response");
			logger.info("SWAPI.tech vehicle detail response for id={}: {}", id, responseBody);
			SwapiVehicleDetailResponse response = objectMapper.readValue(responseBody, SwapiVehicleDetailResponse.class);
			if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty vehicle response");
			return response.toVehicle();
		} catch (RestClientResponseException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				logger.info("SWAPI.tech vehicle not found for id={} response={}", id, exception.getResponseBodyAsString());
				throw new SwapiResourceNotFoundException();
			}
			logger.error("SWAPI.tech returned HTTP {} while fetching vehicle id={} response={}", exception.getStatusCode().value(), id, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while fetching vehicle id={}", id, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid vehicle response for id={}", id, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI.tech vehicle response for id={}", id, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	@Override
	public VehiclePage listVehicles(int page, int size) {
		try {
			String responseBody = restClient.get()
					.uri(uri -> uri.path("/vehicles/").queryParam("page", page).queryParam("limit", size).queryParam("expanded", true).build())
					.retrieve().body(String.class);
			if (responseBody == null || responseBody.isBlank()) throw new IllegalArgumentException("SWAPI.tech returned an empty vehicle page");
			logger.info("SWAPI.tech vehicle list response for page={} size={}: {}", page, size, responseBody);
			SwapiVehiclePageResponse response = objectMapper.readValue(responseBody, SwapiVehiclePageResponse.class);
			if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty vehicle page");
			return response.toVehiclePage(page, size);
		} catch (RestClientResponseException exception) {
			logger.error("SWAPI.tech returned HTTP {} while listing vehicles page={} size={} response={}", exception.getStatusCode().value(), page, size, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while listing vehicles page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid vehicle list for page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI.tech vehicle list for page={} size={}", page, size, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	@Override
	public VehiclePage searchVehicles(String name, String model, int page, int size) {
		try {
			Map<String, VehicleSummary> matches = new LinkedHashMap<>();
			if (name != null) addSearchMatches("name", name, matches, page, size);
			if (model != null) addSearchMatches("model", model, matches, page, size);

			List<VehicleSummary> summaries = new ArrayList<>(matches.values());
			PageSlice<VehicleSummary> slice = Pagination.slice(summaries, page, size);
			return new VehiclePage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
		} catch (RestClientResponseException exception) {
			logger.error("SWAPI.tech returned HTTP {} while searching vehicles name={} model={} page={} size={} response={}",
					exception.getStatusCode().value(), name, model, page, size, exception.getResponseBodyAsString(), exception);
			throw new UpstreamServiceException(exception);
		} catch (RestClientException exception) {
			logger.error("SWAPI.tech request failed while searching vehicles name={} model={} page={} size={}", name, model, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (IllegalArgumentException exception) {
			logger.error("SWAPI.tech returned an invalid vehicle search response for name={} model={} page={} size={}", name, model, page, size, exception);
			throw new UpstreamServiceException(exception);
		} catch (Exception exception) {
			logger.error("Could not deserialize SWAPI.tech vehicle search response for name={} model={} page={} size={}", name, model, page, size, exception);
			throw new UpstreamServiceException(exception);
		}
	}

	private void addSearchMatches(String field, String term, Map<String, VehicleSummary> matches, int page, int size) throws Exception {
		String responseBody = restClient.get()
				.uri(uri -> uri.path("/vehicles/").queryParam(field, term).build())
				.retrieve().body(String.class);
		if (responseBody == null || responseBody.isBlank()) throw new IllegalArgumentException("SWAPI.tech returned an empty vehicle search response");
		logger.info("SWAPI.tech vehicle search response for field={} term={} page={} size={}: {}", field, term, page, size, responseBody);
		SwapiVehicleSearchResponse response = objectMapper.readValue(responseBody, SwapiVehicleSearchResponse.class);
		if (response == null) throw new IllegalArgumentException("SWAPI.tech returned an empty vehicle search response");
		for (VehicleSummary summary : response.toSummaries()) matches.putIfAbsent(summary.uid(), summary);
	}
}
