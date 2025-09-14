package com.benjaminfrancis815.wealthledger.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.benjaminfrancis815.wealthledger.security.dto.RegisterUserRequest;
import com.benjaminfrancis815.wealthledger.security.dto.RegisterUserResponse;
import com.benjaminfrancis815.wealthledger.security.model.Role;
import com.benjaminfrancis815.wealthledger.security.model.User;
import com.benjaminfrancis815.wealthledger.security.repository.RoleRepository;
import com.benjaminfrancis815.wealthledger.security.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;

	@Autowired
	public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder,
			final RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional
	public RegisterUserResponse registerUser(final RegisterUserRequest request) {
		if (this.userRepository.existsByUsername(request.username())) {
			throw new RuntimeException("User already exists...!");
		}
		final User user = new User();
		user.setUsername(request.username());
		user.setPassword(this.passwordEncoder.encode(request.password()));
		final Role role = this.roleRepository.findByName("USER")
				.orElseThrow(() -> new RuntimeException("Role not found...!"));
		user.addRole(role);
		final User savedUser = this.userRepository.save(user);
		return new RegisterUserResponse(savedUser.getId(), savedUser.getUsername());
	}

}
