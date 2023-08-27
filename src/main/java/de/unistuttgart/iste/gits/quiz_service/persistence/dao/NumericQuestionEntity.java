package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import jakarta.persistence.*;
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

    @Embedded
    @Builder.Default
    private ResourceMarkdownEmbeddable text = new ResourceMarkdownEmbeddable("");

    @Column(nullable = false)
    private double correctAnswer;

    @Column(nullable = false)
    private double tolerance;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private ResourceMarkdownEntity feedback;
}
