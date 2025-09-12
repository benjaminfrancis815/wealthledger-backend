package com.benjaminfrancis815.wealthledger.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.benjaminfrancis815.wealthledger.security.JwtTokenProvider;
import com.benjaminfrancis815.wealthledger.security.dto.LoginRequest;
import com.benjaminfrancis815.wealthledger.security.dto.LoginResponse;
import com.benjaminfrancis815.wealthledger.security.dto.RegisterUserRequest;
import com.benjaminfrancis815.wealthledger.security.dto.RegisterUserResponse;
import com.benjaminfrancis815.wealthledger.security.service.UserService;

@RestController
public class AuthController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public AuthController(final UserService userService, final AuthenticationManager authenticationManager,
			final JwtTokenProvider jwtTokenProvider) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping(value = "/v1/auth/register")
	public ResponseEntity<RegisterUserResponse> registerUser(@RequestBody final RegisterUserRequest request) {
		final RegisterUserResponse response = this.userService.registerUser(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/v1/auth/login")
	public ResponseEntity<LoginResponse> login(@RequestBody final LoginRequest request) {
		final Authentication authentication = this.authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final String token = this.jwtTokenProvider.generateToken(authentication);
		final LoginResponse response = new LoginResponse(token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
