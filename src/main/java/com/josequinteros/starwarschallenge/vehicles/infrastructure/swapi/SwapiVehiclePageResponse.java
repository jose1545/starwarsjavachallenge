package com.josequinteros.starwarschallenge.vehicles.infrastructure.swapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.josequinteros.starwarschallenge.shared.pagination.PageSlice;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;
import com.josequinteros.starwarschallenge.vehicles.domain.VehiclePage;
import com.josequinteros.starwarschallenge.vehicles.domain.VehicleSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
record SwapiVehiclePageResponse(
		@JsonProperty("total_records") Long totalRecords,
		@JsonProperty("total_pages") Integer totalPages,
		String next,
		String previous,
		List<SwapiVehicleSummaryResponse> results) {
	VehiclePage toVehiclePage(int page, int size) {
		List<VehicleSummary> summaries = toSummaries();
		PageSlice<VehicleSummary> slice = Pagination.fromPage(totalRecords, summaries, page, size);
		return new VehiclePage(slice.count(), slice.nextPage(), slice.previousPage(), slice.results());
	}

	List<VehicleSummary> toSummaries() {
		if (totalRecords == null || totalRecords < 0 || totalPages == null || totalPages < 0 || results == null) {
			throw new IllegalArgumentException("SWAPI.tech returned an invalid vehicle page");
		}
		return results.stream().map(summary -> {
			if (summary == null) throw new IllegalArgumentException("SWAPI.tech returned a null vehicle summary");
			return summary.toSummary();
		}).toList();
	}
}
