package org.thws.management.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thws.management.server.model.UniModule;

import java.util.Optional;

/**
 * UniModule repository, to interact with the database and retrieve information
 */
@Repository
public interface UniModuleRepository extends JpaRepository<UniModule, Long> {
    Optional<UniModule> findUniModuleByName(String name);

    Optional<UniModule> findByPartnerUniversityIdAndId(Long partnerUniversityId, Long moduleId);

    Page<UniModule> findByPartnerUniversityId(Long partnerUniversityId, Pageable pageable);
}
