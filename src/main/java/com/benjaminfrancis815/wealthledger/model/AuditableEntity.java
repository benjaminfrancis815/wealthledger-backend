package com.benjaminfrancis815.wealthledger.model;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.benjaminfrancis815.wealthledger.security.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@MappedSuperclass
public abstract class AuditableEntity {

	@Column(name = "created_at", updatable = false, insertable = false)
	private LocalDateTime createdAt;

	@Column(name = "modified_at", insertable = false)
	private LocalDateTime modifiedAt;

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "modified_by")
	private Long modifiedBy;

	@PrePersist
	public void prePersist() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final User user = (User) authentication.getPrincipal();
		this.createdBy = user.getId();
		this.modifiedBy = user.getId();
	}

	@PreUpdate
	public void preUpdate() {
		this.modifiedAt = LocalDateTime.now();
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final User user = (User) authentication.getPrincipal();
		this.modifiedBy = user.getId();
	}

}
