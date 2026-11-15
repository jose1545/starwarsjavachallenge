package com.josequinteros.starwarschallenge.people.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.josequinteros.starwarschallenge.people.application.PeopleService;
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
@RequestMapping("/api/people")
@Tag(name = "People", description = "Star Wars character information")
public class PeopleController {

	private final PeopleService peopleService;

	public PeopleController(PeopleService peopleService) {
		this.peopleService = peopleService;
	}

	@GetMapping
	@Operation(summary = "List people", description = "Returns a page of person summaries from SWAPI.tech. The page size defaults to 10 and cannot exceed 100. Use the person ID endpoint to retrieve full details.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "People page retrieved",
					content = @Content(schema = @Schema(implementation = PeoplePageResponse.class))),
			@ApiResponse(responseCode = "400", description = "Page must be positive and size must be between 1 and 100",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "Star Wars data service is unavailable",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<PeoplePageResponse> listPeople(
			@Parameter(name = "page", description = "One-based page number", example = "1",
					schema = @Schema(type = "integer", minimum = "1"))
			@RequestParam(defaultValue = "1") @Positive int page,
			@Parameter(name = "size", description = "Number of results per page (default 10, maximum 100)", example = "10",
					schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
			@RequestParam(defaultValue = "10") @Positive @Max(100) int size) {
		return ResponseEntity.ok(PeoplePageResponse.from(peopleService.listPeople(page, size)));
	}

	@GetMapping("/search")
	@Operation(summary = "Search people by name", description = "Searches names using SWAPI.tech's case-insensitive partial matching and returns a page of summaries.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "People search completed",
					content = @Content(schema = @Schema(implementation = PeoplePageResponse.class))),
			@ApiResponse(responseCode = "400", description = "Name must not be blank; page and size must be valid",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "Star Wars data service is unavailable",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<PeoplePageResponse> searchPeople(
			@Parameter(name = "name", description = "Case-insensitive partial name match", example = "luke", required = true)
			@RequestParam @NotBlank String name,
			@Parameter(name = "page", description = "One-based page number", example = "1", schema = @Schema(type = "integer", minimum = "1"))
			@RequestParam(defaultValue = "1") @Positive int page,
			@Parameter(name = "size", description = "Page size (default 10, maximum 100)", example = "10",
					schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
			@RequestParam(defaultValue = "10") @Positive @Max(100) int size) {
		return ResponseEntity.ok(PeoplePageResponse.from(peopleService.searchPeople(name, page, size)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a person by ID", description = "Retrieves a character from the Star Wars data service.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Person found",
					content = @Content(schema = @Schema(implementation = PersonResponse.class))),
			@ApiResponse(responseCode = "400", description = "The ID must be a positive number",
					content = @Content(schema = @Schema(implementation = com.josequinteros.starwarschallenge.shared.error.ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Person not found",
					content = @Content(schema = @Schema(implementation = com.josequinteros.starwarschallenge.shared.error.ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "Star Wars data service is unavailable",
					content = @Content(schema = @Schema(implementation = com.josequinteros.starwarschallenge.shared.error.ApiErrorResponse.class)))
	})
	public ResponseEntity<PersonResponse> findById(
			@Parameter(name = "id", description = "Positive SWAPI person ID", in = ParameterIn.PATH,
					example = "1", required = true, schema = @Schema(type = "integer", minimum = "1"))
			@PathVariable @Positive long id) {
		return ResponseEntity.ok(PersonResponse.from(peopleService.findById(id)));
	}
}
