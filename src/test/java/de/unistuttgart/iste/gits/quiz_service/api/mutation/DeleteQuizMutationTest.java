package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.generated.dto.QuestionPoolingMode;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import graphql.ErrorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.gits.quiz_service.TestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
class DeleteQuizMutationTest {

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Given an uuid of an existing quiz
     * When the "deleteQuiz" mutation is executed
     * Then the quiz is deleted
     */
    @Test
    void testDeleteQuiz(GraphQlTester graphQlTester) {
        // create quiz in database
        QuizEntity quizEntity = QuizEntity.builder()
                .assessmentId(UUID.randomUUID())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(1)
                .requiredCorrectAnswers(1)
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1,
                                "what is the capital of Germany?",
                                "Berlin", "Paris"
                        ),
                        createClozeQuestion(2,
                                clozeText("This is an example text with a "),
                                clozeBlank("blank"), clozeText(".")
                        ),
                        createAssociationQuestion(3,
                                association("left1", "right1"),
                                association("left2", "right2")),
                        createExactAnswerQuestion(4, "question text", "answer text"),
                        createNumericQuestion(5, "question text", 42),
                        createSelfAssessmentQuestion(6, "question text", "answer text")
                ))
                .build();
        quizRepository.save(quizEntity);

        String query = "mutation { deleteQuiz(assessmentId: \"" + quizEntity.getAssessmentId() + "\") }";

        graphQlTester.document(query)
                .execute()
                .path("data.deleteQuiz")
                .entity(UUID.class)
                .isEqualTo(quizEntity.getAssessmentId());

        assertThat(quizRepository.count(), is(0L));
    }

    /**
     * Given an uuid of a non-existing quiz
     * When the "deleteQuiz" mutation is executed
     * Then an error is returned
     */
    @Test
    void testDeleteNonExistingQuiz(GraphQlTester graphQlTester) {
        String query = "mutation { deleteQuiz(assessmentId: \"" + UUID.randomUUID() + "\") }";

        graphQlTester.document(query)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(), containsString("Quiz with id"));
                    assertThat(errors.get(0).getMessage(), containsString("not found"));
                    assertThat(errors.get(0).getErrorType(), is(ErrorType.DataFetchingException));
                });
    }
}
