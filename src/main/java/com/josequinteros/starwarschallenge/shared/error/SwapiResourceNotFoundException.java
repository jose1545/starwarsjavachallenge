package com.josequinteros.starwarschallenge.shared.error;

public class SwapiResourceNotFoundException extends RuntimeException {
	public SwapiResourceNotFoundException() {
		super("Requested Star Wars resource was not found");
	}
}
