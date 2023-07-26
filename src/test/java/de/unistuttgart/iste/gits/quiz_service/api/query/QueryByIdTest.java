package de.unistuttgart.iste.gits.quiz_service.api.query;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.MultipleChoiceQuestion;
import de.unistuttgart.iste.gits.generated.dto.QuestionPoolingMode;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.gits.quiz_service.TestData.createMultipleChoiceQuestion;
import static de.unistuttgart.iste.gits.quiz_service.matcher.MultipleChoiceQuestionDtoToEntityMatcher.matchesEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
@TablesToDelete({"multiple_choice_question_answers", "multiple_choice_question", "quiz_question_pool", "question", "quiz"})
class QueryByIdTest {

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Given an uuid of an existing quiz
     * When the "quizByAssessmentId" query is executed
     * Then the correct quiz is returned
     */
    @Test
    void testQuizByAssessmentId(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = QuizEntity.builder()
                .assessmentId(UUID.randomUUID())
                .questionPool(List.of())
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .numberOfRandomlySelectedQuestions(0)
                .requiredCorrectAnswers(1)
                .build();
        quizRepository.save(quizEntity);

        QuizEntity quizEntity2 = QuizEntity.builder()
                .assessmentId(UUID.randomUUID())
                .questionPool(List.of())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(0)
                .requiredCorrectAnswers(2)
                .build();
        quizRepository.save(quizEntity2);

        String query = """
                query($id: UUID!) {
                    findQuizzesByAssessmentIds(assessmentIds: [$id]) {
                        assessmentId
                        questionPoolingMode
                        numberOfRandomlySelectedQuestions
                        requiredCorrectAnswers
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", quizEntity2.getAssessmentId())
                .execute()
                .path("findQuizzesByAssessmentIds[0].assessmentId").entity(UUID.class)
                .isEqualTo(quizEntity2.getAssessmentId())

                .path("findQuizzesByAssessmentIds[0].questionPoolingMode").entity(QuestionPoolingMode.class)
                .isEqualTo(quizEntity2.getQuestionPoolingMode())

                .path("findQuizzesByAssessmentIds[0].numberOfRandomlySelectedQuestions").entity(Integer.class)
                .isEqualTo(quizEntity2.getNumberOfRandomlySelectedQuestions());
    }

    /**
     * Given an uuid of an existing quiz
     * When the "quizByAssessmentId" query is executed
     * Then the correct quiz with the correct data is returned
     */
    @Test
    void queryQuizWithQuestions(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = QuizEntity.builder()
                .assessmentId(UUID.randomUUID())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(1)
                .requiredCorrectAnswers(1)
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "What is the answer to life, the universe and everything?",
                                "42", "24")))
                .build();
        quizRepository.save(quizEntity);

        String query = """
                query($id: UUID!) {
                    findQuizzesByAssessmentIds(assessmentIds: [$id]) {
                        assessmentId
                        requiredCorrectAnswers
                        questionPoolingMode
                        numberOfRandomlySelectedQuestions
                        questionPool {
                            number
                            hint
                            type
                            ... on MultipleChoiceQuestion {
                                text
                                answers {
                                    text
                                    correct
                                    feedback
                                }
                            }
                        }
                    }
                }
                """;

        List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .execute()

                .path("findQuizzesByAssessmentIds[0].assessmentId").entity(UUID.class)
                .isEqualTo(quizEntity.getAssessmentId())

                .path("findQuizzesByAssessmentIds[0].requiredCorrectAnswers").entity(Integer.class)
                .isEqualTo(1)

                .path("findQuizzesByAssessmentIds[0].questionPoolingMode").entity(QuestionPoolingMode.class)
                .isEqualTo(QuestionPoolingMode.RANDOM)

                .path("findQuizzesByAssessmentIds[0].numberOfRandomlySelectedQuestions").entity(Integer.class)
                .isEqualTo(1)

                .path("findQuizzesByAssessmentIds[0].questionPool")
                .entityList(MultipleChoiceQuestion.class)
                .get();

        assertThat(questions, hasSize(1));
        assertThat(questions.get(0), matchesEntity(quizEntity.getQuestionPool().get(0)));
    }

    /**
     * Given a quiz with three questions and question pooling mode "ORDERED"
     * When the "quizByAssessmentId" query is executed
     * Then the selected questions are the equal to the question pool in the correct order
     */
    @Test
    void testOrderedQuestionPoolingModeOrdered(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = QuizEntity.builder()
                .assessmentId(UUID.randomUUID())
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .numberOfRandomlySelectedQuestions(1) // should be ignored
                .requiredCorrectAnswers(1)
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "what is the capital of Germany?", "Berlin", "Paris"),
                        createMultipleChoiceQuestion(2, "what is the capital of France?", "Paris", "Madrid"),
                        createMultipleChoiceQuestion(3, "what is the capital of Spain?", "Madrid", "Berlin")))
                .build();
        quizRepository.save(quizEntity);

        String query = """
                query($id: UUID!) {
                    findQuizzesByAssessmentIds(assessmentIds: [$id]) {
                        selectedQuestions {
                            number
                            hint
                            type
                            ... on MultipleChoiceQuestion {
                                text
                                answers {
                                    text
                                    correct
                                    feedback
                                }
                            }
                        }
                    }
                }
                """;

        List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("findQuizzesByAssessmentIds[0].selectedQuestions")
                .entityList(MultipleChoiceQuestion.class)
                .get();

        assertThat(questions, hasSize(3));
        assertThat(questions.get(0), matchesEntity(quizEntity.getQuestionPool().get(0)));
        assertThat(questions.get(1), matchesEntity(quizEntity.getQuestionPool().get(1)));
        assertThat(questions.get(2), matchesEntity(quizEntity.getQuestionPool().get(2)));
    }

    /**
     * Given a quiz with three questions and question pooling mode "RANDOM" and numberOfRandomlySelectedQuestions = 2
     * When the "quizByAssessmentId" query is executed
     * Then the selected questions are the equal to the question pool in a random order
     */
    @Test
    void testOrderedQuestionPoolingModeRandom(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = QuizEntity.builder()
                .assessmentId(UUID.randomUUID())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(1)
                .requiredCorrectAnswers(1)
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "what is the capital of Germany?", "Berlin", "Paris"),
                        createMultipleChoiceQuestion(2, "what is the capital of France?", "Paris", "Madrid"),
                        createMultipleChoiceQuestion(3, "what is the capital of Spain?", "Madrid", "Berlin")))
                .build();
        quizRepository.save(quizEntity);

        String query = """
                query($id: UUID!) {
                    findQuizzesByAssessmentIds(assessmentIds: [$id]) {
                        selectedQuestions {
                            number
                            hint
                            type
                            ... on MultipleChoiceQuestion {
                                text
                                answers {
                                    text
                                    correct
                                    feedback
                                }
                            }
                        }
                    }
                }
                """;

        List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("findQuizzesByAssessmentIds[0].selectedQuestions")
                .entityList(MultipleChoiceQuestion.class)
                .get();

        assertThat(questions, hasSize(1));
        assertThat(questions.get(0), anyOf(
                matchesEntity(quizEntity.getQuestionPool().get(0)),
                matchesEntity(quizEntity.getQuestionPool().get(1)),
                matchesEntity(quizEntity.getQuestionPool().get(2)))
        );
    }

    /**
     * Given an uuid of a non-existing quiz
     * When the "quizByAssessmentId" query is executed
     * Then null is returned
     */
    @Test
    void testFindByAssessmentIdNotExisting(GraphQlTester graphQlTester) {
        String query = """
                query($id: UUID!) {
                    findQuizzesByAssessmentIds(assessmentIds: [$id]) {
                        assessmentId
                    }
                }
                """;

        var quizzes = graphQlTester.document(query)
                .variable("id", UUID.randomUUID())
                .execute()
                .path("findQuizzesByAssessmentIds").entityList(QuizEntity.class)
                .get();

        assertThat(quizzes, hasSize(1));
        assertThat(quizzes.get(0), is(nullValue()));
    }
}
