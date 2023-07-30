package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.gits.quiz_service.matcher.MultipleChoiceQuestionDtoToCreateInputMatcher.matchesInput;
import static de.unistuttgart.iste.gits.quiz_service.matcher.QuizEntityToCreateInputMatcher.matchesCreateQuizInput;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
@TablesToDelete({"multiple_choice_question_answers", "multiple_choice_question", "quiz_question_pool", "question", "quiz"})
class CreateQuizMutationTest {

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Given a quiz with no questions
     * When the "createQuiz" mutation is executed
     * Then the quiz is created
     */
    @Test
    @Transactional
    @Commit
    void testCreateQuizWithoutQuestions(GraphQlTester graphQlTester) {
        UUID assessmentId = UUID.randomUUID();
        CreateQuizInput createQuizInput = CreateQuizInput.builder()
                .setRequiredCorrectAnswers(1)
                .setQuestionPoolingMode(QuestionPoolingMode.RANDOM)
                .setNumberOfRandomlySelectedQuestions(2)
                .setMultipleChoiceQuestions(List.of())
                .build();

        String query = """
                mutation createQuiz($id: UUID!, $input: CreateQuizInput!) {
                    createQuiz(assessmentId: $id, input: $input) {
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
                }""";

        // note that deserialization of the result into Quiz dto is not possible because "Question" is an interface
        // so check the fields manually
        List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
                .variable("input", createQuizInput)
                .variable("id", assessmentId)
                .execute()

                .path("createQuiz.assessmentId").entity(UUID.class)
                .isEqualTo(assessmentId)

                .path("createQuiz.requiredCorrectAnswers").entity(Integer.class)
                .isEqualTo(1)

                .path("createQuiz.questionPoolingMode").entity(QuestionPoolingMode.class)
                .isEqualTo(QuestionPoolingMode.RANDOM)

                .path("createQuiz.numberOfRandomlySelectedQuestions").entity(Integer.class)
                .isEqualTo(2)

                .path("createQuiz.questionPool")
                .entityList(MultipleChoiceQuestion.class)
                .get();

        assertThat(questions, is(empty()));

        assertThat(quizRepository.count(), is(1L));
        assertThat(quizRepository.findAll().get(0), matchesCreateQuizInput(createQuizInput));
    }

    /**
     * Given a quiz with two questions
     * When the "createQuiz" mutation is executed
     * Then the quiz is created
     */
    @Test
    @Transactional
    @Commit
    void testCreateQuizWithQuestions(GraphQlTester graphQlTester) {
        UUID assessmentId = UUID.randomUUID();
        CreateQuizInput createQuizInput = CreateQuizInput.builder()
                .setRequiredCorrectAnswers(1)
                .setQuestionPoolingMode(QuestionPoolingMode.RANDOM)
                .setNumberOfRandomlySelectedQuestions(2)
                .setMultipleChoiceQuestions(List.of(
                        CreateMultipleChoiceQuestionInput.builder()
                                .setNumber(1)
                                .setText("What is the answer to life, the universe and everything?")
                                .setAnswers(List.of(
                                        MultipleChoiceAnswerInput.builder()
                                                .setText("42")
                                                .setCorrect(true)
                                                .build(),
                                        MultipleChoiceAnswerInput.builder()
                                                .setText("24")
                                                .setCorrect(false)
                                                .build()
                                ))
                                .build(),
                        CreateMultipleChoiceQuestionInput.builder()
                                .setNumber(null) // number should be assigned automatically
                                .setText("What is love?")
                                .setAnswers(List.of(
                                        MultipleChoiceAnswerInput.builder()
                                                .setText("Baby don't hurt me")
                                                .setCorrect(true)
                                                .build(),
                                        MultipleChoiceAnswerInput.builder()
                                                .setText("A chemical reaction in the brain")
                                                .setCorrect(false)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        String query = """
                mutation createQuiz($id: UUID!, $input: CreateQuizInput!) {
                    createQuiz(assessmentId: $id, input: $input) {
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
                }""";

        // note that deserialization of the result into Quiz dto is not possible because "Question" is an interface
        // so check the fields manually
        List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
                .variable("input", createQuizInput)
                .variable("id", assessmentId)
                .execute()

                .path("createQuiz.assessmentId").entity(UUID.class)
                .isEqualTo(assessmentId)

                .path("createQuiz.requiredCorrectAnswers").entity(Integer.class)
                .isEqualTo(1)

                .path("createQuiz.questionPoolingMode").entity(QuestionPoolingMode.class)
                .isEqualTo(QuestionPoolingMode.RANDOM)

                .path("createQuiz.numberOfRandomlySelectedQuestions").entity(Integer.class)
                .isEqualTo(2)

                .path("createQuiz.questionPool")
                .entityList(MultipleChoiceQuestion.class)
                .get();

        assertThat(questions, hasSize(2));
        assertThat(questions.get(0), matchesInput(createQuizInput.getMultipleChoiceQuestions().get(0)));

        CreateMultipleChoiceQuestionInput expectedQuestion = createQuizInput.getMultipleChoiceQuestions().get(1);
        expectedQuestion.setNumber(2); // number should be assigned automatically
        assertThat(questions.get(1), matchesInput(expectedQuestion));

        assertThat(quizRepository.count(), is(1L));
        assertThat(quizRepository.findAll().get(0), matchesCreateQuizInput(createQuizInput));
    }

    /**
     * Given a quiz with two questions with the same number
     * When the quiz is created
     * Then a validation error is returned
     */
    @Test
    void testCreateQuizWithDuplicateNumbersInQuestion(GraphQlTester graphQlTester) {
        UUID assessmentId = UUID.randomUUID();
        CreateQuizInput createQuizInput = CreateQuizInput.builder()
                .setRequiredCorrectAnswers(1)
                .setQuestionPoolingMode(QuestionPoolingMode.RANDOM)
                .setNumberOfRandomlySelectedQuestions(2)
                .setMultipleChoiceQuestions(List.of(
                        CreateMultipleChoiceQuestionInput.builder()
                                .setNumber(2)
                                .setText("What is the answer to life, the universe and everything?")
                                .setAnswers(List.of(
                                        MultipleChoiceAnswerInput.builder()
                                                .setText("42")
                                                .setCorrect(true)
                                                .build(),
                                        MultipleChoiceAnswerInput.builder()
                                                .setText("24")
                                                .setCorrect(false)
                                                .build()
                                ))
                                .build(),
                        CreateMultipleChoiceQuestionInput.builder()
                                .setNumber(2)
                                .setText("What is love?")
                                .setAnswers(List.of(
                                        MultipleChoiceAnswerInput.builder()
                                                .setText("Baby don't hurt me")
                                                .setCorrect(true)
                                                .build(),
                                        MultipleChoiceAnswerInput.builder()
                                                .setText("A chemical reaction in the brain")
                                                .setCorrect(false)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        String query = """
                mutation createQuiz($id: UUID!, $input: CreateQuizInput!) {
                    createQuiz(assessmentId: $id, input: $input) {
                        assessmentId
                    }
                }""";

        graphQlTester.document(query)
                .variable("input", createQuizInput)
                .variable("id", assessmentId)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    var error = errors.get(0);
                    assertThat(error.getMessage(), containsString("Question numbers must be unique"));
                    assertThat(error.getExtensions().get("classification"), is("ValidationError"));
                });

    }
}
