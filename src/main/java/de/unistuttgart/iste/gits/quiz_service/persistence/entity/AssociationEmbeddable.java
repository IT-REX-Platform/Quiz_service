package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

    @Column(nullable = true, columnDefinition = "TEXT")
    private String feedback;

}
