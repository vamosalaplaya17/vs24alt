package org.thws.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Automatically generated class to start the system without using Docker, under port 8080
 */
@SpringBootApplication(scanBasePackages = "org.thws.management")
@EnableJpaRepositories
public class ManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagementApplication.class, args);
    }
}
