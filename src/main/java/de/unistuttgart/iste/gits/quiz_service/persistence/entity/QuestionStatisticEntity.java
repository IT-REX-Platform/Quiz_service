package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

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
