package org.thws.management.partneruniversity;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartnerUniversityService {
    private final PartnerUniversityRepository partnerUniversityRepository;

    @Autowired
    public PartnerUniversityService(PartnerUniversityRepository partnerUniversityRepository) {
        this.partnerUniversityRepository = partnerUniversityRepository;
    }

    public List<PartnerUniversity> getPartnerUniversities() {
        return partnerUniversityRepository.findAll();
    }

    public void addNewPartnerUniversity(PartnerUniversity partnerUniversity) {
        Optional<PartnerUniversity> partnerUniversityOptional = partnerUniversityRepository
                .findPartnerUniversityByName(partnerUniversity.getName());

        if (partnerUniversityOptional.isPresent()) {
            throw new IllegalStateException("university already exists");
        }

        partnerUniversityRepository.save(partnerUniversity);
    }

    public void deletePartnerUniversity(Long partneruniversityId) {
        boolean exists = partnerUniversityRepository.existsById(partneruniversityId);
        if (!exists) {
            throw new IllegalStateException("university with id " + partneruniversityId + " does not exist");
        }

        partnerUniversityRepository.deleteById(partneruniversityId);
    }

    @Transactional
    public void updatePartnerUniversity(Long partneruniversityId, PartnerUniversity updateRequest) {
        PartnerUniversity partnerUniversity = partnerUniversityRepository.findById(partneruniversityId)
                .orElseThrow(() -> new IllegalStateException(
                        "university with id " + partneruniversityId + " does not exist"
                ));

        if (updateRequest.getName() != null && !updateRequest.getName().isEmpty()) {
            partnerUniversity.setName(updateRequest.getName());
        }

        if (updateRequest.getCountry() != null && !updateRequest.getCountry().isEmpty()) {
            partnerUniversity.setCountry(updateRequest.getCountry());
        }

        if (updateRequest.getDepartmentName() != null && !updateRequest.getDepartmentName().isEmpty()) {
            partnerUniversity.setDepartmentName(updateRequest.getDepartmentName());
        }

        if (updateRequest.getDepartmentUrl() != null && !updateRequest.getDepartmentUrl().isEmpty()) {
            partnerUniversity.setDepartmentUrl(updateRequest.getDepartmentUrl());
        }

        if (updateRequest.getMaxStudentsIn() != null && updateRequest.getMaxStudentsIn() >= 0) {
            partnerUniversity.setMaxStudentsIn(updateRequest.getMaxStudentsIn());
        }

        if (updateRequest.getMaxStudentsOut() != null && updateRequest.getMaxStudentsOut() >= 0) {
            partnerUniversity.setMaxStudentsOut(updateRequest.getMaxStudentsOut());
        }

        if (updateRequest.getNextSpringSemester() != null) {
            partnerUniversity.setNextSpringSemester(updateRequest.getNextSpringSemester());
        }

        if (updateRequest.getNextSummerSemester() != null) {
            partnerUniversity.setNextSummerSemester(updateRequest.getNextSummerSemester());
        }

        partnerUniversityRepository.save(partnerUniversity);
    }

    public PartnerUniversity getPartnerUniversityById(Long partneruniversityId) {
        return partnerUniversityRepository.findById(partneruniversityId)
                .orElseThrow(() -> new IllegalStateException(
                        "university with id " + partneruniversityId + " does not exist"
                ));
    }

    public Page<PartnerUniversity> getAllPartnerUniversities(Pageable pageable) {
        return partnerUniversityRepository.findAll(pageable);
    }
}
