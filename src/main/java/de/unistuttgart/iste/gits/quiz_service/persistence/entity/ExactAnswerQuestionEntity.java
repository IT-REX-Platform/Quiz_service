package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity(name = "ExactAnswerQuestion")
@ToString(callSuper = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ExactAnswerQuestionEntity extends QuestionEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @ElementCollection
    private List<String> correctAnswers;

    @Column(nullable = false)
    private boolean caseSensitive;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String feedback;
}
