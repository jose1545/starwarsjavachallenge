package com.josequinteros.starwarschallenge.starships.domain;

public record StarshipSummary(String uid, String name, String model, String url) {
	public StarshipSummary(String uid, String name, String url) {
		this(uid, name, null, url);
	}
}
