package de.unistuttgart.iste.gits.quiz_service.validation;

import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuizValidator {
    public void validateCreateQuizInput(CreateQuizInput input) {
        input.getMultipleChoiceQuestions().forEach(this::validateCreateMultipleChoiceQuestionInput);

        List<Integer> allQuestionsNumbers = input.getMultipleChoiceQuestions()
                .stream()
                .map(CreateMultipleChoiceQuestionInput::getNumber)
                .toList();

        validateNumbersUnique(allQuestionsNumbers);
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

    private void validateNumbersUnique(List<Integer> numbers) {
        if (numbers.size() != numbers.stream().distinct().count()) {
            List<Integer> duplicateNumbers = numbers.stream()
                    .filter(number -> numbers.stream().filter(number::equals).count() > 1)
                    .toList();
            throw new ValidationException("Question numbers must be unique, but the following numbers are used multiple times: "
                                          + duplicateNumbers);
        }
    }
}
