package de.unistuttgart.iste.gits.quiz_service.api;


import de.unistuttgart.iste.gits.common.testutil.AuthorizationAsserts;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.InjectCurrentUserHeader;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.gits.generated.dto.ClozeElementInput;
import de.unistuttgart.iste.gits.generated.dto.ClozeElementType;
import de.unistuttgart.iste.gits.generated.dto.CreateClozeQuestionInput;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.gits.common.testutil.TestUsers.userWithMembershipInCourseWithId;

@GraphQlApiTest
@TablesToDelete({"quiz"})
public class AuthorizationTest {

    private static final String ADD_CLOZE_QUESTION_MUTATION = QuizFragments.FRAGMENT_DEFINITION + """
            mutation($id: UUID!, $input: CreateClozeQuestionInput!) {
                mutateQuiz(assessmentId: $id) {
                    addClozeQuestion(input: $input) {
                        ...QuizAllFields
                    }
                }
            }
            """;


    @Autowired
    private QuizRepository quizRepository;
    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, LoggedInUser.UserRoleInCourse.STUDENT);

    @Test
    @Transactional
    @Commit
    void testAddClozeQuestionForAdminOnly(final GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder(courseId)
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        final CreateClozeQuestionInput input = CreateClozeQuestionInput.builder()
                .setHint("hint")
                .setAdditionalWrongAnswers(List.of("wrong1", "wrong2"))
                .setShowBlanksList(false)
                .setClozeElements(List.of(
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.TEXT)
                                .setText("what is the capital of France?")
                                .build(),
                        ClozeElementInput.builder()
                                .setType(ClozeElementType.BLANK)
                                .setCorrectAnswer("Paris")
                                .setFeedback("feedback")
                                .build()
                ))
                .build();


        graphQlTester.document(ADD_CLOZE_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(AuthorizationAsserts::assertIsMissingUserRoleError);

    }


}
