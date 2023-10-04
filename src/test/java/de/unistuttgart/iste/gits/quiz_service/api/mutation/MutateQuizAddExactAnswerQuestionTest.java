package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.InjectCurrentUserHeader;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.gits.generated.dto.CreateExactAnswerQuestionInput;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static de.unistuttgart.iste.gits.common.testutil.TestUsers.userWithMembershipInCourseWithId;


@GraphQlApiTest
@TablesToDelete({"exact_answer_question_correct_answers", "exact_answer_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizAddExactAnswerQuestionTest {

    @Autowired
    private QuizRepository quizRepository;
    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, LoggedInUser.UserRoleInCourse.ADMINISTRATOR);

    private static final String ADD_EXACT_ANSWER_QUESTION_MUTATION = QuizFragments.FRAGMENT_DEFINITION + """
            mutation($id: UUID!, $input: CreateExactAnswerQuestionInput!) {
                mutateQuiz(assessmentId: $id) {
                    addExactAnswerQuestion(input: $input) {
                        ...QuizAllFields
                    }
                }
            }
            """;

    /**
     * Given a quiz
     * When the "addExactAnswer" mutation is called with a new question
     * Then the new question is added to the quiz
     */
    @Test
    @Transactional
    @Commit
    void testAddExactAnswerQuestion(final GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder(courseId)
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        final CreateExactAnswerQuestionInput input = CreateExactAnswerQuestionInput.builder()
                .setHint("hint")
                .setText("question")
                .setFeedback("feedback")
                .setCaseSensitive(true)
                .setCorrectAnswers(List.of("a", "b"))
                .build();


        graphQlTester.document(ADD_EXACT_ANSWER_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.addExactAnswerQuestion.questionPool[0].number")
                .entity(Integer.class).isEqualTo(1)

                .path("mutateQuiz.addExactAnswerQuestion.questionPool[0].hint")
                .entity(String.class).isEqualTo("hint")

                .path("mutateQuiz.addExactAnswerQuestion.questionPool[0].text")
                .entity(String.class).isEqualTo("question")

                .path("mutateQuiz.addExactAnswerQuestion.questionPool[0].feedback")
                .entity(String.class).isEqualTo("feedback")

                .path("mutateQuiz.addExactAnswerQuestion.questionPool[0].caseSensitive")
                .entity(Boolean.class).isEqualTo(true)

                .path("mutateQuiz.addExactAnswerQuestion.questionPool[0].correctAnswers")
                .entityList(String.class).contains("a", "b");

        final QuestionEntity questionEntity = quizRepository.findById(quizEntity.getAssessmentId())
                .orElseThrow()
                .getQuestionPool()
                .get(0);

        assertThat(questionEntity, instanceOf(ExactAnswerQuestionEntity.class));
        final ExactAnswerQuestionEntity exactAnswerQuestionEntity = (ExactAnswerQuestionEntity) questionEntity;

        assertThat(exactAnswerQuestionEntity.getHint(), is("hint"));
        assertThat(exactAnswerQuestionEntity.getText(), is("question"));
        assertThat(exactAnswerQuestionEntity.getFeedback(), is("feedback"));
        assertThat(exactAnswerQuestionEntity.isCaseSensitive(), is(true));
        assertThat(exactAnswerQuestionEntity.getCorrectAnswers(), hasSize(2));
        assertThat(exactAnswerQuestionEntity.getCorrectAnswers(), containsInAnyOrder("a", "b"));

    }
}
