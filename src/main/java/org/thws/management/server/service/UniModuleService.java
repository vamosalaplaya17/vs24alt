package org.thws.management.server.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.model.UniModule;
import org.thws.management.server.repository.PartnerUniversityRepository;
import org.thws.management.server.repository.UniModuleRepository;

/**
 * Service class for managing UniModules in relation to PartnerUniversities
 */
@Service
public class UniModuleService {
    private final PartnerUniversityRepository partnerUniversityRepository;
    private final UniModuleRepository uniModuleRepository;

    /**
     * Constructs a new UniModuleService
     *
     * @param partnerUniversityRepository Repository of PartnerUniversity entities
     * @param uniModuleRepository         Repository of UniModule entities
     */
    @Autowired
    public UniModuleService(PartnerUniversityRepository partnerUniversityRepository, UniModuleRepository uniModuleRepository) {
        this.partnerUniversityRepository = partnerUniversityRepository;
        this.uniModuleRepository = uniModuleRepository;
    }

    /**
     * Adds a new UniModule to an existing PartnerUniversity
     *
     * @param partnerUniversityId ID of the PartnerUniversity to add the UniModule to
     * @param uniModule           UniModule to be added to PartnerUniversity under given ID
     * @return The added UniModule
     * @throws ResponseStatusException If a UniModule with the given name already exists
     */
    public UniModule addNewUniModule(Long partnerUniversityId, UniModule uniModule) {
        PartnerUniversity partnerUniversity = getPartnerUniversity(partnerUniversityId);

        if (uniModuleRepository.findUniModuleByName(uniModule.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "UniModule already exists");
        }

        uniModule.setPartnerUniversity(partnerUniversity);
        return uniModuleRepository.save(uniModule);
    }

    /**
     * Retrieves a page containing UniModules, for requested PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to get UniModules from
     * @param pageable            Paging information
     * @return Every available UniModule divided into pages
     */
    public Page<UniModule> getAllUniModulesByPartnerUniversity(Long partnerUniversityId, Pageable pageable) {
        //checks if PartnerUniversity exists
        checkIfUniversityExists(partnerUniversityId);

        return uniModuleRepository.findByPartnerUniversityId(partnerUniversityId, pageable);
    }

    /**
     * Retrieves page(s) containing filtered UniModules, filtered by any combination of name, semester, and ects,
     * for requested PartnerUniversity
     *
     * @param partnerUniversityId ID of requested PartnerUniversity
     * @param name                Name of UniModule to be filtered by
     * @param semester            Semester of UniModule to be filtered by
     * @param ects                Credits of UniModule to be filtered by
     * @param pageable            Paging information
     * @return Every available UniModule divided into pages, affected by filters
     */
    public Page<UniModule> getAllUniModulesByPartnerUniversityWithFilters(
            Long partnerUniversityId, String name, Integer semester, Integer ects, Pageable pageable) {
        //checks if PartnerUniversity exists
        checkIfUniversityExists(partnerUniversityId);

        if (name != null && semester != null && ects != null) {
            return uniModuleRepository.findByPartnerUniversityIdAndNameIgnoreCaseAndSemesterAndEcts(partnerUniversityId,
                    name.toLowerCase(), semester, ects, pageable);
        } else if (name != null && semester != null) {
            return uniModuleRepository.findByPartnerUniversityIdAndNameIgnoreCaseAndSemester(partnerUniversityId,
                    name.toLowerCase(), semester, pageable);
        } else if (name != null && ects != null) {
            return uniModuleRepository.findByPartnerUniversityIdAndNameIgnoreCaseAndEcts(partnerUniversityId, name,
                    ects, pageable);
        } else if (semester != null && ects != null) {
            return uniModuleRepository.findByPartnerUniversityIdAndSemesterAndEcts(partnerUniversityId, semester, ects, pageable);
        } else if (name != null) {
            return uniModuleRepository.findByPartnerUniversityIdAndNameIgnoreCase(partnerUniversityId, name.toLowerCase(), pageable);
        } else if (semester != null) {
            return uniModuleRepository.findByPartnerUniversityIdAndSemester(partnerUniversityId, semester, pageable);
        } else if (ects != null) {
            return uniModuleRepository.findByPartnerUniversityIdAndEcts(partnerUniversityId, ects, pageable);
        } else {
            return Page.empty();
        }
    }

    /**
     * Retrieve one specific UniModule in relation to a PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to remove UniModule from
     * @param uniModuleId         ID of UniModule to retrieve
     * @return The requested UniModule
     * @throws ResponseStatusException If the UniModule ID does not exist in the requested PartnerUniversity
     */
    public UniModule getUniModuleByPartnerUniversity(Long partnerUniversityId, Long uniModuleId) {
        //checks if PartnerUniversity exists
        checkIfUniversityExists(partnerUniversityId);
        return uniModuleRepository.findByPartnerUniversityIdAndId(partnerUniversityId, uniModuleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "module with id " + uniModuleId + " does not exist in university with id " + partnerUniversityId
                ));
    }

    /**
     * Updates one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity that has the UniModule to update
     * @param uniModuleId         ID of UniModule to update
     * @param updateRequest       Requested changes to make to UniModule
     * @return The updated UniModule
     * @throws ResponseStatusException If the UniModule ID does not exist in the requested PartnerUniversity
     */
    @Transactional
    public UniModule updateUniModuleByPartnerUniversity(Long partnerUniversityId, Long uniModuleId, UniModule updateRequest) {
        checkIfUniversityExists(partnerUniversityId);
        UniModule uniModule = uniModuleRepository.findByPartnerUniversityIdAndId(partnerUniversityId, uniModuleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
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

    /**
     * Deletes one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity to delete UniModule from
     * @param uniModuleId         ID of UniModule to delete
     * @throws ResponseStatusException If UniModule under given ID does not exist in given PartnerUniversity
     */
    public void deleteUniModuleByPartnerUniversity(Long partnerUniversityId, Long uniModuleId) {
        checkIfUniversityExists(partnerUniversityId);
        if (!uniModuleRepository.existsByPartnerUniversityIdAndId(partnerUniversityId, uniModuleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "module with id " + uniModuleId + " does not exist in university with id " + partnerUniversityId);
        }

        uniModuleRepository.deleteById(uniModuleId);
    }

    /**
     * Checks if requested PartnerUniversity exists
     *
     * @param partnerUniversityId ID of requested PartnerUniversity
     * @throws ResponseStatusException If PartnerUniversity under ID partnerUniversityID does not exist
     */
    public void checkIfUniversityExists(Long partnerUniversityId) {
        if (partnerUniversityRepository.findById(partnerUniversityId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "university with id " + partnerUniversityId + " does not exist");
        }
    }

    /**
     * Retrieves a PartnerUniversity by requested ID
     *
     * @param partnerUniversityId ID of requested PartnerUniversity
     * @return The requested PartnerUniversity
     * @throws ResponseStatusException If requested PartnerUniversity does not exist
     */
    private PartnerUniversity getPartnerUniversity(Long partnerUniversityId) {
        return partnerUniversityRepository.findById(partnerUniversityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "university with id " + partnerUniversityId + " does not exist"
                ));
    }
}
