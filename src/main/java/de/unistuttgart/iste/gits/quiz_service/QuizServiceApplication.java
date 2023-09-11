package de.unistuttgart.iste.gits.quiz_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * This is the entry point of the application.
 */
@SpringBootApplication
@EntityScan({"de.unistuttgart.iste.gits.common.resource_markdown", "de.unistuttgart.iste.gits.quiz_service.persistence.entity"})
public class QuizServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizServiceApplication.class, args);
    }

}
