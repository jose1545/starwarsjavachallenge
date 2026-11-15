package com.josequinteros.starwarschallenge.vehicles.api;

import java.util.List;

import com.josequinteros.starwarschallenge.vehicles.domain.Vehicle;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Vehicle", description = "Vehicle details")
public record VehicleResponse(
		String name, String model, String manufacturer, String costInCredits, String length,
		String maxAtmospheringSpeed, String crew, String passengers, String cargoCapacity,
		String consumables, String vehicleClass, List<String> pilots, List<String> films) {
	static VehicleResponse from(Vehicle vehicle) {
		return new VehicleResponse(vehicle.name(), vehicle.model(), vehicle.manufacturer(), vehicle.costInCredits(),
				vehicle.length(), vehicle.maxAtmospheringSpeed(), vehicle.crew(), vehicle.passengers(),
				vehicle.cargoCapacity(), vehicle.consumables(), vehicle.vehicleClass(), vehicle.pilots(), vehicle.films());
	}
}
