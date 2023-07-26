package de.unistuttgart.iste.gits.quiz_service.matcher;

import de.unistuttgart.iste.gits.generated.dto.MultipleChoiceAnswer;
import de.unistuttgart.iste.gits.generated.dto.MultipleChoiceAnswerInput;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Objects;

/**
 * Matcher for comparing a {@link MultipleChoiceAnswer} to a {@link MultipleChoiceAnswerInput}.
 */
public class MultipleChoiceAnswerDtoToInputMatcher extends TypeSafeDiagnosingMatcher<MultipleChoiceAnswer> {

    private final MultipleChoiceAnswerInput expected;

    public MultipleChoiceAnswerDtoToInputMatcher(MultipleChoiceAnswerInput expected) {
        this.expected = expected;
    }

    public static MultipleChoiceAnswerDtoToInputMatcher matchesInput(MultipleChoiceAnswerInput expected) {
        return new MultipleChoiceAnswerDtoToInputMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(MultipleChoiceAnswer item, Description mismatchDescription) {
        if (!item.getText().equals(expected.getText())) {
            mismatchDescription.appendText("text was ").appendValue(item.getText());
            return false;
        }
        if (item.getCorrect() != expected.getCorrect()) {
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
    public void describeTo(Description description) {
        description.appendText("matches MultipleChoiceAnswerInput");
        description.appendValue(expected);
    }
}
