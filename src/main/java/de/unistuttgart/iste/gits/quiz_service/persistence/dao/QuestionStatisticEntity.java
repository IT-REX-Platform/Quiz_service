package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "QuestionStatistic")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStatisticEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "question_id")
    private UUID questionId;

    @Column
    private UUID userId;

    @Column
    private boolean answeredCorrectly;
}
