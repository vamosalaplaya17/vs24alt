package org.thws.management.partneruniversity;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

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
                LocalDate.of(2000, Month.MARCH, 17),
                LocalDate.of(2000, Month.MARCH, 17)
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
                LocalDate.of(1987, Month.JUNE, 5),
                LocalDate.of(1789, Month.JUNE, 5)
        );
    }

    @Bean
    @Order(1)
    @Transactional
    public CommandLineRunner commandLineRunner(PartnerUniversityRepository partnerUniversityRepository) {
        return args -> partnerUniversityRepository.saveAll(List.of(thws(), otherUniversity()));
    }
}
