package com.josequinteros.starwarschallenge.vehicles.api;

import com.josequinteros.starwarschallenge.vehicles.domain.VehicleSummary;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VehicleSummary", description = "A brief reference to a vehicle")
public record VehicleSummaryResponse(String uid, String name,
		@Schema(description = "Vehicle model, included in expanded SWAPI.tech list results") String model,
		String url) {
	static VehicleSummaryResponse from(VehicleSummary vehicle) {
		return new VehicleSummaryResponse(vehicle.uid(), vehicle.name(), vehicle.model(), vehicle.url());
	}
}
