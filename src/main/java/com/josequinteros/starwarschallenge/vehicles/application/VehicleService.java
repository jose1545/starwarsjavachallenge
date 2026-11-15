package com.josequinteros.starwarschallenge.vehicles.application;

import org.springframework.stereotype.Service;

import com.josequinteros.starwarschallenge.vehicles.domain.Vehicle;
import com.josequinteros.starwarschallenge.vehicles.domain.VehiclePage;
import com.josequinteros.starwarschallenge.shared.pagination.Pagination;

@Service
public class VehicleService {
	private final VehiclePort vehiclePort;

	public VehicleService(VehiclePort vehiclePort) {
		this.vehiclePort = vehiclePort;
	}

	public Vehicle findById(long id) {
		if (id < 1) throw new IllegalArgumentException("Vehicle id must be positive");
		return vehiclePort.findById(id);
	}

	public VehiclePage listVehicles(int page, int size) {
		Pagination.validate(page, size);
		return vehiclePort.listVehicles(page, size);
	}

	public VehiclePage searchVehicles(String name, String model, int page, int size) {
		Pagination.validate(page, size);
		name = normalize(name);
		model = normalize(model);
		if (name == null && model == null) throw new IllegalArgumentException("At least one search term is required");
		return vehiclePort.searchVehicles(name, model, page, size);
	}

	private static String normalize(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
