package com.initializer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.initializer.entity.MitigationEffectEntity;

public interface MitigationEffectRepository extends JpaRepository<MitigationEffectEntity, Integer> {
}
