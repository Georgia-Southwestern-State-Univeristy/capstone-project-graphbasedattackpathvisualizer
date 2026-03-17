package com.initializer.controller;

import com.initializer.dto.LoginUserRequest;
import com.initializer.dto.LoginUserResponse;
import com.initializer.dto.RegisterUserRequest;
import com.initializer.dto.RegisterUserResponse;
import com.initializer.entity.UserEntity;
import com.initializer.exception.InvalidLoginException;
import com.initializer.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(
            @RequestBody RegisterUserRequest request) {

        UserEntity savedUser = userService.registerUser(request);

        RegisterUserResponse response = new RegisterUserResponse(
                savedUser.getUserID(),
                savedUser.getUserEmail(),
                savedUser.getUserCreatedAt(),
                "User registered successfully."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> loginUser(
            @RequestBody LoginUserRequest request,
            HttpServletRequest httpRequest) {

        if (request.getUserEmail() == null || request.getPassword() == null) {
            throw new InvalidLoginException("Email and password are required.");
        }

        String normalizedEmail = request.getUserEmail().trim().toLowerCase();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            normalizedEmail,
                            request.getPassword()
                    )
            );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    "SPRING_SECURITY_CONTEXT",
                    securityContext
            );

            UserEntity user = userService.getUserByEmail(normalizedEmail);

            LoginUserResponse response = new LoginUserResponse(
                    user.getUserID(),
                    user.getUserEmail(),
                    "Login successful."
            );

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            throw new InvalidLoginException("Invalid email or password.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Logout successful.");
    }

    @GetMapping("/me")
    public ResponseEntity<LoginUserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidLoginException("No authenticated user found.");
        }

        UserEntity user = userService.getUserByEmail(authentication.getName());

        if (user == null) {
            throw new InvalidLoginException("No authenticated user found.");
        }

        LoginUserResponse response = new LoginUserResponse(
                user.getUserID(),
                user.getUserEmail(),
                "Authenticated user loaded successfully."
        );

        return ResponseEntity.ok(response);
    }
}