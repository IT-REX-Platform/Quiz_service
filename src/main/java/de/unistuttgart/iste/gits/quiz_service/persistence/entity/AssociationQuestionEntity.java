package de.unistuttgart.iste.gits.quiz_service.persistence.entity;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @ElementCollection
    @Builder.Default
    private List<AssociationEmbeddable> correctAssociations = new ArrayList<>();

}
