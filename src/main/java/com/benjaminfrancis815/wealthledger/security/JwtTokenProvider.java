package com.benjaminfrancis815.wealthledger.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expirationMs}")
	private long jwtExpirationMs; // 1 hr

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
	}

	public String generateToken(final Authentication authentication) {
		final String username = authentication.getName();
		final Instant now = Instant.now();
		final Instant expiry = now.plusMillis(this.jwtExpirationMs);
		return Jwts.builder().subject(username).issuedAt(Date.from(now)).expiration(Date.from(expiry))
				.signWith(getSigningKey()).compact();
	}

	public String getUsernameFromToken(final String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean isValidToken(final String token, final UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(final String token) {
		final Date expiration = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload()
				.getExpiration();
		return expiration.toInstant().isBefore(Instant.now());
	}

}
