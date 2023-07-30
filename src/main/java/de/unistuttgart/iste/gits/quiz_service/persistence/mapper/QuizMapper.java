package de.unistuttgart.iste.gits.quiz_service.persistence.mapper;

import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@Component
@RequiredArgsConstructor
public class QuizMapper {

    private final ModelMapper mapper;

    /**
     * Maps a quiz entity to a quiz dto but does not set {@link Quiz#getSelectedQuestions()}.
     */
    public Quiz entityToDto(QuizEntity entity) {
        // manual mapping necessary because of QuestionInterface
        // which cannot automatically be mapped by model mapper
        return Quiz.builder()
                .setAssessmentId(entity.getAssessmentId())
                .setQuestionPoolingMode(entity.getQuestionPoolingMode())
                .setNumberOfRandomlySelectedQuestions(entity.getNumberOfRandomlySelectedQuestions())
                .setRequiredCorrectAnswers(entity.getRequiredCorrectAnswers())
                .setQuestionPool(entity.getQuestionPool().stream().map(this::questionEntityToDto).toList())
                .build();
    }

    private Question questionEntityToDto(QuestionEntity questionEntity) {
        if (questionEntity instanceof MultipleChoiceQuestionEntity multipleChoiceQuestionEntity) {
            return mapper.map(multipleChoiceQuestionEntity, MultipleChoiceQuestion.class);
        }

        // add other question types here
        throw new IllegalArgumentException("Unknown question type: " + questionEntity.getType());
    }

    public QuizEntity createQuizInputToEntity(CreateQuizInput createQuizInput) {
        List<CreateMultipleChoiceQuestionInput> multipleChoiceQuestions = createQuizInput.getMultipleChoiceQuestions();
        // add other questions types here

        assignNumbersIfNull(multipleChoiceQuestions);

        List<QuestionEntity> allQuestions =
                Stream.concat(
                                multipleChoiceQuestions.stream().map(this::multipleChoiceQuestionInputToEntity),
                                // add other questions types here
                                Stream.empty())
                        .sorted(comparing(QuestionEntity::getNumber))
                        .toList();

        QuizEntity entity = mapper.map(createQuizInput, QuizEntity.class);
        entity.setQuestionPool(allQuestions);

        return entity;
    }

    public QuestionEntity multipleChoiceQuestionInputToEntity(CreateMultipleChoiceQuestionInput input) {
        MultipleChoiceQuestionEntity result = mapper.map(input, MultipleChoiceQuestionEntity.class);
        result.setType(QuestionType.MULTIPLE_CHOICE);
        return result;
    }

    public QuestionEntity multipleChoiceQuestionInputToEntity(UpdateMultipleChoiceQuestionInput input) {
        MultipleChoiceQuestionEntity result = mapper.map(input, MultipleChoiceQuestionEntity.class);
        result.setType(QuestionType.MULTIPLE_CHOICE);
        return result;
    }

    private void assignNumbersIfNull(List<CreateMultipleChoiceQuestionInput> createMultipleChoiceQuestionInputs) {
        OptionalInt maxNumber = createMultipleChoiceQuestionInputs.stream()
                .map(CreateMultipleChoiceQuestionInput::getNumber)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max();

        for (CreateMultipleChoiceQuestionInput createMultipleChoiceQuestionInput : createMultipleChoiceQuestionInputs) {
            if (createMultipleChoiceQuestionInput.getNumber() == null) {
                int newNumber = maxNumber.orElse(0) + 1;
                createMultipleChoiceQuestionInput.setNumber(newNumber);
                maxNumber = OptionalInt.of(newNumber);
            }
        }

    }
}
