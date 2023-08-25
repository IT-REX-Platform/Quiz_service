package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceAnswerEmbeddable {

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private ResourceMarkdownEntity answerText;

    @Column(nullable = false)
    @Builder.Default
    private boolean correct = false;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private ResourceMarkdownEntity feedback;
}
