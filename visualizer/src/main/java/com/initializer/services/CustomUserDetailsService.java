package com.initializer.services;

import com.initializer.entity.UserEntity;
import com.initializer.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserEmail(userEmail);

        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return User.builder()
                .username(user.getUserEmail())
                .password(user.getUserPwHash())
                .roles("USER")
                .build();
    }
}
