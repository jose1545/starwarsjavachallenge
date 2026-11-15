package com.josequinteros.starwarschallenge.vehicles.domain;

public record VehicleSummary(String uid, String name, String model, String url) {
	public VehicleSummary(String uid, String name, String url) {
		this(uid, name, null, url);
	}
}
