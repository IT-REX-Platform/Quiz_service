package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import de.unistuttgart.iste.gits.generated.dto.ClozeElementType;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClozeElementEmbeddable {

    @Enumerated(EnumType.ORDINAL)
    private ClozeElementType type;

    @Embedded
    @Builder.Default
    private ResourceMarkdownEmbeddable text = new ResourceMarkdownEmbeddable("");

    @Column(nullable = true)
    private String correctAnswer;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private ResourceMarkdownEntity feedback;
}
