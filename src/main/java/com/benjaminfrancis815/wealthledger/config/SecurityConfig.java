package com.benjaminfrancis815.wealthledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// Since we use JWTs, disable CSRF
				.csrf(csrf -> csrf.disable())
				// No sessions — stateless
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// Enable CORS
				.cors(Customizer.withDefaults())
				// Authorize HTTP requests
				.authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll());
		return http.build();
	}

}
