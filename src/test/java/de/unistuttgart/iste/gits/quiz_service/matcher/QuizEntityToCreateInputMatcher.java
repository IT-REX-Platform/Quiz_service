package de.unistuttgart.iste.gits.quiz_service.matcher;

import de.unistuttgart.iste.gits.generated.dto.CreateQuizInput;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuizEntity;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Objects;

/**
 * Matcher for comparing a {@link QuizEntity} to a {@link CreateQuizInput}.
 */
public class QuizEntityToCreateInputMatcher extends TypeSafeDiagnosingMatcher<QuizEntity> {

    private final CreateQuizInput expected;

    public QuizEntityToCreateInputMatcher(CreateQuizInput expected) {
        this.expected = expected;
    }

    public static QuizEntityToCreateInputMatcher matchesCreateQuizInput(CreateQuizInput expected) {
        return new QuizEntityToCreateInputMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(QuizEntity item, Description mismatchDescription) {
        if (item.getRequiredCorrectAnswers() != expected.getRequiredCorrectAnswers()) {
            mismatchDescription.appendText("requiredCorrectAnswers was ").appendValue(item.getRequiredCorrectAnswers());
            return false;
        }
        if (item.getQuestionPoolingMode() != expected.getQuestionPoolingMode()) {
            mismatchDescription.appendText("questionPoolingMode was ").appendValue(item.getQuestionPoolingMode());
            return false;
        }
        if (!Objects.equals(item.getNumberOfRandomlySelectedQuestions(), expected.getNumberOfRandomlySelectedQuestions())) {
            mismatchDescription.appendText("numberOfRandomlySelectedQuestions was ").appendValue(item.getNumberOfRandomlySelectedQuestions());
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches CreateQuizInput");
        description.appendValue(expected);
    }
}
