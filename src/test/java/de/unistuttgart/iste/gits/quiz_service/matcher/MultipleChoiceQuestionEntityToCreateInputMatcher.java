package de.unistuttgart.iste.gits.quiz_service.matcher;

import de.unistuttgart.iste.gits.generated.dto.CreateMultipleChoiceQuestionInput;
import de.unistuttgart.iste.gits.generated.dto.QuestionType;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.MultipleChoiceQuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuestionEntity;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Objects;

import static de.unistuttgart.iste.gits.quiz_service.matcher.ResourceMarkdownMatchers.markdownMatches;

/**
 * Matcher for comparing a {@link MultipleChoiceQuestionEntity} to a {@link CreateMultipleChoiceQuestionInput}.
 */
public class MultipleChoiceQuestionEntityToCreateInputMatcher extends TypeSafeDiagnosingMatcher<QuestionEntity> {

    private final CreateMultipleChoiceQuestionInput expected;

    public MultipleChoiceQuestionEntityToCreateInputMatcher(CreateMultipleChoiceQuestionInput expected) {
        this.expected = expected;
    }

    public static MultipleChoiceQuestionEntityToCreateInputMatcher matchesInput(CreateMultipleChoiceQuestionInput expected) {
        return new MultipleChoiceQuestionEntityToCreateInputMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(QuestionEntity item, Description mismatchDescription) {
        if (!(item instanceof MultipleChoiceQuestionEntity multipleChoiceQuestionEntity)) {
            mismatchDescription.appendText("question was not a MultipleChoiceQuestionEntity");
            return false;
        }

        if (multipleChoiceQuestionEntity.getNumber() != expected.getNumber()) {
            mismatchDescription.appendText("number was ").appendValue(item.getNumber());
            return false;
        }
        if (!Objects.equals(multipleChoiceQuestionEntity.getType(), QuestionType.MULTIPLE_CHOICE)) {
            mismatchDescription.appendText("type was ").appendValue(item.getType());
            return false;
        }
        if (!markdownMatches(item.getHint(), expected.getHint())) {
            mismatchDescription.appendText("hint was ").appendValue(multipleChoiceQuestionEntity.getHint());
            return false;
        }
        if (!markdownMatches(multipleChoiceQuestionEntity.getText(), expected.getText())) {
            mismatchDescription.appendText("text was ").appendValue(multipleChoiceQuestionEntity.getText());
            return false;
        }
        if (multipleChoiceQuestionEntity.getAnswers().size() != expected.getAnswers().size()) {
            mismatchDescription.appendText("answers size was ").appendValue(multipleChoiceQuestionEntity.getAnswers().size());
            return false;
        }
        for (int i = 0; i < multipleChoiceQuestionEntity.getAnswers().size(); i++) {
            if (!MultipleChoiceAnswerEntityToInputMatcher.matchesInput(expected.getAnswers().get(i))
                    .matchesSafely(multipleChoiceQuestionEntity.getAnswers().get(i), mismatchDescription)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches CreateMultipleChoiceQuestionInput");
        description.appendValue(expected);
    }
}
