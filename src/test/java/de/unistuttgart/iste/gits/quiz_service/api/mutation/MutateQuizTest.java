package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.QuestionPoolingMode;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.gits.quiz_service.TestData.createMultipleChoiceQuestion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@GraphQlApiTest
@TablesToDelete({"multiple_choice_question_answers", "multiple_choice_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizTest {

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Given a quiz
     * When the "mutateQuiz" mutation is called with the quiz's assessment id and some nested mutations
     * Then the quiz is updated accordingly
     */
    @Test
    void testMutateQuiz(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = QuizEntity.builder()
                .courseId(UUID.randomUUID())
                .assessmentId(UUID.randomUUID())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(1)
                .requiredCorrectAnswers(1)
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "what is the capital of Germany?", "Berlin", "Paris")))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        String query = """
                mutation($id: UUID!) {
                    mutateQuiz(assessmentId: $id) {
                        setQuestionPoolingMode(questionPoolingMode: ORDERED) { assessmentId }
                        setNumberOfRandomlySelectedQuestions(numberOfRandomlySelectedQuestions: 2) { assessmentId }
                        result: setRequiredCorrectAnswers(requiredCorrectAnswers: 2) {
                            assessmentId
                            requiredCorrectAnswers
                            questionPoolingMode
                            numberOfRandomlySelectedQuestions
                        }
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.result.assessmentId").entity(UUID.class).isEqualTo(quizEntity.getAssessmentId())

                .path("mutateQuiz.result.requiredCorrectAnswers").entity(Integer.class)
                .isEqualTo(2)

                .path("mutateQuiz.result.questionPoolingMode").entity(QuestionPoolingMode.class)
                .isEqualTo(QuestionPoolingMode.ORDERED)

                .path("mutateQuiz.result.numberOfRandomlySelectedQuestions").entity(Integer.class)
                .isEqualTo(2);

        QuizEntity updatedQuiz = quizRepository.findById(quizEntity.getAssessmentId()).orElseThrow();
        assertThat(updatedQuiz.getRequiredCorrectAnswers(), is(2));
        assertThat(updatedQuiz.getQuestionPoolingMode(), is(QuestionPoolingMode.ORDERED));
        assertThat(updatedQuiz.getNumberOfRandomlySelectedQuestions(), is(2));
    }
}
