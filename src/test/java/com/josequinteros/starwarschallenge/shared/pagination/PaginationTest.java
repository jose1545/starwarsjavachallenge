package com.josequinteros.starwarschallenge.shared.pagination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class PaginationTest {
	@Test
	void slicesResultsAndCalculatesNavigation() {
		var firstPage = Pagination.slice(List.of("a", "b", "c", "d", "e"), 1, 2);
		var secondPage = Pagination.slice(List.of("a", "b", "c", "d", "e"), 2, 2);
		var lastPage = Pagination.slice(List.of("a", "b", "c", "d", "e"), 3, 2);

		assertThat(firstPage.count()).isEqualTo(5);
		assertThat(firstPage.results()).containsExactly("a", "b");
		assertThat(firstPage.nextPage()).isEqualTo(2);
		assertThat(firstPage.previousPage()).isNull();
		assertThat(secondPage.results()).containsExactly("c", "d");
		assertThat(secondPage.nextPage()).isEqualTo(3);
		assertThat(secondPage.previousPage()).isEqualTo(1);
		assertThat(lastPage.results()).containsExactly("e");
		assertThat(lastPage.nextPage()).isNull();
		assertThat(lastPage.previousPage()).isEqualTo(2);
	}

	@Test
	void returnsEmptyResultsForOutOfRangePage() {
		var page = Pagination.slice(List.of("a", "b"), 8, 1);

		assertThat(page.count()).isEqualTo(2);
		assertThat(page.results()).isEmpty();
		assertThat(page.nextPage()).isNull();
		assertThat(page.previousPage()).isEqualTo(2);
	}

	@Test
	void rejectsInvalidPageOrSize() {
		assertThatThrownBy(() -> Pagination.slice(List.of("a"), 0, 10))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> Pagination.slice(List.of("a"), 1, 0))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> Pagination.slice(List.of("a"), 1, 101))
				.isInstanceOf(IllegalArgumentException.class);
	}
}
