package org.thws.management.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thws.management.server.config.PartnerUniversityConfig;
import org.thws.management.server.config.UniModuleConfig;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.repository.PartnerUniversityRepository;
import org.thws.management.server.repository.UniModuleRepository;

import java.util.List;

@Service
public class DatabaseResetService {

    private final PartnerUniversityRepository partnerUniversityRepository;
    private final UniModuleRepository uniModuleRepository;
    private final PartnerUniversityConfig partnerUniversityConfig;
    private final UniModuleConfig uniModuleConfig;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseResetService(PartnerUniversityRepository partnerUniversityRepository,
                                UniModuleRepository uniModuleRepository,
                                PartnerUniversityConfig partnerUniversityConfig,
                                UniModuleConfig uniModuleConfig,
                                JdbcTemplate jdbcTemplate) {
        this.partnerUniversityRepository = partnerUniversityRepository;
        this.uniModuleRepository = uniModuleRepository;
        this.partnerUniversityConfig = partnerUniversityConfig;
        this.uniModuleConfig = uniModuleConfig;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void resetDatabase() throws Exception {
        clearAssociations();
        truncateTablesAndResetSequences();
        reinitializeData();
    }

    private void clearAssociations() {
        jdbcTemplate.execute("DELETE FROM UNI_MODULE");
        jdbcTemplate.execute("DELETE FROM PARTNER_UNIVERSITY");
    }

    private void truncateTablesAndResetSequences() {
        // Reset sequences for H2 database
        resetSequence("UNIMODULE_SEQUENCE");
        resetSequence("PARTNER_UNIVERSITY_SEQUENCE");
    }

    private void resetSequence(String sequenceName) {
        jdbcTemplate.execute("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1");
    }

    private void reinitializeData() throws Exception {
        // Save partner universities first
        PartnerUniversity thws = partnerUniversityConfig.thws();
        PartnerUniversity otherUniversity = partnerUniversityConfig.otherUniversity();
        partnerUniversityRepository.saveAll(List.of(thws, otherUniversity));

        // Reinitialize uni modules
        uniModuleConfig.uniModuleCommandLineRunner(partnerUniversityRepository, uniModuleRepository).run();
    }
}
