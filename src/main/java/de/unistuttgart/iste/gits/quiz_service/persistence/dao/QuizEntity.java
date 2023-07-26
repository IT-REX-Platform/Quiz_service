package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import de.unistuttgart.iste.gits.generated.dto.QuestionPoolingMode;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "Quiz")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizEntity {

    @Id
    private UUID assessmentId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("number ASC")
    private List<QuestionEntity> questionPool;

    @Column(nullable = false)
    private int requiredCorrectAnswers;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private QuestionPoolingMode questionPoolingMode = QuestionPoolingMode.ORDERED;

    @Column(nullable = true)
    @Builder.Default
    private Integer numberOfRandomlySelectedQuestions = null;
}
