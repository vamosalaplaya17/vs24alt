package org.thws.management.partneruniversity;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerUniversityRepository extends JpaRepository<PartnerUniversity, Long> {
    Optional<PartnerUniversity> findPartnerUniversityByName(String name);
    Page<PartnerUniversity> findAll(Pageable pageable);
}
