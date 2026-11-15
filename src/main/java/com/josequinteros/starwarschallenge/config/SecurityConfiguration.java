package com.josequinteros.starwarschallenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josequinteros.starwarschallenge.shared.error.ApiErrorResponse;

@Configuration
public class SecurityConfiguration {
	//Mantener la autenticación stateless: los clientes envían un JWT firmado con cada solicitud protegida.
	@Bean
	SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/people/**", "/api/films/**", "/api/starships/**", "/api/vehicles/**").authenticated()
				.anyRequest().denyAll())
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(errors -> errors.authenticationEntryPoint(apiAuthenticationEntryPoint())
				.accessDeniedHandler(apiAccessDeniedHandler()))
			.oauth2ResourceServer(oauth -> oauth.authenticationEntryPoint(apiAuthenticationEntryPoint())
				.accessDeniedHandler(apiAccessDeniedHandler()).jwt(jwt -> {}));
		return http.build();
	}

	@Bean
	AccessDeniedHandler apiAccessDeniedHandler() {
		return (request, response, exception) -> {
			response.setStatus(403);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			new ObjectMapper().writeValue(response.getOutputStream(), new ApiErrorResponse("Access denied"));
		};
	}

	@Bean
	AuthenticationEntryPoint apiAuthenticationEntryPoint() {
		return (request, response, exception) -> {
			response.setStatus(401);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			new ObjectMapper().writeValue(response.getOutputStream(), new ApiErrorResponse("Authentication required"));
		};
	}

	//Requerir una clave fuerte configurada externamente para que la aplicación nunca defaultea a una clave conocida
	@Bean
	byte[] jwtSigningKey(@Value("${jwt.secret}") String secret) {
		try {
			byte[] bytes = Base64.getDecoder().decode(secret);
			if (bytes.length < 32) throw new IllegalArgumentException();
			return bytes;
		} catch (IllegalArgumentException exception) {
			throw new IllegalStateException("JWT_SECRET must be valid Base64 containing at least 32 random bytes");
		}
	}


	//Solo aceptar tokens HS256 emitidos por esta aplicación, con una caducidad válida y un subject no vacío.
	@Bean
	JwtDecoder jwtDecoder(byte[] bytes, @Value("${jwt.issuer}") String issuer) {
		var decoder = NimbusJwtDecoder.withSecretKey(new SecretKeySpec(bytes, "HmacSHA256"))
				.macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
		OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> subjectValidator = jwt ->
			jwt.getClaimAsString(JwtClaimNames.SUB) != null && !jwt.getClaimAsString(JwtClaimNames.SUB).isBlank()
					? org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success()
					: org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
							new OAuth2Error("invalid_token", "Token subject is required", null));
		decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), subjectValidator));
		return decoder;
	}

	@Bean
	JwtEncoder jwtEncoder(byte[] bytes) {
		return NimbusJwtEncoder.withSecretKey(new SecretKeySpec(bytes, "HmacSHA256"))
				.algorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
	}

	@Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

	@Bean AuthenticationManager authenticationManager(UserDetailsService users, PasswordEncoder encoder) {
		var provider = new DaoAuthenticationProvider(users);
		provider.setPasswordEncoder(encoder);
		return new ProviderManager(provider);
	}
}
