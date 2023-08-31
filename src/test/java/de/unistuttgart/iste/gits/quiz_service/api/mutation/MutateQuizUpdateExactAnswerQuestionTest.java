package de.unistuttgart.iste.gits.quiz_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.ResourceMarkdownInput;
import de.unistuttgart.iste.gits.generated.dto.UpdateExactAnswerQuestionInput;
import de.unistuttgart.iste.gits.quiz_service.TestData;
import de.unistuttgart.iste.gits.quiz_service.api.QuizFragments;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.ExactAnswerQuestionEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.dao.QuizEntity;
import de.unistuttgart.iste.gits.quiz_service.persistence.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static de.unistuttgart.iste.gits.quiz_service.TestData.createExactAnswerQuestion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
@TablesToDelete({"exact_answer_question_correct_answers", "exact_answer_question", "quiz_question_pool", "question", "quiz"})
class MutateQuizUpdateExactAnswerQuestionTest {

    @Autowired
    private QuizRepository quizRepository;

    @Test
    @Transactional
    @Commit
    void testUpdateExactAnswerQuestion(GraphQlTester graphQlTester) {
        QuizEntity quizEntity = TestData.exampleQuizBuilder()
                .questionPool(List.of(
                        createExactAnswerQuestion(1, "question", "answer")))
                .build();
        quizEntity = quizRepository.save(quizEntity);

        UpdateExactAnswerQuestionInput input = UpdateExactAnswerQuestionInput.builder()
                .setId(quizEntity.getQuestionPool().get(0).getId())
                .setHint(new ResourceMarkdownInput("new hint"))
                .setText(new ResourceMarkdownInput("new question"))
                .setFeedback(new ResourceMarkdownInput("new feedback"))
                .setCaseSensitive(false)
                .setCorrectAnswers(List.of("newA", "newB"))
                .build();

        String query = QuizFragments.FRAGMENT_DEFINITION + """
                mutation($id: UUID!, $input: UpdateExactAnswerQuestionInput!) {
                    mutateQuiz(assessmentId: $id) {
                        updateExactAnswerQuestion(input: $input) {
                            ...QuizAllFields
                        }
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", quizEntity.getAssessmentId())
                .variable("input", input)
                .execute()
                .path("mutateQuiz.updateExactAnswerQuestion.questionPool[0].number").entity(Integer.class).isEqualTo(1)
                .path("mutateQuiz.updateExactAnswerQuestion.questionPool[0].text.text").entity(String.class).isEqualTo("new question")
                .path("mutateQuiz.updateExactAnswerQuestion.questionPool[0].hint.text").entity(String.class).isEqualTo("new hint")
                .path("mutateQuiz.updateExactAnswerQuestion.questionPool[0].feedback.text").entity(String.class).isEqualTo("new feedback")
                .path("mutateQuiz.updateExactAnswerQuestion.questionPool[0].caseSensitive").entity(Boolean.class).isEqualTo(false)
                .path("mutateQuiz.updateExactAnswerQuestion.questionPool[0].correctAnswers").entityList(String.class).contains("newA", "newB");

        QuizEntity updatedQuiz = quizRepository.findById(quizEntity.getAssessmentId()).orElseThrow();
        assertThat(updatedQuiz.getQuestionPool(), hasSize(1));
        ExactAnswerQuestionEntity updatedQuestion = (ExactAnswerQuestionEntity) updatedQuiz.getQuestionPool().get(0);
        assertThat(updatedQuestion.getText().getText(), is("new question"));
        assertThat(updatedQuestion.getHint().getText(), is("new hint"));
        assertThat(updatedQuestion.getFeedback().getText(), is("new feedback"));
        assertThat(updatedQuestion.isCaseSensitive(), is(false));
        assertThat(updatedQuestion.getCorrectAnswers(), containsInAnyOrder("newA", "newB"));
    }
}
