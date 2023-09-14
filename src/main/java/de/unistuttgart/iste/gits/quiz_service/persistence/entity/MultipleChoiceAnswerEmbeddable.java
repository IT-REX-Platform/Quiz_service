package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceAnswerEmbeddable {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @Column(nullable = false)
    @Builder.Default
    private boolean correct = false;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String feedback;
}
