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

import java.util.Optional;

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
     * @throws ResponseStatusException When UniModule with requested name already exists
     */
    public UniModule addNewUniModule(Long partnerUniversityId, UniModule uniModule) {
        PartnerUniversity partnerUniversity = partnerUniversityRepository.findById(partnerUniversityId).orElse(null);

        if (uniModuleRepository.findUniModuleByName(uniModule.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "UniModule already exists");
        }

        uniModule.setPartnerUniversity(partnerUniversity);
        return uniModuleRepository.save(uniModule);
    }

    /**
     * Fetches an UniModule by its ID
     *
     * @param uniModuleId ID of UniModule
     * @return UniModule of requested ID
     */
    public UniModule getUniModuleById(Long uniModuleId) {
        return uniModuleRepository.findById(uniModuleId).orElse(null);
    }

    /**
     * Retrieves a page containing UniModules, for requested PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to get UniModules from
     * @param pageable            Paging information
     * @return Every available UniModule divided into pages
     */
    public Page<UniModule> getAllUniModulesByPartnerUniversity(Long partnerUniversityId, Pageable pageable) {
        return uniModuleRepository.findByPartnerUniversityId(partnerUniversityId, pageable);
    }

    /**
     * Retrieve one specific UniModule in relation to a PartnerUniversity
     *
     * @param partnerUniversityId ID of PartnerUniversity to retrieve UniModule from
     * @param uniModuleId         ID of UniModule to retrieve
     * @return The requested UniModule
     */
    public UniModule getUniModuleByPartnerUniversity(Long partnerUniversityId, Long uniModuleId) {
        return uniModuleRepository.findByPartnerUniversityIdAndId(partnerUniversityId, uniModuleId).orElse(null);
    }

    /**
     * Updates one specific UniModule
     *
     * @param partnerUniversityId ID of PartnerUniversity that has the UniModule to update
     * @param uniModuleId         ID of UniModule to update
     * @param updateRequest       Requested changes to make to UniModule
     * @return The updated UniModule
     */
    @Transactional
    public UniModule updateUniModuleByPartnerUniversity(Long partnerUniversityId, Long uniModuleId, UniModule updateRequest) {
        Optional<UniModule> optionalModule = uniModuleRepository.findByPartnerUniversityIdAndId(partnerUniversityId, uniModuleId);
        UniModule uniModule = optionalModule.orElseThrow();

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
     * @param uniModuleId ID of UniModule to delete
     */
    public void deleteUniModuleByPartnerUniversity(Long uniModuleId) {
        uniModuleRepository.deleteById(uniModuleId);
    }
}
