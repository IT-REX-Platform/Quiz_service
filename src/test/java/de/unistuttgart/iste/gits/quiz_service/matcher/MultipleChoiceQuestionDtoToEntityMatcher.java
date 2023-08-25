package de.unistuttgart.iste.gits.quiz_service.matcher;

import de.unistuttgart.iste.gits.generated.dto.MultipleChoiceQuestion;
import de.unistuttgart.iste.gits.generated.dto.QuestionType;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.MultipleChoiceQuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuestionEntity;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Objects;

import static de.unistuttgart.iste.gits.quiz_service.matcher.ResourceMarkdownMatchers.markdownMatches;

/**
 * Matcher for comparing a {@link MultipleChoiceQuestionEntity} to a {@link MultipleChoiceQuestionEntity}.
 */
public class MultipleChoiceQuestionDtoToEntityMatcher extends TypeSafeDiagnosingMatcher<MultipleChoiceQuestion> {

    private final QuestionEntity expected;

    public MultipleChoiceQuestionDtoToEntityMatcher(QuestionEntity expected) {
        this.expected = expected;
    }

    public static MultipleChoiceQuestionDtoToEntityMatcher matchesEntity(QuestionEntity expected) {
        return new MultipleChoiceQuestionDtoToEntityMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(MultipleChoiceQuestion item, Description mismatchDescription) {
        if (!(expected instanceof MultipleChoiceQuestionEntity multipleChoiceQuestionEntity)) {
            mismatchDescription.appendText("expected was not a MultipleChoiceQuestionEntity");
            return false;
        }

        // for convenience, we do not check the id as it is usually null before saving

        if (item.getNumber() != expected.getNumber()) {
            mismatchDescription.appendText("number was ").appendValue(item.getNumber());
            return false;
        }
        if (!Objects.equals(item.getType(), QuestionType.MULTIPLE_CHOICE)) {
            mismatchDescription.appendText("type was ").appendValue(item.getType());
            return false;
        }
        if (!markdownMatches(item.getHint(), expected.getHint())) {
            mismatchDescription.appendText("hint was ").appendValue(item.getHint().getText());
            return false;
        }
        if (!markdownMatches(item.getText(), multipleChoiceQuestionEntity.getText())) {
            mismatchDescription.appendText("text was ").appendValue(item.getText().getText());
            return false;
        }
        if (item.getAnswers().size() != multipleChoiceQuestionEntity.getAnswers().size()) {
            mismatchDescription.appendText("answers size was ").appendValue(item.getAnswers().size());
            return false;
        }
        for (int i = 0; i < item.getAnswers().size(); i++) {
            if (!MultipleChoiceAnswerDtoToEntityMatcher
                    .matchesEntity(multipleChoiceQuestionEntity.getAnswers().get(i))
                    .matchesSafely(item.getAnswers().get(i), mismatchDescription)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches QuestionEntity");
        description.appendValue(expected);
    }
}
