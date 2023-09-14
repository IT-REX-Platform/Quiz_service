package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity(name = "NumericQuestion")
@ToString(callSuper = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NumericQuestionEntity extends QuestionEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private double correctAnswer;

    @Column(nullable = false)
    private double tolerance;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String feedback;
}
