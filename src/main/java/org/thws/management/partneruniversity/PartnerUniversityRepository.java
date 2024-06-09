package org.thws.management.partneruniversity;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerUniversityRepository extends JpaRepository<PartnerUniversity, Long>, PagingAndSortingRepository<PartnerUniversity, Long> {
    Optional<PartnerUniversity> findPartnerUniversityByName(String name);

    @NonNull
    Page<PartnerUniversity> findAll(@NonNull Pageable pageable);

    Page<PartnerUniversity> findByNameIgnoreCase(String name, Pageable pageable);

    Page<PartnerUniversity> findByCountryIgnoreCase(String country, Pageable pageable);

    Page<PartnerUniversity> findByDepartmentNameIgnoreCase(String departmentName, Pageable pageable);

    Page<PartnerUniversity> findByNameAndCountryAndDepartmentNameAllIgnoreCase(String name, String country, String departmentName, Pageable pageable);

    Page<PartnerUniversity> findByNameAndCountryAllIgnoreCase(String name, String country, Pageable pageable);

    Page<PartnerUniversity> findByNameAndDepartmentNameAllIgnoreCase(String name, String departmentName, Pageable pageable);

    Page<PartnerUniversity> findByCountryAndDepartmentNameAllIgnoreCase(String country, String departmentName, Pageable pageable);
}
