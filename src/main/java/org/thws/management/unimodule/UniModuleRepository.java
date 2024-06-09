package org.thws.management.unimodule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniModuleRepository extends JpaRepository<UniModule, Long> {
    Page<UniModule> findByPartnerUniversityId(Long partnerUniversityId, Pageable pageable);

    boolean existsByIdAndPartnerUniversityId(Long moduleId, Long partnerUniversityId);

    Page<UniModule> findByPartnerUniversityIdAndNameIgnoreCase(Long partnerUniversityId, String name, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndSemesterAndEcts(Long partnerUniversityId, Integer semester, Integer ects, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndSemester(Long partnerUniversityId, Integer semester, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndEcts(Long partnerUniversityId, Integer ects, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndNameIgnoreCaseAndSemester(Long partnerUniversityId, String name, Integer semester, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndNameIgnoreCaseAndEcts(Long partnerUniversityId, String name, Integer ects, Pageable pageable);

    Page<UniModule> findByPartnerUniversityIdAndNameIgnoreCaseAndSemesterAndEcts(Long partnerUniversityId, String name, Integer semester, Integer ects, Pageable pageable);

    Optional<UniModule> findByIdAndPartnerUniversityId(Long id, Long partnerUniversityId);
}
