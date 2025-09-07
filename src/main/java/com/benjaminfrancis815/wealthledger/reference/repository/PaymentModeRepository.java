package com.benjaminfrancis815.wealthledger.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.reference.model.PaymentMode;

public interface PaymentModeRepository extends JpaRepository<PaymentMode, Long> {

}
