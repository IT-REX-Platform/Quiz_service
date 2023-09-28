package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.MultipleChoiceQuestion;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.MultipleChoiceQuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static de.unistuttgart.iste.gits.quiz_service.TestData.createMultipleChoiceQuestion;
import static de.unistuttgart.iste.gits.quiz_service.matcher.MultipleChoiceQuestionDtoToEntityMatcher.matchesEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
@TablesToDelete({"multiple_choice_question_answers", "multiple_choice_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizRemoveQuestionTest {

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Given a quiz with 3 questions
     * When the "removeQuestion" mutation is called with the number of the second question
     * Then the second question is removed from the quiz
     */
    @Test
    @Transactional
    @Commit
    void testRemoveQuestion(final GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "what is the capital of Germany?", "Berlin", "Paris"),
                        createMultipleChoiceQuestion(2, "what is the capital of France?", "Paris", "Madrid"),
                        createMultipleChoiceQuestion(3, "what is the capital of Spain?", "Madrid", "Berlin")))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        final String query = QuizFragments.FRAGMENT_DEFINITION + """
                mutation($id: UUID!) {
                    mutateQuiz(assessmentId: $id) {
                        removeQuestion(number: 2) {
                            ...QuizAllFields
                        }
                    }
                }
                """;

        final List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.removeQuestion.questionPool")
                .entityList(MultipleChoiceQuestion.class)
                .get();

        assertThat(questions, hasSize(2));
        assertThat(questions.get(0), matchesEntity(quizEntity.getQuestionPool().get(0)));

        // test that the number is updated in the following question
        final MultipleChoiceQuestionEntity updatedQuestion = (MultipleChoiceQuestionEntity) quizEntity.getQuestionPool().get(1);
        updatedQuestion.setNumber(2);
        assertThat(questions.get(1), matchesEntity(updatedQuestion));

        final QuizEntity newQuizEntity = quizRepository.findById(quizEntity.getAssessmentId()).orElseThrow();
        assertThat(newQuizEntity.getQuestionPool(), hasSize(2));
        assertThat(newQuizEntity.getQuestionPool(),
                containsInAnyOrder(quizEntity.getQuestionPool().get(0), updatedQuestion));
    }

    /**
     * Given a quiz with 2 questions
     * When the "removeQuestion" mutation is called with a non-existing question number
     * Then an error is returned
     */
    @Test
    void testRemoveQuestionNonExisting(final GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "what is the capital of Germany?", "Berlin", "Paris"),
                        createMultipleChoiceQuestion(2, "what is the capital of France?", "Paris", "Madrid")))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        final String query = """
                mutation($id: UUID!) {
                    mutateQuiz(assessmentId: $id) {
                        removeQuestion(number: 3) {
                            questionPool {
                                number
                            }
                        }
                    }
                }
                """;
        graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(), containsString("Question with number 3 not found"));
                });
    }
}
