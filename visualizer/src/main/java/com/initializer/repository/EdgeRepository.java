package com.initializer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.initializer.entity.EdgeEntity;

public interface EdgeRepository extends JpaRepository<EdgeEntity, Integer> {
}
