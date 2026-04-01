package com.initializer.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.initializer.entity.MitigationEntity;
import com.initializer.repository.MitigationRepository;

@Service
public class MitigationHelperService {

    private final MitigationRepository mitigationRepository;

    public MitigationHelperService(MitigationRepository mitigationRepository) {
        this.mitigationRepository = mitigationRepository;
    }

    public List<String> getMitigationNamesByIds(List<Integer> mitigationIds) {

        List<String> mitigationNames = new ArrayList<>();

        if (mitigationIds == null || mitigationIds.isEmpty()) {
            return mitigationNames;
        }

        List<MitigationEntity> mitigations =
                mitigationRepository.findAllById(mitigationIds);

        for (MitigationEntity mitigation : mitigations) {
            mitigationNames.add(mitigation.getMitName());
        }

        return mitigationNames;
    }
}
