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

    boolean existsByPartnerUniversityIdAndId(Long partnerUniversityId, Long moduleId);

    //various filtering methods, by partnerUniversityId and any combination of name, semester, and ects
    Page<UniModule> findByPartnerUniversityId(Long partnerUniversityId, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndNameIgnoreCaseAndSemesterAndEcts(Long partnerUniversityId, String name, Integer semester, Integer ects, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndNameIgnoreCaseAndSemester(Long partnerUniversityId, String name, Integer semester, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndNameIgnoreCaseAndEcts(Long partnerUniversityId, String name, Integer ects, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndSemesterAndEcts(Long partnerUniversityId, Integer semester, Integer ects, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndNameIgnoreCase(Long partnerUniversityId, String name, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndSemester(Long partnerUniversityId, Integer semester, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndEcts(Long partnerUniversityId, Integer ects, Pageable pageable);


}
