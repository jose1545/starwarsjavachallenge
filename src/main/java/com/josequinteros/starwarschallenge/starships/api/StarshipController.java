package com.josequinteros.starwarschallenge.starships.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.josequinteros.starwarschallenge.shared.error.ApiErrorResponse;
import com.josequinteros.starwarschallenge.starships.application.StarshipService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
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
@RequestMapping("/api/starships")
@Tag(name = "Starships", description = "Star Wars starship information")
public class StarshipController {
	private final StarshipService starshipService;

	public StarshipController(StarshipService starshipService) {
		this.starshipService = starshipService;
	}

	@GetMapping
	@Operation(summary = "List starships", description = "Returns a page of starship summaries from SWAPI.tech. The page size defaults to 10 and cannot exceed 100. Use the starship ID endpoint for full details.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Starship page retrieved", content = @Content(schema = @Schema(implementation = StarshipPageResponse.class))),
			@ApiResponse(responseCode = "400", description = "Page must be positive and size must be between 1 and 100", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<StarshipPageResponse> listStarships(
			@Parameter(description = "One-based page number", example = "1", schema = @Schema(type = "integer", minimum = "1"))
			@RequestParam(defaultValue = "1") @Positive int page,
			@Parameter(name = "size", description = "Number of results per page (default 10, maximum 100)", example = "10",
					schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
			@RequestParam(defaultValue = "10") @Positive @Max(100) int size) {
		return ResponseEntity.ok(StarshipPageResponse.from(starshipService.listStarships(page, size)));
	}

	@GetMapping("/search")
	@Operation(summary = "Search starships by name or model", description = "Searches each supplied field using case-insensitive partial matching. If both fields are supplied, results matching either field are returned.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Starship search completed", content = @Content(schema = @Schema(implementation = StarshipPageResponse.class))),
			@ApiResponse(responseCode = "400", description = "At least one of name or model must be nonblank; page and size must be valid", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<StarshipPageResponse> searchStarships(
			@Parameter(name = "name", description = "Case-insensitive partial name match", example = "falcon")
			@RequestParam(required = false) String name,
			@Parameter(name = "model", description = "Case-insensitive partial model match", example = "YT-1300")
			@RequestParam(required = false) String model,
			@Parameter(name = "page", description = "One-based result page", example = "1", schema = @Schema(type = "integer", minimum = "1"))
			@RequestParam(defaultValue = "1") @Positive int page,
			@Parameter(name = "size", description = "Page size (default 10, maximum 100)", example = "10",
					schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
			@RequestParam(defaultValue = "10") @Positive @Max(100) int size) {
		return ResponseEntity.ok(StarshipPageResponse.from(starshipService.searchStarships(name, model, page, size)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a starship by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Starship found", content = @Content(schema = @Schema(implementation = StarshipResponse.class))),
			@ApiResponse(responseCode = "400", description = "ID must be positive", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Starship not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<StarshipResponse> findById(
			@Parameter(name = "id", description = "Positive SWAPI.tech starship ID", in = ParameterIn.PATH, example = "9", required = true,
					schema = @Schema(type = "integer", minimum = "1"))
			@PathVariable @Positive long id) {
		return ResponseEntity.ok(StarshipResponse.from(starshipService.findById(id)));
	}
}
