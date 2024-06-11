package org.thws.management.unimodule;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.thws.management.partneruniversity.PartnerUniversity;
import org.thws.management.partneruniversity.PartnerUniversityRepository;

import java.util.List;

/**
 * Class for initializing a couple UniModules at startup, so there is something to work with
 */
@Configuration
public class UniModuleConfig {

    /**
     * CommandLineRunner initializes standard UniModule data in the database
     *
     * @param partnerUniversityRepository Repository of the PartnerUniversities
     * @param uniModuleRepository Repository of the UniModules
     * @return Initialized UniModule data
     */
    @Bean
    @Order(2)
    @Transactional
    public CommandLineRunner uniModuleCommandLineRunner(PartnerUniversityRepository partnerUniversityRepository, UniModuleRepository uniModuleRepository) {
        return args -> {
            PartnerUniversity thws = partnerUniversityRepository.findById(1L).orElse(null);
            PartnerUniversity otherUniversity = partnerUniversityRepository.findById(2L).orElse(null);

            if (thws != null && otherUniversity != null) {
                UniModule quantumComputing = new UniModule(
                        "Module 1",
                        1,
                        6,
                        thws
                );

                UniModule machineLearning = new UniModule(
                        "Module 2",
                        2,
                        6,
                        thws
                );

                UniModule databaseSystems = new UniModule(
                        "Module 3",
                        1,
                        5,
                        otherUniversity
                );

                uniModuleRepository.saveAll(List.of(quantumComputing, machineLearning, databaseSystems));
            }
        };
    }
}
