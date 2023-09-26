package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.gits.quiz_service.matcher.QuizEntityToCreateInputMatcher.matchesCreateQuizInput;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

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
    void testCreateQuizWithoutQuestions(final GraphQlTester graphQlTester) {
        final UUID assessmentId = UUID.randomUUID();
        final CreateQuizInput createQuizInput = CreateQuizInput.builder()
                .setRequiredCorrectAnswers(1)
                .setQuestionPoolingMode(QuestionPoolingMode.RANDOM)
                .setNumberOfRandomlySelectedQuestions(2)
                .build();

        final String query = QuizFragments.FRAGMENT_DEFINITION + """
                mutation createQuiz($id: UUID!, $input: CreateQuizInput!) {
                    createQuiz(assessmentId: $id, input: $input) {
                        ...QuizAllFields
                    }
                }""";

        // note that deserialization of the result into Quiz dto is not possible because "Question" is an interface
        // so check the fields manually
        final List<MultipleChoiceQuestion> questions = graphQlTester.document(query)
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
}
