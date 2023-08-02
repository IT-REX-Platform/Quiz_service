package de.unistuttgart.iste.gits.quiz_service.persistence.repository;

import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {

    /**
     * returns all Quiz Entities that contain one of the Assessment IDs provided in the input list
     *
     * @param assessmentIds list of assessment IDs
     * @return all Quiz ENtities matchin one of the provided Assessment IDs
     */
    List<QuizEntity> findQuizEntitiesByAssessmentIdIn(List<UUID> assessmentIds);

}
