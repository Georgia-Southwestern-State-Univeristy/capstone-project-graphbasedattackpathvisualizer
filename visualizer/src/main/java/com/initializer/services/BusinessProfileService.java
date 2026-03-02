package com.initializer.services;

import org.springframework.stereotype.Service;

import com.initializer.entity.BusinessProfileEntity;
import com.initializer.repository.BusinessProfileRepository;

@Service
public class BusinessProfileService {

    private final BusinessProfileRepository businessProfileRepository;

    public BusinessProfileService(BusinessProfileRepository businessProfileRepository) {
        this.businessProfileRepository = businessProfileRepository;
    }

    public BusinessProfileEntity saveProfile(BusinessProfileEntity profile) {
        return businessProfileRepository.save(profile);
    }

    public BusinessProfileEntity getLatestProfile() {
        return businessProfileRepository.findTopByOrderByProfileIDDesc();
    }
}
