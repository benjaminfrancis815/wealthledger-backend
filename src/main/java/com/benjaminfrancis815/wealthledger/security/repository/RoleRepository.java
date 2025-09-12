package com.benjaminfrancis815.wealthledger.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.security.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(final String name);

}
