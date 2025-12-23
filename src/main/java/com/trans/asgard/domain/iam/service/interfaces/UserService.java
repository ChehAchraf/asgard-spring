package com.trans.asgard.domain.iam.service.interfaces;

import com.trans.asgard.domain.iam.dto.RegisterUserRequest;
import com.trans.asgard.domain.iam.dto.UserResponse;
import com.trans.asgard.domain.iam.model.User;

import java.util.Optional;

public interface UserService {
    UserResponse registerUser(RegisterUserRequest request);

    Optional<User> findByLogin(String login);

}
