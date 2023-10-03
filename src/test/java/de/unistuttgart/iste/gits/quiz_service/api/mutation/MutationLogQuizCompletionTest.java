package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.common.event.UserProgressLogEvent;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.MockTestPublisherConfiguration;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@GraphQlApiTest
@ContextConfiguration(classes = MockTestPublisherConfiguration.class)
@TablesToDelete({"multiple_choice_question_answers", "multiple_choice_question", "quiz_question_pool", "question", "quiz"})
class MutationLogQuizCompletionTest {

    @Autowired
    private TopicPublisher mockTopicPublisher;

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Given a quiz
     * When the "logQuizCompletion" mutation is called with the quiz's assessment id
     * Then the dapr topic publisher is called and the correct feedback is returned
     */
    @Test
    @Transactional
    @Commit
    void testLogQuizCompletion(final HttpGraphQlTester graphQlTester) {
        //init
        final UUID assessmentId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();
        final UUID courseId = UUID.randomUUID();

        // create Database entities
        final List<QuestionEntity> questions = TestData.createDummyQuestions();
        QuizEntity quizEntity = QuizEntity.builder().assessmentId(assessmentId)
                .courseId(courseId)
                .questionPool(questions)
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .requiredCorrectAnswers(2)
                .numberOfRandomlySelectedQuestions(2).build();
        quizEntity = quizRepository.save(quizEntity);

        // create Inputs
        final QuestionCompletedInput inputQuestion = QuestionCompletedInput.builder()
                .setQuestionId(quizEntity.getQuestionPool().get(0).getId())
                .setCorrect(true)
                .setUsedHint(false)
                .build();
        final QuestionCompletedInput inputQuestion2 = QuestionCompletedInput.builder()
                .setQuestionId(quizEntity.getQuestionPool().get(1).getId())
                .setCorrect(false)
                .setUsedHint(true)
                .build();

        final QuizCompletedInput quizCompletedInput = QuizCompletedInput.builder()
                .setQuizId(assessmentId)
                .setCompletedQuestions(List.of(inputQuestion, inputQuestion2))
                .build();

        // create expected Progress event
        final UserProgressLogEvent expectedUserProgressLogEvent = UserProgressLogEvent.builder()
                .userId(userId)
                .contentId(assessmentId)
                .hintsUsed(1)
                .success(false)
                .timeToComplete(null)
                .correctness(1.0 / quizEntity.getQuestionPool().size())
                .build();
        final QuizCompletionFeedback expectedQuizCompletionFeedback = QuizCompletionFeedback.builder()
                .setCorrectness(1.0 / quizEntity.getQuestionPool().size())
                .setHintsUsed(1)
                .setSuccess(false)
                .build();

        final String currentUser = """
                {
                    "id": "%s",
                    "userName": "MyUserName",
                    "firstName": "John",
                    "lastName": "Doe",
                    "courseMemberships": []
                }
                """.formatted(userId);


        final String query = """
                mutation($input: QuizCompletedInput!) {
                    logQuizCompleted(input: $input) {
                        correctness
                        hintsUsed
                        success
                    }
                }
                """;

        final QuizCompletionFeedback actualFeedback = graphQlTester
                .mutate()
                .header("CurrentUser", currentUser)
                .build()
                .document(query)
                .variable("input", quizCompletedInput)
                .execute()
                .path("logQuizCompleted").entity(QuizCompletionFeedback.class)
                .get();

        assertThat(actualFeedback, is(expectedQuizCompletionFeedback));

        verify(mockTopicPublisher, times(1))
                .notifyUserWorkedOnContent(expectedUserProgressLogEvent);


    }

}
