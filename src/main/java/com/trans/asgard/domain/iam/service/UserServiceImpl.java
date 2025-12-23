package com.trans.asgard.domain.iam.service;

import com.trans.asgard.domain.iam.dto.RegisterUserRequest;
import com.trans.asgard.domain.iam.dto.UserResponse;
import com.trans.asgard.domain.iam.mapper.UserMapper;
import com.trans.asgard.domain.iam.model.User;
import com.trans.asgard.domain.iam.repository.UserRepository;
import com.trans.asgard.domain.iam.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(RegisterUserRequest request) {
        if(userRepository.existsByLogin(request.login())){
            throw new RuntimeException("this login is already used");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }
}
