package com.josequinteros.starwarschallenge.shared.pagination;

import java.util.List;

public final class Pagination {
	public static final int MAX_SIZE = 100;

	private Pagination() {
	}

	public static <T> PageSlice<T> slice(List<T> allResults, int page, int size) {
		validate(page, size);
		if (allResults == null) {
			throw new IllegalArgumentException("Results must not be null");
		}

		long count = allResults.size();
		long offset = (long) (page - 1) * size;
		List<T> results = offset >= count
				? List.of()
				: List.copyOf(allResults.subList((int) offset, (int) Math.min(offset + size, count)));
		return fromPage(count, results, page, size);
	}

	public static <T> PageSlice<T> fromPage(long count, List<T> results, int page, int size) {
		validate(page, size);
		if (count < 0 || results == null) {
			throw new IllegalArgumentException("Page count and results are invalid");
		}
		long totalPages = (count + size - 1) / size;

		Integer previousPage = page > 1 && totalPages > 0
				? (int) Math.min((long) page - 1, totalPages)
				: null;
		Integer nextPage = page < totalPages ? page + 1 : null;
		return new PageSlice<>(count, nextPage, previousPage, results);
	}

	public static void validate(int page, int size) {
		if (page < 1) {
			throw new IllegalArgumentException("Page must be positive");
		}
		if (size < 1 || size > MAX_SIZE) {
			throw new IllegalArgumentException("Size must be between 1 and " + MAX_SIZE);
		}
	}
}
