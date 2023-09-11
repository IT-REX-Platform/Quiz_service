package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import de.unistuttgart.iste.gits.generated.dto.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Entity(name = "Question")
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private QuestionType type;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private ResourceMarkdownEntity hint;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    @Builder.Default
    private List<QuestionStatisticEntity> questionStatistics = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionEntity that = (QuestionEntity) o;
        if (getNumber() != that.getNumber()) return false;
        if (!Objects.equals(getId(), that.getId())) return false;
        if (getType() != that.getType()) return false;
        if (!(Objects.equals(getHint(), that.getHint()))) return false;

        // list equals does not work for some reason
        if (questionStatistics.size() != that.questionStatistics.size()) return false;
        for (int i = 0; i < questionStatistics.size(); i++) {
            if (!questionStatistics.get(i).equals(that.questionStatistics.get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNumber(), getType(), getHint(), getQuestionStatistics());
    }
}
