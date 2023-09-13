package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity(name = "SelfAssessmentQuestion")
@ToString(callSuper = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SelfAssessmentQuestionEntity extends QuestionEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String solutionSuggestion;
}
