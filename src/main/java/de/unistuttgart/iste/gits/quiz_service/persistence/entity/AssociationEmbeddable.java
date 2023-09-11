package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssociationEmbeddable {

    @Column(nullable = false, length = 255, name = "left_side")
    private String left;

    @Column(nullable = false, length = 255, name = "right_side")
    private String right;

    @Embedded
    @Builder.Default
    private ResourceMarkdownEmbeddable feedback = new ResourceMarkdownEmbeddable("");

}
