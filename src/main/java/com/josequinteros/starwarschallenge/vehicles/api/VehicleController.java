package com.josequinteros.starwarschallenge.vehicles.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.josequinteros.starwarschallenge.shared.error.ApiErrorResponse;
import com.josequinteros.starwarschallenge.vehicles.application.VehicleService;
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
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Star Wars vehicle information")
public class VehicleController {
	private final VehicleService vehicleService;

	public VehicleController(VehicleService vehicleService) {
		this.vehicleService = vehicleService;
	}

	@GetMapping
	@Operation(summary = "List vehicles", description = "Returns a page of vehicle summaries from SWAPI.tech. The page size defaults to 10 and cannot exceed 100. Use the vehicle ID endpoint for full details.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Vehicle page retrieved", content = @Content(schema = @Schema(implementation = VehiclePageResponse.class))),
			@ApiResponse(responseCode = "400", description = "Page must be positive and size must be between 1 and 100", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<VehiclePageResponse> listVehicles(
			@Parameter(description = "One-based page number", example = "1", schema = @Schema(type = "integer", minimum = "1"))
			@RequestParam(defaultValue = "1") @Positive int page,
			@Parameter(name = "size", description = "Number of results per page (default 10, maximum 100)", example = "10",
					schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
			@RequestParam(defaultValue = "10") @Positive @Max(100) int size) {
		return ResponseEntity.ok(VehiclePageResponse.from(vehicleService.listVehicles(page, size)));
	}

	@GetMapping("/search")
	@Operation(summary = "Search vehicles by name or model", description = "Searches each supplied field using case-insensitive partial matching. If both fields are supplied, results matching either field are returned.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Vehicle search completed", content = @Content(schema = @Schema(implementation = VehiclePageResponse.class))),
			@ApiResponse(responseCode = "400", description = "At least one of name or model must be nonblank; page and size must be valid", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<VehiclePageResponse> searchVehicles(
			@Parameter(name = "name", description = "Case-insensitive partial name match", example = "crawler")
			@RequestParam(required = false) String name,
			@Parameter(name = "model", description = "Case-insensitive partial model match", example = "digger")
			@RequestParam(required = false) String model,
			@Parameter(name = "page", description = "One-based result page", example = "1", schema = @Schema(type = "integer", minimum = "1"))
			@RequestParam(defaultValue = "1") @Positive int page,
			@Parameter(name = "size", description = "Page size (default 10, maximum 100)", example = "10",
					schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
			@RequestParam(defaultValue = "10") @Positive @Max(100) int size) {
		return ResponseEntity.ok(VehiclePageResponse.from(vehicleService.searchVehicles(name, model, page, size)));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a vehicle by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Vehicle found", content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
			@ApiResponse(responseCode = "400", description = "ID must be positive", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "SWAPI.tech is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
	})
	public ResponseEntity<VehicleResponse> findById(
			@Parameter(name = "id", description = "Positive SWAPI.tech vehicle ID", in = ParameterIn.PATH, example = "4", required = true,
					schema = @Schema(type = "integer", minimum = "1"))
			@PathVariable @Positive long id) {
		return ResponseEntity.ok(VehicleResponse.from(vehicleService.findById(id)));
	}
}
