package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.*;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.*;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
@TablesToDelete({"association_question_correct_associations", "association_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizAddAssociationQuestionTest {

    @Autowired
    private QuizRepository quizRepository;

    private static final String ADD_ASSOCIATION_QUESTION_MUTATION = QuizFragments.FRAGMENT_DEFINITION + """
            mutation($id: UUID!, $input: CreateAssociationQuestionInput!) {
                mutateQuiz(assessmentId: $id) {
                    addAssociationQuestion(input: $input) {
                        ...QuizAllFields
                    }
                }
            }
            """;

    /**
     * Given a quiz
     * When the "addAssociationQuestion" mutation is called with a new question
     * Then the new question is added to the quiz
     */
    @Test
    @Transactional
    @Commit
    void testAddAssociationQuestion(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateAssociationQuestionInput input = CreateAssociationQuestionInput.builder()
                .setHint(new ResourceMarkdownInput("hint"))
                .setText(new ResourceMarkdownInput("question"))
                .setCorrectAssociations(List.of(
                        new AssociationInput("a", "b", new ResourceMarkdownInput("feedback1")),
                        new AssociationInput("c", "d", new ResourceMarkdownInput("feedback2"))))
                .build();


        graphQlTester.document(ADD_ASSOCIATION_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .path("mutateQuiz.addAssociationQuestion.questionPool[0].number")
                .entity(Integer.class)
                .isEqualTo(1)

                .path("mutateQuiz.addAssociationQuestion.questionPool[0].text.text")
                .entity(String.class)
                .isEqualTo("question")

                .path("mutateQuiz.addAssociationQuestion.questionPool[0].hint.text")
                .entity(String.class)
                .isEqualTo("hint")

                .path("mutateQuiz.addAssociationQuestion.questionPool[0].correctAssociations")
                .entityList(SingleAssociation.class)
                .contains(
                        new SingleAssociation("a", "b", new ResourceMarkdown("feedback1", List.of())),
                        new SingleAssociation("c", "d", new ResourceMarkdown("feedback2", List.of())))

                .path("mutateQuiz.addAssociationQuestion.questionPool[0].leftSide")
                .entityList(String.class)
                .contains("a", "c")

                .path("mutateQuiz.addAssociationQuestion.questionPool[0].rightSide")
                .entityList(String.class)
                .contains("b", "d");

        QuestionEntity questionEntity = quizRepository.findById(quizEntity.getAssessmentId())
                .orElseThrow()
                .getQuestionPool()
                .get(0);

        assertThat(questionEntity, instanceOf(AssociationQuestionEntity.class));
        AssociationQuestionEntity associationQuestionEntity = (AssociationQuestionEntity) questionEntity;

        assertThat(associationQuestionEntity.getHint().getText(), is("hint"));
        assertThat(associationQuestionEntity.getText().getText(), is("question"));
        assertThat(associationQuestionEntity.getCorrectAssociations(), hasSize(2));
        assertThat(associationQuestionEntity.getCorrectAssociations(), containsInAnyOrder(
                new AssociationEmbeddable("a", "b", new ResourceMarkdownEmbeddable("feedback1")),
                new AssociationEmbeddable("c", "d", new ResourceMarkdownEmbeddable("feedback2"))));

    }

    /**
     * Given a quiz and an association question where the associations are not unique
     * When the "addAssociationQuestion" mutation is called with a new question
     * Then the new question is added to the quiz
     */
    @Test
    @Transactional
    void testAddAssociationQuestionNotUniqueAnswer(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of())
                .build();
        quizEntity = quizRepository.save(quizEntity);

        CreateAssociationQuestionInput input = CreateAssociationQuestionInput.builder()
                .setNumber(2)
                .setHint(new ResourceMarkdownInput("hint"))
                .setText(new ResourceMarkdownInput("question"))
                .setCorrectAssociations(List.of(
                        new AssociationInput("a", "b", new ResourceMarkdownInput("feedback1")),
                        new AssociationInput("c", "b", new ResourceMarkdownInput("feedback2"))))
                .build();

        graphQlTester.document(ADD_ASSOCIATION_QUESTION_MUTATION)
                .variable("input", input)
                .variable("id", quizEntity.getAssessmentId())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(), containsString("Each side of the associations must only contain unique values"));
                });
    }
}
