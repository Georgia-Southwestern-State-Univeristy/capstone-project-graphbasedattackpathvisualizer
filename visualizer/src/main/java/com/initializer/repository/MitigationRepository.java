package com.initializer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.initializer.entity.MitigationEntity;

public interface MitigationRepository extends JpaRepository<MitigationEntity, Integer> {
}
