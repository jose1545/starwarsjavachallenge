package com.josequinteros.starwarschallenge.vehicles.domain;

import java.util.List;

public record VehiclePage(long count, Integer nextPage, Integer previousPage, List<VehicleSummary> results) {
	public VehiclePage {
		results = List.copyOf(results);
	}
}
