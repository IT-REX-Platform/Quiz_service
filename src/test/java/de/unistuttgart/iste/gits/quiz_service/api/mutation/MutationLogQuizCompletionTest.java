package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.event.UserProgressLogEvent;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import de.unistuttgart.iste.gits.quiz_service.test_config.MockTopicPublisherConfiguration;
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
@ContextConfiguration(classes = MockTopicPublisherConfiguration.class)
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
    void testLogQuizCompletion(HttpGraphQlTester graphQlTester) {
        //init
        UUID assessmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // create Database entities
        List<QuestionEntity> questions = TestData.createDummyQuestions();
        QuizEntity quizEntity = QuizEntity.builder().assessmentId(assessmentId)
                .questionPool(questions)
                .questionPoolingMode(QuestionPoolingMode.ORDERED)
                .requiredCorrectAnswers(2)
                .numberOfRandomlySelectedQuestions(2).build();
        quizEntity = quizRepository.save(quizEntity);

        // create Inputs
        QuestionCompletedInput inputQuestion = QuestionCompletedInput.builder()
                .setQuestionId(quizEntity.getQuestionPool().get(0).getId())
                .setCorrect(true)
                .setUsedHint(false)
                .build();
        QuestionCompletedInput inputQuestion2 = QuestionCompletedInput.builder()
                .setQuestionId(quizEntity.getQuestionPool().get(1).getId())
                .setCorrect(false)
                .setUsedHint(true)
                .build();

        QuizCompletedInput quizCompletedInput = QuizCompletedInput.builder()
                .setQuizId(assessmentId)
                .setCompletedQuestions(List.of(inputQuestion, inputQuestion2))
                .build();

        // create expected Progress event
        UserProgressLogEvent expectedUserProgressLogEvent = UserProgressLogEvent.builder()
                .userId(userId)
                .contentId(assessmentId)
                .hintsUsed(1)
                .success(false)
                .timeToComplete(null)
                .correctness(1.0 / quizEntity.getQuestionPool().size())
                .build();
        QuizCompletionFeedback expectedQuizCompletionFeedback = QuizCompletionFeedback.builder()
                .setCorrectness(1.0 / quizEntity.getQuestionPool().size())
                .setHintsUsed(1)
                .setSuccess(false)
                .build();

        String currentUser = """
                {
                    "id": "%s",
                    "userName": "MyUserName",
                    "firstName": "John",
                    "lastName": "Doe",
                    "courseMemberships": []
                }
                """.formatted(userId);


        String query = """
                mutation($input: QuizCompletedInput!) {
                    logQuizCompleted(input: $input) {
                        correctness
                        hintsUsed
                        success
                    }
                }
                """;

        QuizCompletionFeedback actualFeedback = graphQlTester
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
