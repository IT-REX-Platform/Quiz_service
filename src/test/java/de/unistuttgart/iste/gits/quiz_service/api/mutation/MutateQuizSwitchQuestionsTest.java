package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.MultipleChoiceQuestion;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuizEntity;
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
class MutateQuizSwitchQuestionsTest {

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Given a quiz with 3 questions
     * When the "switchQuestions" mutation is called with the first and second question number
     * Then the questions are switched
     */
    @Test
    @Transactional
    @Commit
    void testSwitchQuestion(GraphQlTester graphQlTester) {
        // store questions in separate variable because spring apparently caches the quiz entity instance
        // so the following quiz entity instance is updated during the mutation
        List<QuestionEntity> questionEntities = List.of(
                createMultipleChoiceQuestion(1, "what is the capital of Germany?", "Berlin", "Paris"),
                createMultipleChoiceQuestion(2, "what is the capital of France?", "Paris", "Madrid"),
                createMultipleChoiceQuestion(3, "what is the capital of Spain?", "Madrid", "Berlin"));

        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(questionEntities)
                .build();
        quizEntity = quizRepository.save(quizEntity);

        String query = QuizFragments.FRAGMENT_DEFINITION + """
                mutation($id: UUID!) {
                    mutateQuiz(assessmentId: $id) {
                        switchQuestions(firstNumber: 2, secondNumber: 3) {
                            ...QuizAllFields
                        }
                    }
                }
                """;
        List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.switchQuestions.questionPool")
                .entityList(MultipleChoiceQuestion.class)
                .get();

        assertThat(questions, hasSize(3));

        QuestionEntity expectedFirstEntity = questionEntities.get(0);
        expectedFirstEntity.setId(quizEntity.getQuestionPool().get(0).getId());
        assertThat(questions.get(0), matchesEntity(expectedFirstEntity));

        QuestionEntity expectedSecondEntity = questionEntities.get(2);
        expectedSecondEntity.setNumber(2);
        expectedSecondEntity.setId(quizEntity.getQuestionPool().get(1).getId());
        assertThat(questions.get(1), matchesEntity(expectedSecondEntity));

        QuestionEntity expectedThirdEntity = questionEntities.get(1);
        expectedThirdEntity.setNumber(3);
        expectedThirdEntity.setId(quizEntity.getQuestionPool().get(2).getId());
        assertThat(questions.get(2), matchesEntity(expectedThirdEntity));

        QuizEntity newQuizEntity = quizRepository.findById(quizEntity.getAssessmentId()).orElseThrow();
        assertThat(newQuizEntity.getQuestionPool(), hasSize(3));

        assertThat(newQuizEntity.getQuestionPool().get(0), is(expectedFirstEntity));
        assertThat(newQuizEntity.getQuestionPool().get(1), is(expectedSecondEntity));
        assertThat(newQuizEntity.getQuestionPool().get(2), is(expectedThirdEntity));
    }

    /**
     * Given a quiz with 2 questions
     * When the "switchQuestion" mutation is called with a non-existing question number
     * Then an error is returned
     */
    @Test
    void testSwotchQuestionNonExisting(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "what is the capital of Germany?", "Berlin", "Paris"),
                        createMultipleChoiceQuestion(2, "what is the capital of France?", "Paris", "Madrid")))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        String query = """
                mutation($id: UUID!) {
                    mutateQuiz(assessmentId: $id) {
                        switchQuestions(firstNumber: 1, secondNumber: 3) {
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
