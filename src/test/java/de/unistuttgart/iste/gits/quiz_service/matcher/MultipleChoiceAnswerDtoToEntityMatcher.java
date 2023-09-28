package de.unistuttgart.iste.gits.quiz_service.matcher;

import de.unistuttgart.iste.gits.generated.dto.MultipleChoiceAnswer;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.MultipleChoiceAnswerEmbeddable;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Objects;

/**
 * Matcher for comparing a {@link MultipleChoiceAnswer} to a {@link MultipleChoiceAnswerEmbeddable}.
 */
public class MultipleChoiceAnswerDtoToEntityMatcher extends TypeSafeDiagnosingMatcher<MultipleChoiceAnswer> {

    private final MultipleChoiceAnswerEmbeddable expected;

    public MultipleChoiceAnswerDtoToEntityMatcher(final MultipleChoiceAnswerEmbeddable expected) {
        this.expected = expected;
    }

    public static MultipleChoiceAnswerDtoToEntityMatcher matchesEntity(final MultipleChoiceAnswerEmbeddable expected) {
        return new MultipleChoiceAnswerDtoToEntityMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(final MultipleChoiceAnswer item, final Description mismatchDescription) {
        if (!Objects.equals(item.getAnswerText(), expected.getAnswerText())) {
            mismatchDescription.appendText("answer text was ").appendValue(item.getAnswerText());
        }
        if (item.getCorrect() != expected.isCorrect()) {
            mismatchDescription.appendText("correct was ").appendValue(item.getCorrect());
            return false;
        }
        if (!Objects.equals(item.getFeedback(), expected.getFeedback())) {
            mismatchDescription.appendText("feedback was ").appendValue(item.getFeedback());
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("matches MultipleChoiceAnswerEmbeddable");
        description.appendValue(expected);
    }
}
