package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.UpdateSelfAssessmentQuestionInput;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.SelfAssessmentQuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static de.unistuttgart.iste.gits.quiz_service.TestData.createSelfAssessmentQuestion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@GraphQlApiTest
@TablesToDelete({"self_assessment_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizUpdateSelfAssessmentQuestionTest {

    @Autowired
    private QuizRepository quizRepository;

    @Test
    @Transactional
    @Commit
    void testUpdateSelfAssessmentQuestion(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of(
                        createSelfAssessmentQuestion(1, "question", "answer")))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        UpdateSelfAssessmentQuestionInput input = UpdateSelfAssessmentQuestionInput.builder()
                .setId(quizEntity.getQuestionPool().get(0).getId())
                .setHint("new hint")
                .setText("new question")
                .setSolutionSuggestion("new solution suggestion")
                .build();

        String query = QuizFragments.FRAGMENT_DEFINITION + """
                mutation($id: UUID!, $input: UpdateSelfAssessmentQuestionInput!) {
                    mutateQuiz(assessmentId: $id) {
                        updateSelfAssessmentQuestion(input: $input) {
                            ...QuizAllFields
                        }
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .variable("input", input)
                .execute()
                .path("mutateQuiz.updateSelfAssessmentQuestion.questionPool[0].number").entity(Integer.class).isEqualTo(1)
                .path("mutateQuiz.updateSelfAssessmentQuestion.questionPool[0].text").entity(String.class).isEqualTo("new question")
                .path("mutateQuiz.updateSelfAssessmentQuestion.questionPool[0].hint").entity(String.class).isEqualTo("new hint")
                .path("mutateQuiz.updateSelfAssessmentQuestion.questionPool[0].solutionSuggestion").entity(String.class).isEqualTo("new solution suggestion");

        QuizEntity updatedQuiz = quizRepository.findById(quizEntity.getAssessmentId()).orElseThrow();
        assertThat(updatedQuiz.getQuestionPool(), hasSize(1));
        SelfAssessmentQuestionEntity updatedQuestion = (SelfAssessmentQuestionEntity) updatedQuiz.getQuestionPool().get(0);
        assertThat(updatedQuestion.getText(), is("new question"));
        assertThat(updatedQuestion.getHint(), is("new hint"));
        assertThat(updatedQuestion.getSolutionSuggestion(), is("new solution suggestion"));
    }
}
