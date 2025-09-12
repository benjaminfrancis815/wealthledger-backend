package com.benjaminfrancis815.wealthledger.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.security.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(final String username);

	boolean existsByUsername(final String username);

}
