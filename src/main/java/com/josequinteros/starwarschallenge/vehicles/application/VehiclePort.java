package com.josequinteros.starwarschallenge.vehicles.application;

import com.josequinteros.starwarschallenge.vehicles.domain.Vehicle;
import com.josequinteros.starwarschallenge.vehicles.domain.VehiclePage;

public interface VehiclePort {
	Vehicle findById(long id);
	VehiclePage listVehicles(int page, int size);
	VehiclePage searchVehicles(String name, String model, int page, int size);
}
