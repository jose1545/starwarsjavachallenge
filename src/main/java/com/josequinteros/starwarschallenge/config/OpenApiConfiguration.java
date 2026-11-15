package com.josequinteros.starwarschallenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;

@Configuration
public class OpenApiConfiguration {

	//Mostrar autenticación bearer en operaciones SWAPI mientras se permite a los usuarios llamar al endpoint de login primero.
	@Bean
	OpenAPI starWarsOpenAPI() {
		var api = new OpenAPI().info(new Info()
				.title("Star Wars Challenge API")
				.description("API for retrieving Star Wars data from SWAPI.")
				.version("1.0.0"))
				.components(new Components().addSecuritySchemes("bearerAuth",
					new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
		var secured = new SecurityRequirement().addList("bearerAuth");
		var operation = new io.swagger.v3.oas.models.Operation().security(java.util.List.of(secured));
		var methods = new io.swagger.v3.oas.models.PathItem()
				.get(operation).post(operation).put(operation).delete(operation);
		api.path("/api/people", methods).path("/api/people/{id}", methods)
				.path("/api/people/search", methods).path("/api/films", methods)
				.path("/api/films/{id}", methods).path("/api/films/search", methods)
				.path("/api/starships", methods).path("/api/starships/{id}", methods)
				.path("/api/starships/search", methods).path("/api/vehicles", methods)
				.path("/api/vehicles/{id}", methods).path("/api/vehicles/search", methods);
		return api;
	}

	@Bean
	OpenApiCustomizer publicLogin() {
		return api -> {
			api.getPaths().forEach((path, item) -> {
				if (path.startsWith("/api/people/") || path.equals("/api/people")
						|| path.startsWith("/api/films/") || path.equals("/api/films")
						|| path.startsWith("/api/starships/") || path.equals("/api/starships")
						|| path.startsWith("/api/vehicles/") || path.equals("/api/vehicles")) {
					var requirement = new SecurityRequirement().addList("bearerAuth");
					if (item.getGet() != null) item.getGet().setSecurity(java.util.List.of(requirement));
					if (item.getPost() != null) item.getPost().setSecurity(java.util.List.of(requirement));
					if (item.getPut() != null) item.getPut().setSecurity(java.util.List.of(requirement));
					if (item.getDelete() != null) item.getDelete().setSecurity(java.util.List.of(requirement));
				}
			});
			var path = api.getPaths().get("/api/auth/login");
			if (path != null && path.getPost() != null) path.getPost().setSecurity(java.util.List.of());
		};
	}
}
