package org.thws.management.server.config;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.thws.management.server.model.PartnerUniversity;
import org.thws.management.server.repository.PartnerUniversityRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

/**
 * Class for initializing a PartnerUniversities at startup, so there is something to work with
 */
@Configuration
public class PartnerUniversityConfig {
    @Bean
    public PartnerUniversity thws() {
        return new PartnerUniversity(
                "THWS",
                "Germany",
                "Department Name 1",
                "web@site.de",
                "Edin Putzu",
                30,
                30,
                LocalDate.of(2000, 3, 17),
                LocalDate.of(2000, 3, 17)
        );
    }

    @Bean
    public PartnerUniversity otherUniversity() {
        return new PartnerUniversity(
                "Other University",
                "Italy",
                "Department Name 2",
                "department@url.it",
                "Zlatan Ibrahimovic",
                45,
                45,
                LocalDate.of(1987, 5, 5),
                LocalDate.of(1789, 5, 5)
        );
    }

    /**
     * CommandLineRunner initializes standard PartnerUniversity data in the database
     *
     * @param partnerUniversityRepository Repository of the PartnerUniversities
     * @return Initialized PartnerUniversity data
     */
    @Bean
    @Order(1)
    @Transactional
    public CommandLineRunner commandLineRunner(PartnerUniversityRepository partnerUniversityRepository) {
        return args -> partnerUniversityRepository.saveAll(List.of(thws(), otherUniversity()));
    }
}
