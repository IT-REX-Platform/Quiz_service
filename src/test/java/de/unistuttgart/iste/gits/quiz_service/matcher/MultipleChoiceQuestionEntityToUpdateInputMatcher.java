package de.unistuttgart.iste.gits.quiz_service.matcher;

import de.unistuttgart.iste.gits.generated.dto.QuestionType;
import de.unistuttgart.iste.gits.generated.dto.UpdateMultipleChoiceQuestionInput;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.MultipleChoiceQuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuestionEntity;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Objects;

/**
 * Matcher for comparing a {@link QuestionEntity} to a {@link UpdateMultipleChoiceQuestionInput}.
 */
public class MultipleChoiceQuestionEntityToUpdateInputMatcher extends TypeSafeDiagnosingMatcher<QuestionEntity> {

    private final UpdateMultipleChoiceQuestionInput expected;

    public MultipleChoiceQuestionEntityToUpdateInputMatcher(UpdateMultipleChoiceQuestionInput expected) {
        this.expected = expected;
    }

    public static MultipleChoiceQuestionEntityToUpdateInputMatcher matchesUpdateQuizInput(UpdateMultipleChoiceQuestionInput expected) {
        return new MultipleChoiceQuestionEntityToUpdateInputMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(QuestionEntity item, Description mismatchDescription) {
        if (!Objects.equals(item.getId(), expected.getId())) {
            mismatchDescription.appendText("id was ").appendValue(item.getId());
            return false;
        }
        if (!Objects.equals(item.getType(), QuestionType.MULTIPLE_CHOICE)) {
            mismatchDescription.appendText("type was ").appendValue(item.getType());
            return false;
        }
        if (!Objects.equals(item.getHint(), expected.getHint())) {
            mismatchDescription.appendText("hint was ").appendValue(item.getHint());
            return false;
        }

        if (!(item instanceof MultipleChoiceQuestionEntity multipleChoiceQuestionEntity)) {
            mismatchDescription.appendText("question was not a MultipleChoiceQuestionEntity");
            return false;
        }

        if (!Objects.equals(multipleChoiceQuestionEntity.getText(), expected.getText())) {
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
        description.appendText("matches UpdateMultipleChoiceQuestionInput");
        description.appendValue(expected);
    }
}
