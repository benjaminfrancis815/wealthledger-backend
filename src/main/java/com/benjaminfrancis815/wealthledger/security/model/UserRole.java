package com.benjaminfrancis815.wealthledger.security.model;

import com.benjaminfrancis815.wealthledger.model.AuditableEntity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_roles")
public class UserRole extends AuditableEntity {

	@EmbeddedId
	private UserRoleId id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@MapsId("roleId")
	@JoinColumn(name = "role_id")
	private Role role;

	public UserRoleId getId() {
		return this.id;
	}

	public void setId(final UserRoleId id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(final Role role) {
		this.role = role;
	}

}
