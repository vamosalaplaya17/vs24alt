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

/**
 * Service class used for resetting the database to initial state
 */
@Service
public class DatabaseResetService {

    private final PartnerUniversityRepository partnerUniversityRepository;
    private final UniModuleRepository uniModuleRepository;
    private final PartnerUniversityConfig partnerUniversityConfig;
    private final UniModuleConfig uniModuleConfig;
    private final JdbcTemplate jdbcTemplate;

    //constructor
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

    /**
     * Method used for resetting the database
     *
     * @throws Exception when something goes wrong
     */
    @Transactional
    public void resetDatabase() throws Exception {
        deleteTables();
        resetSequences();
        reinitializeData();
    }

    /**
     * Deletes first the UniModule table, then the PartnerUniversity table
     */
    private void deleteTables() {
        jdbcTemplate.execute("DELETE FROM UNI_MODULE");
        jdbcTemplate.execute("DELETE FROM PARTNER_UNIVERSITY");
    }

    /**
     * Resets sequences, making IDs start from 1 again after resetting the database
     */
    private void resetSequences() {
        jdbcTemplate.execute("ALTER SEQUENCE UNIMODULE_SEQUENCE RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE PARTNER_UNIVERSITY_SEQUENCE RESTART WITH 1");
    }

    /**
     * Reinitializes database, using existing config classes
     *
     * @throws Exception when something goes wrong
     */
    private void reinitializeData() throws Exception {
        PartnerUniversity thws = partnerUniversityConfig.thws();
        PartnerUniversity otherUniversity = partnerUniversityConfig.otherUniversity();
        partnerUniversityRepository.saveAll(List.of(thws, otherUniversity));

        uniModuleConfig.uniModuleCommandLineRunner(partnerUniversityRepository, uniModuleRepository).run();
    }
}
