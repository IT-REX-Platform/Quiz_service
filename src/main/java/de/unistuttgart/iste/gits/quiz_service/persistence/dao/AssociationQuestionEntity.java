package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "AssociationQuestion")
@ToString(callSuper = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AssociationQuestionEntity extends QuestionEntity {

    @Embedded
    @Builder.Default
    private ResourceMarkdownEmbeddable text = new ResourceMarkdownEmbeddable("");

    @ElementCollection
    @Builder.Default
    private List<AssociationEmbeddable> correctAssociations = new ArrayList<>();

}
