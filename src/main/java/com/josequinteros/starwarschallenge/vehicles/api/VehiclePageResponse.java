package com.josequinteros.starwarschallenge.vehicles.api;

import java.util.List;

import com.josequinteros.starwarschallenge.vehicles.domain.VehiclePage;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VehiclePage", description = "A page of vehicle summaries")
public record VehiclePageResponse(long count, Integer nextPage, Integer previousPage, List<VehicleSummaryResponse> results) {
	static VehiclePageResponse from(VehiclePage page) {
		return new VehiclePageResponse(page.count(), page.nextPage(), page.previousPage(),
				page.results().stream().map(VehicleSummaryResponse::from).toList());
	}
}
