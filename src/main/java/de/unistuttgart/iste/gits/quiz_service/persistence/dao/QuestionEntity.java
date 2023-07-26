package de.unistuttgart.iste.gits.quiz_service.persistence.dao;

import de.unistuttgart.iste.gits.generated.dto.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

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

    @Column(length = 1000, nullable = true)
    private String hint;

}
