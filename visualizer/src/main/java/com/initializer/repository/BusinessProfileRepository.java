package com.initializer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.initializer.entity.BusinessProfileEntity;

@Repository
public interface BusinessProfileRepository
        extends JpaRepository<BusinessProfileEntity, Integer> {

    BusinessProfileEntity findTopByOrderByProfileIDDesc();
}
