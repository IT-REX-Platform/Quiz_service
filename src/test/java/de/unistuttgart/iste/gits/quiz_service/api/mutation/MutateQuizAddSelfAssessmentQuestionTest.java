package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.CreateSelfAssessmentQuestionInput;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@GraphQlApiTest
@TablesToDelete({"self_assessment_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizAddSelfAssessmentQuestionTest {

    @Autowired
    private QuizRepository quizRepository;

    private static final String ADD_SELF_ASSESSMENT_QUESTION_MUTATION = QuizFragments.FRAGMENT_DEFINITION + """
            mutation($id: UUID!, $input: CreateSelfAssessmentQuestionInput!) {
                mutateQuiz(assessmentId: $id) {
                    addSelfAssessmentQuestion(input: $input) {
                        ...QuizAllFields
                    }
                }
            }
            """;

    /**
     * Given a quiz
     * When the "addSelfAssessmentQuestion" mutation is called with a new question
     * Then the new question is added to the quiz
     */
    @Test
    @Transactional
    @Commit
    void testAddSelfAssessmentQuestion(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateSelfAssessmentQuestionInput input = CreateSelfAssessmentQuestionInput.builder()
                .setHint("hint")
                .setText("question")
                .setSolutionSuggestion("solution suggestion")
                .build();


        graphQlTester.document(ADD_SELF_ASSESSMENT_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.addSelfAssessmentQuestion.questionPool[0].number")
                .entity(Integer.class).isEqualTo(1)

                .path("mutateQuiz.addSelfAssessmentQuestion.questionPool[0].hint")
                .entity(String.class).isEqualTo("hint")

                .path("mutateQuiz.addSelfAssessmentQuestion.questionPool[0].text")
                .entity(String.class).isEqualTo("question")

                .path("mutateQuiz.addSelfAssessmentQuestion.questionPool[0].solutionSuggestion")
                .entity(String.class).isEqualTo("solution suggestion");

        QuestionEntity questionEntity = quizRepository.findById(quizEntity.getAssessmentId())
                .orElseThrow()
                .getQuestionPool()
                .get(0);

        assertThat(questionEntity, instanceOf(SelfAssessmentQuestionEntity.class));
        SelfAssessmentQuestionEntity selfAssessmentQuestionEntity = (SelfAssessmentQuestionEntity) questionEntity;

        assertThat(selfAssessmentQuestionEntity.getHint(), is("hint"));
        assertThat(selfAssessmentQuestionEntity.getText(), is("question"));
        assertThat(selfAssessmentQuestionEntity.getSolutionSuggestion(), is("solution suggestion"));

    }
}
