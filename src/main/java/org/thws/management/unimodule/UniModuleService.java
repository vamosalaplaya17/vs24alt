package org.thws.management.unimodule;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thws.management.partneruniversity.PartnerUniversity;
import org.thws.management.partneruniversity.PartnerUniversityRepository;

@Service
public class UniModuleService {
    private final PartnerUniversityRepository partnerUniversityRepository;
    private final UniModuleRepository uniModuleRepository;

    @Autowired
    public UniModuleService(PartnerUniversityRepository partnerUniversityRepository, UniModuleRepository uniModuleRepository) {
        this.partnerUniversityRepository = partnerUniversityRepository;
        this.uniModuleRepository = uniModuleRepository;
    }

    public UniModule addNewUniModule(Long partnerUniversityId, UniModule uniModule) {
        PartnerUniversity partnerUniversity = partnerUniversityRepository.findById(partnerUniversityId)
                .orElseThrow(() -> new IllegalStateException(
                        "university with id " + partnerUniversityId + " does not exist"
                ));

        uniModule.setPartnerUniversity(partnerUniversity);
        return uniModuleRepository.save(uniModule);
    }

    public void deleteUniModuleByPartnerUniversity(Long partnerUniversityId, Long uniModuleId) {
        if (!uniModuleRepository.existsByIdAndPartnerUniversityId(uniModuleId, partnerUniversityId)) {
            throw new IllegalStateException("module with id " + uniModuleId + " does not exist in university with id " + partnerUniversityId);
        }

        uniModuleRepository.deleteById(uniModuleId);
    }

    @Transactional
    public UniModule updateUniModuleByPartnerUniversity(Long partnerUniversityId, Long uniModuleId, UniModule updateRequest) {
        UniModule uniModule = uniModuleRepository.findByIdAndPartnerUniversityId(uniModuleId, partnerUniversityId)
                .orElseThrow(() -> new IllegalStateException(
                        "module with id " + uniModuleId + " does not exist in university with id " + partnerUniversityId
                ));

        if (updateRequest.getName() != null && !updateRequest.getName().isEmpty()) {
            uniModule.setName(updateRequest.getName());
        }

        if (updateRequest.getSemester() != null) {
            uniModule.setSemester(updateRequest.getSemester());
        }

        if (updateRequest.getEcts() != null) {
            uniModule.setEcts(updateRequest.getEcts());
        }

        return uniModuleRepository.save(uniModule);
    }

    public UniModule getUniModuleById(Long uniModuleId) {
        return uniModuleRepository.findById(uniModuleId)
                .orElseThrow(() -> new IllegalStateException(
                        "module with id " + uniModuleId + " does not exist"
                ));
    }

    public Page<UniModule> getUniModulesByNameAndSemesterAndEcts(
            Long partnerUniversityId, String name, Integer semester, Integer ects, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityIdAndNameIgnoreCaseAndSemesterAndEcts(
                partnerUniversityId, name, semester, ects, pageable);
    }

    public Page<UniModule> getUniModulesByNameAndSemester(
            Long partnerUniversityId, String name, Integer semester, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityIdAndNameIgnoreCaseAndSemester(
                partnerUniversityId, name, semester, pageable);
    }

    public Page<UniModule> getUniModulesByNameAndEcts(
            Long partnerUniversityId, String name, Integer ects, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityIdAndNameIgnoreCaseAndEcts(
                partnerUniversityId, name, ects, pageable);
    }

    public Page<UniModule> getUniModulesBySemesterAndEcts(
            Long partnerUniversityId, Integer semester, Integer ects, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityIdAndSemesterAndEcts(
                partnerUniversityId, semester, ects, pageable);
    }

    public Page<UniModule> getUniModulesByName(
            Long partnerUniversityId, String name, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityIdAndNameIgnoreCase(
                partnerUniversityId, name, pageable);
    }

    public Page<UniModule> getUniModulesBySemester(
            Long partnerUniversityId, Integer semester, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityIdAndSemester(
                partnerUniversityId, semester, pageable);
    }

    public Page<UniModule> getUniModulesByEcts(
            Long partnerUniversityId, Integer ects, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityIdAndEcts(
                partnerUniversityId, ects, pageable);
    }

    public Page<UniModule> getAllUniModulesByPartnerUniversity(Long partnerUniversityId, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityId(partnerUniversityId, pageable);
    }

    public UniModule getUniModuleByPartnerUniversity(Long partnerUniversityId, Long uniModuleId) {
        return uniModuleRepository.findByIdAndPartnerUniversityId(uniModuleId, partnerUniversityId)
                .orElseThrow(() -> new IllegalStateException(
                        "module with id " + uniModuleId + " does not exist in university with id " + partnerUniversityId
                ));
    }
}
