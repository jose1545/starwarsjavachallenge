package com.josequinteros.starwarschallenge.shared.pagination;

import java.util.List;

public record PageSlice<T>(long count, Integer nextPage, Integer previousPage, List<T> results) {
	public PageSlice {
		results = List.copyOf(results);
	}
}
