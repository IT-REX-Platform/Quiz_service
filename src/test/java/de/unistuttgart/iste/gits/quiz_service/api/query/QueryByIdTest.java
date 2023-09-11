package de.unistuttgart.iste.gits.quiz_service.api.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.gits.quiz_service.TestData.*;
import static de.unistuttgart.iste.gits.quiz_service.matcher.MultipleChoiceQuestionDtoToEntityMatcher.matchesEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
@TablesToDelete({"multiple_choice_question_answers", "multiple_choice_question",
        "cloze_question_additional_wrong_answers", "cloze_question_cloze_elements", "cloze_question",
        "association_question_correct_associations", "association_question",
        "exact_answer_question_correct_answers", "exact_answer_question",
        "numeric_question", "self_assessment_question",
        "quiz_question_pool", "question", "quiz"})
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
    void queryQuizWithQuestions(GraphQlTester graphQlTester) throws Exception {
        QuizEntity quizEntity = QuizEntity.builder()
                .assessmentId(UUID.randomUUID())
                .questionPoolingMode(QuestionPoolingMode.RANDOM)
                .numberOfRandomlySelectedQuestions(1)
                .requiredCorrectAnswers(1)
                .questionPool(List.of(
                        createMultipleChoiceQuestion(1, "What is the answer to life, the universe and everything?",
                                "42", "24"),
                        createAssociationQuestion(2, association("A", "1"), association("B", "2")),
                        createClozeQuestion(3, clozeText("text"), clozeBlank("answer")),
                        createExactAnswerQuestion(4, "question", "answer"),
                        createNumericQuestion(5, "question", 42),
                        createSelfAssessmentQuestion(6, "question", "answer")))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        Question[] expectedQuestions = new Question[]{
                MultipleChoiceQuestion.builder()
                        .setNumber(1)
                        .setText(new ResourceMarkdown("What is the answer to life, the universe and everything?", List.of()))
                        .setHint(new ResourceMarkdown("hint", List.of()))
                        .setId(quizEntity.getQuestionPool().get(0).getId())
                        .setAnswers(List.of(
                                MultipleChoiceAnswer.builder()
                                        .setAnswerText(new ResourceMarkdown("42", List.of()))
                                        .setCorrect(true)
                                        .setFeedback(new ResourceMarkdown("feedback", List.of()))
                                        .build(),
                                MultipleChoiceAnswer.builder()
                                        .setAnswerText(new ResourceMarkdown("24", List.of()))
                                        .setCorrect(false)
                                        .setFeedback(new ResourceMarkdown("feedback", List.of()))
                                        .build()))
                        .setType(QuestionType.MULTIPLE_CHOICE)
                        .setNumberOfCorrectAnswers(1)
                        .build(),
                AssociationQuestion.builder()
                        .setNumber(2)
                        .setId(quizEntity.getQuestionPool().get(1).getId())
                        .setText(new ResourceMarkdown("text", List.of()))
                        .setHint(new ResourceMarkdown("hint", List.of()))
                        .setId(quizEntity.getQuestionPool().get(1).getId())
                        .setType(QuestionType.ASSOCIATION)
                        .setCorrectAssociations(List.of(
                                new SingleAssociation("A", "1", new ResourceMarkdown("feedback", List.of())),
                                new SingleAssociation("B", "2", new ResourceMarkdown("feedback", List.of()))))
                        .setLeftSide(List.of("A", "B"))
                        .setRightSide(List.of("1", "2"))
                        .build(),
                ClozeQuestion.builder()
                        .setNumber(3)
                        .setHint(new ResourceMarkdown("hint", List.of()))
                        .setId(quizEntity.getQuestionPool().get(2).getId())
                        .setType(QuestionType.CLOZE)
                        .setShowBlanksList(true)
                        .setAdditionalWrongAnswers(List.of("wrong1", "wrong2"))
                        .setClozeElements(List.of(
                                ClozeTextElement.builder()
                                        .setText(new ResourceMarkdown("text", List.of()))
                                        .build(),
                                ClozeBlankElement.builder()
                                        .setCorrectAnswer("answer")
                                        .setFeedback(new ResourceMarkdown("feedback", List.of()))
                                        .build()))
                        .setAllBlanks(List.of("answer", "wrong1", "wrong2"))
                        .build(),
                ExactAnswerQuestion.builder()
                        .setNumber(4)
                        .setHint(new ResourceMarkdown("hint", List.of()))
                        .setId(quizEntity.getQuestionPool().get(3).getId())
                        .setType(QuestionType.EXACT_ANSWER)
                        .setText(new ResourceMarkdown("question", List.of()))
                        .setCorrectAnswers(List.of("answer"))
                        .setCaseSensitive(true)
                        .setFeedback(new ResourceMarkdown("feedback", List.of()))
                        .build(),
                NumericQuestion.builder()
                        .setNumber(5)
                        .setHint(new ResourceMarkdown("hint", List.of()))
                        .setId(quizEntity.getQuestionPool().get(4).getId())
                        .setType(QuestionType.NUMERIC)
                        .setText(new ResourceMarkdown("question", List.of()))
                        .setCorrectAnswer(42)
                        .setFeedback(new ResourceMarkdown("feedback", List.of()))
                        .setTolerance(1)
                        .build(),
                SelfAssessmentQuestion.builder()
                        .setNumber(6)
                        .setHint(new ResourceMarkdown("hint", List.of()))
                        .setId(quizEntity.getQuestionPool().get(5).getId())
                        .setType(QuestionType.SELF_ASSESSMENT)
                        .setText(new ResourceMarkdown("question", List.of()))
                        .setSolutionSuggestion(new ResourceMarkdown("answer", List.of()))
                        .build()
        };

        String expectedJson = new ObjectMapper().writeValueAsString(expectedQuestions);

        String query = QuizFragments.FRAGMENT_DEFINITION + """
                query($id: UUID!) {
                    findQuizzesByAssessmentIds(assessmentIds: [$id]) {
                        ...QuizAllFields
                    }
                }
                """;

        graphQlTester.document(query)
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
                .matchesJson(expectedJson);

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

        String query = QuizFragments.FRAGMENT_DEFINITION + """
                query($id: UUID!) {
                    findQuizzesByAssessmentIds(assessmentIds: [$id]) {
                        ...QuizAllFields
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
     * Given a quiz with three questions and question pooling mode "RANDOM" and numberOfRandomlySelectedQuestions = 1
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

        String query = QuizFragments.FRAGMENT_DEFINITION + """
                query($id: UUID!) {
                    findQuizzesByAssessmentIds(assessmentIds: [$id]) {
                        ...QuizAllFields
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
