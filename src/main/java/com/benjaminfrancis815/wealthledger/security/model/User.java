package com.benjaminfrancis815.wealthledger.security.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.benjaminfrancis815.wealthledger.model.AuditableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends AuditableEntity implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private final Set<UserRole> userRoles;

	public User() {
		this.userRoles = new HashSet<>();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.userRoles.stream()
				.map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName())).toList();
	}

	public void addRole(final Role role) {
		final UserRole userRole = new UserRole();
		final UserRoleId userRoleId = new UserRoleId();
		userRoleId.setUserId(this.getId());
		userRoleId.setRoleId(role.getId());
		userRole.setId(userRoleId);
		userRole.setUser(this);
		userRole.setRole(role);
		this.userRoles.add(userRole);
	}

}
