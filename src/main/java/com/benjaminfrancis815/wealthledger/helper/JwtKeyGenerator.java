package com.benjaminfrancis815.wealthledger.helper;

import java.util.Base64;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

public class JwtKeyGenerator {

	public static void main(final String[] args) {
		final SecretKey secretKey = Jwts.SIG.HS256.key().build();
		final String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		System.out.println(encodedKey);
	}

}
