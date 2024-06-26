package org.thws.management.server.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.repository.PartnerUniversityRepository;

/**
 * Service class for managing PartnerUniversities
 */
@Service
public class PartnerUniversityService {
    private final PartnerUniversityRepository partnerUniversityRepository;

    /**
     * Constructs a PartnerUniversityService
     *
     * @param partnerUniversityRepository Repository of PartnerUniversity entities
     */
    @Autowired
    public PartnerUniversityService(PartnerUniversityRepository partnerUniversityRepository) {
        this.partnerUniversityRepository = partnerUniversityRepository;
    }

    /**
     * Creates a new PartnerUniversity
     *
     * @param partnerUniversity PartnerUniversity to be created
     * @return The created PartnerUniversity
     * @throws ResponseStatusException When PartnerUniversity with requested name already exists
     */
    public PartnerUniversity addNewPartnerUniversity(PartnerUniversity partnerUniversity) {
        if (partnerUniversityRepository.findPartnerUniversityByName(partnerUniversity.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Partner university already exists");
        }

        return partnerUniversityRepository.save(partnerUniversity);
    }

    /**
     * Retrieves all available PartnerUniversities, divided into pages
     *
     * @param pageable Paging information
     * @return Page of PartnerUniversities
     */
    public Page<PartnerUniversity> getAllPartnerUniversities(Pageable pageable) {
        return partnerUniversityRepository.findAll(pageable);
    }

    /**
     * Retrieves all available PartnerUniversities, divided into pages
     * With optional filtering by name, country, and departmentName
     *
     * @param name           Name of PartnerUniversity to filter by
     * @param country        Country of PartnerUniversity to filter by
     * @param departmentName Department name of PartnerUniversity to filter by
     * @param pageable       Paging information
     * @return A page of PartnerUniversity with the applied filters. Returns an empty page if nothing is found
     */
    public Page<PartnerUniversity> getAllPartnerUniversitiesWithFilters(String name, String country, String departmentName, Pageable pageable) {
        if (name != null && country != null && departmentName != null) {
            return partnerUniversityRepository.findByNameAndCountryAndDepartmentNameAllIgnoreCase(
                    name.toLowerCase(), country.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (name != null && country != null) {
            return partnerUniversityRepository.findByNameAndCountryAllIgnoreCase(
                    name.toLowerCase(), country.toLowerCase(), pageable);
        } else if (name != null && departmentName != null) {
            return partnerUniversityRepository.findByNameAndDepartmentNameAllIgnoreCase(
                    name.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (country != null && departmentName != null) {
            return partnerUniversityRepository.findByCountryAndDepartmentNameAllIgnoreCase(
                    country.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (name != null) {
            return partnerUniversityRepository.findByNameIgnoreCase(
                    name.toLowerCase(), pageable);
        } else if (country != null) {
            return partnerUniversityRepository.findByCountryIgnoreCase(
                    country.toLowerCase(), pageable);
        } else if (departmentName != null) {
            return partnerUniversityRepository.findByDepartmentNameIgnoreCase(
                    departmentName.toLowerCase(), pageable);
        } else {
            return Page.empty();
        }
    }

    /**
     * Retrieves one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to be retrieved
     * @return The requested PartnerUniversity
     */
    public PartnerUniversity getPartnerUniversityById(Long partnerUniversityId) {
        return partnerUniversityRepository.findById(partnerUniversityId).orElse(null);
    }

    /**
     * Updates one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to be updated
     * @param updateRequest       Contains the content for the PartnerUniversity be updated with
     * @return The updated PartnerUniversity
     */
    @Transactional
    public PartnerUniversity updatePartnerUniversity(Long partnerUniversityId, PartnerUniversity updateRequest) {
        PartnerUniversity partnerUniversity = partnerUniversityRepository.findById(partnerUniversityId).orElse(null);

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

    /**
     * Deletes one specific PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to be deleted
     */
    public void deletePartnerUniversity(Long partnerUniversityId) {
        partnerUniversityRepository.deleteById(partnerUniversityId);
    }
}
