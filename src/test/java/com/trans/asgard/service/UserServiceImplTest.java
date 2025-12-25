package com.trans.asgard.service;

import com.trans.asgard.domain.Entrepot.model.Entrepot;
import com.trans.asgard.domain.iam.dto.RegisterUserRequest;
import com.trans.asgard.domain.iam.dto.UserResponse;
import com.trans.asgard.domain.iam.enums.Role;
import com.trans.asgard.domain.iam.mapper.UserMapper;
import com.trans.asgard.domain.iam.model.User;
import com.trans.asgard.domain.iam.repository.UserRepository;
import com.trans.asgard.domain.iam.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User userEntity;
    private RegisterUserRequest request;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        userEntity = User.builder()
                .id(1L)
                .login("john.doe")
                .nom("Doe")
                .prenom("John")
                .email("john.doe@example.com")
                .password("encodedPassword123")
                .role(Role.ROLE_GESTIONNAIRE)
                .entrepotAssigne(null) // optional
                .build();

        request = new RegisterUserRequest(
                "john.doe",
                "Doe",
                "John",
                "john.doe@example.com",
                "secret123",
                Role.ROLE_GESTIONNAIRE
        );

        response = new UserResponse(
                "1",
                "john.doe",
                "Doe",
                "John",
                "john.doe@example.com",
                Role.ROLE_GESTIONNAIRE
        );
    }

    @Nested
    @DisplayName("registerUser")
    class RegisterUserTest {

        @Test
        @DisplayName("Should successfully register new user when login is available")
        void shouldRegisterUserSuccessfully() {
            // Arrange
            when(userRepository.existsByLogin(request.login())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(userEntity);
            when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword123");
            when(userRepository.save(any(User.class))).thenReturn(userEntity);
            when(userMapper.toResponse(userEntity)).thenReturn(response);

            // Act
            UserResponse result = userService.registerUser(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.login()).isEqualTo("john.doe");
            assertThat(result.email()).isEqualTo("john.doe@example.com");
            assertThat(result.role()).isEqualTo(Role.ROLE_GESTIONNAIRE);

            verify(userRepository).existsByLogin("john.doe");
            verify(userMapper).toEntity(request);
            verify(passwordEncoder).encode("secret123");
            verify(userRepository).save(any(User.class));
            verify(userMapper).toResponse(userEntity);
            verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
        }

        @Test
        @DisplayName("Should throw exception when login already exists")
        void shouldThrowExceptionWhenLoginAlreadyExists() {
            // Arrange
            when(userRepository.existsByLogin("john.doe")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.registerUser(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("this login is already used");

            verify(userRepository).existsByLogin("john.doe");
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(userMapper, passwordEncoder);
        }

        @Test
        @DisplayName("Should encode password before saving user")
        void shouldEncodePasswordBeforeSaving() {
            // Arrange
            when(userRepository.existsByLogin(anyString())).thenReturn(false);
            when(userMapper.toEntity(any())).thenReturn(userEntity);
            when(passwordEncoder.encode("secret123")).thenReturn("superSecretEncodedHash");
            when(userRepository.save(any(User.class))).thenReturn(userEntity);
            when(userMapper.toResponse(any())).thenReturn(response);

            // Act
            userService.registerUser(request);

            // Assert
            verify(passwordEncoder).encode("secret123");
            verify(userRepository).save(argThat(user ->
                    "superSecretEncodedHash".equals(user.getPassword())
            ));
        }
    }

    @Nested
    @DisplayName("findByLogin")
    class FindByLoginTest {

        @Test
        @DisplayName("Should return user when found by login")
        void shouldFindUserByLogin() {
            // Arrange
            when(userRepository.findUserByLogin("john.doe"))
                    .thenReturn(Optional.of(userEntity));

            // Act
            Optional<User> result = userService.findByLogin("john.doe");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getLogin()).isEqualTo("john.doe");
            assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");

            verify(userRepository).findUserByLogin("john.doe");
        }

        @Test
        @DisplayName("Should return empty optional when user not found")
        void shouldReturnEmptyWhenUserNotFound() {
            // Arrange
            when(userRepository.findUserByLogin("unknown")).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userService.findByLogin("unknown");

            // Assert
            assertThat(result).isEmpty();

            verify(userRepository).findUserByLogin("unknown");
        }
    }
}