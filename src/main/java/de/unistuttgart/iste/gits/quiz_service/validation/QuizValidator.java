package de.unistuttgart.iste.gits.quiz_service.validation;

import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuizValidator {
    public void validateCreateQuizInput(CreateQuizInput input) {
        // no validation needed
    }

    public void validateCreateMultipleChoiceQuestionInput(CreateMultipleChoiceQuestionInput input) {
        validateAtLeastOneAnswerCorrect(input.getAnswers());
    }

    public void validateUpdateMultipleChoiceQuestionInput(UpdateMultipleChoiceQuestionInput input) {
        validateAtLeastOneAnswerCorrect(input.getAnswers());
    }

    private void validateAtLeastOneAnswerCorrect(List<MultipleChoiceAnswerInput> answers) {
        if (answers.stream().noneMatch(MultipleChoiceAnswerInput::getCorrect)) {
            throw new ValidationException("At least one answer must be correct");
        }
    }
}
