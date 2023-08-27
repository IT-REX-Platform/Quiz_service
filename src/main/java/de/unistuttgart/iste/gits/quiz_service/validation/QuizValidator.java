package de.unistuttgart.iste.gits.quiz_service.validation;

import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.unistuttgart.iste.gits.generated.dto.ClozeElementType.BLANK;
import static de.unistuttgart.iste.gits.generated.dto.ClozeElementType.TEXT;

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

    public void validateCreateClozeQuestionInput(CreateClozeQuestionInput input) {
        validateClozeElements(input.getClozeElements());
    }

    public void validateUpdateClozeQuestionInput(UpdateClozeQuestionInput input) {
        validateClozeElements(input.getClozeElements());
    }

    private void validateClozeElements(List<ClozeElementInput> clozeElements) {
        if (clozeElements.stream().map(ClozeElementInput::getType).noneMatch(BLANK::equals)) {
            throw new ValidationException("Cloze quiz most contain at least one blank");
        }
        clozeElements.forEach(this::validateClozeElement);
    }

    private void validateClozeElement(ClozeElementInput clozeElement) {
        if (clozeElement.getType() == BLANK) {
            if (clozeElement.getCorrectAnswer() == null) {
                throw new ValidationException("correct answer is required for cloze blank elements");
            }
            if (clozeElement.getText() != null) {
                throw new ValidationException("text is not allowed for cloze blank elements");
            }
        }
        if (clozeElement.getType() == TEXT) {
            if (clozeElement.getCorrectAnswer() != null) {
                throw new ValidationException("correct answer is not allowed for text elements");
            }
            if (clozeElement.getText() == null) {
                throw new ValidationException("text is required for cloze text elements");
            }
            if (clozeElement.getFeedback() != null) {
                throw new ValidationException("text elements cannot have feedback");
            }
        }
    }
}
