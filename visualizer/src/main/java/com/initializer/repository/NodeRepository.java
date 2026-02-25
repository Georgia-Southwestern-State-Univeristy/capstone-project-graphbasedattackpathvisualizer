package com.initializer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.initializer.entity.NodeEntity;

public interface NodeRepository extends JpaRepository<NodeEntity, Integer> {
}
