package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceAnswerEmbeddable {

    @Column(length = 1000, nullable = false)
    @Builder.Default
    private String text = "";

    @Column(nullable = false)
    @Builder.Default
    private boolean correct = false;

    @Column(length = 1000, nullable = true)
    private String feedback;
}
