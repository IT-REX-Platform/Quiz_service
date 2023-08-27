package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
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

    @Embedded
    @Builder.Default
    private ResourceMarkdownEmbeddable text = new ResourceMarkdownEmbeddable("");

    @ElementCollection
    private List<String> correctAnswers;

    @Column(nullable = false)
    private boolean caseSensitive;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private ResourceMarkdownEntity feedback;
}
