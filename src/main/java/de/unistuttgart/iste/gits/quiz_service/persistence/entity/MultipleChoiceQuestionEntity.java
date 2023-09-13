package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import de.unistuttgart.iste.gits.generated.dto.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Entity(name = "MultipleChoiceQuestion")
@ToString(callSuper = true)
@Getter
@Setter
@SuperBuilder
public class MultipleChoiceQuestionEntity extends QuestionEntity {

    public MultipleChoiceQuestionEntity() {
        super();
        this.setType(QuestionType.MULTIPLE_CHOICE);
    }

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @ElementCollection
    @Builder.Default
    private List<MultipleChoiceAnswerEmbeddable> answers = new ArrayList<>();

    public int getNumberOfCorrectAnswers() {
        return (int) answers.stream()
                .filter(MultipleChoiceAnswerEmbeddable::isCorrect)
                .count();
    }

    // custom equals method because the answers list is not compared correctly

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultipleChoiceQuestionEntity that)) return false;
        if (!super.equals(o)) return false;
        if (!Objects.equals(text, that.text)) return false;

        // list equals does not work for some reason
        if (answers.size() != that.answers.size()) return false;
        for (int i = 0; i < answers.size(); i++) {
            if (!answers.get(i).equals(that.answers.get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, answers);
    }
}
