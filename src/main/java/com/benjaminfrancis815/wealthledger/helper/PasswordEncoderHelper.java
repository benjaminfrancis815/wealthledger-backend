package com.benjaminfrancis815.wealthledger.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderHelper {

	public static void main(final String[] args) {
		final String password = new BCryptPasswordEncoder().encode(args[0]);
		System.out.println(password);
	}

}
