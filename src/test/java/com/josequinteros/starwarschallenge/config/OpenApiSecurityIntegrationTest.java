package com.josequinteros.starwarschallenge.config;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.josequinteros.starwarschallenge.people.application.PeopleService;
import com.josequinteros.starwarschallenge.people.domain.Person;
import com.josequinteros.starwarschallenge.auth.infrastructure.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import java.time.Instant;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@SpringBootTest(properties = {
		"auth.seed.username=test-user",
		"auth.seed.password=test-password",
		"jwt.secret=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
		"jwt.issuer=starwarschallenge-test",
		"jwt.ttl-seconds=1800"
})
@AutoConfigureMockMvc
class OpenApiSecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PeopleService peopleService;

	@Autowired
	private AppUserRepository users;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtEncoder jwtEncoder;

	@Test
	void exposesOpenApiDocumentWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("Star Wars Challenge API"))
				.andExpect(jsonPath("$.info.version").value("1.0.0"))
				.andExpect(jsonPath("$.paths['/api/people'].get").exists())
				.andExpect(jsonPath("$.components.schemas.PersonSummary.properties.uid").exists())
				.andExpect(jsonPath("$.paths['/api/people/{id}'].get").exists())
				.andExpect(jsonPath("$.paths['/api/films'].get").exists())
				.andExpect(jsonPath("$.paths['/api/films/{id}'].get").exists())
				.andExpect(jsonPath("$.components.schemas.FilmSummary.properties.uid").exists())
				.andExpect(jsonPath("$.paths['/api/starships'].get").exists())
				.andExpect(jsonPath("$.paths['/api/starships/{id}'].get").exists())
				.andExpect(jsonPath("$.components.schemas.StarshipSummary.properties.uid").exists())
				.andExpect(jsonPath("$.paths['/api/vehicles'].get").exists())
				.andExpect(jsonPath("$.paths['/api/vehicles/{id}'].get").exists())
				.andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
				.andExpect(jsonPath("$.paths['/api/auth/login'].post.security").isEmpty())
				.andExpect(jsonPath("$.paths['/api/people'].get.security[0].bearerAuth").exists())
				.andExpect(jsonPath("$.components.schemas.VehicleSummary.properties.uid").exists())
				.andExpect(jsonPath("$.paths['/api/people/{id}'].get.responses['200']").exists())
				.andExpect(jsonPath("$.paths['/api/people/{id}'].get.responses['400']").exists())
				.andExpect(jsonPath("$.paths['/api/people/{id}'].get.responses['404']").exists())
				.andExpect(jsonPath("$.paths['/api/people/{id}'].get.responses['502']").exists());
	}

	@Test
	void exposesSwaggerUiWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk());
	}

	@Test
	void keepsPeopleEndpointAuthenticated() throws Exception {
		mockMvc.perform(get("/api/people/1"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/people/1").header("Authorization", "Bearer broken.token.value"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/people/1").header("Authorization", "Bearer " + token("wrong-issuer", Instant.now().plusSeconds(60), "test-user")))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/people/1").header("Authorization", "Bearer " + token("starwarschallenge-test", Instant.now().minusSeconds(60), "test-user")))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/people/1").header("Authorization", "Bearer " + token("starwarschallenge-test", Instant.now().plusSeconds(60), " ")))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/people/1").header("Authorization", "Bearer " + tokenWithOtherKey()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void allSwapiRouteFamiliesRejectAnonymousRequests() throws Exception {
		for (String uri : java.util.List.of("/api/people", "/api/people/search?name=luke", "/api/people/1",
				"/api/films", "/api/films/search?title=hope", "/api/films/1",
				"/api/starships", "/api/starships/search?name=falcon", "/api/starships/1",
				"/api/vehicles", "/api/vehicles/search?name=crawler", "/api/vehicles/1")) {
			mockMvc.perform(get(uri)).andExpect(status().isUnauthorized());
		}
	}

	@Test
	void authenticatedUserCanStillUsePeopleEndpoint() throws Exception {
		when(peopleService.findById(1)).thenReturn(
				new Person("Luke Skywalker", "172", "77", "blond", "fair", "blue", "19BBY", "male"));

		mockMvc.perform(get("/api/people/1").header("Authorization", "Bearer " + loginToken()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Luke Skywalker"));
	}

	@Test
	void loginIssuesNoStoreBearerTokenAndBadCredentialsAreGeneric() throws Exception {
		org.junit.jupiter.api.Assertions.assertTrue(users.findByUsername("test-user").isPresent());
		org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("test-password",
				users.findByUsername("test-user").orElseThrow().getPasswordHash()));
		mockMvc.perform(post("/api/auth/login").contentType("application/json")
				.content("{\"username\":\"test-user\",\"password\":\"test-password\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.expiresIn").value(1800))
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Cache-Control", "no-store"));
		mockMvc.perform(post("/api/auth/login").contentType("application/json")
				.content("{\"username\":\"test-user\",\"password\":\"wrong\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error").value("Invalid credentials"));
		mockMvc.perform(post("/api/auth/login").contentType("application/json").content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void authenticatedRequestDoesNotCreateSession() throws Exception {
		when(peopleService.findById(1)).thenReturn(new Person("Luke Skywalker", "172", "77", "blond", "fair", "blue", "19BBY", "male"));
		var result = mockMvc.perform(get("/api/people/1").header("Authorization", "Bearer " + loginToken()))
				.andExpect(status().isOk()).andReturn();
		org.junit.jupiter.api.Assertions.assertNull(result.getRequest().getSession(false));
		org.junit.jupiter.api.Assertions.assertNull(result.getResponse().getCookie("JSESSIONID"));
	}

	private String loginToken() throws Exception {
		String body = mockMvc.perform(post("/api/auth/login").contentType("application/json")
				.content("{\"username\":\"test-user\",\"password\":\"test-password\"}"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		return new com.fasterxml.jackson.databind.ObjectMapper().readTree(body).get("accessToken").asText();
	}

	private String token(String issuer, Instant expiresAt, String subject) {
		var now = expiresAt.isBefore(Instant.now()) ? expiresAt.minusSeconds(120) : Instant.now();
		return jwtEncoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder().issuer(issuer).subject(subject)
				.issuedAt(now).expiresAt(expiresAt).build())).getTokenValue();
	}

	private String tokenWithOtherKey() {
		byte[] otherKey = new byte[32];
		java.util.Arrays.fill(otherKey, (byte) 7);
		var encoder = NimbusJwtEncoder.withSecretKey(new javax.crypto.spec.SecretKeySpec(otherKey, "HmacSHA256"))
				.algorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
		var now = Instant.now();
		return encoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder().issuer("starwarschallenge-test")
				.subject("test-user").issuedAt(now).expiresAt(now.plusSeconds(60)).build())).getTokenValue();
	}
}
