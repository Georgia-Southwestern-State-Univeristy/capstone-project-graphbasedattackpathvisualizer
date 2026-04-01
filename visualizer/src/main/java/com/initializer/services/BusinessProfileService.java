package com.initializer.services;

import org.springframework.stereotype.Service;

import com.initializer.entity.BusinessProfileEntity;
import com.initializer.entity.UserEntity;
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

    public BusinessProfileEntity getProfileByUser(UserEntity user) {
        return businessProfileRepository.findByUser(user);
    }

    // Converts a BusinessProfileEntity into a BusinessProfileDTO
    public BusinessProfileDTO toDTO(BusinessProfileEntity profile) {
        return new BusinessProfileDTO(
                profile.getProfileID(),
                profile.isUsesVPN(),
                profile.isHasFileServer(),
                profile.isUsesSaaS(),
                profile.isHasPublicWebApp(),
                profile.isUsesIdentityProvider(),
                profile.isHasEmailServer(),
                profile.isHasDomainController(),
                profile.isHasInternalApp(),
                profile.isHasHRSystem(),
                profile.isHasFinanceSystem(),
                profile.isHasBackupServer(),
                profile.isHasMDMServer(),
                profile.isHasWirelessAccessPoint(),
                profile.isHasFirewall(),
                profile.isHasDNSServer()
        );
    }
}
