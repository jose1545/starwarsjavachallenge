package com.josequinteros.starwarschallenge.shared.error;

public class UpstreamServiceException extends RuntimeException {
	public UpstreamServiceException() {
		super("Star Wars data service is temporarily unavailable");
	}

	public UpstreamServiceException(Throwable cause) {
		super("Star Wars data service is temporarily unavailable", cause);
	}
}
