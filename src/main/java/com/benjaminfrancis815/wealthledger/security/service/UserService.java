package com.benjaminfrancis815.wealthledger.security.service;

import com.benjaminfrancis815.wealthledger.security.dto.RegisterUserRequest;
import com.benjaminfrancis815.wealthledger.security.dto.RegisterUserResponse;

public interface UserService {

	RegisterUserResponse registerUser(final RegisterUserRequest request);

}
