package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import jakarta.persistence.*;
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

    @Embedded
    @Builder.Default
    private ResourceMarkdownEmbeddable text = new ResourceMarkdownEmbeddable("");

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @Builder.Default
    private ResourceMarkdownEntity solutionSuggestion = new ResourceMarkdownEntity("");
}
