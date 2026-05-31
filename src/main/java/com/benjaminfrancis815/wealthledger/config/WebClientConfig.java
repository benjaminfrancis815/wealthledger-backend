package com.benjaminfrancis815.wealthledger.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient webClient() {
		final HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(10000))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000).followRedirect(true);
		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
	}

}
