package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.AssociationQuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.entity.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static de.unistuttgart.iste.gits.quiz_service.TestData.association;
import static de.unistuttgart.iste.gits.quiz_service.TestData.createAssociationQuestion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
@TablesToDelete({"association_question_correct_associations", "association_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizUpdateAssociationQuestionTest {

    @Autowired
    private QuizRepository quizRepository;

    @Test
    @Transactional
    @Commit
    void testUpdateAssociationQuestion(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of(
                        createAssociationQuestion(1, association("a", "b"), association("c", "d"))))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        UpdateAssociationQuestionInput input = UpdateAssociationQuestionInput.builder()
                .setId(quizEntity.getQuestionPool().get(0).getId())
                .setHint("new hint")
                .setText("new question")
                .setCorrectAssociations(List.of(
                        new AssociationInput("newA", "newC", "new feedback1"),
                        new AssociationInput("newB", "newD", "new feedback2")))
                .build();

        String query = QuizFragments.FRAGMENT_DEFINITION + """
                mutation($id: UUID!, $input: UpdateAssociationQuestionInput!) {
                    mutateQuiz(assessmentId: $id) {
                        updateAssociationQuestion(input: $input) {
                            ...QuizAllFields
                        }
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .variable("input", input)
                .execute()
                .path("mutateQuiz.updateAssociationQuestion.questionPool[0].number").entity(Integer.class).isEqualTo(1)
                .path("mutateQuiz.updateAssociationQuestion.questionPool[0].text").entity(String.class).isEqualTo("new question")
                .path("mutateQuiz.updateAssociationQuestion.questionPool[0].hint").entity(String.class).isEqualTo("new hint")
                .path("mutateQuiz.updateAssociationQuestion.questionPool[0].correctAssociations").entityList(SingleAssociation.class)
                .contains(
                        new SingleAssociation("newA", "newC", "new feedback1"),
                        new SingleAssociation("newB", "newD", "new feedback2"));

        QuizEntity updatedQuiz = quizRepository.findById(quizEntity.getAssessmentId()).orElseThrow();
        assertThat(updatedQuiz.getQuestionPool(), hasSize(1));
        AssociationQuestionEntity updatedQuestion = (AssociationQuestionEntity) updatedQuiz.getQuestionPool().get(0);
        assertThat(updatedQuestion.getText(), is("new question"));
        assertThat(updatedQuestion.getHint(), is("new hint"));
        assertThat(updatedQuestion.getCorrectAssociations(), hasSize(2));
        assertThat(updatedQuestion.getCorrectAssociations(), containsInAnyOrder(
                association("newA", "newC", "new feedback1"),
                association("newB", "newD", "new feedback2")));


    }
}
