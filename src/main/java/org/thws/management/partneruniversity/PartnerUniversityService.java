package org.thws.management.partneruniversity;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PartnerUniversityService {
    private final PartnerUniversityRepository partnerUniversityRepository;

    @Autowired
    public PartnerUniversityService(PartnerUniversityRepository partnerUniversityRepository) {
        this.partnerUniversityRepository = partnerUniversityRepository;
    }

    public PartnerUniversity addNewPartnerUniversity(PartnerUniversity partnerUniversity) {
        Optional<PartnerUniversity> partnerUniversityOptional = partnerUniversityRepository
                .findPartnerUniversityByName(partnerUniversity.getName());

        if (partnerUniversityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.SEE_OTHER,
                    "university already exists");
        }

        return partnerUniversityRepository.save(partnerUniversity);
    }

    public void deletePartnerUniversity(Long partnerUniversityId) {
        boolean exists = partnerUniversityRepository.existsById(partnerUniversityId);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "university with id " + partnerUniversityId + " does not exist");
        }

        partnerUniversityRepository.deleteById(partnerUniversityId);
    }

    @Transactional
    public PartnerUniversity updatePartnerUniversity(Long partnerUniversityId, PartnerUniversity updateRequest) {
        PartnerUniversity partnerUniversity = partnerUniversityRepository.findById(partnerUniversityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "university with id " + partnerUniversityId + " does not exist"
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

        return partnerUniversityRepository.save(partnerUniversity);
    }

    public PartnerUniversity getPartnerUniversityById(Long partnerUniversityId) {
        return partnerUniversityRepository.findById(partnerUniversityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "university with id " + partnerUniversityId + " does not exist"
                ));
    }

    public Page<PartnerUniversity> getAllPartnerUniversities(Pageable pageable) {
        return partnerUniversityRepository.findAll(pageable);
    }

    public Page<PartnerUniversity> getPartnerUniversitiesByName(String name, Pageable pageable) {
        return partnerUniversityRepository.findByNameIgnoreCase(name, pageable);
    }

    public Page<PartnerUniversity> getPartnerUniversitiesByCountry(String country, Pageable pageable) {
        return partnerUniversityRepository.findByCountryIgnoreCase(country, pageable);
    }

    public Page<PartnerUniversity> getPartnerUniversitiesByDepartmentName(String departmentName, Pageable pageable) {
        return partnerUniversityRepository.findByDepartmentNameIgnoreCase(departmentName, pageable);
    }

    public Page<PartnerUniversity> getPartnerUniversitiesByNameAndCountryAndDepartmentName(
            String name, String country, String departmentName, Pageable pageable) {
        return partnerUniversityRepository.findByNameAndCountryAndDepartmentNameAllIgnoreCase(name, country, departmentName, pageable);
    }

    public Page<PartnerUniversity> getPartnerUniversitiesByNameAndCountry(
            String name, String country, Pageable pageable) {
        return partnerUniversityRepository.findByNameAndCountryAllIgnoreCase(name, country, pageable);
    }

    public Page<PartnerUniversity> getPartnerUniversitiesByNameAndDepartmentName(
            String name, String departmentName, Pageable pageable) {
        return partnerUniversityRepository.findByNameAndDepartmentNameAllIgnoreCase(name, departmentName, pageable);
    }

    public Page<PartnerUniversity> getPartnerUniversitiesByCountryAndDepartmentName(
            String country, String departmentName, Pageable pageable) {
        return partnerUniversityRepository.findByCountryAndDepartmentNameAllIgnoreCase(country, departmentName, pageable);
    }
}
