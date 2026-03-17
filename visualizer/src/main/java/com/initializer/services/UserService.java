package com.initializer.services;

import com.initializer.dto.LoginUserRequest;
import com.initializer.dto.RegisterUserRequest;
import com.initializer.entity.UserEntity;
import com.initializer.exception.DuplicateUserException;
import com.initializer.exception.InvalidLoginException;
import com.initializer.exception.InvalidRegistrationException;
import com.initializer.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity registerUser(RegisterUserRequest request) {

        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            throw new InvalidRegistrationException("Email is required.");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidRegistrationException("Password is required.");
        }

        String normalizedEmail = request.getUserEmail().trim().toLowerCase();

        if (userRepository.existsByUserEmail(normalizedEmail)) {
            throw new DuplicateUserException("An account with this email already exists.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity(normalizedEmail, hashedPassword);

        return userRepository.save(user);
    }

    public UserEntity loginUser(LoginUserRequest request) {

        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            throw new InvalidLoginException("Email is required.");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidLoginException("Password is required.");
        }

        String normalizedEmail = request.getUserEmail().trim().toLowerCase();

        UserEntity user = userRepository.findByUserEmail(normalizedEmail);

        if (user == null) {
            throw new InvalidLoginException("Invalid email or password.");
        }

        boolean passwordMatches =
                passwordEncoder.matches(request.getPassword(), user.getUserPwHash());

        if (!passwordMatches) {
            throw new InvalidLoginException("Invalid email or password.");
        }

        return user;
    }

    public UserEntity getUserByEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }
}
