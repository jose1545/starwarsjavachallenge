package com.josequinteros.starwarschallenge.vehicles.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;
import com.josequinteros.starwarschallenge.vehicles.domain.VehiclePage;
import com.josequinteros.starwarschallenge.vehicles.domain.VehicleSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiVehicleSearchResponse(String message, List<SwapiVehicleSummaryResponse> result) {
	List<VehicleSummary> toSummaries() {
		if (result == null) throw new IllegalArgumentException("SWAPI.tech returned an invalid vehicle search response");
		return result.stream().map(summary -> {
			if (summary == null) throw new IllegalArgumentException("SWAPI.tech returned a null vehicle search result");
			return summary.toSummary();
		}).toList();
	}

	VehiclePage toVehiclePage(int page, int size) {
		PageSlice<VehicleSummary> slice = Pagination.slice(toSummaries(), page, size);
		return new VehiclePage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
	}
}
