package com.josequinteros.starwarschallenge.films.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.josequinteros.starwarschallenge.films.application.FilmService;
import com.josequinteros.starwarschallenge.shared.error.ApiErrorResponse;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/films")
@Tag(name = "Films", description = "Star Wars film information")
public class FilmController {
	private final FilmService filmService;

	public FilmController(FilmService filmService) {
		this.filmService = filmService;
	}

	@GetMapping
	@Operation(summary = "List films", description = "Returns one locally paginated page of film summaries. Page size defaults to 10 and cannot exceed 100. Use the film ID endpoint for full details.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Film list retrieved", content = @Content(schema = @Schema(implementation = FilmPageResponse.class))),
			@ApiResponse(responseCode = "400", description = "Page must be positive and size must be between 1 and 100", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<FilmPageResponse> listFilms(
			@Parameter(name = "page", description = "One-based page number", example = "1",
					schema = @Schema(type = "integer", minimum = "1"))
			@RequestParam(defaultValue = "1") @Positive int page,
			@Parameter(name = "size", description = "Number of results per page (default 10, maximum 100)", example = "10",
					schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
			@RequestParam(defaultValue = "10") @Positive @Max(100) int size) {
		return ResponseEntity.ok(FilmPageResponse.from(filmService.listFilms(page, size)));
	}

	@GetMapping("/search")
	@Operation(summary = "Search films by title", description = "Searches titles using SWAPI.tech's case-insensitive partial matching and returns a page of summaries.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Film search completed", content = @Content(schema = @Schema(implementation = FilmPageResponse.class))),
			@ApiResponse(responseCode = "400", description = "Title must not be blank; page and size must be valid", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<FilmPageResponse> searchFilms(
			@Parameter(name = "title", description = "Case-insensitive partial title match", example = "hope", required = true)
			@RequestParam @NotBlank String title,
			@Parameter(name = "page", description = "One-based page number", example = "1", schema = @Schema(type = "integer", minimum = "1"))
			@RequestParam(defaultValue = "1") @Positive int page,
			@Parameter(name = "size", description = "Page size (default 10, maximum 100)", example = "10",
					schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
			@RequestParam(defaultValue = "10") @Positive @Max(100) int size) {
		return ResponseEntity.ok(FilmPageResponse.from(filmService.searchFilms(title, page, size)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a film by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Film found", content = @Content(schema = @Schema(implementation = FilmResponse.class))),
			@ApiResponse(responseCode = "400", description = "ID must be positive", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Film not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<FilmResponse> findById(
			@Parameter(name = "id", description = "Positive SWAPI.tech film ID", in = ParameterIn.PATH, example = "1", required = true,
					schema = @Schema(type = "integer", minimum = "1"))
			@PathVariable @Positive long id) {
		return ResponseEntity.ok(FilmResponse.from(filmService.findById(id)));
	}
}
