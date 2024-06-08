package org.thws.management.partneruniversity;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;

import java.util.List;

@Configuration
public class PartnerUniversityConfig {

    @Bean
    CommandLineRunner commandLineRunner(PartnerUniversityRepository partnerUniversityRepository) {
        return args -> {
            PartnerUniversity thws = new PartnerUniversity(
                    1L,
                    "THWS",
                    "Germany",
                    "Department of Information Technology",
                    "web.site",
                    "Edin Putzu",
                    30,
                    30,
                    LocalDate.of(2000, Month.MARCH, 17),
                    LocalDate.of(2000, Month.MARCH, 17)
            );

            PartnerUniversity quiweh = new PartnerUniversity(
                    "quiweh",
                    "qwe",
                    "sfsdfe",
                    "iqzwe7iuq",
                    "uzqwte",
                    30,
                    30,
                    LocalDate.of(2000, Month.MARCH, 17),
                    LocalDate.of(2000, Month.MARCH, 17)
            );

            partnerUniversityRepository.saveAll(
                    List.of(thws, quiweh)
            );
        };
    }
}
