package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.CreateNumericQuestionInput;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@GraphQlApiTest
@TablesToDelete({"numeric_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizAddNumericQuestionTest {

    @Autowired
    private QuizRepository quizRepository;

    private static final String ADD_NUMERIC_QUESTION_MUTATION = QuizFragments.FRAGMENT_DEFINITION + """
            mutation($id: UUID!, $input: CreateNumericQuestionInput!) {
                mutateQuiz(assessmentId: $id) {
                    addNumericQuestion(input: $input) {
                        ...QuizAllFields
                    }
                }
            }
            """;

    /**
     * Given a quiz
     * When the "addNumericQuestion" mutation is called with a new question
     * Then the new question is added to the quiz
     */
    @Test
    @Transactional
    @Commit
    void testAddNumericQuestion(final GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        final CreateNumericQuestionInput input = CreateNumericQuestionInput.builder()
                .setHint("hint")
                .setText("question")
                .setFeedback("feedback")
                .setCorrectAnswer(2.0)
                .setTolerance(0.5)
                .build();


        graphQlTester.document(ADD_NUMERIC_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.addNumericQuestion.questionPool[0].number")
                .entity(Integer.class).isEqualTo(1)

                .path("mutateQuiz.addNumericQuestion.questionPool[0].hint")
                .entity(String.class).isEqualTo("hint")

                .path("mutateQuiz.addNumericQuestion.questionPool[0].text")
                .entity(String.class).isEqualTo("question")

                .path("mutateQuiz.addNumericQuestion.questionPool[0].feedback")
                .entity(String.class).isEqualTo("feedback")

                .path("mutateQuiz.addNumericQuestion.questionPool[0].correctAnswer")
                .entity(Double.class).isEqualTo(2.0)

                .path("mutateQuiz.addNumericQuestion.questionPool[0].tolerance")
                .entity(Double.class).isEqualTo(0.5);

        final QuestionEntity questionEntity = quizRepository.findById(quizEntity.getAssessmentId())
                .orElseThrow()
                .getQuestionPool()
                .get(0);

        assertThat(questionEntity, instanceOf(NumericQuestionEntity.class));
        final NumericQuestionEntity numericQuestionEntity = (NumericQuestionEntity) questionEntity;

        assertThat(numericQuestionEntity.getHint(), is("hint"));
        assertThat(numericQuestionEntity.getText(), is("question"));
        assertThat(numericQuestionEntity.getFeedback(), is("feedback"));
        assertThat(numericQuestionEntity.getCorrectAnswer(), is(2.0));
        assertThat(numericQuestionEntity.getTolerance(), is(0.5));

    }
}
