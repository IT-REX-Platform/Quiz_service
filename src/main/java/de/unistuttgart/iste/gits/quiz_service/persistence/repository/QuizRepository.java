package de.unistuttgart.iste.gits.quiz_service.persistence.repository;

import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {

}
