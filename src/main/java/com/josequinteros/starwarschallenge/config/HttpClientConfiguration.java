package com.josequinteros.starwarschallenge.config;

import java.net.http.HttpClient;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SwapiProperties.class)
public class HttpClientConfiguration {


	// Mantener la URL y los tiempos de espera en la configuración para que los adaptadores no tengan que manejar la configuración operativa.
	@Bean(destroyMethod = "close")
	HttpClient swapiHttpClient(SwapiProperties properties) {
		return HttpClient.newBuilder()
				.connectTimeout(properties.connectionTimeout())
				.build();
	}

	@Bean
	RestClient swapiRestClient(RestClient.Builder builder, HttpClient swapiHttpClient, SwapiProperties properties) {
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(swapiHttpClient);
		requestFactory.setReadTimeout(properties.readTimeout());

		return builder
				.baseUrl(properties.baseUrl().toString())
				.requestFactory(requestFactory)
				.build();
	}
}
