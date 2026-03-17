package com.initializer.repository;

import com.initializer.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUserEmail(String userEmail);

    boolean existsByUserEmail(String userEmail);
}
